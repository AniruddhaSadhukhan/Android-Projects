package com.ani.parseusers;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.parse.LogInCallback;
import com.parse.LogOutCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //------------------------------------------------------------------------------------------
        /*
        //Sign Up User
        ParseUser user = new ParseUser();

        user.setUsername("ani");
        user.setPassword("myPass");

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null)
                    Log.i("SignUp Success","Successfully Signed Up !!!");
                else
                    Log.i("SignUp Failed",e.toString());
            }
        });

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        */
        //------------------------------------------------------------------------------------------
        /*
        //Sign In User
        ParseUser.logInInBackground("ani", "myPass", new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(user!=null)
                    Log.i("SignIn Success","Successfully Signed In !!!");
                else
                    Log.i("SignIn Failed",e.toString());
            }
        });
        */
        //------------------------------------------------------------------------------------------
        /*
        //Log Out User
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null)
                    Log.i("SignOut Success","Successfully Signed Out !!!");
                else
                    Log.i("SignOut Failed",e.toString());
            }
        });
        */
        //------------------------------------------------------------------------------------------

        //Check if Signed In
        if(ParseUser.getCurrentUser()!=null)
            Log.i("Signed In as",ParseUser.getCurrentUser().getUsername());
        else
            Log.i("Not Signed In","Please sign in");


        //------------------------------------------------------------------------------------------
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

    }
}
