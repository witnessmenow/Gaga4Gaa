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
import com.ladinc.gaga.core.objects.AIPlayer;
import com.ladinc.gaga.core.objects.Ball;
import com.ladinc.gaga.core.objects.BoxProp;
import com.ladinc.gaga.core.objects.BoxProp.Line;
import com.ladinc.gaga.core.objects.Player;

public class GameScreen implements Screen {
	private static final String BALL = "BALL";
	public static float AI_CREATION_RATE = 1;
	private final Box2DDebugRenderer debugRenderer;
	private final Gaga game;
	// Used for sprites etc
	private final int screenWidth;
	public static int screenHeight;

	private GameContactListener contactListener;

	// Used for Box2D
	private final float worldWidth;
	private final float worldHeight;
	private static int PIXELS_PER_METER = 10;

	public static Vector2 center = new Vector2();
	private final OrthographicCamera camera;

	private final SpriteBatch spriteBatch;
	private World world;

	public static Map<String, Sprite> textureMap = new HashMap<String, Sprite>();

	Map<Integer, Vector2> positionVectorMap = new HashMap<Integer, Vector2>();

	// these maps will use the same key as the positionVectorMap
	public static Map<Integer, Vector2> defendingPositionsMap = new HashMap<Integer, Vector2>();
	public static Map<Integer, Vector2> attackingPositionsMap = new HashMap<Integer, Vector2>();

	private Texture ballTexture;
	public static Ball ball;
	public static Map<Integer, Player> homeTeamPlayerMap = new HashMap<Integer, Player>();

	public static Map<Integer, AIPlayer> awayTeamPlayerMap = new HashMap<Integer, AIPlayer>();

	public static boolean attacking = false;
	public static boolean ballAtFeet;

	public GameScreen(Gaga game) {
		this.game = game;

		this.screenWidth = this.game.screenWidth;
		screenHeight = this.game.screenHeight;

		this.worldHeight = screenHeight / PIXELS_PER_METER;
		this.worldWidth = this.screenWidth / PIXELS_PER_METER;

		center = new Vector2(worldWidth / 2, worldHeight / 2);

		this.camera = new OrthographicCamera();
		this.camera.setToOrtho(false, this.screenWidth, screenHeight);

		spriteBatch = new SpriteBatch();

		setUpTextureMap();

		this.debugRenderer = new Box2DDebugRenderer();
	}

	public static void updateSprite(Sprite sprite, SpriteBatch spriteBatch,
			int PIXELS_PER_METER, Body body) {
		if (sprite != null && spriteBatch != null && body != null) {
			setSpritePosition(sprite, PIXELS_PER_METER, body);
			sprite.draw(spriteBatch);
		}
	}

	private void setUpTextureMap() {
		ballTexture = new Texture(Gdx.files.internal("ball.png"));
		textureMap.put(BALL, new Sprite(ballTexture));
	}

	public static void setSpritePosition(Sprite sprite, int PIXELS_PER_METER,
			Body body) {

		sprite.setPosition(
				PIXELS_PER_METER * body.getWorldCenter().x - sprite.getWidth()
						/ 2, PIXELS_PER_METER * body.getWorldCenter().y
						- sprite.getHeight() / 2);

		sprite.setRotation((MathUtils.radiansToDegrees * body.getAngle()));
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		Gaga.delta = delta;

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

		// updateSprite(textureMap.get(BALL), this.spriteBatch,
		// PIXELS_PER_METER, ball.body);
		updatePlayerPositions();

		checkBallPosition();
		this.spriteBatch.end();

		debugRenderer.render(world, camera.combined.scale(PIXELS_PER_METER,
				PIXELS_PER_METER, PIXELS_PER_METER));
	}

	public void moveCamera(float x, float y) {
		camera.position.set(x, y, 0);
		camera.update();
	}

	// Since the ball is a sensor, it will not 'hit' the walls, so we need ti
	// change its velocity to 'bounce' back in if it comes to the line of the
	// wall. Also need to stop the ball if it passes over a player unless its
	// too high

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

	static void goalScored() {
		System.out.println("Goal Scored");
	}

