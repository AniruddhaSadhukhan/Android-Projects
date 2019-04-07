package com.ani.anitweet;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class TweetActivity extends AppCompatActivity {

    MultiAutoCompleteTextView multiAutoCompleteTextView;

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

    public void showFeed(View view)
    {
        Intent intent = new Intent( getApplicationContext(), FeedActivity.class);
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
        setContentView(R.layout.activity_tweet);

        List<String> following = ParseUser.getCurrentUser().getList("isFollowing");

        multiAutoCompleteTextView = findViewById(R.id.multiAutoCompleteTextView);

        TextInputLayout textInputLayout = findViewById(R.id.textInputLayout);
        textInputLayout.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/myfont.ttf"));

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, following);

        multiAutoCompleteTextView.setTokenizer(new SpaceTokenizer());
        multiAutoCompleteTextView.setAdapter(arrayAdapter);
        multiAutoCompleteTextView.setThreshold(1);


    }

    public void tweet(View view)
    {
        String post = multiAutoCompleteTextView.getText().toString().trim();

        if(post.matches(""))
            Toast.makeText(this, "Tweet can't be empty", Toast.LENGTH_SHORT).show();
        else
        {
            ParseObject tweet = new ParseObject("Tweet");
            tweet.put("tweet", post);
            tweet.put("username", ParseUser.getCurrentUser().getUsername());

            tweet.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e==null)
                    {
                        Toast.makeText(TweetActivity.this, "Tweet sent!", Toast.LENGTH_SHORT).show();
                        multiAutoCompleteTextView.setText("");
                    }else
                    {
                        Toast.makeText(TweetActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }
}

class SpaceTokenizer implements MultiAutoCompleteTextView.Tokenizer {

    public int findTokenStart(CharSequence text, int cursor) {
        int i = cursor;

        while (i > 0 && text.charAt(i - 1) != ' ') {
            i--;
        }
        while (i < cursor && text.charAt(i) == ' ') {
            i++;
        }

        return i;
    }

    public int findTokenEnd(CharSequence text, int cursor) {
        int i = cursor;
        int len = text.length();

        while (i < len) {
            if (text.charAt(i) == ' ') {
                return i;
            } else {
                i++;
            }
        }

        return len;
    }

    public CharSequence terminateToken(CharSequence text) {
        int i = text.length();

        while (i > 0 && text.charAt(i - 1) == ' ') {
            i--;
        }

        if (i > 0 && text.charAt(i - 1) == ' ') {
            return text;
        } else {
            if (text instanceof Spanned) {
                SpannableString sp = new SpannableString(text + " ");
                TextUtils.copySpansFrom((Spanned) text, 0, text.length(),
                        Object.class, sp, 0);
                return sp;
            } else {
                return text + " ";
            }
        }
    }
}