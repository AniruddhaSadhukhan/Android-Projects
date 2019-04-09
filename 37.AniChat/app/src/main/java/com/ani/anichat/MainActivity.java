package com.ani.anichat;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;



public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    Boolean signUpModeActive = true;
    TextView loginTextView;
    EditText usernameEditText;
    EditText passwordEditText;
    ImageView logoImageView;
    ConstraintLayout backgroundLayout;


    public void showHome()
    {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }

    public void signUpClicked(View view)
    {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if(username.matches("") || password.matches(""))
        {
            Toast.makeText(this, "Username and Password are required", Toast.LENGTH_SHORT).show();
        }
        else if(signUpModeActive)
        {
            //Sign Up User
            Toast.makeText(this, "Please wait...Signing up", Toast.LENGTH_SHORT).show();
            ParseUser user = new ParseUser();

            user.setUsername(username);
            user.setPassword(password);

            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if(e==null) {
                        Toast.makeText(MainActivity.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                        showHome();
                    }
                    else
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            //Sign In User
            Toast.makeText(this, "Please wait...Signing in", Toast.LENGTH_SHORT).show();
            ParseUser.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if(user!=null) {
                        Toast.makeText(MainActivity.this, "Signed in", Toast.LENGTH_SHORT).show();
                        showHome();
                    }
                    else
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginTextView = findViewById(R.id.loginTextView);
        loginTextView.setOnClickListener(this);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        passwordEditText.setOnKeyListener(this);

        logoImageView = findViewById(R.id.logoImageView);
        backgroundLayout = findViewById(R.id.backgroundLayout);

        logoImageView.setOnClickListener(this);
        backgroundLayout.setOnClickListener(this);

        //Check if Signed In
        if(ParseUser.getCurrentUser()!=null)
        {
            Toast.makeText(this, "Signed in as "+ParseUser.getCurrentUser().getUsername(), Toast.LENGTH_SHORT).show();
            showHome();
        }
        else
            Toast.makeText(this, "Please sign in", Toast.LENGTH_SHORT).show();


        //------------------------------------------------------------------------------------------
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.loginTextView)
        {
            Button signUpButton = findViewById(R.id.signUpButton);

            if(signUpModeActive)
            {
                signUpModeActive = false;
                signUpButton.setText("Login");
                loginTextView.setText("or, Signup");
            }
            else
            {
                signUpModeActive = true;
                signUpButton.setText("Signup");
                loginTextView.setText("or, Login");
            }
        }
        else if(view.getId() == R.id.logoImageView || view.getId() == R.id.backgroundLayout)
        {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN && v.getId() == R.id.passwordEditText)
        {
            signUpClicked(v);
        }
        return false;
    }
}