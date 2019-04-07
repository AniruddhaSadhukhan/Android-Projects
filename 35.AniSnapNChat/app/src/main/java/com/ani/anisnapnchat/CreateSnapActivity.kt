package com.ani.anisnapnchat

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import java.io.ByteArrayOutputStream
import java.util.*


class CreateSnapActivity : AppCompatActivity() {

    var snapImageView: ImageView?= null
    var messageEditText: EditText?= null

    val imageName = UUID.randomUUID().toString() + ".jpg"

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_snap)

        snapImageView = findViewById(R.id.snapImageView)
        messageEditText = findViewById(R.id.messageEditText)


    }

    fun getPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    fun chooseImage(view: View)
    {
        if(Build.VERSION.SDK_INT >=23 && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
        else
            getPhoto()


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            try {

                val selectedImage = data.data
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                snapImageView!!.setImageBitmap(bitmap)

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1 && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getPhoto()
        }
    }

    fun nextClicked(view: View)
    {
        //Disable Button while uploading
        Toast.makeText(this, "Please wait...",Toast.LENGTH_SHORT).show()
        view.isEnabled = false


        // Get the data from an ImageView as bytes
        snapImageView?.isDrawingCacheEnabled = true
        snapImageView?.buildDrawingCache()
        val bitmap = (snapImageView?.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 5, baos)
        val data = baos.toByteArray()


        var ref = FirebaseStorage.getInstance().getReference().child("Images").child(imageName)
        var uploadTask = ref.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            Toast.makeText(this, it.message , Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {

            Toast.makeText(this, "Send to...", Toast.LENGTH_SHORT).show()

            ref.downloadUrl.addOnSuccessListener {
                val intent = Intent(this, SenderActivity::class.java)
                intent.putExtra("imageURL", it.toString())
                intent.putExtra("imageName", imageName )
                intent.putExtra("message", messageEditText?.text.toString().trim() )

                startActivity(intent)
            }

        }.addOnCompleteListener{

            view.isEnabled = true
        }

    }
}
