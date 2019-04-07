package com.ani.tic_tac_toe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.support.v7.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    int turnCross = 1; //1: Cross, 0: Circle , 2: Empty
    int[] gameState = {2,2,2,2,2,2,2,2,2};
    int [][] winningPositions = {{0,1,2},{3,4,5},{6,7,8},
                                {0,3,6},{1,4,7},{2,5,8},
                                {0,4,8},{2,4,6}};
    boolean gameActive = true;
    int crossScore = 0, circleScore = 0;

    public void appear(View view){
        ImageView counter = (ImageView) view;

        ImageView turnImageView = (ImageView) findViewById(R.id.turnImageView);

        int taggedCounter = Integer.parseInt(counter.getTag().toString());

        if (gameState[taggedCounter]!=2 || !gameActive)
            return;

        gameState[taggedCounter] = turnCross;

        if(turnCross == 1)
        {
            counter.setImageResource(R.drawable.cross);
            turnImageView.setImageResource(R.drawable.circle);
            turnCross = 0;
        }
        else
        {
            counter.setImageResource(R.drawable.circle);
            turnImageView.setImageResource(R.drawable.cross);
            turnCross = 1;
        }
        counter.animate().alpha(1).setDuration(1000);



        for(int[] winningPosition : winningPositions)
            if(gameState[winningPosition[0]]!=2 &&
                    gameState[winningPosition[0]]==gameState[winningPosition[1]] &&
                    gameState[winningPosition[1]]==gameState[winningPosition[2]])
            {
                if(gameState[winningPosition[0]]==1)
                {
                    turnImageView.setImageResource(R.drawable.cross);
                    crossScore ++;
                    TextView crossScoreTextView = (TextView) findViewById(R.id.crossScoreTextView);
                    crossScoreTextView.setText(Integer.toString(crossScore));
                }
                else
                {
                    turnImageView.setImageResource(R.drawable.circle);
                    circleScore ++;
                    TextView circleScoreTextView = (TextView) findViewById(R.id.circleScoreTextView);
                    circleScoreTextView.setText(Integer.toString(circleScore));
                }
                gameActive = false;
                Button playAgainButton = (Button) findViewById(R.id.playAgainButton);
                playAgainButton.setVisibility(View.VISIBLE);
                TextView turnTextView = (TextView) findViewById(R.id.turnTextView);
                turnTextView.setText("Winner :");

                break;
            }
    }

    public void playAgain(View view)
    {
        Button playAgainButton = (Button) findViewById(R.id.playAgainButton);
        playAgainButton.setVisibility(View.INVISIBLE);

        GridLayout gridLayout = (GridLayout) findViewById(R.id.gridLayout);
        for(int i=0;i<gridLayout.getChildCount();i++)
        {
            ImageView counter = (ImageView) gridLayout.getChildAt(i);
            counter.setImageDrawable(null);
        }

        turnCross = 1; //1: Cross, 0: Circle , 2: Empty
        gameActive = true;
        for(int i=0;i<gameState.length;i++)
            gameState[i] = 2;

        TextView turnTextView = (TextView) findViewById(R.id.turnTextView);
        turnTextView.setText("Turn :");
        ImageView turnImageView = (ImageView) findViewById(R.id.turnImageView);
        turnImageView.setImageResource(R.drawable.cross);

    }

    public void reset(View view)
    {
        crossScore = 0;
        circleScore = 0;
        TextView crossScoreTextView = (TextView) findViewById(R.id.crossScoreTextView);
        crossScoreTextView.setText(Integer.toString(crossScore));
        TextView circleScoreTextView = (TextView) findViewById(R.id.circleScoreTextView);
        circleScoreTextView.setText(Integer.toString(circleScore));
        playAgain(view);
    }

    public void quit(View view)
    {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
