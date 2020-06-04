package com.game.catchme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // start 버튼 클릭시 playgame 액티비티로 이동 (인텐트로)
    public void onStartClick(View v){
        Intent playGameIntent = new Intent(this, PlayGameActivity.class);
        startActivity(playGameIntent);
    }
}