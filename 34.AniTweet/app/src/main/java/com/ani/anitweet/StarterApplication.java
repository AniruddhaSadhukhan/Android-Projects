package com.ani.anitweet;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class StarterApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ViewPump.init(ViewPump.builder()
        .addInterceptor(new CalligraphyInterceptor(
                new CalligraphyConfig.Builder()
                    .setDefaultFontPath("fonts/myfont.ttf")
                    .setFontAttrId(R.attr.fontPath)
                    .build()))
        .build());

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Add your initialization code here
        Parse.initialize(new Parse.Configuration.Builder(this.getApplicationContext())
                .applicationId("ed76b20a-029d-48e6-8815-96f31e1de366")
                .clientKey("q440LJEXzTvZLwf0tytB05YNvfe5Nugq")
                .server("https://parse.buddy.com/parse/")
                .build()
        );


        //ParseUser.enableAutomaticUser();

        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

    }
}
