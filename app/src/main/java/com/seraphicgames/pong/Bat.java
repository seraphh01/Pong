package com.seraphicgames.pong;

import android.graphics.RectF;

public class Bat {

    public BatOrientation getOrientation() {
        return orientation;
    }

    protected BatOrientation orientation = BatOrientation.HORIZONTAL;
    protected RectF mRect;
    protected float mWidth;
    protected float mXCoord;
    protected float mBatSpeed;
    protected int mScreenX;

    BatState mState = BatState.STOPPED;

    Bat(int x, int y) {
        mScreenX = x;

        mWidth = mScreenX / 8f;
        float height = y / 40f;
        mXCoord = mScreenX / 2f;

        float mYCoord = y - height;

        mRect = new RectF(mXCoord, mYCoord, mXCoord + mWidth, y);

        mBatSpeed = mScreenX;
    }

    // Return a reference to the mRect object
    RectF getRect(){
        return mRect;
    }

    // Update the movement state passed
// in by the onTouchEvent method
    void setMovementState(BatState newState){
        mState =  newState;
    }

    void update(long fps){
        switch (mState){
            case LEFT:
                mXCoord = mXCoord - mBatSpeed / fps;
                break;
            case RIGHT:
                mXCoord += mBatSpeed / fps;
                break;
        }

        if(mXCoord < 0)
            mXCoord = 0;
        else if(mXCoord + mWidth > mScreenX)
            mXCoord = mScreenX - mWidth;

        mRect.left = mXCoord;
        mRect.right = mRect.left + mWidth;
    }
}
