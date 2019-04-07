package com.ani.languagepreferences;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    String[] languages = {"English","Hindi","Bengali"};
    String language = null;
    Menu menu = null;

    public void selectLanguage(String language)
    {
        this.language = language;
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText("Language Selected : "+language);

        if(menu!=null)
            menu.findItem(getResources().getIdentifier(language.toLowerCase(),"id",getPackageName())).setChecked(true);

    }

    public void saveLanguage()
    {
        sharedPreferences.edit().putString("Language",language).apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        this.menu = menu;
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.language_menu,menu);

        if(language!=null)
            menu.findItem(getResources().getIdentifier(language.toLowerCase(),"id",getPackageName())).setChecked(true);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId())
        {
            case R.id.english:  selectLanguage("English");
                                saveLanguage();
                                return true;

            case R.id.hindi:    selectLanguage("Hindi");
                                saveLanguage();
                                return true;

            case R.id.bengali:  selectLanguage("Bengali");
                                saveLanguage();
                                return true;

            default: return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = this.getSharedPreferences("com.ani.languagepreferences", Context.MODE_PRIVATE);

        language = sharedPreferences.getString("Language",null);

        if(language != null)
            selectLanguage(language);
        else
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_menu_preferences)
                    .setTitle("Select Language!!!")
                    .setItems(languages, new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    selectLanguage(languages[which]);
                                    saveLanguage();
                                }
                            })
                    .show();

    }
}
