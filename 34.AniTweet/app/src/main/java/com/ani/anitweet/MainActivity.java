package com.ani.anitweet;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.Arrays;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class MainActivity extends AppCompatActivity implements View.OnKeyListener, View.OnClickListener {

    ConstraintLayout backgroundLayout;
    ImageView logoImageView;
    EditText usernameEditText;
    EditText passwordEditText;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    public void signupLogin(View view)
    {

        final String username = usernameEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        if(username.matches("") || password.matches(""))
        {
            Toast.makeText(this, "Username and Password are required", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Please wait...Trying to sign in", Toast.LENGTH_SHORT).show();
            ParseUser.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (user!= null)
                    {
                        Toast.makeText(MainActivity.this, "Signed in as "+user.getUsername(), Toast.LENGTH_SHORT).show();
                        showFeed();
                    }
                    else
                    {
                        new AlertDialog.Builder(MainActivity.this)
                                .setIcon(android.R.drawable.ic_menu_add)
                                .setTitle("Sign Up?")
                                .setMessage("Username or Password didn't match with existing username,\nWant to create a new user?")
                                .setPositiveButton("Create new user", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        ParseUser user = new ParseUser();

                                        user.setUsername(username);
                                        user.setPassword(password);
                                        user.put("isFollowing", Arrays.asList());

                                        user.signUpInBackground(new SignUpCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if(e==null)
                                                {
                                                    Toast.makeText(MainActivity.this, "Signup successful", Toast.LENGTH_SHORT).show();
                                                    showFeed();
                                                }
                                                else
                                                {
                                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    }
                                })
                                .setNegativeButton("Nope",null)
                                .show();
                    }
                }
            });
        }




    }

    public void showFeed()
    {
        try{
            Intent intent = new Intent( getApplicationContext(), FeedActivity.class);
            startActivity(intent);
            finish();
        }catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        backgroundLayout = findViewById(R.id.backgroundLayout);
        backgroundLayout.setOnClickListener(this);
        logoImageView = findViewById(R.id.logoImageView);
        logoImageView.setOnClickListener(this);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordEditText.setOnKeyListener(this);

        //Check if Signed In
        if(ParseUser.getCurrentUser()!=null)
        {
            Toast.makeText(this, "Signed in as "+ParseUser.getCurrentUser().getUsername(), Toast.LENGTH_SHORT).show();
            showFeed();
        }
        else
            Toast.makeText(this, "Please sign in", Toast.LENGTH_SHORT).show();


        ParseAnalytics.trackAppOpenedInBackground(getIntent());

    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.logoImageView || view.getId() == R.id.backgroundLayout)
        {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN && v.getId() == R.id.passwordEditText)
        {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            signupLogin(v);
        }
        return false;
    }
}
