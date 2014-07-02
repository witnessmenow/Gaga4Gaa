package com.ladinc.gaga.screens;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.ladinc.gaga.core.Gaga;
import com.ladinc.gaga.core.objects.Ball;
import com.ladinc.gaga.core.objects.BoxProp;
import com.ladinc.gaga.core.objects.Player;

public class GameScreen implements Screen {
	private static final String BALL = "BALL";
	public static float AI_CREATION_RATE = 1;
	private Box2DDebugRenderer debugRenderer;
	private Gaga game;
	// Used for sprites etc
	private int screenWidth;
	private int screenHeight;

	private static final float GAP_BETWEEN_TOPBOTTOMWALL_AND_EDGE = 3.0f;
	
	// Used for Box2D
	private float worldWidth;
	private float worldHeight;
	private static int PIXELS_PER_METER = 10;

	private Vector2 center;
	private OrthographicCamera camera;

	private SpriteBatch spriteBatch;
	private World world;

	public static Map<String, Texture> textureMap = new HashMap<String, Texture>();
	
	Map<Integer, Vector2> positionVector = new HashMap<Integer, Vector2>();
	private Texture ballTexture;
	
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

		setUpStartPositionsMap();
		setUpTextureMap();
		
		this.debugRenderer = new Box2DDebugRenderer();
	}

	public static void updateSprite(Sprite sprite, SpriteBatch spriteBatch, int PIXELS_PER_METER, Body body)
    {
        if(sprite != null && spriteBatch != null && body != null)
        {
                setSpritePosition(sprite, PIXELS_PER_METER, body);

                sprite.draw(spriteBatch);
        }
    }
	
	private void setUpTextureMap() {
		ballTexture = new Texture(Gdx.files.internal("ball.png"));
		textureMap.put(BALL, ballTexture);
	}

	public static void setSpritePosition(Sprite sprite, int PIXELS_PER_METER, Body body)
    {
        
        sprite.setPosition(PIXELS_PER_METER * body.getPosition().x - sprite.getWidth()/2,
                        PIXELS_PER_METER * body.getPosition().y  - sprite.getHeight()/2);
        
        
        sprite.setRotation((MathUtils.radiansToDegrees * body.getAngle()));
    }
	
	private void setUpStartPositionsMap() {
		
		//keeper
		this.positionVector.put(1, new Vector2(this.screenWidth/2/PIXELS_PER_METER, 10));
		
		//backs
		this.positionVector.put(2, new Vector2(this.screenWidth/(PIXELS_PER_METER*4), 30));
		this.positionVector.put(3, new Vector2((this.screenWidth*2)/(PIXELS_PER_METER*4), 30));
		this.positionVector.put(4, new Vector2((this.screenWidth*3)/(PIXELS_PER_METER*4), 30));
		
		//midfielders
		this.positionVector.put(5, new Vector2((this.screenWidth)/(PIXELS_PER_METER*5), 50));
		this.positionVector.put(6, new Vector2((this.screenWidth*2)/(PIXELS_PER_METER*5), 50));
		this.positionVector.put(7, new Vector2((this.screenWidth*3)/(PIXELS_PER_METER*5), 50));
		this.positionVector.put(8, new Vector2((this.screenWidth*4)/(PIXELS_PER_METER*5), 50));
		
		//forwards
		this.positionVector.put(9, new Vector2((this.screenWidth)/(PIXELS_PER_METER*4), 70));
		this.positionVector.put(10, new Vector2((this.screenWidth*2)/(PIXELS_PER_METER*4), 70));
		this.positionVector.put(11, new Vector2((this.screenWidth*3)/(PIXELS_PER_METER*4), 70));
	}

	@Override
	public void render(float delta) 
	{
		Gdx.gl.glClearColor(0f, 0f, 0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		Gaga.delta  = delta;

		camera.zoom = 2f;
		camera.update();
		// TODO: spriteBatch.setProjectionMatrix(camera.combined);

		world.step(Gdx.app.getGraphics().getDeltaTime(), 10, 10);
		// world.clearForces();
		// world.step(1/60f, 3, 3);
		world.clearForces();

		this.spriteBatch.begin();
		
		//updateSprite(birdSprite, spriteBatch, PIXELS_PER_METER, bird.body);
		
		this.spriteBatch.end();

		debugRenderer.render(world, camera.combined.scale(PIXELS_PER_METER,
				PIXELS_PER_METER, PIXELS_PER_METER));
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	private void createPlayers() {
		
		//create a full team of players here. use starting positions using
		//map and player number as the index
		for(int i = 1; i <= this.positionVector.size(); i++){
			new Player(world, this.positionVector.get(i), camera);
		}
	}

	@Override
	public void show() {
		world = new World(new Vector2(0.0f, 0.0f), true);
		addWalls();
		createPlayers();
		addBall();
	}

	private void addBall() {
		new Ball(world, 30, 30, new Sprite(textureMap.get(BALL)));
	}

	//add walls, length of the pitch is 1.5 times screen height
	private void addWalls() {
		new BoxProp(world, screenWidth/10, 1, new Vector2(screenWidth/20,
						GAP_BETWEEN_TOPBOTTOMWALL_AND_EDGE)); // bottom
		new BoxProp(world, screenWidth/10, 1, new Vector2(screenWidth/20, screenHeight/6)); // top
		new BoxProp(world, 1, screenHeight/6, new Vector2(3, screenHeight / 12));// left
		new BoxProp(world, 1, screenHeight/6, new Vector2(190, screenHeight /12)); // right
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