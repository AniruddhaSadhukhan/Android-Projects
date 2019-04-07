package com.ani.anisnapnchat

import android.content.Context
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import java.net.HttpURLConnection
import java.net.URL








class ViewSnapActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    var messageTextView : TextView?= null
    var senderTextView : TextView?= null
    var snapImageView : ImageView?= null


    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    inner class DownloadImageTask : AsyncTask<String, Void, Void>() {
        override fun doInBackground(vararg urls: String): Void?{

            try {
                val url = URL(urls[0])
                val connection = url.openConnection() as HttpURLConnection

                val `in` = connection.inputStream
                val bitmap = BitmapFactory.decodeStream(`in`)

                runOnUiThread {
                    snapImageView?.setImageBitmap(bitmap)

                }

            } catch (e: Exception) {
                runOnUiThread{
                    Toast.makeText(this@ViewSnapActivity,"Failed : "+e.message, Toast.LENGTH_LONG).show()
                }
            }

            return null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(R.layout.activity_view_snap)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        messageTextView = findViewById(R.id.messageTextView)
        senderTextView = findViewById(R.id.senderTextView)
        snapImageView = findViewById(R.id.snapImageView)


        val sender = "Snap from\n"+ intent.getStringExtra("from")

        senderTextView?.text = sender
        messageTextView?.text = intent.getStringExtra("message")
        messageTextView?.movementMethod = ScrollingMovementMethod()

        val task = DownloadImageTask()
        val url = intent.getStringExtra("imageURL")
        try {
            task.execute(url)

        } catch (e: Exception) {
            Toast.makeText(this, e.message , Toast.LENGTH_SHORT).show()
        }


    }

    override fun onDestroy() {
        super.onDestroy()

        FirebaseStorage.getInstance().reference.child("Images").child(intent.getStringExtra("imageName")).delete()

        FirebaseDatabase.getInstance().reference.child("Users").child(auth.currentUser!!.uid).child("Snaps").child(intent.getStringExtra("snapKey")).removeValue()
    }
}
