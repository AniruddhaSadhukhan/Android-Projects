package com.ani.coinman.game;

import android.os.Bundle;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.ani.coinman.game.CoinMan;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Toast.makeText(this, "Developed by Aniruddha", Toast.LENGTH_LONG).show();
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new CoinMan(), config);
	}
}
