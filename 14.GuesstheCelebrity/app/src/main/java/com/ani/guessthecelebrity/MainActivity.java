package com.ani.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    ArrayList<String> names,pics,allNames;
    Random rand;
    ImageView img;
    Button b1,b2,b3,b4;
    String correctAnswer;

    public class DownloadImageTask extends AsyncTask<String,Void,Boolean>
    {
        @Override
        protected Boolean doInBackground(String... urls)
        {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                InputStream in = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(in);

                img.setImageBitmap(bitmap);

                return true;
            }catch (Exception e){
                return false;
            }
        }
    }

    public class DownloadWebPageTask extends AsyncTask<String,Void,String>
    {
        @Override
        protected String doInBackground(String... urls)
        {
            StringBuilder websiteHTML = new StringBuilder();
            String regex = "<img alt=\"(.*?)\"\n" +
                    "height=\"209\"\n" +
                    "src=\"(.*?)\"\n" +
                    "width=\"140\" />";
            Pattern pattern = Pattern.compile(regex);
            names = new ArrayList<String>();
            pics = new ArrayList<String>();


            int x;
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                x = reader.read();
                while(x != -1){
                    websiteHTML.append ((char) x);
                    x = reader.read();
                }


                Matcher matcher = pattern.matcher(websiteHTML);
                while(matcher.find())
                {
                    names. add(matcher.group(1));
                    pics. add(matcher.group(2));
                }
                allNames = (ArrayList) names.clone();

                return "Done";
            }catch (Exception e){
                return "Failed : "+e.toString();
            }
        }
    }

    public void start()
    {
        DownloadWebPageTask task = new DownloadWebPageTask();
        String url = "https://www.imdb.com/list/ls052283250/";
        try
        {
            String result = task.execute(url).get();
            Toast.makeText(MainActivity.this,"Start", Toast.LENGTH_LONG).show();


        }catch (Exception e)
        {
            Toast.makeText(MainActivity.this, e.toString() , Toast.LENGTH_SHORT).show();
        }
    }

    public void createGame()
    {
        int n = names.size();
        int i = rand.nextInt(n);
        String imgURL = pics.get(i);
        DownloadImageTask task = new DownloadImageTask();
        try{
            if(task.execute(imgURL).get()){
                correctAnswer = names.get(i);
                ArrayList<String> options = new ArrayList<String>();
                int x,c=0;
                while(true)
                {
                    x = rand.nextInt(allNames.size());
                    if(!(allNames.get(x).equals(correctAnswer)))
                    {
                        options.add(allNames.get(x));
                        c++;
                    }
                    if (c==3) break;
                }
                options.add(correctAnswer);

                Collections.shuffle(options);

                b1.setText(options.get(0));
                b2.setText(options.get(1));
                b3.setText(options.get(2));
                b4.setText(options.get(3));

                names.remove(i);
                pics.remove(i);

            }
        }catch (Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void checkAnswer(View view)
    {
        Button button = (Button) view;
        if(button.getText().equals(correctAnswer)) {
            Toast.makeText(this, "Correct !!!", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Wrong ! It's " + correctAnswer, Toast.LENGTH_SHORT).show();
        }

        if (names.size()==0)
            start();

        createGame();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start();
        rand = new Random();
        img = (ImageView) findViewById(R.id.imageView);
        b1 = (Button) findViewById(R.id.button1);
        b2 = (Button) findViewById(R.id.button2);
        b3 = (Button) findViewById(R.id.button3);
        b4 = (Button) findViewById(R.id.button4);

        createGame();
    }
}
