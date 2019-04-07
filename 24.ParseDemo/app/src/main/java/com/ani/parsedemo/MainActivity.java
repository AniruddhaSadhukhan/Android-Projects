package com.ani.parsedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //------------------------------------------------------------------------------------------
        /*
        // Save to Parse
        ParseObject scores = new ParseObject("Scores");
        scores.put("username", "ani");
        scores.put("score", 55);
        scores.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null)
                    Log.i("Success","We saved the score");
                else
                    Log.i("Failed",e.toString());
            }
        });

        scores = new ParseObject("Scores");
        scores.put("username","rick");
        scores.put("score", 30);
        scores.saveInBackground();

        scores = new ParseObject("Scores");
        scores.put("username","mary");
        scores.put("score", 45);
        scores.saveInBackground();

        scores = new ParseObject("Scores");
        scores.put("username","velma");
        scores.put("score", 62);
        scores.saveInBackground();
        */

        //------------------------------------------------------------------------------------------

        /*
        // Get info from Parse using objectID and update
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Scores");
        query.getInBackground("iCZo8aqNIW", new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e==null && object!=null){
                    object.put("score", 65);
                    object.saveInBackground();

                    Log.i("Username", object.getString("username"));
                    Log.i("Score", Integer.toString(object.getInt("score")));
                }
            }
        });
        */

        //------------------------------------------------------------------------------------------

        // Get all info from Parse without using objectID
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Scores");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null && objects.size()>0)
                {
                    for(ParseObject object: objects)
                    {
                        Log.i("Username", object.getString("username"));
                        Log.i("Score", Integer.toString(object.getInt("score")));
                    }
                }
            }
        });


        //------------------------------------------------------------------------------------------
        /*
        // Get all info from Parse having name ani and limit to only 2 object
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Scores");
        query.whereEqualTo("username","ani");
        query.setLimit(2);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null && objects.size()>0)
                {
                    for(ParseObject object: objects)
                    {
                        Log.i("Username", object.getString("username"));
                        Log.i("Score", Integer.toString(object.getInt("score")));
                    }
                }
            }
        });
        */

        //------------------------------------------------------------------------------------------
        /*
        // Add 20 to score of everyone having score greater than 50 and update
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Scores");
        query.whereGreaterThan("score",50);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null && objects.size()>0)
                {
                    for(ParseObject object: objects)
                    {
                        Log.i("Username", object.getString("username"));
                        Log.i("Old Score", Integer.toString(object.getInt("score")));
                        object.put("score", object.getInt("score")+20);
                        object.saveInBackground();
                        Log.i("New Score", Integer.toString(object.getInt("score")));


                    }
                }
            }
        });
        */
        //------------------------------------------------------------------------------------------
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

    }
}
