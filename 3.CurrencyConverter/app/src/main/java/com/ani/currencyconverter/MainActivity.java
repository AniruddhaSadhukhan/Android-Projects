package com.ani.currencyconverter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public void convertCurrency(View view){
        EditText rupeesEditText = (EditText) findViewById(R.id.rupeesEditText);

        try{
            double rupees = Double.parseDouble(rupeesEditText.getText().toString()),
               dollar;

            dollar = Math.round((0.014*rupees)*1000.0)/1000.0;

            Toast.makeText(this, "Rs. "+rupees+" = "+dollar+" $", Toast.LENGTH_LONG).show();
        }
        catch(Exception e)
        {
            Toast.makeText(this, "Enter amount above", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
