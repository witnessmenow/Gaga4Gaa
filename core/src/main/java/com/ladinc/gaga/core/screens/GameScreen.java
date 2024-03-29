package com.ladinc.gaga.core.screens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.ladinc.gaga.core.GaGa;
import com.ladinc.gaga.core.objects.AIPlayer;
import com.ladinc.gaga.core.objects.Ball;
import com.ladinc.gaga.core.objects.BoxProp;
import com.ladinc.gaga.core.objects.BoxProp.Line;
import com.ladinc.gaga.core.objects.Player;
import com.ladinc.gaga.core.objects.UserPlayer;

public class GameScreen implements Screen {
	public static float AI_CREATION_RATE = 1;
	public static boolean attacking = false;
	public static Map<Integer, Vector2> attackingPositionsMap = new HashMap<Integer, Vector2>();
	public static Map<Integer, AIPlayer> awayTeamPlayerMap = new HashMap<Integer, AIPlayer>();
	public static Map<Integer, Sprite> awayTeamTextureMap = new HashMap<Integer, Sprite>();
	public static Ball ball;
	private static final String BALL = "BALL";
	public static boolean ballAtFeet;

	private static final String BTM_GOAL = "BTM_GOAL";

	public static Vector2 center = new Vector2();
	// these maps will use the same key as the positionVectorMap
	public static Map<Integer, Vector2> defendingPositionsMap = new HashMap<Integer, Vector2>();
	public static float delta;
	private static final String GRASS = "GRASS";

	public static Map<Integer, UserPlayer> homeTeamPlayerMap = new HashMap<Integer, UserPlayer>();
	public static Map<Integer, Sprite> homeTeamTextureMap = new HashMap<Integer, Sprite>();

	private static int PIXELS_PER_METER = 10;
	public static Player playerWithBall;

	public static int screenHeight;

	public static Map<String, Sprite> textureMap = new HashMap<String, Sprite>();

	private static final String TOP_GOAL = "TOP_GOAL";

	static void goalScored() {
		System.out.println("Goal Scored");
	}

	public static void moveBall(Vector2 vector2) {
		ball.body.applyForce(vector2, ball.body.getWorldCenter(), true);

	}

	public static void setSpritePosition(Sprite sprite, int PIXELS_PER_METER,
			Body body) {

		sprite.setPosition(
				PIXELS_PER_METER * body.getWorldCenter().x - sprite.getWidth()
						/ 2, PIXELS_PER_METER * body.getWorldCenter().y
						- sprite.getHeight() / 2);

		sprite.setRotation((MathUtils.radiansToDegrees * body.getAngle()));
	}

	public static void updateSprite(Sprite sprite, SpriteBatch spriteBatch,
			int PIXELS_PER_METER, Body body) {
		if (sprite != null && spriteBatch != null && body != null) {
			setSpritePosition(sprite, PIXELS_PER_METER, body);
			sprite.draw(spriteBatch);
		}
	}

	private Texture aiPlayerTexture1;
	private Texture aiPlayerTexture2;
	private Texture aiPlayerTexture3;
	private Texture aiPlayerTexture4;
	private Texture aiPlayerTexture5;
	private Texture aiPlayerTexture6;
	private Texture aiPlayerTexture7;

	private Texture ballTexture;
	private Texture btmGoalTexture;

	private final OrthographicCamera camera;

	private GameContactListener contactListener;
	private final Box2DDebugRenderer debugRenderer;

	private final GaGa game;

	private Texture grassTexture;

	private Texture homePlayerTexture1;
	private Texture homePlayerTexture2;
	private Texture homePlayerTexture3;
	private Texture homePlayerTexture4;
	private Texture homePlayerTexture5;
	private Texture homePlayerTexture6;
	private Texture homePlayerTexture7;
	private final float localDelta = 0f;
	Map<Integer, Vector2> positionVectorMap = new HashMap<Integer, Vector2>();
	// Used for sprites etc
	private final int screenWidth;
	private final SpriteBatch spriteBatch;
	private Texture topGoalTexture;

	private World world;
	private final float worldHeight;
	// Used for Box2D
	private final float worldWidth;

	public GameScreen(GaGa game) {
		this.game = game;

		this.screenWidth = this.game.screenWidth;
		screenHeight = this.game.screenHeight;

		this.worldHeight = screenHeight / PIXELS_PER_METER;
		this.worldWidth = this.screenWidth / PIXELS_PER_METER;

		center = new Vector2(worldWidth / 2, worldHeight / 2);

		this.camera = new OrthographicCamera();
		this.camera.setToOrtho(false, this.screenWidth, screenHeight);

		spriteBatch = new SpriteBatch();

		setUpTextureMaps();

		this.debugRenderer = new Box2DDebugRenderer();
	}

