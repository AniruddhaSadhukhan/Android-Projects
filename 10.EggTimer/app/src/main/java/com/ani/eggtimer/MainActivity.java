package com.ani.eggtimer;

import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    SeekBar seekBar;
    CountDownTimer timer;
    Button goButton;
    boolean buttonIsEnabled;

    public void go(View view)
    {
        if(!buttonIsEnabled)
        {
            buttonIsEnabled = true;
            goButton.setText("Stop");

            int time = seekBar.getProgress() * 1000 + 100;

            seekBar.setEnabled(false);

            timer = new CountDownTimer(time, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                    int progress = (int) millisUntilFinished / 1000;
                    seekBar.setProgress(progress);
                    textView.setText(String.valueOf(progress / 60) + ":" + String.format("%02d", progress - 60 * (progress / 60)));
                }

                @Override
                public void onFinish() {
                    Toast.makeText(MainActivity.this, "Timer Finished", Toast.LENGTH_SHORT).show();
                    seekBar.setProgress(30);
                    textView.setText("0:30");
                    seekBar.setEnabled(true);
                    MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.alarm);
                    mediaPlayer.start();
                    buttonIsEnabled = false;
                    goButton.setText("Go !");

                }
            }.start();
        }
        else
        {
            buttonIsEnabled = false;
            goButton.setText("Go !");

            seekBar.setEnabled(true);

            timer.cancel();

        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        goButton = (Button) findViewById(R.id.goButton);
        buttonIsEnabled = false;

        textView = (TextView) findViewById(R.id.textView);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(900);
        seekBar.setProgress(30);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                {
                    textView.setText(String.valueOf(progress/60) + ":" + String.format("%02d",progress-60*(progress/60)));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



    }
}
