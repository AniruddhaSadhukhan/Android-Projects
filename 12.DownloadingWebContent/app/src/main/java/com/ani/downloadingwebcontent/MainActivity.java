  package com.ani.downloadingwebcontent;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

  public class MainActivity extends AppCompatActivity {


    TextView textView;

    public class DownloadWebContentTask extends AsyncTask<String,Void,String>
    {
        @Override
        protected String doInBackground(String... urls)
        {
            String websiteHTML = "";
            int x;
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                x = reader.read();
                while(x != -1){
                    websiteHTML += (char) x;
                    //Log.i("Getting", websiteHTML);
                    x = reader.read();
                }

                textView.setText(websiteHTML);
                return "Done";
            }catch (Exception e){
                return "Failed : "+e.toString();
            }
        }
    }

    public void downloadHTML(View view)
    {
        DownloadWebContentTask task = new DownloadWebContentTask();
        EditText urlEditText = (EditText) findViewById(R.id.websiteEditText);
        String url = urlEditText.getText().toString();
        try
        {
            String result = task.execute(url).get();
            Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();

        }catch (Exception e)
        {
            Toast.makeText(MainActivity.this, e.toString() , Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        textView.setMovementMethod(new ScrollingMovementMethod());
    }
}