	private void addAwayTeam() {
		// create a full team of players here. use starting positions using
		// map and player number as the index
		AIPlayer player1 = new AIPlayer(world, new Vector2(this.screenWidth / 2
				/ PIXELS_PER_METER, 165), new Vector2(this.screenWidth / 2
				/ PIXELS_PER_METER, 165), new Vector2(this.screenWidth / 2
				/ PIXELS_PER_METER, 165), 1, camera);

		AIPlayer player2 = new AIPlayer(world, new Vector2(this.screenWidth / 6
				/ PIXELS_PER_METER, 150), new Vector2(this.screenWidth / 6
				/ PIXELS_PER_METER, 70), new Vector2(this.screenWidth / 6
				/ PIXELS_PER_METER, 150), 2, camera);

		AIPlayer player3 = new AIPlayer(
				world,
				new Vector2((this.screenWidth + 150) / 2 / PIXELS_PER_METER,
						150),
				new Vector2((this.screenWidth + 150) / 2 / PIXELS_PER_METER, 70),
				new Vector2((this.screenWidth + 150) / 2 / PIXELS_PER_METER,
						150), 3, camera);

		AIPlayer player4 = new AIPlayer(
				world,
				new Vector2((this.screenWidth - 150) / 2 / PIXELS_PER_METER,
						150),
				new Vector2((this.screenWidth - 150) / 2 / PIXELS_PER_METER, 70),
				new Vector2((this.screenWidth - 150) / 2 / PIXELS_PER_METER,
						150), 4, camera);

		AIPlayer player5 = new AIPlayer(world, new Vector2(
				((this.screenWidth - 200) / 2) * 2 / PIXELS_PER_METER, 150),
				new Vector2(((this.screenWidth - 200) / 2) * 2
						/ PIXELS_PER_METER, 70), new Vector2(
						((this.screenWidth - 200) / 2) * 2 / PIXELS_PER_METER,
						150), 5, camera);

		AIPlayer player6 = new AIPlayer(world, new Vector2(this.screenWidth / 6
				/ PIXELS_PER_METER, 100), new Vector2(this.screenWidth / 6
				/ PIXELS_PER_METER, 100), new Vector2(this.screenWidth / 6
				/ PIXELS_PER_METER, 100), 6, camera);

		AIPlayer player7 = new AIPlayer(world, new Vector2(
				(this.screenWidth + 150) / 2 / PIXELS_PER_METER, 100),
				new Vector2((this.screenWidth + 150) / 2 / PIXELS_PER_METER,
						100), new Vector2((this.screenWidth + 150) / 2
						/ PIXELS_PER_METER, 100), 7, camera);

		AIPlayer player8 = new AIPlayer(world, new Vector2(
				(this.screenWidth - 150) / 2 / PIXELS_PER_METER, 100),
				new Vector2((this.screenWidth - 150) / 2 / PIXELS_PER_METER,
						100), new Vector2((this.screenWidth - 150) / 2
						/ PIXELS_PER_METER, 100), 8, camera);
		//
		// AIPlayer player9 = new AIPlayer(world, new Vector2(
		// ((this.screenWidth - 200) / 2) * 2 / PIXELS_PER_METER, 100),
		// new Vector2(((this.screenWidth - 200) / 2) * 2
		// / PIXELS_PER_METER, 100), new Vector2(
		// ((this.screenWidth - 200) / 2) * 2 / PIXELS_PER_METER,
		// 100), 9, camera);
		//
		// AIPlayer player10 = new AIPlayer(
		// world,
		// new Vector2((this.screenWidth + 100) / 2 / PIXELS_PER_METER, 80),
		// new Vector2((this.screenWidth + 100) / 2 / PIXELS_PER_METER, 80),
		// new Vector2((this.screenWidth + 100) / 2 / PIXELS_PER_METER, 80),
		// 10, camera);
		//
		// AIPlayer player11 = new AIPlayer(
		// world,
		// new Vector2((this.screenWidth - 100) / 2 / PIXELS_PER_METER, 80),
		// new Vector2((this.screenWidth - 100) / 2 / PIXELS_PER_METER, 80),
		// new Vector2((this.screenWidth - 100) / 2 / PIXELS_PER_METER, 80),
		// 11, camera);

		awayTeamPlayerMap.put(1, player1);
		awayTeamPlayerMap.put(2, player2);
		awayTeamPlayerMap.put(3, player3);
		awayTeamPlayerMap.put(4, player4);
		awayTeamPlayerMap.put(5, player5);
		awayTeamPlayerMap.put(6, player6);
		awayTeamPlayerMap.put(7, player7);
		awayTeamPlayerMap.put(8, player8);
		// awayTeamPlayerMap.put(9, player9);
		// awayTeamPlayerMap.put(10, player10);
		// awayTeamPlayerMap.put(11, player11);

	}

