package com.ani.anisnapnchat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import io.github.inflationx.viewpump.ViewPumpContextWrapper


class MainActivity : AppCompatActivity(), View.OnClickListener, View.OnKeyListener {

    var backgroundLayout: ConstraintLayout? = null
    var logoImageView: ImageView? = null

    var emailEditText: EditText?= null
    var passwordEditText: EditText?= null

    private lateinit var auth: FirebaseAuth

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    fun signupLogin(view: View)
    {
        val email = emailEditText!!.text.toString().trim()
        val password = passwordEditText!!.text.toString().trim()

        if (email.matches("^$".toRegex()) || password.matches("^$".toRegex()))
            Toast.makeText(this@MainActivity, "Email or Password can't be blank",Toast.LENGTH_SHORT).show()

        //else if (!email.matches("^.*@.*.\\..+$".toRegex())) //OR
        else if (!email.matches(android.util.Patterns.EMAIL_ADDRESS.toRegex()))
            Toast.makeText(this@MainActivity, "Invalid Email",Toast.LENGTH_SHORT).show()

        else {

            //Try to sign in
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success
                        logIn()
                    } else {

                        // Sign in fails, ask for signup

                        AlertDialog.Builder(this@MainActivity)
                            .setIcon(android.R.drawable.ic_menu_add)
                            .setTitle("Sign Up?")
                            .setMessage("Username or Password didn't match with existing username,\nWant to create a new user?")
                            .setPositiveButton("Create new user") { dialog, which ->

                                auth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(this) { task ->
                                        if (task.isSuccessful) {
                                            // Sign up success
                                            Toast.makeText(this@MainActivity, "Signup successful", Toast.LENGTH_SHORT).show()

                                            //Add user to Database
                                            FirebaseDatabase.getInstance().getReference().child("Users").child(task.result!!.user.uid).child("email").setValue(email)

                                            logIn()
                                        } else {
                                            // Sign Up fails
                                            Toast.makeText(baseContext, task.exception!!.message, Toast.LENGTH_SHORT).show()
                                        }

                                        // ...
                                    }

                            }
                            .setNegativeButton("Nope", null)
                            .show()

                    }

                }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

        backgroundLayout = findViewById(R.id.backgroundLayout);
        backgroundLayout!!.setOnClickListener(this);
        logoImageView = findViewById(R.id.logoImageView);
        logoImageView!!.setOnClickListener(this);
        passwordEditText!!.setOnKeyListener(this);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        if(auth.currentUser != null)
        {
            logIn()
        }
    }

    fun logIn()
    {
        Toast.makeText(this@MainActivity,"Signed in as "+auth.currentUser!!.email, Toast.LENGTH_SHORT).show()

        //Move to next activity
        val intent = Intent(this, SnapsActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onClick(view: View) {

        if (view.id == R.id.logoImageView || view.id == R.id.backgroundLayout) {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }

    }

    override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {

        if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN && v.id == R.id.passwordEditText) {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            signupLogin(v)
            return true
        }
        else
            return false
    }

}
