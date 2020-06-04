package com.game.catchme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RestartGameActivity extends AppCompatActivity {

    private int highScoreValue = 0;

    private static final String scoreIntentData = "점수";
    private static final String highScoreIntentData = "최고 점수";

    // 게임 재시작 액티비티 초기화
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restart_game);

        // 받아야할 데이터(점수, 최고 점수)를 위한 intent
        Intent passedIntent = getIntent();

        // 인텐트에 있는 데이터 받아옴
        int scoreValue = passedIntent.getIntExtra(scoreIntentData, 0);
        highScoreValue = passedIntent.getIntExtra(highScoreIntentData, 0);

        // 받아온 데이터를 UI에 반영
        TextView scoreText = (TextView) findViewById(R.id.lastScore);
        TextView highScore = (TextView) findViewById(R.id.highScore);

        String scoreString = getResources().getString(R.string.score) + " " + scoreValue;
        scoreText.setText(scoreString);

        String highScoreString = "최고 점수 : " + highScoreValue;
        highScore.setText(highScoreString);
    }

    // 재시작 버튼 클릭시, 게임 플레이 액티비티로 이동 (최고점수를 intent 로 넘김)
    public void onClick(View v){
        Intent switchIntent = new Intent(this, PlayGameActivity.class);
        switchIntent.putExtra(highScoreIntentData, highScoreValue);
        startActivity(switchIntent);
    }
}