	private void updatePlayerPositions() {
		// if attacking, players should run to attacking target positions, one
		// per player
		// if defending, closest player should run to ball, the rest should
		// return to target position for defending,,
		// which is their starting positions

		for (Entry<Integer, Player> entry : homeTeamPlayerMap.entrySet()) {
			Player player = entry.getValue();
			Vector2 linearVel;

			// get closest player to ball
			setClosestPlayerToBall(new ArrayList<Player>(
					homeTeamPlayerMap.values()));

			// TODO: Should players move while they have the ball?
			if (attacking && !player.getHasBall()) {
				// pass in either the ball of the player target defending
				// position, based on the 'isClosestToBall' boolean
				linearVel = getAttackOrDefendingMovement(player, true);
			} else {

				// pass in either the ball of the player target defending
				// position, based on the 'isClosestToBall' boolean
				linearVel = getAttackOrDefendingMovement(player, false);
			}

			player.body.setLinearVelocity(Player.PLAYER_SPEED * linearVel.x,
					Player.PLAYER_SPEED * linearVel.y);

			// player.body.setLinearVelocity(Player.PLAYER_SPEED,
			// Player.PLAYER_SPEED);
		}
	}

	private Vector2 getAttackOrDefendingMovement(Player player,
			boolean attacking) {
		Vector2 movement;

		if (attacking) {
			movement = player.getMovemenOfPlayerTowardsTargetDest(player.body
					.getPosition(),
					player.getIsClosestPlayerToBall() ? ball.getPosition()
							: player.getAttackingPos());
		} else {
			movement = player.getMovemenOfPlayerTowardsTargetDest(player.body
					.getPosition(),
					player.getIsClosestPlayerToBall() ? ball.getPosition()
							: player.getDefendingPos());
		}

		return movement;
	}

	// go through list of players on the field. Set the player that is closest
	// to the ball
	// as he will be the only one drawn towards the ball when defending
	private void setClosestPlayerToBall(ArrayList<Player> playerList) {
		double minDist = 100000000; // some really high number so that at least
									// one player will definitely be less than
									// it

		for (Player player : playerList) {
			double distanceOfBallFromPlayer = ball
					.getDistanceFromPlayer(player.body);
			player.setDistFromBall(distanceOfBallFromPlayer);

			if (distanceOfBallFromPlayer < minDist) {
				minDist = distanceOfBallFromPlayer;

				// reset all 'IsClosestPlayerToBall' player values to false as
				// there is a 'new' closest player value
				resetClosePlayerBools(playerList);

				player.setIsClosestPlayerToBall(true);

			}
		}
	}

