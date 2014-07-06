package com.ladinc.gaga.screens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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

	public static Map<String, Sprite> textureMap = new HashMap<String, Sprite>();
	
	Map<Integer, Vector2> positionVectorMap = new HashMap<Integer, Vector2>();
	
	//these maps will use the same key as the positionVectorMap
	public static Map<Integer, Vector2> defendingPositionsMap = new HashMap<Integer, Vector2>();
	public static Map<Integer, Vector2> attackingPositionsMap = new HashMap<Integer, Vector2>();
	
	private Texture ballTexture;
	private Ball ball;
	public static Map<Integer, Player> playerMap = new HashMap<Integer, Player>();
	
	public static boolean attacking = false;
	
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
		
		setUpTextureMap();
		
		this.debugRenderer = new Box2DDebugRenderer();
	}

	private void applyAttackingAndDefendingPositionsToPlayers() {
		for(int i = 1; i<=playerMap.size(); i++){
			playerMap.get(i).setTargetDefendingPosition(defendingPositionsMap.get(i));
			playerMap.get(i).setTargetAttackingPosition(attackingPositionsMap.get(i));
		}
	}

	//These will be the positions that the players run to when they are attacking
	//TODO Need to draw this out, dont want all players just moving forward consistently, try stagger their position
	//also need to stagger speed of players based on position, i.e. positions 0-4 are all defenders, they should be slower etc.
	
	//TODO Need to vary the x-values a lot here, so that players make 'intelligent' runs and dont just all run forward in a straight line
	private void setUpAttackingPositionsMap() {
		//keeper
		attackingPositionsMap.put(1, new Vector2(this.positionVectorMap.get(1).x , this.positionVectorMap.get(1).y + 20)); 
		
		//defender //TODO want to stagger their positions a little when defending, so the y-values will vary slightly
		attackingPositionsMap.put(2, new Vector2(this.positionVectorMap.get(2).x, this.positionVectorMap.get(2).y + 25));
		attackingPositionsMap.put(3, new Vector2(this.positionVectorMap.get(3).x, this.positionVectorMap.get(3).y + 0));
		attackingPositionsMap.put(4, new Vector2(this.positionVectorMap.get(4).x, this.positionVectorMap.get(4).y + 45));
		
		//mids
		attackingPositionsMap.put(5, new Vector2(this.positionVectorMap.get(5).x, this.positionVectorMap.get(5).y + 45));
		attackingPositionsMap.put(6, new Vector2(this.positionVectorMap.get(6).x, this.positionVectorMap.get(6).y + 75));
		attackingPositionsMap.put(7, new Vector2(this.positionVectorMap.get(7).x, this.positionVectorMap.get(7).y + 65));
		attackingPositionsMap.put(8, new Vector2(this.positionVectorMap.get(8).x, this.positionVectorMap.get(8).y + 80));
		
		//forwards
		attackingPositionsMap.put(9, new Vector2(this.positionVectorMap.get(9).x, this.positionVectorMap.get(9).y + 75));
		attackingPositionsMap.put(10, new Vector2(this.positionVectorMap.get(10).x, this.positionVectorMap.get(10).y + 65));
		attackingPositionsMap.put(11, new Vector2(this.positionVectorMap.get(11).x, this.positionVectorMap.get(11).y + 80));		
	}

	//These will be the positions the players aim to get to when defending,
	//except for the player thats closest to the ball - he will just run towards the ball
	private void setUpDefendingPositionsMap() {
		//keeper
		defendingPositionsMap.put(1, new Vector2(this.positionVectorMap.get(1).x, this.positionVectorMap.get(1).y)); 
		
		//defenders //TODO want to stagger their positions a little when defending, so the y-values will vary slightly
		defendingPositionsMap.put(2, new Vector2(this.positionVectorMap.get(2).x, this.positionVectorMap.get(2).y - 5));
		defendingPositionsMap.put(3, new Vector2(this.positionVectorMap.get(3).x, this.positionVectorMap.get(3).y - 0));
		defendingPositionsMap.put(4, new Vector2(this.positionVectorMap.get(4).x, this.positionVectorMap.get(4).y - 5));
		
		//mids
		defendingPositionsMap.put(5, new Vector2(this.positionVectorMap.get(5).x, this.positionVectorMap.get(5).y + 5));
		defendingPositionsMap.put(6, new Vector2(this.positionVectorMap.get(6).x, this.positionVectorMap.get(6).y + 5));
		defendingPositionsMap.put(7, new Vector2(this.positionVectorMap.get(7).x, this.positionVectorMap.get(7).y - 15));
		defendingPositionsMap.put(8, new Vector2(this.positionVectorMap.get(8).x, this.positionVectorMap.get(8).y - 0));
	
		//forwards
		defendingPositionsMap.put(9, new Vector2(this.positionVectorMap.get(9).x, this.positionVectorMap.get(9).y + 5));
		defendingPositionsMap.put(10, new Vector2(this.positionVectorMap.get(10).x, this.positionVectorMap.get(10).y - 15));
		defendingPositionsMap.put(11, new Vector2(this.positionVectorMap.get(11).x, this.positionVectorMap.get(11).y - 0));	
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
		textureMap.put(BALL, new Sprite(ballTexture));
	}

	public static void setSpritePosition(Sprite sprite, int PIXELS_PER_METER, Body body)
    {
        
        sprite.setPosition(PIXELS_PER_METER * body.getWorldCenter().x - sprite.getWidth()/2,
                        PIXELS_PER_METER * body.getWorldCenter().y  - sprite.getHeight()/2);
        
        
        sprite.setRotation((MathUtils.radiansToDegrees * body.getAngle()));
    }
	
	private void setUpStartPositionsMap() {
		
		//keeper
		this.positionVectorMap.put(1, new Vector2(this.screenWidth/2/PIXELS_PER_METER, 10));
		
		//backs
		this.positionVectorMap.put(2, new Vector2(this.screenWidth/(PIXELS_PER_METER*4), 30));
		this.positionVectorMap.put(3, new Vector2((this.screenWidth*2)/(PIXELS_PER_METER*4), 30));
		this.positionVectorMap.put(4, new Vector2((this.screenWidth*3)/(PIXELS_PER_METER*4), 30));
		
		//midfielders
		this.positionVectorMap.put(5, new Vector2((this.screenWidth)/(PIXELS_PER_METER*5), 50));
		this.positionVectorMap.put(6, new Vector2((this.screenWidth*2)/(PIXELS_PER_METER*5), 50));
		this.positionVectorMap.put(7, new Vector2((this.screenWidth*3)/(PIXELS_PER_METER*5), 50));
		this.positionVectorMap.put(8, new Vector2((this.screenWidth*4)/(PIXELS_PER_METER*5), 50));
		
		//forwards
		this.positionVectorMap.put(9, new Vector2((this.screenWidth)/(PIXELS_PER_METER*4), 70));
		this.positionVectorMap.put(10, new Vector2((this.screenWidth*2)/(PIXELS_PER_METER*4), 70));
		this.positionVectorMap.put(11, new Vector2((this.screenWidth*3)/(PIXELS_PER_METER*4), 70));
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
		
		//updateSprite(textureMap.get(BALL), this.spriteBatch, PIXELS_PER_METER, ball.body);
		updatePlayerPositions();
		
		this.spriteBatch.end();

		debugRenderer.render(world, camera.combined.scale(PIXELS_PER_METER,
				PIXELS_PER_METER, PIXELS_PER_METER));
	}

	//TODO Could use this method to draw an arrow on to the player to aim a pass/shot
