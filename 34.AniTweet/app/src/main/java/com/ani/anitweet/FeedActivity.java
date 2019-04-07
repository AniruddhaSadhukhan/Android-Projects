package com.ani.anitweet;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class FeedActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    public void logout(View view)
    {
        ParseUser.logOut();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void showTweet(View view)
    {
        Intent intent = new Intent( getApplicationContext(), TweetActivity.class);
        startActivity(intent);
        finish();
    }

    public void showFollow(View view)
    {
        Intent intent = new Intent( getApplicationContext(), FollowActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        final ListView listView = findViewById(R.id.listView);

        final List<Map<String,String>> tweetData = new ArrayList<>();


        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Tweet");
        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Tweet");

        query1.whereContainedIn("username", ParseUser.getCurrentUser().getList("isFollowing"));
        query2.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

        List<ParseQuery<ParseObject>> queryList = new ArrayList<>();
        queryList.add(query1);
        queryList.add(query2);

        ParseQuery<ParseObject> query = ParseQuery.or(queryList);
        query.orderByDescending("createdAt");
        query.setLimit(25);


        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null)
                {
                    if(objects.size() > 0) {
                        for (ParseObject tweet : objects) {
                            Map<String, String> tweetInfo = new HashMap<>();

                            tweetInfo.put("username", tweet.getString("username"));
                            tweetInfo.put("content", tweet.getString("tweet"));

                            tweetData.add(tweetInfo);
                        }
                    }
                    else
                    {
                        Map<String, String> tweetInfo = new HashMap<>();

                        tweetInfo.put("username", "<<Follow users to see their Tweets>>");
                        tweetInfo.put("content", "You can see all users and follow them from the right button in the bottom bar\n\nYou can tweet from the middle button in the bottom bar\n\nTo come back to your feed use the left button on bottom bar\n\nAlso you can logout using the top right corner button");

                        tweetData.add(tweetInfo);
                    }


                    SimpleAdapter simpleAdapter = new SimpleAdapter(FeedActivity.this,tweetData,android.R.layout.simple_list_item_2, new String[]{"username","content"}, new int[]{android.R.id.text1, android.R.id.text2}){
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View view = super.getView(position, convertView, parent);

                            TextView text1View = view.findViewById(android.R.id.text1);

                            text1View.setTextColor(R.color.darkBlue);
                            text1View.setTypeface(text1View.getTypeface(), Typeface.BOLD);

                            

                            return view;

                        }
                    };

                    listView.setAdapter(simpleAdapter);

                }
                else
                {
                    Toast.makeText(FeedActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });






    }
}
