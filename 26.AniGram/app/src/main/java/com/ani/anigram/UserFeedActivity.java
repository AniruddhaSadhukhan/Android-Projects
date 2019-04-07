package com.ani.anigram;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class UserFeedActivity extends AppCompatActivity {

    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed);

        linearLayout = findViewById(R.id.linearLayout);

        Intent intent = getIntent();
        final String username = intent.getStringExtra("username");

        try {

            if (username != null)
                setTitle("AniGram : " + username + "'s Feed");
            else
                setTitle("Feed");

            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Image");

            if (username != null)
                query.whereEqualTo("username", username);

            query.orderByDescending("createdAt");

            Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null && objects.size() > 0) {
                        for (ParseObject object : objects) {
                            ParseFile file = (ParseFile) object.get("image");
                            final String author = object.getString("username");

                            file.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if (e == null && data != null) {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                                        ImageView imageView = new ImageView(getApplicationContext());

                                        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT
                                        ));
                                        imageView.setAdjustViewBounds(true);
                                        imageView.setImageBitmap(bitmap);

                                        TextView textView = new TextView(getApplicationContext());
                                        textView.setLayoutParams(new ViewGroup.LayoutParams(
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT
                                        ));
                                        textView.setText("\n" + author + " uploaded ");
                                        textView.setTextColor(Color.BLUE);
                                        textView.setGravity(Gravity.CENTER_HORIZONTAL);
                                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

                                        linearLayout.addView(textView, linearLayout.getChildCount());
                                        linearLayout.addView(imageView, linearLayout.getChildCount());
                                    }else
                                    {
                                        Toast.makeText(UserFeedActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                    else
                    {
                        Toast.makeText(UserFeedActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }



    }
}
