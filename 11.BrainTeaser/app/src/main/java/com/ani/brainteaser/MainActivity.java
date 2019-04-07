package com.ani.brainteaser;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    int a,b,nRight,nWrong;
    Random rand;
    GridLayout gridLayout;
    TextView questionTextView;
    TextView statusTextView;
    TextView scoreTextView;
    TextView timeTextView;
    Button goButton;
    Button playAgainButton;



    public void createGameState()
    {
        int nChoice = gridLayout.getChildCount(),c;

        a = 1 + rand.nextInt(49);
        b = 1 + rand.nextInt(49);

        ArrayList<Integer> choices = new ArrayList<Integer>();
        choices.add(a+b);
        for (int i=1;i<nChoice; i++)
        {
            do {
                c = 2 + rand.nextInt(98);
            }while (c == a+b);
            choices.add(c);
        }


        Collections.shuffle(choices);

        scoreTextView.setText(Integer.toString(nRight) + "/" + Integer.toString(nRight+nWrong));
        questionTextView.setText(Integer.toString(a) + "+" + Integer.toString(b));

        for(int i=0;i<nChoice;i++)
        {
            Button b = (Button) gridLayout.getChildAt(i);
            b.setText(Integer.toString(choices.get(i)));
        }
    }

    public void checkAnswer(View view)
    {
        Button clickedButton = (Button) view;

        int answer = Integer.parseInt(clickedButton.getText().toString());

        if (answer == (a+b))
        {
            statusTextView.setText("Correct :-)");
            nRight++;
        }
        else
        {
            statusTextView.setText("Wrong :-(");
            nWrong++;
        }

        statusTextView.setVisibility(View.VISIBLE);

        createGameState();
    }

    public void startPlaying(View view)
    {
        nRight=0;
        nWrong=0;
        playAgainButton.setVisibility(View.INVISIBLE);
        goButton.setVisibility(View.INVISIBLE);
        gridLayout.setVisibility(View.VISIBLE);
        timeTextView.setVisibility(View.VISIBLE);
        questionTextView.setVisibility(View.VISIBLE);
        scoreTextView.setVisibility(View.VISIBLE);
        statusTextView.setVisibility(View.VISIBLE);
        questionTextView.setEnabled(true);
        for(int i=0; i<gridLayout.getChildCount(); i++)
            ((Button) gridLayout.getChildAt(i)).setEnabled(true);


        createGameState();
        new CountDownTimer(30100, 1000)
        {
            @Override
            public void onTick(long millisUntilFinished) {
                timeTextView.setText(millisUntilFinished/1000 + "s");
            }

            @Override
            public void onFinish() {
                questionTextView.setEnabled(false);
                for(View v : gridLayout.getTouchables())
                    ((Button) v).setEnabled(false);
                statusTextView.setText("");
                timeTextView.setText("0s");
                statusTextView.setVisibility(View.INVISIBLE);
                playAgainButton.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        gridLayout = (GridLayout) findViewById(R.id.gridLayout);
        questionTextView = (TextView) findViewById(R.id.questionTextView);
        scoreTextView = (TextView) findViewById(R.id.scoreTextView);
        timeTextView = (TextView) findViewById(R.id.timeTextView);
        statusTextView = (TextView) findViewById(R.id.statusTextView);
        goButton = (Button) findViewById(R.id.goButton);
        playAgainButton = (Button) findViewById(R.id.playAgainButton);
        rand = new Random();

    }
}
