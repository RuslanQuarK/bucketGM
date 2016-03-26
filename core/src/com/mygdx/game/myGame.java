package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

import javafx.scene.input.KeyCode;

public class myGame extends ApplicationAdapter {


	private boolean fire = false;

	private BitmapFont font;

	private int scoreDrops;
	private int scoreFailDrops;

	private Music bgMusic;
	private Sound waterDrop;

	private Texture enemyImage;
	private Texture bucketImage;
	private Texture dropImage;
	private Texture shootImage;

	private SpriteBatch batch;

	private Rectangle shoot;
	private Rectangle bucket;

	private Array<Rectangle> rainDropsArray;
	private Array<Rectangle> enemyArray;

	private long lastDropTime;
	private long lastEnemySpawned;

	@Override
	public void create () {
		font=new BitmapFont();
		font.setColor(Color.RED);

		enemyImage= new Texture(Gdx.files.internal("enemy.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));
		dropImage= new Texture(Gdx.files.internal("droplet.png"));
		shootImage=new Texture(Gdx.files.internal("shoot.png"));

		batch=new SpriteBatch();
		bgMusic=Gdx.audio.newMusic(Gdx.files.internal("undertreeinrain.mp3"));
		bgMusic.play();
		waterDrop=Gdx.audio.newSound(Gdx.files.internal("waterdrop.wav"));

		enemyArray=new Array<Rectangle>();


		bucket = new Rectangle();
		bucket.x=800/2;
		bucket.y=480/2;
		bucket.width=64;
		bucket.height=64;
		rainDropsArray = new Array<Rectangle>();

		shoot = new Rectangle();
		shoot.x=bucket.x;
		shoot.y=bucket.y;
		shoot.height=64;
		shoot.width=64;
	}

	private void spawnRainDrops(){
		Rectangle rainDrops= new Rectangle();
		rainDrops.x= MathUtils.random(0,800-64);
		rainDrops.y=480;
		rainDrops.width=64;
		rainDrops.height=64;
		rainDropsArray.add(rainDrops);
		lastDropTime= TimeUtils.nanoTime();
	}

	private void enemySpawn(){
		Rectangle enemy = new Rectangle();
		enemy.width=64;
		enemy.height=64;
		enemy.x=MathUtils.random(0,800-64);
		enemy.y=480;
		enemyArray.add(enemy);
		lastEnemySpawned=TimeUtils.nanoTime();
	}

	private Rectangle shootMethod(){
		Rectangle rect = new Rectangle();
		rect.x=bucket.x;
		rect.y=bucket.y;
		rect.height=64;
		rect.width=64;
		return rect;
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		//Draw
		batch.begin();
		batch.draw(bucketImage, bucket.x, bucket.y);
		for (Rectangle rainDrop : rainDropsArray) {
			batch.draw(dropImage, rainDrop.x, rainDrop.y);
		}
		for (Rectangle enemy: enemyArray){
			batch.draw(enemyImage,enemy.x,enemy.y);
		}

		//Creating a bullet to shoot
//		if (Gdx.input.(Input.Keys.SPACE))
//			batch.draw(shootImage, bucket.x, bucket.y);

		font.draw(batch, "Score : " + scoreDrops, 10, 470);
		font.draw(batch, "Not catched drops :" + scoreFailDrops, 10, 440);

		batch.end();

		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket.x -= 200 * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket.x += 200 * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Input.Keys.UP)) bucket.y += 200 * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) bucket.y -= 200 * Gdx.graphics.getDeltaTime();

		if (bucket.x < 0) bucket.x = 0;
		if (bucket.x > 800 - 64) bucket.x = 800 - 64;
		if (bucket.y < 0) bucket.y = 0;
		if (bucket.y > 480 - 64) bucket.y = 480 - 64;

		if (TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRainDrops();
		if (TimeUtils.nanoTime()-lastEnemySpawned>1000000000) enemySpawn();

		//EnemySpawn
		Iterator<Rectangle> iter=enemyArray.iterator();
		while(iter.hasNext()){
			Rectangle r =iter.next();
			r.y-=200*Gdx.graphics.getDeltaTime();
			if (r.y<0) iter.remove();
			if (r.overlaps(bucket)){
				scoreDrops =0;
				iter.remove();
			}
		}
	//HEre is some
		//RaindropsSpawn
		Iterator<Rectangle> it = rainDropsArray.iterator();
		while (it.hasNext()) {
			Rectangle r = it.next();
			r.y -= 200 * Gdx.graphics.getDeltaTime();
			if (r.overlaps(bucket)) {
				waterDrop.play();
				scoreDrops++;
				it.remove();
			}
			if (r.y<0) {
				scoreFailDrops++;
				it.remove();
			}
		}
	}


	@Override
	public void dispose() {
		super.dispose();
		bgMusic.dispose();
		waterDrop.dispose();
		batch.dispose();
		bucketImage.dispose();
		dropImage.dispose();
		shootImage.dispose();
	}
}
