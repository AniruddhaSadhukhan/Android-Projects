package com.ani.higherorlower;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Random rand = new Random();
    private int value = rand.nextInt(48) + 2;

    public void guessFunction(View view)
    {
        EditText numberEditText = (EditText) findViewById(R.id.numberEditText);

        int guess = Integer.parseInt(numberEditText.getText().toString());

        //Log.i("Info","Number : "+value);
        
        if(guess<value)
            Toast.makeText(this, "Guess Higher", Toast.LENGTH_SHORT).show();
        else if (guess>value)
            Toast.makeText(this, "Guess Lower", Toast.LENGTH_SHORT).show();
        else
        {
            Toast.makeText(this, "Correct !!! Guess a new number", Toast.LENGTH_SHORT).show();
            value = rand.nextInt(50) + 1;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
