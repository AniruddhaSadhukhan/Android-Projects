package com.ani.anibank;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    ShapeDrawable border;


    private String decode(String plaintext)
    {
        String key = "XATVKQPMF";
        StringBuilder res = new StringBuilder();

        for(int i=0,j=0; i<plaintext.length(); i++)
        {
            char c = plaintext.charAt(i);

            res.append((char) (c - key.charAt(j) + 'A'));

            j = (j+1)%key.length();
        }

        return res.toString();

    }



    public void logout(View view)
    {
        new AlertDialog.Builder(this)
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setTitle("Log Out?")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void addNew(View view)
    {
        Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
        intent.putExtra("isNew",true);
        startActivity(intent);
        finish();
    }

    public TextView createTextView(String s, int width)
    {
        TextView textView = new TextView(HomeActivity.this);
        if(s==null)
            textView.setText("");
        else
            textView.setText(s);
        textView.setWidth(width);
        textView.setBackground(border);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,24);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        return textView;
    }
    public TextView createTextView(String s,int width, Boolean isHeading)
    {
        TextView textView = new TextView(HomeActivity.this);
        if(!isHeading) return textView;
        textView.setText(s);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD_ITALIC);
        textView.setTextColor(getResources().getColor(R.color.darkBlue));
        textView.setWidth(width);
        textView.setBackground(border);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,24);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        return textView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth == null)
            finish();
        else
        {
            border = new ShapeDrawable(new RectShape());
            border.getPaint().setStyle(Paint.Style.STROKE);
            border.getPaint().setColor(Color.BLACK);

            final TableLayout table = findViewById(R.id.tableLayout);



            TableRow row = new TableRow(HomeActivity.this);
            //row.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            row.addView(createTextView(" Bank ",250,true));
            row.addView(createTextView(" Account Number ",600,true));
            row.addView(createTextView(" First Name ",450,true));
            row.addView(createTextView(" Second Name ",500,true));
            row.addView(createTextView(" Start Date ",400,true));
            row.addView(createTextView(" Maturity Date ",450,true));
            row.addView(createTextView(" Amount ",400,true));
            row.addView(createTextView(" Percentage ",400,true));
            row.addView(createTextView(" Interest ",400,true));
            row.addView(createTextView(" Type ",400,true));
            row.addView(createTextView(" Notes ",1000,true));

            table.addView(row);


            final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

            database.child("Users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("Banks").orderByChild("MaturityTimeStamp").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {
                    TableRow row = new TableRow(HomeActivity.this);


                    row.addView(createTextView(decode(dataSnapshot.child("BankName").getValue().toString()),250));
                    row.addView(createTextView(decode(dataSnapshot.child("AccountNumber").getValue().toString()),600));
                    row.addView(createTextView(decode(dataSnapshot.child("FirstName").getValue().toString()),450));
                    row.addView(createTextView(decode(dataSnapshot.child("SecondName").getValue().toString()),500));
                    row.addView(createTextView(decode(dataSnapshot.child("StartDate").getValue().toString()),400));
                    row.addView(createTextView(decode(dataSnapshot.child("MaturityDate").getValue().toString()),450));
                    row.addView(createTextView(decode(dataSnapshot.child("Amount").getValue().toString()),400));
                    row.addView(createTextView(decode(dataSnapshot.child("Percentage").getValue().toString()),400));
                    row.addView(createTextView(decode(dataSnapshot.child("Interest").getValue().toString()),400));
                    row.addView(createTextView(decode(dataSnapshot.child("Type").getValue().toString()),400));
                    row.addView(createTextView(decode(dataSnapshot.child("Notes").getValue().toString()),1000));

                    row.setClickable(true);
                    row.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
                            intent.putExtra("isNew",false);
                            intent.putExtra("Key", dataSnapshot.getKey());
                            intent.putExtra("BankName", decode(dataSnapshot.child("BankName").getValue().toString()));
                            intent.putExtra("AccountNumber", decode(dataSnapshot.child("AccountNumber").getValue().toString()));
                            intent.putExtra("FirstName", decode(dataSnapshot.child("FirstName").getValue().toString()));
                            intent.putExtra("SecondName",decode( dataSnapshot.child("SecondName").getValue().toString()));
                            intent.putExtra("StartDate", decode(dataSnapshot.child("StartDate").getValue().toString()));
                            intent.putExtra("MaturityDate", decode(dataSnapshot.child("MaturityDate").getValue().toString()));
                            intent.putExtra("Amount", decode(dataSnapshot.child("Amount").getValue().toString()));
                            intent.putExtra("Percentage", decode(dataSnapshot.child("Percentage").getValue().toString()));
                            intent.putExtra("Interest", decode(dataSnapshot.child("Interest").getValue().toString()));
                            intent.putExtra("Type", decode(dataSnapshot.child("Type").getValue().toString()));
                            intent.putExtra("Notes", decode(dataSnapshot.child("Notes").getValue().toString()));

                            startActivity(intent);
                            finish();

                        }
                    });


                    table.addView(row);

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });




        }
    }
}

