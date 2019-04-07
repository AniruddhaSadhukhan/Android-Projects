 package com.ani.anisnapnchat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import io.github.inflationx.viewpump.ViewPumpContextWrapper

 class SenderActivity : AppCompatActivity() {

     var usersListView: ListView ?= null
     var emails: ArrayList<String> = ArrayList()
     var keys: ArrayList<String> = ArrayList()
     var destinationSelected = false

     override fun attachBaseContext(newBase: Context) {
         super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
     }

     override fun onDestroy() {
         if (!destinationSelected)
         {
             //Delete Image
             FirebaseStorage.getInstance().reference.child("Images").child(intent.getStringExtra("imageName")).delete()

         }
         super.onDestroy()
     }

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sender)

        usersListView = findViewById(R.id.usersListView)

        val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1, emails)

        usersListView?.adapter = adapter

        FirebaseDatabase.getInstance().reference.child("Users").addChildEventListener(object: ChildEventListener{

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                emails.add(p0.child("email").value as String)
                keys.add(p0.key!!)

                adapter.notifyDataSetChanged()


            }


            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot) {}
        })

        usersListView?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->

            val snapMap: Map<String, String> = mapOf("from" to FirebaseAuth.getInstance().currentUser!!.email!!,
                                                    "imageName" to intent.getStringExtra("imageName"),
                                                    "imageURL" to intent.getStringExtra("imageURL"),
                                                    "message" to intent.getStringExtra("message"))

            FirebaseDatabase.getInstance().reference.child("Users").child(keys.get(position)).child("Snaps").push().setValue(snapMap)

            destinationSelected = true

            val intent = Intent(this,SnapsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)

        }
    }
}
