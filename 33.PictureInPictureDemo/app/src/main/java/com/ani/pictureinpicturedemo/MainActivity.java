package com.ani.pictureinpicturedemo;

import android.content.res.Configuration;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public void goPiP(View view)
    {
        enterPictureInPictureMode();
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);

        TextView textView = findViewById(R.id.textView);
        Button button = findViewById(R.id.button);

        if(isInPictureInPictureMode)
        {
            //Going into PIP

            textView.setText("Hello Ani,\n You are into PiP mode");
            getSupportActionBar().hide();
            button.setVisibility(View.INVISIBLE);
        }
        else
        {
            //Going out of PIP

            textView.setText("Hello Ani");
            getSupportActionBar().show();
            button.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
