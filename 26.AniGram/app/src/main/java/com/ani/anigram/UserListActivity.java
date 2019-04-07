package com.ani.anigram;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    public void getPhoto()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.share_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.share)
        {
            if(Build.VERSION.SDK_INT >=23 && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            else
                getPhoto();


        }else if(item.getItemId() == R.id.logout)
        {
            //Log Out User
            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    if(e==null) {
                        Toast.makeText(UserListActivity.this, "Successfully Signed Out", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else
                        Toast.makeText(UserListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data != null)
        {
            try {
                //Upload Image to Parse
                Toast.makeText(this, "Please wait...Preparing Image", Toast.LENGTH_SHORT).show();
                Uri selectedImage = data.getData();

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream);

                byte[] byteArray = stream.toByteArray();

                ParseFile file = new ParseFile("image.jpeg", byteArray);

                ParseObject object = new ParseObject("Image");

                object.put("image", file);
                object.put("username", ParseUser.getCurrentUser().getUsername());

                Toast.makeText(this, "Sharing...", Toast.LENGTH_SHORT).show();

                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null)
                            Toast.makeText(UserListActivity.this, "Image has been shared", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(UserListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            getPhoto();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        setTitle("AniGram : User List");

        ListView listView = findViewById(R.id.listView);
        final ArrayList<String> usernames = new ArrayList<String>();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, usernames);

        usernames.add("~~~~~All Users~~~~~");
        usernames.add("~~~~~    Me   ~~~~~");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Intent intent = new Intent(getApplicationContext(), UserFeedActivity.class);
                    if (position > 1)
                        intent.putExtra("username", usernames.get(position));
                    else if (position == 1)
                        intent.putExtra("username", ParseUser.getCurrentUser().getUsername());
                    startActivity(intent);
                }catch (Exception e)
                {
                    Toast.makeText(UserListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.addAscendingOrder("username");

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e==null && objects.size() > 0)
                {
                    for (ParseUser user : objects)
                    {
                        usernames.add(user.getUsername());
                    }
                    arrayAdapter.notifyDataSetChanged();
                }
                else
                {
                    Toast.makeText(UserListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        listView.setAdapter(arrayAdapter);

    }
}