	// adapted from here
	// http://www.box2d.org/forum/viewtopic.php?f=3&t=8833

	private void addBall() {
		ball = new Ball(world, 30, 30);
	}

	// goals will just be simple rectangles, formed using 3 narrow boxes, and
	// the fourth side is the end line
	private void addGoals() {
		// bottom goal
		new BoxProp(world, 1, 10, new Vector2(center.x - 30, -1), Line.sideLine); // left
		new BoxProp(world, 1, 10, new Vector2(center.x + 30, -1), Line.sideLine); // right
		new BoxProp(world, 60, 1, new Vector2(center.x, -6), Line.endLine); // back

		// top goal
		new BoxProp(world, 1, 10, new Vector2(center.x - 30,
				screenHeight / 6 + 4), Line.sideLine); // left
		new BoxProp(world, 1, 10, new Vector2(center.x + 30,
				screenHeight / 6 + 4), Line.sideLine); // right
		new BoxProp(world, 60, 1, new Vector2(center.x, screenHeight / 6 + 10),
				Line.endLine); // back
	}

	// TODO Starting positions and defending positions are the same for the
	// moment
	private void addHomeTeam() {

		// create a full team of players here. use starting positions using
		// map and player number as the index
		UserPlayer player1 = new UserPlayer(world, new Vector2(this.screenWidth
				/ 2 / PIXELS_PER_METER, 15), new Vector2(this.screenWidth / 2
				/ PIXELS_PER_METER, 20), new Vector2(this.screenWidth / 2
				/ PIXELS_PER_METER, 15), 1, camera);

		UserPlayer player2 = new UserPlayer(world, new Vector2(this.screenWidth
				/ 6 / PIXELS_PER_METER, 30), new Vector2(this.screenWidth / 6
				/ PIXELS_PER_METER, 70), new Vector2(this.screenWidth / 6
				/ PIXELS_PER_METER, 30), 2, camera);

		UserPlayer player3 = new UserPlayer(
				world,
				new Vector2((this.screenWidth + 150) / 2 / PIXELS_PER_METER, 30),
				new Vector2((this.screenWidth + 150) / 2 / PIXELS_PER_METER, 70),
				new Vector2((this.screenWidth + 150) / 2 / PIXELS_PER_METER, 30),
				3, camera);

		UserPlayer player4 = new UserPlayer(
				world,
				new Vector2((this.screenWidth - 150) / 2 / PIXELS_PER_METER, 30),
				new Vector2((this.screenWidth - 150) / 2 / PIXELS_PER_METER, 70),
				new Vector2((this.screenWidth - 150) / 2 / PIXELS_PER_METER, 30),
				4, camera);

		UserPlayer player5 = new UserPlayer(world, new Vector2(
				((this.screenWidth - 200) / 2) * 2 / PIXELS_PER_METER, 30),
				new Vector2(((this.screenWidth - 200) / 2) * 2
						/ PIXELS_PER_METER, 70), new Vector2(
						((this.screenWidth - 200) / 2) * 2 / PIXELS_PER_METER,
						30), 5, camera);

		UserPlayer player6 = new UserPlayer(world, new Vector2(this.screenWidth
				/ 6 / PIXELS_PER_METER, 100), new Vector2(this.screenWidth / 6
				/ PIXELS_PER_METER, 140), new Vector2(this.screenWidth / 6
				/ PIXELS_PER_METER, 100), 6, camera);

		UserPlayer player7 = new UserPlayer(world, new Vector2(
				(this.screenWidth + 150) / 2 / PIXELS_PER_METER, 100),
				new Vector2((this.screenWidth + 150) / 2 / PIXELS_PER_METER,
						140), new Vector2((this.screenWidth + 150) / 2
						/ PIXELS_PER_METER, 100), 7, camera);

		UserPlayer player8 = new UserPlayer(world, new Vector2(
				(this.screenWidth - 150) / 2 / PIXELS_PER_METER, 100),
				new Vector2((this.screenWidth - 150) / 2 / PIXELS_PER_METER,
						140), new Vector2((this.screenWidth - 150) / 2
						/ PIXELS_PER_METER, 100), 8, camera);

		homeTeamPlayerMap.put(1, player1);
		homeTeamPlayerMap.put(2, player2);
		homeTeamPlayerMap.put(3, player3);
		homeTeamPlayerMap.put(4, player4);
		homeTeamPlayerMap.put(5, player5);
		homeTeamPlayerMap.put(6, player6);
		homeTeamPlayerMap.put(7, player7);
		homeTeamPlayerMap.put(8, player8);
	}

