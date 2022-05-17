package com.seraphicgames.pong;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

@SuppressLint("ViewConstructor")
public class PongGame extends SurfaceView implements Runnable {
    protected boolean DEBUGGING = true;
    private final int INITIAL_LIVES = 2000;

    protected SurfaceHolder mOurHolder;
    protected Canvas canvas;
    protected Paint paint;
    // How many frames per second did we get?
    protected long mFPS;
    // The number of milliseconds in a second
    protected final int MILLIS_IN_SECOND = 1000;
    // Holds the resolution of the screen
    protected int mScreenX;
    protected int mScreenY;
    // How big will the text be?
    protected int fontSize;
    protected int mFontMargin;
    // The game objects
    protected Bat mBat;
    protected Ball mBall;

    protected int mScore;
    protected int mLives;

    private Thread gameThread = null;
    protected volatile boolean mPlaying;
    protected boolean mPaused = false;

    private ArrayList<Long> fpsHistory = new ArrayList<>();

    public PongGame(Context context, int x, int y) {
        super(context);
        this.mScreenX = x;
        this.mScreenY = y;

        fontSize = mScreenX / 20;
        mFontMargin = mScore / 75;

        mOurHolder = getHolder();
        paint = new Paint();

        //Initialize the bat and the ball
        mBall = new Ball(mScreenX);
        mBat = new Bat(mScreenX, mScreenY);


        //start the game
        startNewGame();
    }


    protected void startNewGame() {
        //put the ball back to starting position
        mBall.reset(mScreenX, mScreenY);

        mScore = 0;
        mLives = INITIAL_LIVES;

        mPlaying = true;
    }


    protected void draw() {
        if (mOurHolder.getSurface().isValid()) {
            //ready to draw so lock it
            canvas = mOurHolder.lockCanvas();

            //fill the screen
            canvas.drawColor(Color.argb(255, 26, 128, 182));

            paint.setColor(Color.WHITE);

            //draw the ball and the bat
            drawObjects();

            //font size
            paint.setTextSize(fontSize);

            //draw the HUD
            drawHUD();

            if (DEBUGGING)
                printDebuggingText();

            //display the drawing
            mOurHolder.unlockCanvasAndPost(canvas);
        }
    }

    protected void drawObjects(){
        canvas.drawRect(mBall.getRect(), paint);
        canvas.drawRect(mBat.getRect(), paint);
    }

    protected void drawHUD(){
        canvas.drawText("Score: " + mScore +
                        " Lives: " + mLives,
                mFontMargin, fontSize, paint);
    }

    private long getAverageFPS(){
        int size = fpsHistory.size();
        long average = 0;
        for (long frameTime: fpsHistory)
            average += frameTime;
        return average / size;
    }

    private void addToFPSHistory(long frameTime){
        fpsHistory.add(frameTime);
        if(fpsHistory.size() > 5)
            fpsHistory.remove(0);
    }

    private void printDebuggingText() {
        int debugSize = fontSize / 2;
        int debugStart = 150;
        paint.setTextSize(debugSize);
        canvas.drawText("FPS: " + getAverageFPS(),
                10, debugStart + debugSize, paint);
    }

    @Override
    public void run() {
        while(mPlaying){
            long frameStartTime = System.currentTimeMillis();

            draw();

            if(!mPaused) {
                update();
                detectCollisions();
            }


            long timeFrame = System.currentTimeMillis() - frameStartTime;

            if(timeFrame > 0) {
                mFPS = MILLIS_IN_SECOND / timeFrame;
                addToFPSHistory(mFPS);
            }

            if(mLives <= 0)
                mPaused = true;
        }
    }

    protected void detectCollisions() {
        RectF ballRect = mBall.getRect();
        if(ballRect.top < 1)
            mBall.reverseYVelocity();
        if(ballRect.bottom > mScreenY - 1) {
            mBall.reset(mScreenX, mScreenY);
            mLives--;
        }
        if(ballRect.left < 1)
            mBall.reverseXVelocity();
        if(ballRect.right > mScreenX - 1)
            mBall.reverseXVelocity();

        if(mBat.getRect().intersect(mBall.getRect()))
            mBall.batBounce(mBat.getRect(), BatOrientation.HORIZONTAL);
    }

    protected void update() {

        mBall.update(mFPS);
        mBat.update(mFPS);

    }

    public void pause() {
        mPlaying = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error: ", "joining thread had an error");
        }
    }

    // This method is called by PongActivity
    // when the player starts the game
    public void resume() {
        mPlaying = true;
        // Initialize the instance of Thread
        gameThread = new Thread(this);
        // Start the thread
        gameThread.start();
    }

    private void moveBat(float x, float y){
        if(x <= mScreenX / 2f)
            mBat.setMovementState(BatState.LEFT);
        else
            mBat.setMovementState(BatState.RIGHT);
    }

    private void stopBat(){
        mBat.setMovementState(BatState.STOPPED);
    }

    protected RectF simpleRect(){
        return new RectF(0,0,50, 50);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        handleOnTouchEvent(event);
        return true;
    }

    protected void handleOnTouchEvent(MotionEvent event){
        mPaused = false;
        Log.d("X", String.valueOf(event.getX()));
        int action = event.getAction();

        float x = event.getX();
        float y = event.getY();

        switch (action){
            case MotionEvent.ACTION_DOWN:
                moveBat(x, y);
                return;
            case MotionEvent.ACTION_UP:
                stopBat();
                return;
            case MotionEvent.ACTION_POINTER_DOWN:
                RectF rect = simpleRect();
                rect.offsetTo(x, y);
                canvas.drawRect(rect, paint);
        }
    }
}
