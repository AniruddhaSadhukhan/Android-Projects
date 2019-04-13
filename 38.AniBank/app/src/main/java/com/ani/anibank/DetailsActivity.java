package com.ani.anibank;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DetailsActivity extends AppCompatActivity {

    ImageButton saveButton, editButton, deleteButton;
    EditText bankNameEditText,accountNoEditText,startDateEditText, maturityDateEditText,amountEditText,percentageEditText,interestEditText,notesEditText;
    AutoCompleteTextView fNameEditText,sNameEditText;
    RadioGroup typeRadioGroup;
    Boolean isNew;
    FirebaseAuth mAuth;
    String key;


    private String encode(String plaintext)
    {
        String key = "XATVKQPMF";
        StringBuilder res = new StringBuilder();

        for(int i=0,j=0; i<plaintext.length(); i++)
        {
            char c = plaintext.charAt(i);

            res.append((char)(c + key.charAt(j) - 'A'));

            j = (j+1)%key.length();
        }

        return res.toString();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }

    public void delete(View view)
    {
        new AlertDialog.Builder(this)
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setTitle("Delete "+bankNameEditText.getText().toString()+"?")
                .setMessage("Are you sure you want to delete this"+"?\nIt can't be undone. ")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("Banks").child(key).removeValue();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void EnableAll(Boolean bool)
    {
        bankNameEditText.setEnabled(bool);
        accountNoEditText.setEnabled(bool);
        startDateEditText.setEnabled(bool);
        maturityDateEditText.setEnabled(bool);
        amountEditText.setEnabled(bool);
        percentageEditText.setEnabled(bool);
        interestEditText.setEnabled(bool);
        notesEditText.setEnabled(bool);
        fNameEditText.setEnabled(bool);
        sNameEditText.setEnabled(bool);
        for(int i=0;i<typeRadioGroup.getChildCount();i++)
            typeRadioGroup.getChildAt(i).setClickable(bool);
            //typeRadioGroup.getChildAt(i).setEnabled(bool);
    }

    public void edit(View view)
    {
        editButton.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);
        saveButton.setVisibility(View.VISIBLE);

        EnableAll(true);
        
    }

    public void save(View view)
    {
        if(bankNameEditText.getText().toString().equals(""))
        {
            Toast.makeText(this, "Give a Bank Name", Toast.LENGTH_SHORT).show();
            return;
        }
        String maturityDateTimeStamp;
        try{
            DateFormat formatter = new SimpleDateFormat("dd-MM-yy");
            Date date = (Date) formatter.parse(maturityDateEditText.getText().toString());
            Timestamp timestamp = new Timestamp(date.getTime());
            maturityDateTimeStamp = timestamp.toString();

        }catch (Exception e)
        {
            Toast.makeText(DetailsActivity.this, "Date format should be dd-MM-yy", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String,String> map = new HashMap<>();
        map.put("BankName", encode(bankNameEditText.getText().toString()));
        map.put("AccountNumber", encode(accountNoEditText.getText().toString()));
        map.put("FirstName", encode(fNameEditText.getText().toString()));
        map.put("SecondName", encode(sNameEditText.getText().toString()));
        map.put("StartDate", encode(startDateEditText.getText().toString()));
        map.put("MaturityDate", encode(maturityDateEditText.getText().toString()));
        map.put("Amount", encode(amountEditText.getText().toString()));
        map.put("Percentage", encode(percentageEditText.getText().toString()));
        map.put("Interest", encode(interestEditText.getText().toString()));
        map.put("Type", encode(((RadioButton)findViewById(typeRadioGroup.getCheckedRadioButtonId())).getText().toString()));
        map.put("Notes", encode(notesEditText.getText().toString()));
        map.put("MaturityTimeStamp", maturityDateTimeStamp);


        EnableAll(false);

        if(isNew)
        {
            try {
                FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("Banks").push().setValue(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(DetailsActivity.this, "Successfully Saved", Toast.LENGTH_SHORT).show();
                                editButton.setVisibility(View.VISIBLE);
                                deleteButton.setVisibility(View.VISIBLE);
                                saveButton.setVisibility(View.GONE);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(DetailsActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                EnableAll(true);
                            }
                        });
            }catch (Exception e)
            {
                Toast.makeText(DetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }else
        {
            try {
                FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("Banks").child(key).setValue(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(DetailsActivity.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                                editButton.setVisibility(View.VISIBLE);
                                deleteButton.setVisibility(View.VISIBLE);
                                saveButton.setVisibility(View.GONE);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(DetailsActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                EnableAll(true);
                            }
                        });
            }catch (Exception e)
            {
                Toast.makeText(DetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        saveButton = findViewById(R.id.saveButton);
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);
        bankNameEditText = findViewById(R.id.bankNameEditText);
        accountNoEditText = findViewById(R.id.accountNoEditText);
        startDateEditText = findViewById(R.id.startDateEditText);
        maturityDateEditText = findViewById(R.id.maturityDateEditText);
        amountEditText = findViewById(R.id.amountEditText);
        percentageEditText = findViewById(R.id.percentageEditText);
        interestEditText = findViewById(R.id.interestEditText);
        notesEditText = findViewById(R.id.notesEditText);
        fNameEditText  = findViewById(R.id.firstNameEditText);
        sNameEditText  = findViewById(R.id.secondNameEditText);
        typeRadioGroup  = findViewById(R.id.typeRadioGroup);

        String[] names = {"Mithu", "Amitava", "Aniruddha"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
        fNameEditText.setAdapter(arrayAdapter);
        sNameEditText.setAdapter(arrayAdapter);
        fNameEditText.setThreshold(0);
        sNameEditText.setThreshold(0);

        mAuth = FirebaseAuth.getInstance();
        
        Intent intent = getIntent();

        isNew = intent.getBooleanExtra("isNew", true);

        if(isNew)
        {
            edit(null);
        }else
        {
            key = intent.getStringExtra("Key");
            bankNameEditText.setText(intent.getStringExtra("BankName"));
            accountNoEditText.setText(intent.getStringExtra("AccountNumber"));
            startDateEditText.setText(intent.getStringExtra("StartDate"));
            maturityDateEditText.setText(intent.getStringExtra("MaturityDate"));
            amountEditText.setText(intent.getStringExtra("Amount"));
            percentageEditText.setText(intent.getStringExtra("Percentage"));
            interestEditText.setText(intent.getStringExtra("Interest"));
            notesEditText.setText(intent.getStringExtra("Notes"));
            fNameEditText.setText(intent.getStringExtra("FirstName"));
            sNameEditText.setText(intent.getStringExtra("SecondName"));
            if(intent.getStringExtra("Type").equals("C"))
                typeRadioGroup.check(R.id.radioButton2);
            else
                typeRadioGroup.check(R.id.radioButton);



            EnableAll(false);

        }
    }
}
