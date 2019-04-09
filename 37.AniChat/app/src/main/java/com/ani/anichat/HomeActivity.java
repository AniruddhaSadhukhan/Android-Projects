package com.ani.anichat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    ArrayList<String> contacts = new ArrayList<>();
    LinkedHashSet<String> chatContact = new LinkedHashSet<>();
    ArrayAdapter<String> arrayAdapter;
    ListView listView;

    Handler handler = new Handler();
    int delay = 60000;
    Runnable runnable;
    ParseQuery<ParseObject> query;

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    @Override
    protected void onResume() {

        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {

                chatContact.clear();
                contacts.clear();
                //Toast.makeText(HomeActivity.this, "Refreshing...", Toast.LENGTH_SHORT).show();
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {

                        if(e == null)
                        {
                            if(objects.size()>0)
                            {

                                for (ParseObject message: objects)
                                {
                                    if(message.getString("Sender").equals(ParseUser.getCurrentUser().getUsername()))
                                    {
                                        chatContact.add(message.getString("Receiver"));
                                        //contacts.add(message.getString("Receiver"));

                                    }else
                                    {
                                        chatContact.add(message.getString("Sender"));
                                        //contacts.add(message.getString("Sender"));
                                    }

                                }
                                contacts = new ArrayList<>(chatContact);
                                //Toast.makeText(HomeActivity.this, "Updating :"+contacts.toString(), Toast.LENGTH_SHORT).show();
                                arrayAdapter = new ArrayAdapter<String>(HomeActivity.this, android.R.layout.simple_list_item_1, contacts);
                                listView.setAdapter(arrayAdapter);

                            }


                        }else
                        {
                            Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });



                handler.postDelayed(runnable,delay);

            }
        }, 0);


        super.onResume();
    }


    public void logout(View view)
    {
        new AlertDialog.Builder(this)
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setTitle("Log Out?")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ParseUser.logOut();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void showUsers(View view)
    {
        Intent intent = new Intent( getApplicationContext(), UsersActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        contacts.add("Please wait...");

        listView = findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,contacts);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent( getApplicationContext(), ChatActivity.class);
                intent.putExtra("username", contacts.get(position));
                startActivity(intent);
            }
        });


        ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("Message");
        query1.whereEqualTo("Sender", ParseUser.getCurrentUser().getUsername());


        ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("Message");
        query2.whereEqualTo("Receiver", ParseUser.getCurrentUser().getUsername());


        List<ParseQuery<ParseObject>> queries = new ArrayList<>();
        queries.add(query1);
        queries.add(query2);

        query = ParseQuery.or(queries);

        query.orderByDescending("createdAt");





    }
}
