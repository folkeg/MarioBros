package com.folkeg.mariobros;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.folkeg.mariobros.Screens.PlayScreen;

public class MarioBros extends Game {

    public final static int V_WIDTH = 400;
	public final static int V_HEIGHT = 208;
	public final static float PPM = 100;

	public final static short NOTHING_BIT = 0;
	public final static short GROUND_BIT = 1;
	public final static short MARIO_BIT = 2;
	public final static short BRICK_BIT = 4;
	public final static short COIN_BIT = 8;
	public final static short DESTROYED_BIT = 16;
	public final static short OBJECT_BIT = 32;
	public final static short ENEMY_BIT = 64;
	public final static short ENEMY_HEAD_BIT = 128;
	public final static short ITEM_BIT = 256;
	public final static short MARIO_HEAD_BIT = 512;
	public final static short FIREBALL_BIT = 1024;



	public SpriteBatch batch;
	public static AssetManager assetManager;

	@Override
	public void create () {
		batch = new SpriteBatch();

		assetManager = new AssetManager();
		assetManager.load("audio/music/mario_music.ogg", Music.class);
		assetManager.load("audio/sounds/coin.wav", Sound.class);
		assetManager.load("audio/sounds/bump.wav", Sound.class);
		assetManager.load("audio/sounds/breakblock.wav", Sound.class);
		assetManager.load("audio/sounds/powerup_spawn.wav", Sound.class);
		assetManager.load("audio/sounds/powerup.wav", Sound.class);
		assetManager.load("audio/sounds/powerdown.wav", Sound.class);
		assetManager.load("audio/sounds/stomp.wav", Sound.class);
		assetManager.load("audio/sounds/mariodie.wav", Sound.class);
		assetManager.finishLoading();

		setScreen(new PlayScreen(this));
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.render();
	}

	@Override
	public void dispose() {
		super.dispose();
		assetManager.dispose();
		batch.dispose();
	}
}
