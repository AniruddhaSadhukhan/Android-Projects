package com.ani.notes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

public class EditActivity extends AppCompatActivity {

    int n;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        editText = (EditText) findViewById(R.id.editText);
        n = getIntent().getIntExtra("position",-1);

        if(n>-1)
        {


            editText.setText(MainActivity.notes.get(n));
        }
    }


    @Override
    protected void onDestroy() {
        if (n==-1)
        {
            String newNote = editText.getText().toString();
            MainActivity.notes.add(0,newNote);
        }
        else if(n>-1)
        {
            String editedNote = editText.getText().toString();
            MainActivity.notes.set(n,editedNote);
        }
        MainActivity.arrayAdapter.notifyDataSetChanged();

        try {
            MainActivity.sharedPreferences.edit().putString("savedNotes", ObjectSerializer.serialize(MainActivity.notes)).apply();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error Saving Notes", Toast.LENGTH_SHORT).show();
        }


        super.onDestroy();
    }

}
