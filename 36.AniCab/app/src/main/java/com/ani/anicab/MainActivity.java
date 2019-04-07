package com.ani.anicab;

import android.content.Intent;
import android.content.ServiceConnection;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public void redirectActivity()
    {
        if(ParseUser.getCurrentUser().get("RiderOrDriver").equals("Rider"))
        {
            Intent intent = new Intent(getApplicationContext(), RiderActivity.class);
            startActivity(intent);
            finish();
        }else
        {
            Intent intent = new Intent(getApplicationContext(), ViewRequestActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void getStarted(View view)
    {
        Switch userTypeSwitch = findViewById(R.id.userTypeSwitch);

        String userType = "Rider";

        if(userTypeSwitch.isChecked())
            userType = "Driver";

        if(ParseUser.getCurrentUser() != null)
        {
            ParseUser.getCurrentUser().put("RiderOrDriver", userType);
            final String finalUserType = userType;
            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e==null)
                    {
                        Toast.makeText(MainActivity.this, "Redirecting as "+ finalUserType, Toast.LENGTH_SHORT).show();
                        redirectActivity();
                    }
                    else
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else
        {
            Toast.makeText(this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
        }



    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ParseUser.getCurrentUser() == null)
        {
            ParseAnonymousUtils.logIn(new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if(e!=null)
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else if(ParseUser.getCurrentUser().get("RiderOrDriver") != null && ParseUser.getCurrentUser().get("RiderOrDriver") != "")
        {
            Toast.makeText(this, "Redirecting as "+ParseUser.getCurrentUser().get("RiderOrDriver"), Toast.LENGTH_SHORT).show();
            redirectActivity();
        }

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

    }
}
