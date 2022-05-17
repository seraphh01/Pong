package com.seraphicgames.pong;

import android.graphics.RectF;

public class VerticalBat extends Bat{
    protected float mYCoord;
    protected int mScreenY;
    protected float mHeight;

    private RectF savedRect;

    VerticalBat(int screenX, int screenY, float spawnX, float spawnY) {
        super(screenX, screenY);
        mScreenY = screenY;
        mHeight = mScreenY / 6f;
        mWidth = mScreenX / 50f;
        mYCoord = screenY/2f - mHeight /2f;

        mRect = new RectF(0, mYCoord, mWidth, mYCoord+mHeight);
        mRect.offsetTo(spawnX, spawnY);
        savedRect = mRect;
        mYCoord = mRect.centerY();
        mBatSpeed = mScreenY / 4f;
    }

    @Override
    RectF getRect() {
        return super.getRect();
    }

    @Override
    void setMovementState(BatState newState) {
        super.setMovementState(newState);
    }

    @Override
    void update(long fps) {
        switch (mState){
            case TOP:
                mYCoord -= mBatSpeed / fps;
                break;
            case BOTTOM:
                mYCoord += mBatSpeed / fps;
                break;
            case STOPPED:
                return;
        }

        if(mYCoord - mHeight / 2f < 0f)
            mYCoord = mHeight / 2f;
        else if(mYCoord + mHeight /2f > mScreenY)
            mYCoord = mScreenY - mHeight / 2f;

        mRect.top = mYCoord - mHeight / 2f;
        mRect.bottom = mRect.top + mHeight;
    }

    void refreshRect(){
        float top = mRect.top;
        float left = mRect.left;
        mRect = savedRect;
        mRect.offsetTo(left, top);
    }
}
