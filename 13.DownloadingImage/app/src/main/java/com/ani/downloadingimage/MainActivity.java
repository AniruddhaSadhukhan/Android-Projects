  package com.ani.downloadingimage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

  public class MainActivity extends AppCompatActivity {


    ImageView imageView;

    public class DownloadImageTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... urls)
        {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                InputStream in = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(in);

                imageView.setImageBitmap(bitmap);

                return "Done";
            }catch (Exception e){
                return "Failed : "+e.toString();
            }
        }
    }

    public void downloadImage(View view)
    {
        DownloadImageTask task = new DownloadImageTask();
        EditText urlEditText = (EditText) findViewById(R.id.imageUrlEditText);
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

        imageView = (ImageView) findViewById(R.id.imageView);

    }
}
