package com.ladinc.gaga.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.ladinc.gaga.core.Gaga;

public class GameScreen implements Screen {
	public static float AI_CREATION_RATE = 1;
	private Box2DDebugRenderer debugRenderer;
	private Gaga game;
	// Used for sprites etc
	private int screenWidth;
	private int screenHeight;

	// Used for Box2D
	private float worldWidth;
	private float worldHeight;
	private static int PIXELS_PER_METER = 10;

	private Vector2 center;
	private OrthographicCamera camera;

	private SpriteBatch spriteBatch;
	private World world;

	public GameScreen(Gaga game) {
		this.game = game;

		this.screenWidth = this.game.screenWidth;
		this.screenHeight = this.game.screenHeight;

		this.worldHeight = this.screenHeight / PIXELS_PER_METER;
		this.worldWidth = this.screenWidth / PIXELS_PER_METER;

		this.center = new Vector2(worldWidth / 2, worldHeight / 2);

		this.camera = new OrthographicCamera();
		this.camera.setToOrtho(false, this.screenWidth, this.screenHeight);

		spriteBatch = new SpriteBatch();

		this.debugRenderer = new Box2DDebugRenderer();
	}

	@Override
	public void render(float delta) 
	{
		Gdx.gl.glClearColor(0f, 0f, 0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		Gaga.delta  = delta;

		camera.zoom = 1.1f;
		camera.update();
		// TODO: spriteBatch.setProjectionMatrix(camera.combined);

		world.step(Gdx.app.getGraphics().getDeltaTime(), 10, 10);
		// world.clearForces();
		// world.step(1/60f, 3, 3);
		world.clearForces();

		 this.spriteBatch.begin();
		 this.spriteBatch.end();

		debugRenderer.render(world, camera.combined.scale(PIXELS_PER_METER,
				PIXELS_PER_METER, PIXELS_PER_METER));
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	private void createPlayers() {
		
	}

	@Override
	public void show() {
		world = new World(new Vector2(0.0f, 0.0f), true);
//		new BoxProp(world, screenWidth, 1, new Vector2(3,
//						GAP_BETWEEN_TOPBOTTOMWALL_AND_EDGE)); // bottom
//		 	new BoxProp(world, screenWidth, 1, new Vector2(screenWidth / 2, 105)); // top
//		 		new BoxProp(world, 1, screenHeight, new Vector2(3, screenHeight / 2));// left
//		 	new BoxProp(world, 1, screenHeight, new Vector2(190, screenHeight / 2)); // right

		createPlayers();
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}