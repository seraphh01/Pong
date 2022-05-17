package com.seraphicgames.pong;

import android.graphics.RectF;

public class Ball {
    private RectF mRect;
    private float mXVelocity;
    private float mYVelocity;
    private float mBallWidth;
    private float mBallHeight;

    Ball(int screenX) {
        // Make the ball square and 1% of screen width
        // of the screen width
        mBallWidth = screenX / 100f;
        mBallHeight = screenX / 100f;

        // Initialize the RectF with 0, 0, 0, 0
        // We do it here because we only want to
        // do it once.
        // We will initialize the detail
        // at the start of each game
        mRect = new RectF();
    }

    void update(long fps) {
        //move the top left corner
//        mRect.offset(mXVelocity / fps, mYVelocity / fps);
        mRect.left = mRect.left + (mXVelocity / fps);
        mRect.top = mRect.top + (mYVelocity / fps);

        //match up the bottom right corner based on the size of the ball
        mRect.right = mRect.left + mBallWidth;
        mRect.bottom = mRect.top + mBallHeight;
    }

    public RectF getRect() {
        return mRect;
    }

    void reverseXVelocity() {
        mXVelocity = -mXVelocity;
    }

    void reverseYVelocity() {
        mYVelocity = -mYVelocity;
    }

    void reset(int x, int y) {
        mRect.left = x / 2f;
        mRect.top = 0;
        mRect.right = x / 2f + mBallWidth;
        mRect.bottom = mBallHeight;

        mRect.offsetTo(x/2f, y/2f);

        mYVelocity = (y / 10f);
        mXVelocity = (y / 3f);
    }

    void increaseVelocity(){
        mXVelocity *= 1.05f;
        mYVelocity *= 1.05f;
    }

    //bounce the ball back back based on
    //whether it hits the left or right side
    void batBounce(RectF batRect, BatOrientation batOrientation){
        switch (batOrientation){
            case HORIZONTAL:
                horizontalBatBounce(batRect);
                break;
            case VERTICAL:
                verticalBatBounce(batRect);
                break;
        }
    }

    private void horizontalBatBounce(RectF batRect){
        // Detect centre of bat
        float batCenter = batRect.left +
                (batRect.width() / 2);
        // detect the centre of the ball
        float ballCenter = mRect.left +
                (mBallWidth / 2);
        // Where on the bat did the ball hit?
        float batWidth = batRect.width();

        if(ballCenter < batCenter - batWidth / 3f)
        {
            //move left
            mXVelocity = -(Math.abs(mXVelocity) * 1.05f);
        }else if(ballCenter > batCenter + batWidth / 3f){
            mXVelocity = Math.abs(mXVelocity * 1.05f);
        }else{
            mXVelocity *= 0.95f;
        }
        //increaseVelocity();

        // Having calculated left or right for
        // horizontal direction simply reverse the
        // vertical direction to go back up
        // the screen
        reverseYVelocity();
    }

    private void verticalBatBounce(RectF batRect){
        // Detect centre of bat
        float batCenter = batRect.centerY();
        // detect the centre of the ball
        float ballCenter = mRect.centerY();
        // Where on the bat did the ball hit?
        float batHeight = batRect.height();

        if(ballCenter < batCenter - batHeight / 3f)
        {
            //move top
            mYVelocity = -(Math.abs(mYVelocity) * 1.05f);
        }else if(ballCenter > batCenter + batHeight / 3f){
            //move down
            mYVelocity = Math.abs(mYVelocity * 1.05f);
        }else{
            //move the same direction
            mYVelocity *= 0.95f;
        }
        //increaseVelocity();

        // Having calculated left or right for
        // horizontal direction simply reverse the
        // vertical direction to go back up
        // the screen
        reverseXVelocity();
    }

}
