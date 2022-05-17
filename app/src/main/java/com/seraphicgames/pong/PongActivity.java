package com.seraphicgames.pong;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Point;
import android.view.Display;

public class PongActivity extends Activity {

    private PongGame pongGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();
        Point displaySize = new Point();
        display.getSize(displaySize);

        pongGame = new PongGameDuo(this, displaySize.x, displaySize.y);

        setContentView(pongGame);
    }

    @Override
    protected void onResume() {
        super.onResume();

        pongGame.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        pongGame.pause();
    }
}