	private void resetClosePlayerBools(ArrayList<Player> playerList) {
		for (Player player : playerList) {
			player.setIsClosestPlayerToBall(false);
		}

	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	// TODO Starting positions and defending positions are the same for the
	// moment
	private void addHomeTeam() {

		// create a full team of players here. use starting positions using
		// map and player number as the index
		Player player1 = new Player(world, new Vector2(this.screenWidth / 2
				/ PIXELS_PER_METER, 15), new Vector2(this.screenWidth / 2
				/ PIXELS_PER_METER, 20), new Vector2(this.screenWidth / 2
				/ PIXELS_PER_METER, 15), 1, camera);

		Player player2 = new Player(world, new Vector2(this.screenWidth / 6
				/ PIXELS_PER_METER, 30), new Vector2(this.screenWidth / 6
				/ PIXELS_PER_METER, 70), new Vector2(this.screenWidth / 6
				/ PIXELS_PER_METER, 30), 2, camera);

		Player player3 = new Player(
				world,
				new Vector2((this.screenWidth + 150) / 2 / PIXELS_PER_METER, 30),
				new Vector2((this.screenWidth + 150) / 2 / PIXELS_PER_METER, 70),
				new Vector2((this.screenWidth + 150) / 2 / PIXELS_PER_METER, 30),
				3, camera);

		Player player4 = new Player(
				world,
				new Vector2((this.screenWidth - 150) / 2 / PIXELS_PER_METER, 30),
				new Vector2((this.screenWidth - 150) / 2 / PIXELS_PER_METER, 70),
				new Vector2((this.screenWidth - 150) / 2 / PIXELS_PER_METER, 30),
				4, camera);

		Player player5 = new Player(world, new Vector2(
				((this.screenWidth - 200) / 2) * 2 / PIXELS_PER_METER, 30),
				new Vector2(((this.screenWidth - 200) / 2) * 2
						/ PIXELS_PER_METER, 70), new Vector2(
						((this.screenWidth - 200) / 2) * 2 / PIXELS_PER_METER,
						30), 5, camera);

		Player player6 = new Player(world, new Vector2(this.screenWidth / 6
				/ PIXELS_PER_METER, 100), new Vector2(this.screenWidth / 6
				/ PIXELS_PER_METER, 140), new Vector2(this.screenWidth / 6
				/ PIXELS_PER_METER, 100), 6, camera);

		Player player7 = new Player(world, new Vector2((this.screenWidth + 150)
				/ 2 / PIXELS_PER_METER, 100), new Vector2(
				(this.screenWidth + 150) / 2 / PIXELS_PER_METER, 140),
				new Vector2((this.screenWidth + 150) / 2 / PIXELS_PER_METER,
						100), 7, camera);

		Player player8 = new Player(world, new Vector2((this.screenWidth - 150)
				/ 2 / PIXELS_PER_METER, 100), new Vector2(
				(this.screenWidth - 150) / 2 / PIXELS_PER_METER, 140),
				new Vector2((this.screenWidth - 150) / 2 / PIXELS_PER_METER,
						100), 8, camera);

		Player player9 = new Player(world, new Vector2(
				((this.screenWidth - 200) / 2) * 2 / PIXELS_PER_METER, 100),
				new Vector2(((this.screenWidth - 200) / 2) * 2
						/ PIXELS_PER_METER, 140), new Vector2(
						((this.screenWidth - 200) / 2) * 2 / PIXELS_PER_METER,
						100), 9, camera);

		Player player10 = new Player(world, new Vector2(
				(this.screenWidth + 100) / 2 / PIXELS_PER_METER, 120),
				new Vector2((this.screenWidth + 100) / 2 / PIXELS_PER_METER,
						160), new Vector2((this.screenWidth + 100) / 2
						/ PIXELS_PER_METER, 120), 10, camera);

		Player player11 = new Player(world, new Vector2(
				(this.screenWidth - 100) / 2 / PIXELS_PER_METER, 120),
				new Vector2((this.screenWidth - 100) / 2 / PIXELS_PER_METER,
						160), new Vector2((this.screenWidth - 100) / 2
						/ PIXELS_PER_METER, 120), 11, camera);

		homeTeamPlayerMap.put(1, player1);
		homeTeamPlayerMap.put(2, player2);
		homeTeamPlayerMap.put(3, player3);
		homeTeamPlayerMap.put(4, player4);
		homeTeamPlayerMap.put(5, player5);
		homeTeamPlayerMap.put(6, player6);
		homeTeamPlayerMap.put(7, player7);
		homeTeamPlayerMap.put(8, player8);
		homeTeamPlayerMap.put(9, player9);
		homeTeamPlayerMap.put(10, player10);
		homeTeamPlayerMap.put(11, player11);
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

		AIPlayer player9 = new AIPlayer(world, new Vector2(
				((this.screenWidth - 200) / 2) * 2 / PIXELS_PER_METER, 100),
				new Vector2(((this.screenWidth - 200) / 2) * 2
						/ PIXELS_PER_METER, 100), new Vector2(
						((this.screenWidth - 200) / 2) * 2 / PIXELS_PER_METER,
						100), 9, camera);

		AIPlayer player10 = new AIPlayer(
				world,
				new Vector2((this.screenWidth + 100) / 2 / PIXELS_PER_METER, 80),
				new Vector2((this.screenWidth + 100) / 2 / PIXELS_PER_METER, 80),
				new Vector2((this.screenWidth + 100) / 2 / PIXELS_PER_METER, 80),
				10, camera);

		AIPlayer player11 = new AIPlayer(
				world,
				new Vector2((this.screenWidth - 100) / 2 / PIXELS_PER_METER, 80),
				new Vector2((this.screenWidth - 100) / 2 / PIXELS_PER_METER, 80),
				new Vector2((this.screenWidth - 100) / 2 / PIXELS_PER_METER, 80),
				11, camera);

		awayTeamPlayerMap.put(1, player1);
		awayTeamPlayerMap.put(2, player2);
		awayTeamPlayerMap.put(3, player3);
		awayTeamPlayerMap.put(4, player4);
		awayTeamPlayerMap.put(5, player5);
		awayTeamPlayerMap.put(6, player6);
		awayTeamPlayerMap.put(7, player7);
		awayTeamPlayerMap.put(8, player8);
		awayTeamPlayerMap.put(9, player9);
		awayTeamPlayerMap.put(10, player10);
		awayTeamPlayerMap.put(11, player11);

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

	private void addBall() {
		ball = new Ball(world, 30, 30, new Sprite(textureMap.get(BALL)));
	}

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

	public static void moveBall(Vector2 vector2) {
		ball.body.applyForce(vector2, ball.body.getWorldCenter(), true);

	}

}