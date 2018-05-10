package com.mygdx.game;

import Screens.PlayScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MarioBros extends Game {

	public static final int Virtual_Width = 400;
	public static final int Virtual_Height = 208;
	//pixels per meter
	public static final float PPM = 100;

	//default bit for every category fixture.
	//this allows us to filter collisions using masks

	//no collisions
	public static final short GAMEOVERFILTER = 0;
	//ground bit
	public static final short GROUNDFILTER = 1;
	//mario bit
	public static final short MARIOFILTER = 2;
	//brick bit
	public static final short BRICKFILTER = 4;
	//coin bit
	public static final short COINFILTER = 8;
	//destroyed
	public static final short DESTROYERFILTER = 16;
	//object
	public static final short OBJECTFILTER = 32;
	//enemy
	public static final short ENEMYFILTER = 64;
	//enemy head bit
	public static final short ENEMYHEADFILTER = 128;
	//item bit
	public static final short ITEMFILTER = 256;
	//collisions
	public static final short MARIOHEADFILTER = 512;

	//public so we can keep memory low
	public SpriteBatch batch;
	Texture img;

	@Override
	public void create () {
		batch = new SpriteBatch();
		setScreen(new PlayScreen(this));

	}

	@Override
	public void render () {
		//render screen to whichever is currently active.
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		if(img != null)
			img.dispose();
	}
}