//	private void checkIfPlayerHasBall() {
//		for(Entry<Integer, Player> playerSet : playerMap.entrySet()){
//			if(playerSet.getValue().getHasBall()){
//				
//			}
//		}
//		
//	}

	private void updatePlayerPositions() {
		//if attacking, players should run to attacking target positions, one per player
		//if defending, closest player should run to ball, the rest should return to target position for defending,,
		//which is their starting positions
	
		for(Entry<Integer, Player> entry : playerMap.entrySet()){
			Player player = entry.getValue();
			Vector2 linearVel;
			
			//TODO: Should players move while they have the ball?
			if(attacking && !player.getHasBall()){
				linearVel = player.getMovementoPlayerTowardsTargetDest(player.body.getPosition(), player.getTargetAttackingPosition());
				player.body.setLinearVelocity(new Vector2(Player.PLAYER_SPEED * linearVel.x, Player.PLAYER_SPEED * linearVel.y));
			}
			else{
				//get closest player to ball
				setClosestPlayerToBall(new ArrayList<Player>(playerMap.values()));
				
				//pass in either the ball of the player target defending position, based on the 'isClosestToBall' boolean 
				linearVel = player.getMovementoPlayerTowardsTargetDest(player.body.getPosition(), player.getIsClosestPlayerToBall()?ball.getPosition():player.getTargetDefendingPosition());
				
				player.body.setLinearVelocity(Player.PLAYER_SPEED * linearVel.x, Player.PLAYER_SPEED * linearVel.y);
			}
		}
	}

	//go through list of players on the field. Set the player that is closest to the ball
	//as he will be the only one drawn towards the ball when defending
	private void setClosestPlayerToBall(ArrayList<Player> playerList) {
		double minDist = 100000000; //some really high number so that at least one player will definitely be less than it
		
		for(Player player : playerList){
			double distanceFromPlayer = ball.getDistanceFromPlayer(player.body);
			if(distanceFromPlayer<minDist){
				minDist = distanceFromPlayer;
				
				if(minDist < 3 && ball.getBallHeight()<Player.PLAYER_HEIGHT){
					player.setHasBall(true);
					attacking = true;
				}
				else{
					player.setHasBall(false);
				}
				//reset all 'IsClosestPlayerToBall' player values to false as there is a 'new' closest player value
				resetClosePlayerBools(playerList);
				
				player.setIsClosestPlayerToBall(true);
				
			}	
		}
	}

	private void resetClosePlayerBools(ArrayList<Player> playerList) {
		for(Player player : playerList){
			player.setIsClosestPlayerToBall(false);
		}
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	private void createPlayers() {
		
		//create a full team of players here. use starting positions using
		//map and player number as the index
		for(int i = 1; i <= this.positionVectorMap.size(); i++){
			Player player = new Player(world, this.positionVectorMap.get(i), camera);
			playerMap.put(i, player);
		}
	}

	@Override
	public void show() {
		world = new World(new Vector2(0.0f, 0.0f), true);
		addWalls();
		setUpStartPositionsMap();
		
		setUpAttackingPositionsMap();
		setUpDefendingPositionsMap();
		
		createPlayers();
		applyAttackingAndDefendingPositionsToPlayers();
		
		addBall();
	}

	private void addBall() {
		this.ball = new Ball(world, 30, 30, new Sprite(textureMap.get(BALL)));
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