	// Since the ball is a sensor, it will not 'hit' the walls, so we need ti
	// change its velocity to 'bounce' back in if it comes to the line of the
	// wall. Also need to stop the ball if it passes over a player unless its
	// too high

	// add walls, length of the pitch is 1.5 times screen height
	private void addWalls() {
		new BoxProp(world, screenWidth / 10, 1,
				new Vector2(screenWidth / 20, 3), Line.endLine); // bottom
		new BoxProp(world, screenWidth / 10, 1, new Vector2(screenWidth / 20,
				screenHeight / 6), Line.endLine); // top
		new BoxProp(world, 1, screenHeight / 6, new Vector2(3,
				screenHeight / 12), Line.sideLine);// left
		new BoxProp(world, 1, screenHeight / 6, new Vector2(190,
				screenHeight / 12), Line.sideLine); // right
	}

	private void applyAITextures() {
		aiPlayerTexture1 = new Texture(Gdx.files.internal("playerRed1.png"));
		awayTeamTextureMap.put(1, new Sprite(aiPlayerTexture1));

		aiPlayerTexture2 = new Texture(Gdx.files.internal("playerRed2.png"));
		awayTeamTextureMap.put(2, new Sprite(aiPlayerTexture2));

		aiPlayerTexture3 = new Texture(Gdx.files.internal("playerRed3.png"));
		awayTeamTextureMap.put(3, new Sprite(aiPlayerTexture3));

		aiPlayerTexture4 = new Texture(Gdx.files.internal("playerRed4.png"));
		awayTeamTextureMap.put(4, new Sprite(aiPlayerTexture4));

		aiPlayerTexture5 = new Texture(Gdx.files.internal("playerRed5.png"));
		awayTeamTextureMap.put(5, new Sprite(aiPlayerTexture5));

		aiPlayerTexture6 = new Texture(Gdx.files.internal("playerRed6.png"));
		awayTeamTextureMap.put(6, new Sprite(aiPlayerTexture6));

		aiPlayerTexture7 = new Texture(Gdx.files.internal("playerRed7.png"));
		awayTeamTextureMap.put(7, new Sprite(aiPlayerTexture7));
	}

	private void applyPitchAndGoalTextures() {
		ballTexture = new Texture(Gdx.files.internal("ball.png"));
		textureMap.put(BALL, new Sprite(ballTexture));

		grassTexture = new Texture(Gdx.files.internal("grassMowed.png"));
		textureMap.put(GRASS, new Sprite(grassTexture));

		topGoalTexture = new Texture(Gdx.files.internal("top_goal.png"));
		textureMap.put(TOP_GOAL, new Sprite(topGoalTexture));

		btmGoalTexture = new Texture(Gdx.files.internal("btm_goal.png"));
		textureMap.put(BTM_GOAL, new Sprite(btmGoalTexture));
	}

	private void applyUserPlayerTextures() {
		homePlayerTexture1 = new Texture(
				Gdx.files.internal("playerBlueShirt1.png"));
		homeTeamTextureMap.put(1, new Sprite(homePlayerTexture1));

		homePlayerTexture2 = new Texture(
				Gdx.files.internal("playerBlueShirt2.png"));
		homeTeamTextureMap.put(2, new Sprite(homePlayerTexture2));

		homePlayerTexture3 = new Texture(
				Gdx.files.internal("playerBlueShirt3.png"));
		homeTeamTextureMap.put(3, new Sprite(homePlayerTexture3));

		homePlayerTexture4 = new Texture(
				Gdx.files.internal("playerBlueShirt4.png"));
		homeTeamTextureMap.put(4, new Sprite(homePlayerTexture4));

		homePlayerTexture5 = new Texture(
				Gdx.files.internal("playerBlueShirt5.png"));
		homeTeamTextureMap.put(5, new Sprite(homePlayerTexture5));

		homePlayerTexture6 = new Texture(
				Gdx.files.internal("playerBlueShirt6.png"));
		homeTeamTextureMap.put(6, new Sprite(homePlayerTexture6));

		homePlayerTexture7 = new Texture(
				Gdx.files.internal("playerBlueShirt7.png"));
		homeTeamTextureMap.put(7, new Sprite(homePlayerTexture7));
	}

