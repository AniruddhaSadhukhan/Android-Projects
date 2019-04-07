package com.ani.coinman.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;



public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;

	Texture background;

	BitmapFont fontB, fontS, fontM;


	Texture[] man;
	int manState = 0 ;
	int pause = 0;
	float gravity = 0.4f;
	float velocity = 0;
	int manY;
    Rectangle playerRectangle;

	ArrayList<Integer> coinXs = new ArrayList<Integer>();
	ArrayList<Integer> coinYs = new ArrayList<Integer>();
	ArrayList<Rectangle> coinRectangles = new ArrayList<Rectangle>();
	Texture coin;
	int coinCount = 50;

	ArrayList<Integer> bombXs = new ArrayList<Integer>();
	ArrayList<Integer> bombYs = new ArrayList<Integer>();
	ArrayList<Rectangle> bombRectangles = new ArrayList<Rectangle>();
	Texture bomb;
	int bombCount = 0;
	int bombInterval = 500;

    int score = 0;
    int highScore;
    int gameState = 0;

	Random random;

    FreeTypeFontGenerator generator;

    GlyphLayout glyphLayout;

    Preferences pref;

	@Override
	public void create () {
		batch = new SpriteBatch();

		pref = Gdx.app.getPreferences("CoinManPreference");
		highScore = pref.getInteger("HighScore",0);

		background = new Texture("bg.png");

		man = new Texture[5];
        man[0] = new Texture("frame-1.png");
        man[1] = new Texture("frame-2.png");
        man[2] = new Texture("frame-3.png");
        man[3] = new Texture("frame-4.png");
        man[4] = new Texture("dizzy-1.png");

        manY = Gdx.graphics.getHeight()/2 - man[0].getHeight()/2;

        coin = new Texture("coin.png");

        bomb = new Texture("bomb.png");

        random = new Random();

        generator = new FreeTypeFontGenerator(Gdx.files.internal("myfont.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = (int) Gdx.graphics.getDensity() * 64;
        fontB = generator.generateFont(parameter);

        parameter.size = (int) Gdx.graphics.getDensity() * 50;
        fontM = generator.generateFont(parameter);

        parameter.size = (int) Gdx.graphics.getDensity() * 31;
        fontS = generator.generateFont(parameter);




        glyphLayout = new GlyphLayout ();

	}

	public void makeCoin()
    {
        int height = random.nextInt(Gdx.graphics.getHeight() - coin.getHeight());

        coinYs.add(height);
        coinXs.add(Gdx.graphics.getWidth());
    }

    public void makeBomb()
    {
        int height = random.nextInt(Gdx.graphics.getHeight() - bomb.getHeight());

        bombYs.add(height);
        bombXs.add(Gdx.graphics.getWidth());
    }

	@Override
	public void render () {

	    batch.begin();

	    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Background~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	    batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

	    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Game State Check~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	    if(gameState == 1)
        {
            //Game is Live
            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Coins~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            if (coinCount < 100)
                coinCount++;
            else
            {
                coinCount = 0;
                makeCoin();
            }

            coinRectangles.clear();
            for (int i = 0; i < coinXs.size(); i++) {
                batch.draw(coin, coinXs.get(i), coinYs.get(i));
                coinXs.set(i, coinXs.get(i) - 4);
                coinRectangles.add(new Rectangle(coinXs.get(i),coinYs.get(i), coin.getWidth(), coin.getHeight()));
            }

            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Bombs~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            if (bombCount < bombInterval)
                bombCount++;
            else
            {
                bombCount = 0;
                makeBomb();
                if(bombInterval > 150)
                    bombInterval--;
            }

            bombRectangles.clear();
            for (int i = 0; i < bombXs.size(); i++) {
                batch.draw(bomb, bombXs.get(i), bombYs.get(i));
                bombXs.set(i, bombXs.get(i) - 8);
                bombRectangles.add(new Rectangle(bombXs.get(i),bombYs.get(i), bomb.getWidth(), bomb.getHeight()));
            }

            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Player~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            if(Gdx.input.justTouched())
            {
                velocity = -10;
            }

            if(pause<8)
                pause++;
            else
            {
                manState = (manState+1) % 4;
                pause = 0;
            }

            velocity += gravity;
            manY = (int) Math.min(Gdx.graphics.getHeight()-man[manState].getHeight(), Math.max(0, manY - velocity));



        }else if(gameState == 0)
        {
            //Waiting to start
            glyphLayout.setText(fontB, "Coin - Man");
            fontB.draw(batch, glyphLayout, Gdx.graphics.getWidth()/2 - glyphLayout.width/2, Gdx.graphics.getHeight()*3/4);

            glyphLayout.setText(fontS, " Tap to jump\nCollect Coins\nAvoid Bombs");
            fontS.draw(batch, glyphLayout, Gdx.graphics.getWidth()/2 - glyphLayout.width/2, Gdx.graphics.getHeight()/4);

            if(Gdx.input.justTouched())
                gameState = 1;


        }else if(gameState == 2)
        {
            //Game Over
            if(Gdx.input.justTouched()) {
                gameState = 1;

                manY = Gdx.graphics.getHeight() / 2 - man[0].getHeight()/2;
                score = 0;
                velocity = 0;

                coinXs.clear();
                coinYs.clear();
                coinRectangles.clear();
                coinCount = 50;

                bombXs.clear();
                bombYs.clear();
                bombRectangles.clear();
                bombCount = 0;
                bombInterval = 500;


            }

        }
	    



        batch.draw(man[manState], Gdx.graphics.getWidth()/2 - man[manState].getWidth()/2, manY);

        playerRectangle = new Rectangle(Gdx.graphics.getWidth()/2 - man[manState].getWidth()/2,manY, man[manState].getWidth(), man[manState].getHeight());

        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Collision Detection~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        for (int i = 0; i < coinRectangles.size(); i++) {
            if(Intersector.overlaps(playerRectangle,coinRectangles.get(i)))
            {
                score++;

                coinRectangles.remove(i);
                coinXs.remove(i);
                coinYs.remove(i);

                break;
            }
        }

        for (int i = 0; i < bombRectangles.size(); i++) {
            if(Intersector.overlaps(playerRectangle,bombRectangles.get(i)))
            {
                glyphLayout.setText(fontM, "Game Over !!!");
                fontM.draw(batch, glyphLayout, Gdx.graphics.getWidth()/2 - glyphLayout.width/2, Gdx.graphics.getHeight()-500);
                if(score>highScore)
                {
                    pref.putInteger("HighScore", score);
                    pref.flush();
                    highScore = score;
                }
                manState = 4;
                gameState = 2;
                break;
            }
        }


        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Score~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        glyphLayout.setText(fontB,String.valueOf(score));
        fontB.draw(batch, glyphLayout, 100,200);
        if(score>highScore)
            glyphLayout.setText(fontS,"High Score : "+ String.valueOf(score));
        else
            glyphLayout.setText(fontS,"High Score : "+ String.valueOf(highScore));
        fontS.draw(batch, glyphLayout, Gdx.graphics.getWidth()-glyphLayout.width-40,Gdx.graphics.getHeight() - glyphLayout.height-10);



	    batch.end();

	}
	
	@Override
	public void dispose () {
	    generator.dispose();
		batch.dispose();
	}
}
