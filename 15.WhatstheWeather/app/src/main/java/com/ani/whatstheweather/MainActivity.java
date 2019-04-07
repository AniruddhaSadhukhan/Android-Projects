package com.ani.whatstheweather;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    public class DownloadWeatherInfoTask extends AsyncTask<String,Void,String>
    {

        StringBuilder result;
        @Override
        protected String doInBackground(String... urls) {
            result = new StringBuilder();
            try {

                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int x = reader.read();
                while (x != -1) {
                    result.append((char) x);
                    x = reader.read();
                }
                connection.disconnect();
                return result.toString();
            }catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try
            {
                JSONObject jsonObject = new JSONObject(s);

                JSONArray weatherInfos = new JSONArray(jsonObject.getString("weather"));
                JSONObject mainInfo = new JSONObject(jsonObject.getString("main"));
                StringBuilder weather = new StringBuilder();
                weather.append(jsonObject.getString("name")+", ");
                weather.append((new JSONObject(jsonObject.getString("sys"))).getString("country")+"\n");



                for(int i=0;i<weatherInfos.length();i++)
                {
                    JSONObject weatherInfo = weatherInfos.getJSONObject(i);
                    weather.append(weatherInfo.getString("main")+" : ");
                    weather.append(weatherInfo.getString("description")+"\n");
                }
                weather.append(mainInfo.getString("temp")+"\u00B0C\nHumidity: ");
                weather.append(mainInfo.getString("humidity")+"%\n");
                textView.setText(weather);


            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void getWeather(View view)
    {
        EditText cityEditText = (EditText) findViewById(R.id.editText);
        textView.setText("");

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityEditText.getWindowToken(),0);

        try{
            String city = cityEditText.getText().toString();
            if(city!=null)
            {
                DownloadWeatherInfoTask task = new DownloadWeatherInfoTask();
                String res = task.execute("http://api.openweathermap.org/data/2.5/weather?q="+city+"&units=metric&APPID=801e2ac5db7b07d453200a037be4e506").get();
                if (res == null)
                    Toast.makeText(this, "Invalid City or Network Problem", Toast.LENGTH_SHORT).show();
            }else
            {
                Toast.makeText(this, "Enter a city", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
    }
}
