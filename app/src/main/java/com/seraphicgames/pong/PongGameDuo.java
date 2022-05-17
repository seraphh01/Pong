package com.seraphicgames.pong;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;

public class PongGameDuo extends PongGame {
    private VerticalBat leftBat;
    private VerticalBat rightBat;

    private int leftScore;
    private int rightScore;

    private ArrayList<PointF> touchPoints = new ArrayList<>();

    public PongGameDuo(Context context, int x, int y) {
        super(context, x, y);

        leftBat = new VerticalBat(x, y, 0, y / 2f);
        rightBat = new VerticalBat(x, y, mScreenX * 49f / 50f, y / 2f);


        leftScore = 0;
        rightScore = 0;

        DEBUGGING = false;
    }


    @Override
    protected void draw() {
        super.draw();
    }

    @Override
    protected void drawHUD() {
        paint.setTextSize(mScreenY / 15f);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("" + leftScore + ":" + rightScore, mScreenX / 2f, paint.getTextSize() + 10, paint);
    }

    @Override
    protected void drawObjects() {
        canvas.drawRect(mBall.getRect(), paint);
        canvas.drawRect(leftBat.getRect(), paint);
        canvas.drawRect(rightBat.getRect(), paint);
    }

    @Override
    public void run() {
        super.run();
    }

    private boolean rectIntersect(RectF first, RectF second){
        return first.intersect(second) | second.intersect(first);
    }

    @Override
    protected void detectCollisions() {
        float ballCenterX = mBall.getRect().centerX();
        float ballCenterY = mBall.getRect().centerY();
        if ((ballCenterX < mScreenX / 5f) && rectIntersect(mBall.getRect(), leftBat.getRect()))
        {
            mBall.batBounce(leftBat.getRect(), leftBat.getOrientation());
            leftBat.refreshRect();
        }
        else if ((ballCenterX > mScreenX * 0.8f) && rectIntersect(mBall.getRect(), rightBat.getRect()))
        {
            mBall.batBounce(rightBat.getRect(), rightBat.getOrientation());
            rightBat.refreshRect();
        }


        if (ballCenterX < 1) {
            //right player scores
            rightScore++;

            //reset the ball
            mBall.reset(mScreenX, mScreenY);
        } else if (ballCenterX > mScreenX - 1) {
            //left player scores
            leftScore++;

            //reset the ball
            mBall.reset(mScreenX, mScreenY);
        }

        if (ballCenterY < 1) {
            //hit the top  of the screen
            mBall.reverseYVelocity();
        } else if (ballCenterY > mScreenY - 1) {
            //hit the bottom of the screen
            mBall.reverseYVelocity();
        }
    }

    @Override
    protected void update() {
        mBall.update(mFPS);
        leftBat.update(mFPS);
        rightBat.update(mFPS);

        checkBats();
    }

    private void checkBats(){
        if (touchPoints.size() == 0) {
            leftBat.setMovementState(BatState.STOPPED);
            rightBat.setMovementState(BatState.STOPPED);
        }
    }

    private void moveBats() {
        boolean movedLeft = false, movedRight = false;

        for (int i = 0; i < touchPoints.size(); i++) {
            PointF point = touchPoints.get(i);

            if (movedLeft && movedRight)
                return;

            if (!movedLeft && point.x < mScreenX / 2f - 50) {
                movedLeft = true;
                //move the leftBat top or left
                if (point.y < mScreenY / 2f) {
                    leftBat.setMovementState(BatState.TOP);
                } else {
                    leftBat.setMovementState(BatState.BOTTOM);
                }
            }
            if (!movedRight && point.x > mScreenX / 2f + 50) {
                movedRight = true;
                //move the rightBat top or left
                if (point.y < mScreenY / 2f) {
                    rightBat.setMovementState(BatState.TOP);
                } else {
                    rightBat.setMovementState(BatState.BOTTOM);
                }
            }
        }
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

    private boolean isMultiTouch;

    @Override
    protected void handleOnTouchEvent(MotionEvent event) {
        mPaused = false;

        int action = event.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                isMultiTouch = false;
                setPoints(event);
                invalidate();
                break;
            case MotionEvent.ACTION_UP: {
                isMultiTouch = false;
                touchPoints.clear();
                return;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                isMultiTouch = true;
                setPoints(event);
                invalidate();
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                setPoints(event);
                invalidate();
                return;
            }
            case MotionEvent.ACTION_MOVE: {
                if (isMultiTouch) {
                    setPoints(event);
                    invalidate();
                }
                setPoints(event);
                break;
            }
        }
    }

    private void setPoints(MotionEvent event) {
        touchPoints.clear();

        int pointerCount = event.getPointerCount();
        for (int i = 0; i < pointerCount; i++) {


            float x = event.getX(i);
            float y = event.getY(i);

            touchPoints.add(new PointF(x, y));
        }

        moveBats();
    }

}
