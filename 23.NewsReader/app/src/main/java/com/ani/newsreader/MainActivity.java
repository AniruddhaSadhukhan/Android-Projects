package com.ani.newsreader;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    int numberOfItems = 20;
    ArrayList<String> titles = new ArrayList<>();
    ArrayList<String> links = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    SQLiteDatabase articlesDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        articlesDB = this.openOrCreateDatabase("Articles",MODE_PRIVATE,null);
        articlesDB.execSQL("CREATE TABLE IF NOT EXISTS articles (id INTEGER PRIMARY KEY,articleID INTEGER, title VARCHAR, link VARCHAR)");

        ListView listView = (ListView) findViewById(R.id.newsListView);

        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,titles);
        listView.setAdapter(arrayAdapter);
        updateListView();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(),ArticleActivity.class);
                intent.putExtra("link", links.get(position));
//                Toast.makeText(MainActivity.this, "Getting "+ links.get(position), Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });

        DownloadTask downloadTask = new DownloadTask();

        try {
            downloadTask.execute("https://hacker-news.firebaseio.com/v0/topstories.json");
        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    public void updateListView()
    {
        Cursor c = articlesDB.rawQuery("SELECT * FROM articles", null);

        int titleIndex = c.getColumnIndex("title");
        int linkIndex = c.getColumnIndex("link");

        if(c.moveToFirst()){
            titles.clear();
            links.clear();

            do {
                titles.add(c.getString(titleIndex));
                links.add(c.getString(linkIndex));
            }while (c.moveToNext());
        }

        arrayAdapter.notifyDataSetChanged();

    }

    public class DownloadTask extends AsyncTask<String,Void,String>
    {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            updateListView();
        }

        @Override
        protected String doInBackground(String... urls) {

            StringBuilder result = new StringBuilder();

            try
            {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data!=-1)
                {
                    result.append((char)data);
                    data = reader.read();
                }

                JSONArray jsonArray = new JSONArray(result.toString());

                if(jsonArray.length()<numberOfItems)
                {
                    numberOfItems = jsonArray.length();
                }

                articlesDB.execSQL("DELETE FROM articles");

                for(int i = 0;i<numberOfItems;i++)
                {
                    String articleID = jsonArray.getString(i);

                    url = new URL("https://hacker-news.firebaseio.com/v0/item/"+articleID+".json");

                    urlConnection = (HttpURLConnection) url.openConnection();

                    in = urlConnection.getInputStream();
                    reader = new InputStreamReader(in);

                    data = reader.read();

                    result.setLength(0);

                    while (data!=-1)
                    {
                        result.append((char)data);
                        data = reader.read();
                    }

                    JSONObject jsonObject = new JSONObject(result.toString());

                    if(!jsonObject.isNull("title") && !jsonObject.isNull("url"))
                    {

                        String sql = "INSERT INTO articles(articleID,title,link) VALUES(?,?,?)";
                        SQLiteStatement statement = articlesDB.compileStatement(sql);
                        statement.bindString(1,articleID);
                        statement.bindString(2,jsonObject.getString("title"));
                        statement.bindString(3,jsonObject.getString("url"));

                        statement.execute();

//                        Log.d("Title",jsonObject.getString("title") );
//                        Log.d("Link",jsonObject.getString("url") );
                    }

                }

                return "Done";

            }catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }

        }

    }
}
