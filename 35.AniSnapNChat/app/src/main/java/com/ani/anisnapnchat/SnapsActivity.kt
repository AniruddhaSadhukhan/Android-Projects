package com.ani.anisnapnchat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import io.github.inflationx.viewpump.ViewPumpContextWrapper



class SnapsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var snapsListView: ListView ?= null
    var emails: ArrayList<String> = ArrayList()
    var snaps: ArrayList<DataSnapshot> = ArrayList()


    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    fun logOut(view: View)
    {
        AlertDialog.Builder(this)
            .setIconAttribute(android.R.attr.alertDialogIcon)
            .setTitle("Log Out?")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Log Out") { dialog, which ->

                auth.signOut()
                //Move to signin activity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()

            }
            .setNegativeButton("Cancel", null)
            .show()


    }

    fun newSnap(view: View)
    {
        val intent = Intent(this, CreateSnapActivity::class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snaps)

        snapsListView = findViewById(R.id.listView)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, emails)
        snapsListView?.adapter = adapter

        FirebaseDatabase.getInstance().reference.child("Users").child(auth.currentUser!!.uid).child("Snaps").addChildEventListener(object : ChildEventListener{

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                emails.add(p0.child("from").value as String)
                snaps.add(p0)
                adapter.notifyDataSetChanged()


            }


            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot) {

                var index = 0
                for (snap: DataSnapshot in snaps)
                {
                    if(snap.key == p0.key)
                    {
                        snaps.removeAt(index)
                        emails.removeAt(index)
                        adapter.notifyDataSetChanged()
                        break
                    }
                    index++
                }

            }


        })

        snapsListView?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->

            val snapshot = snaps[position]

            val intent = Intent(this,ViewSnapActivity::class.java)

            intent.putExtra("imageName", snapshot.child("imageName").value as String)
            intent.putExtra("imageURL", snapshot.child("imageURL").value as String)
            intent.putExtra("message", snapshot.child("message").value as String)
            intent.putExtra("from", snapshot.child("from").value as String)
            intent.putExtra("snapKey", snapshot.key)

            startActivity(intent)

        }


    }


}
