package com.ani.listviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.listView);

        final ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("Aniruddha");
        arrayList.add("Ani");
        arrayList.add("Paul");
        arrayList.add("Rob");
        arrayList.add("Anne");
        arrayList.add("Elizabeth");
        arrayList.add("Jack");
        arrayList.add("Jill");
        arrayList.add("Leo");
        arrayList.add("Sheldon");
        arrayList.add("Raj");
        arrayList.add("Peter");
        arrayList.add("Rob");
        arrayList.add("Anne");
        arrayList.add("Elizabeth");
        arrayList.add("Jack");
        arrayList.add("Jill");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, arrayList.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
