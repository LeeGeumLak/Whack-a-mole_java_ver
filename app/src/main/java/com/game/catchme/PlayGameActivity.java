package com.game.catchme;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class PlayGameActivity extends AppCompatActivity {

    private int overallScore = 0; // 획득 점수

    private final static int fullPlayTime = 30000; // 게임 플레이시간
    private static int playTimeInterval = 1000; // 플레이시간 인터벌
    private static final int numOfMoles = 9; // 나오는 두더지의 최대 개수
    private static int upperTimerLimit = 500;
    private static int lowerTimerLimit = 500;

    private ImageButton[] moleButtons = new ImageButton[numOfMoles];
    private ImageButton[] holeButtons = new ImageButton[numOfMoles];

    private final boolean[] buttonStatus = new boolean[numOfMoles];
    private CountDownTimer[] buttonTimers = new CountDownTimer[numOfMoles];

    private static final String LeftTimeString = "남은 시간 : ";
    private static final String scoreString = "점수 : ";
    private static final String scoreIntentData = "점수";
    private static final String highScoreIntentData = "최고 점수";

    // 각 새 스레드 시작과 게임의 초기 상태를 생성
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);

        // 플레이한 게임 중 얻은 최고 점수 intent data
        Intent passedIntent = getIntent();
        final int highScoreValue = passedIntent.getIntExtra(highScoreIntentData, 0);

        // 게임 타이머
        new CountDownTimer(fullPlayTime, playTimeInterval) {
            TextView timeText = (TextView) findViewById(R.id.countdownTimer);

            @Override
            public void onTick(long timeLeftMillis) {
                // 1초마다 증가하는 현재 시간 UI에 적용
                String timeTextString = LeftTimeString + TimeUnit.MILLISECONDS.toMinutes(timeLeftMillis) + ":" + TimeUnit.MILLISECONDS.toSeconds(timeLeftMillis);
                if (TimeUnit.MILLISECONDS.toSeconds(timeLeftMillis) <= 9){
                    timeTextString = LeftTimeString + TimeUnit.MILLISECONDS.toMinutes(timeLeftMillis) + ":0" + TimeUnit.MILLISECONDS.toSeconds(timeLeftMillis);
                }
                timeText.setText(timeTextString);
            }

            @Override
            public void onFinish() {
                // 타이머가 끝나면 최종점수를 putExtra하고 restartgame 액티비티로 이동
                Intent restartGameIntent = new Intent(PlayGameActivity.this, RestartGameActivity.class);
                restartGameIntent.putExtra(scoreIntentData, overallScore);

                // 최고 점수 계산
                if (highScoreValue > overallScore){
                    restartGameIntent.putExtra(highScoreIntentData, highScoreValue);
                } else {
                    restartGameIntent.putExtra(highScoreIntentData, overallScore);
                }
                startActivity(restartGameIntent);
            }
        }.start();

        updateScoreText();
        enableButtonsForGame();
        gameLogic();

    }

    // 구멍과 두더지 이미지 초기화 및 점수계산
    public void enableButtonsForGame(){
        //Populating each array of buttons for access elsewhere
        holeButtons[0] = (ImageButton) findViewById(R.id.hole1);
        holeButtons[1] = (ImageButton) findViewById(R.id.hole2);
        holeButtons[2] = (ImageButton) findViewById(R.id.hole3);
        holeButtons[3] = (ImageButton) findViewById(R.id.hole4);
        holeButtons[4] = (ImageButton) findViewById(R.id.hole5);
        holeButtons[5] = (ImageButton) findViewById(R.id.hole6);
        holeButtons[6] = (ImageButton) findViewById(R.id.hole7);
        holeButtons[7] = (ImageButton) findViewById(R.id.hole8);
        holeButtons[8] = (ImageButton) findViewById(R.id.hole9);

        moleButtons[0] = (ImageButton) findViewById(R.id.moleButton1);
        moleButtons[1] = (ImageButton) findViewById(R.id.moleButton2);
        moleButtons[2] = (ImageButton) findViewById(R.id.moleButton3);
        moleButtons[3] = (ImageButton) findViewById(R.id.moleButton4);
        moleButtons[4] = (ImageButton) findViewById(R.id.moleButton5);
        moleButtons[5] = (ImageButton) findViewById(R.id.moleButton6);
        moleButtons[6] = (ImageButton) findViewById(R.id.moleButton7);
        moleButtons[7] = (ImageButton) findViewById(R.id.moleButton8);
        moleButtons[8] = (ImageButton) findViewById(R.id.moleButton9);

        // 구멍 이미지 클릭할 때, 점수 계산(감소)
        for (int index = 0; index < numOfMoles; index++){
            onClickSubtractScore(holeButtons[index]);
        }

        // 두더지 이미지 클릭할 때, 점수 계산(증가)
        for (int index = 0; index < numOfMoles; index++){
            onClickAddScore(moleButtons[index], index);
        }
    }

    // 구멍 이미지 클릭할 때, 점수 계산(감소) 메서드
    public void onClickSubtractScore(ImageButton buttonToListen){
        buttonToListen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                if (overallScore > 0) {
                    overallScore--;
                } else {
                    overallScore = 0;
                }
                updateScoreText();
            }
        });
    }

    // 두더지 이미지 클릭할 때, 점수 계산(증가) 메서드
    public void onClickAddScore(ImageButton buttonToListen, int buttonNumber){
        final int currentButtonIndex = buttonNumber;

        buttonToListen.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                overallScore++;
                updateScoreText();

                // 두더지 이미지 클릭시, 두더지의 등장시간 타이머 취소 및 이미지 disable
                buttonTimers[currentButtonIndex].cancel();
                disableMoleButton(currentButtonIndex);

                // 랜덤으로 두더지 이미지 enable ( disable 된 이미지 중에서 )
                Random randomNumGenerator = new Random();
                int timerStartTime = randomNumGenerator.nextInt(upperTimerLimit) + lowerTimerLimit;
                new CountDownTimer(timerStartTime, playTimeInterval){

                    @Override
                    public void onTick(long l) { }

                    @Override
                    public void onFinish() {
                        restartButton(currentButtonIndex);
                    }
                }.start();
            }
        });
    }

    // 획득 점수가 바뀌면 UI update 하기 위한 메서드
    public void updateScoreText(){
        TextView scoreText = (TextView) findViewById(R.id.scoreCounter);
        scoreText.setText(scoreString + overallScore);
    }

    // true/false를 랜덤으로 얻기위한 메서드
    boolean getRandomTrueFalse(int upperBound){
        Random randNumGenerator = new Random();
        int randomStatus = randNumGenerator.nextInt(upperBound);
        return randomStatus == 1;
    }

    // 게임의 로직(9개의 두더지 이미지)을 세팅하기 위한 메서드
    public void gameLogic(){
        for (int index = 0; index < numOfMoles; index++) {
            restartButton(index);
        }
    }

    // 인자로 받은 index의 두더지 이미지를 등장시킬지 말지를 결정하는 메서드
    public void restartButton(int buttonNumber){
        Random getRandomNum = new Random();

        int chanceTrue = 2;
        buttonStatus[buttonNumber] = getRandomTrueFalse(chanceTrue);

        final int currentButtonNumber = buttonNumber;
        int timerStartTime = getRandomNum.nextInt(upperTimerLimit) + lowerTimerLimit;
        buttonTimers[buttonNumber] = new CountDownTimer(timerStartTime, playTimeInterval){

            @Override
            public void onTick(long l) { }

            @Override
            public void onFinish() {
                restartButton(currentButtonNumber);
            }
        }.start();

        // 설정한 버튼 상태에 따라서 두더지 이미지 enable/disable 함
        if (buttonStatus[buttonNumber]){
            enableMoleButton(buttonNumber);

        } else {
            disableMoleButton(buttonNumber);
        }
    }

    // 인자로 받은 숫자에 맞는 두더지 이미지 disable
    public void disableMoleButton(int buttonNumber){
        moleButtons[buttonNumber].setEnabled(false);
        moleButtons[buttonNumber].setAlpha(0f);
        holeButtons[buttonNumber].bringToFront();
    }

    // 인자로 받은 숫자에 맞는 두더지 이미지 enable
    public void enableMoleButton(int buttonNumber){
        moleButtons[buttonNumber].setEnabled(true);
        moleButtons[buttonNumber].setAlpha(1f);
        moleButtons[buttonNumber].bringToFront();
    }
}