	private void checkBallPosition() {
		Vector2 position = ball.body.getWorldCenter();

		// check if the ball is it the line of the walls
		if (position.x < 3 || position.x > 190) {
			// reverseBallDirection(true);
		} else if (position.y > screenHeight / 6 || position.y < 3) {
			if (position.x > center.x - 30 && position.x < center.x + 30) {
				goalScored();
			} else {
				// reverseBallDirection(false);
			}
		}
	}

	private void checkForBestAIPass() {
		// get the best pass for the opposition
		// if a player is further forward (ie. less in the y-direction)
		// try pass to him, unless there is a 'home' team player in the line of
		// the path, in which case pick the next best option
		AIPlayer bestOption = null;

		for (AIPlayer player : awayTeamPlayerMap.values()) {
			// i.e. further up the pitch than the
			// player with the ball
			if (player.body.getWorldCenter().y < playerWithBall.body
					.getWorldCenter().y) {
				bestOption = player;

				if (checkLineOfPassOK(bestOption)) {
					passBallToPlayer(bestOption);
					break;
				}
			}

			// TODO more logic here, if no passes available pass backwards
			// etc...
		}
		if (bestOption == null) {
			for (AIPlayer player : awayTeamPlayerMap.values()) {
				if (checkLineOfPassOK(player)) {
					passBallToPlayer(player);
				}
			}
		}
	}

