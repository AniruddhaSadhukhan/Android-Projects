package com.ani.exampleapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public void clickFunction(View view){
        EditText nameEditText = (EditText)findViewById(R.id.nameEditText);
        EditText passwordEditText = (EditText)findViewById(R.id.passwordEditText);


        Log.i("Values","Username: "+nameEditText.getText().toString()+" , Password : "+passwordEditText.getText().toString());

        Toast.makeText(this,"Hi "+nameEditText.getText().toString()+" !!!", Toast.LENGTH_SHORT).show();

        ImageView img = (ImageView)findViewById(R.id.imageView);

        img.setImageResource(R.drawable.dog);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