	// check whether any of the home team players are in the line of the pass
	private boolean checkLineOfPassOK(AIPlayer bestOption) {
		boolean passOk = true;

		for (UserPlayer userPlayer : homeTeamPlayerMap.values()) {
			passOk = !Intersector.intersectSegmentCircle(
					ball.body.getPosition(), bestOption.body.getPosition(),
					userPlayer.body.getPosition(), 2);
		}

		return passOk;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	private Vector2 getAttackOrDefendingMovement(Player player,
			boolean attacking) {
		Vector2 movement;

		if (player.getHasBall()) {
			movement = new Vector2(0, 0);
		} else if (attacking && !player.getHasBall()) {
			movement = player.getMovemenOfPlayerTowardsTargetDest(player.body
					.getPosition(),
					player.isClosestPlayerToBall() ? ball.getPosition()
							: player.getAttackingPos());
		} else {
			movement = player.getMovemenOfPlayerTowardsTargetDest(player.body
					.getPosition(),
					player.isClosestPlayerToBall() ? ball.getPosition()
							: player.getDefendingPos());
		}

		return movement;
	}

	public Vector2 getNormailesedMovementDirection(Vector2 playerLocation,
			Vector2 interceptionPosition) {

		Vector2 temp = new Vector2(interceptionPosition.x - playerLocation.x,
				(interceptionPosition.y - playerLocation.y));

		int direcitonX = 1;
		int directionY = 1;

		if (temp.x < 1) {
			direcitonX = -1;
		}

		if (temp.y < 1) {
			directionY = -1;
		}

		float absX = Math.abs(temp.x);
		float absY = Math.abs(temp.y);

		if (absX > absY) {
			temp.x = direcitonX * 1;
			temp.y = directionY * (absY / absX);
		} else {

			temp.y = directionY * 1;
			temp.x = direcitonX * (absX / absY);

		}
		return temp;

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	public void moveCamera(float x, float y) {
		camera.position.set(x, y, 0);
		camera.update();
	}

	private void passBallToPlayer(AIPlayer bestOption) {

		Vector2 playerPos = bestOption.body.getWorldCenter();

		GameScreen.moveBall(new Vector2(100 * (playerPos.x - ball.body
				.getWorldCenter().x), 100 * (playerPos.y - ball.body
				.getWorldCenter().y)));
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(float delta) {
		GameScreen.delta += delta;

		Gdx.gl.glClearColor(0f, 0f, 0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		GaGa.delta = delta;

		camera.zoom = 2f;
		camera.update();
		// TODO: spriteBatch.setProjectionMatrix(camera.combined);

		world.step(Gdx.app.getGraphics().getDeltaTime(), 10, 10);
		// world.clearForces();
		// world.step(1/60f, 3, 3);
		world.clearForces();

		moveCamera(ball.body.getPosition().x * PIXELS_PER_METER,
				ball.body.getPosition().y * PIXELS_PER_METER);
		spriteBatch.setProjectionMatrix(camera.combined);

		this.spriteBatch.begin();

		this.spriteBatch.draw(textureMap.get(GRASS), 35, 35, 1850, 1750);
		this.spriteBatch.draw(textureMap.get(TOP_GOAL), 610, 1790, 750, 120);
		this.spriteBatch.draw(textureMap.get(BTM_GOAL), 580, -70, 750, 110);

		updateSprite(textureMap.get(BALL), this.spriteBatch, PIXELS_PER_METER,
				ball.body);

		updatePlayerPositions(new ArrayList<Player>(homeTeamPlayerMap.values()));
		updatePlayerPositions(new ArrayList<Player>(awayTeamPlayerMap.values()));

		updatePlayerSprites();

		checkBallPosition();
		if (!GameScreen.attacking && GameScreen.ballAtFeet
				&& GameScreen.delta > 1) {
			GameScreen.delta = 0;
			checkForBestAIPass();
		}
		this.spriteBatch.end();

		debugRenderer.render(world, camera.combined.scale(PIXELS_PER_METER,
				PIXELS_PER_METER, PIXELS_PER_METER));
	}

	private void resetClosePlayerBools(List<Player> listPlayers) {
		for (Player player : listPlayers) {
			player.setClosestPlayerToBall(false);
		}

	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	// go through list of players on the field. Set the player that is closest
	// to the ball
	// as he will be the only one drawn towards the ball when defending.
	// This method will be used for both usrr and ai players
	private void setClosestPlayerToBall(List<Player> listPlayers) {
		double minDist = 100000000; // some really high number so that at least
									// one player will definitely be less than
									// it

		for (Player player : listPlayers) {
			double distanceOfBallFromPlayer = ball
					.getDistanceFromPlayer(player.body);
			player.setDistFromBall(distanceOfBallFromPlayer);

			if (distanceOfBallFromPlayer < minDist) {
				minDist = distanceOfBallFromPlayer;

				// reset all 'IsClosestPlayerToBall' player values to false as
				// there is a 'new' closest player value
				resetClosePlayerBools(listPlayers);

				player.setClosestPlayerToBall(true);

			}
		}
	}

	private void setUpTextureMaps() {

		applyAITextures();
		applyUserPlayerTextures();

		applyPitchAndGoalTextures();
	}

	@Override
	public void show() {
		world = new World(new Vector2(0.0f, 0.0f), true);

		this.contactListener = new GameContactListener();

		world.setContactListener(contactListener);

		addWalls();
		addGoals();

		addHomeTeam();
		addAwayTeam();

		addBall();
	}

	private void updatePlayerPositions(List<Player> listPlayers) {
		// if attacking, players should run to attacking target positions, one
		// per player
		// if defending, closest player should run to ball, the rest should
		// return to target position for defending,,
		// which is their starting positions
		for (Player player : listPlayers) {
			Vector2 linearVel;

			// get closest player to ball
			setClosestPlayerToBall(listPlayers);

			if (!player.getHasBall()) {
				// pass in either the ball of the player target defending
				// position, based on the 'isClosestToBall' boolean
				linearVel = getAttackOrDefendingMovement(player, true);
			} else {
				// pass in either the ball or the player target defending
				// position, based on the 'isClosestToBall' boolean
				linearVel = getAttackOrDefendingMovement(player, false);
			}

			player.body.setLinearVelocity(
					UserPlayer.PLAYER_SPEED * linearVel.x,
					UserPlayer.PLAYER_SPEED * linearVel.y);
		}
	}

	private void updatePlayerSprites() {
		for (Entry<Integer, AIPlayer> player : awayTeamPlayerMap.entrySet()) {
			int playerNumber = player.getValue().getPlayerNumber();

			updateSprite(
					awayTeamTextureMap.get(playerNumber > 6 ? playerNumber - 6
							: playerNumber), this.spriteBatch,
					PIXELS_PER_METER, player.getValue().body);
		}

		for (Entry<Integer, UserPlayer> player : homeTeamPlayerMap.entrySet()) {
			int playerNumber = player.getValue().getPlayerNumber();

			updateSprite(
					homeTeamTextureMap.get(playerNumber > 7 ? playerNumber - 7
							: playerNumber), this.spriteBatch,
					PIXELS_PER_METER, player.getValue().body);
		}

	}
}