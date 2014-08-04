package com.ladinc.gaga.core.screens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

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
import com.ladinc.gaga.core.GaGa;
import com.ladinc.gaga.core.objects.AIPlayer;
import com.ladinc.gaga.core.objects.Ball;
import com.ladinc.gaga.core.objects.BoxProp;
import com.ladinc.gaga.core.objects.BoxProp.Line;
import com.ladinc.gaga.core.objects.Player;
import com.ladinc.gaga.core.objects.UserPlayer;

public class GameScreen implements Screen {
	private static final String AI_PLAYER = "AI_PLAYER";
	private static final String GRASS = "GRASS";
	private static final String BALL = "BALL";
	public static float AI_CREATION_RATE = 1;
	private final Box2DDebugRenderer debugRenderer;
	private final GaGa game;
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
	public static Map<Integer, Sprite> awayTeamTextureMap = new HashMap<Integer, Sprite>();

	Map<Integer, Vector2> positionVectorMap = new HashMap<Integer, Vector2>();

	// these maps will use the same key as the positionVectorMap
	public static Map<Integer, Vector2> defendingPositionsMap = new HashMap<Integer, Vector2>();
	public static Map<Integer, Vector2> attackingPositionsMap = new HashMap<Integer, Vector2>();

	private Texture ballTexture;
	private Texture grassTexture;

	private Texture aiPlayerTexture0;
	private Texture aiPlayerTexture1;
	private Texture aiPlayerTexture2;
	private Texture aiPlayerTexture3;
	private Texture aiPlayerTexture4;
	private Texture aiPlayerTexture5;
	private Texture aiPlayerTexture6;

	public static Ball ball;
	public static Map<Integer, UserPlayer> homeTeamPlayerMap = new HashMap<Integer, UserPlayer>();

	public static Map<Integer, AIPlayer> awayTeamPlayerMap = new HashMap<Integer, AIPlayer>();

	public static boolean attacking = false;
	public static boolean ballAtFeet;

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

	public static void updateSprite(Sprite sprite, SpriteBatch spriteBatch,
			int PIXELS_PER_METER, Body body) {
		if (sprite != null && spriteBatch != null && body != null) {
			setSpritePosition(sprite, PIXELS_PER_METER, body);
			sprite.draw(spriteBatch);
		}
	}

	private void setUpTextureMaps() {
		ballTexture = new Texture(Gdx.files.internal("ball.png"));
		textureMap.put(BALL, new Sprite(ballTexture));

		grassTexture = new Texture(Gdx.files.internal("grassMowed.png"));
		textureMap.put(GRASS, new Sprite(grassTexture));

		aiPlayerTexture0 = new Texture(Gdx.files.internal("playerRed0.png"));
		awayTeamTextureMap.put(0, new Sprite(aiPlayerTexture0));

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

		// spriteBatch.draw(textureMap.get(BALL), 400, 800);

		addAwayTeamSprites();

		updateSprite(textureMap.get(BALL), this.spriteBatch, PIXELS_PER_METER,
				ball.body);

		updateUserPlayerPositions(new ArrayList<Player>(
				homeTeamPlayerMap.values()));

		updateAIPlayerPositions();

		checkBallPosition();
		this.spriteBatch.end();

		debugRenderer.render(world, camera.combined.scale(PIXELS_PER_METER,
				PIXELS_PER_METER, PIXELS_PER_METER));
	}

	private void addAwayTeamSprites() {
		for (Entry<Integer, AIPlayer> aiPlayer : awayTeamPlayerMap.entrySet()) {
			// random number between 0 and 6
			int Low = 0;
			int High = 6;
			Random r = new Random();
			int rand = r.nextInt(High - Low) + Low;
			updateSprite(awayTeamTextureMap.get(rand), this.spriteBatch,
					PIXELS_PER_METER, aiPlayer.getValue().body);
		}
	}

	private void updateAIPlayerPositions() {
		// if attacking is set, then the ai team are actually defending
		ArrayList<Player> listAwayPlayers = new ArrayList<Player>(
				awayTeamPlayerMap.values());

		resetClosePlayerBools(listAwayPlayers);
		setClosestPlayerToBall(listAwayPlayers);
		Player closestAwayPlayer = getClosestPlayer(listAwayPlayers);

		if (attacking) {
			if (ballAtFeet) {
				// when defending, if the ball is at the attacker's players
				// feet,
				// the closest defender should go towards the ball

				// if the ball is moving, a defender should move to intercept it
				// TODO Which player should do this though??

				// closest defender should move to the ball
				Vector2 aiPLayerToBallVector = getAttackOrDefendingMovement(
						closestAwayPlayer, false);

				// TODO maybe have different values for Player.PLAYER_SPEED
				// depending on the difficulty of the away team
				closestAwayPlayer.body.setLinearVelocity(
						(Player.PLAYER_SPEED * aiPLayerToBallVector.x),
						(Player.PLAYER_SPEED * aiPLayerToBallVector.y));

			} else {
				// // Using formulae from
				// //
				// http://stackoverflow.com/questions/2248876/2d-game-fire-at-a-moving-target-by-predicting-intersection-of-projectile-and-u
				//
				// float xCompSq = ball.body.getLinearVelocity().x
				// * ball.body.getLinearVelocity().x;
				// float yCompSq = ball.body.getLinearVelocity().y
				// * ball.body.getLinearVelocity().y;
				//
				// //float ballSpeed = (float) Math.pow((xCompSq + yCompSq),
				// 0.5);
				//
				// float ballSpeedSq = UserPlayer.PLAYER_SPEED *
				// UserPlayer.PLAYER_SPEED;
				//
				// float a = (float) (xCompSq + yCompSq - ballSpeedSq);
				//
				// Gdx.app.debug("Disc", "a" + a);
				//
				// float b = 2
				// *
				// (ball.body.getLinearVelocity().x *
				// (ball.body.getWorldCenter().x -
				// closestAwayPlayer.body.getWorldCenter().x)
				// +
				// ball.body.getLinearVelocity().y *
				// (ball.body.getWorldCenter().y -
				// closestAwayPlayer.body.getWorldCenter().y));
				//
				// Gdx.app.debug("Disc", "b" + b);
				//
				// float c = (float) ((Math.pow((ball.body.getWorldCenter().x -
				// closestAwayPlayer.body.getWorldCenter().x), 2))
				// +
				// (Math.pow((ball.body.getWorldCenter().y -
				// closestAwayPlayer.body.getWorldCenter().y), 2)));
				//
				//
				// Gdx.app.debug("Disc", "c" + c);
				//
				// float disc = (float) Math.pow(b, 2) - 4 * a * c;
				//
				// Gdx.app.debug("Disc", "Disc" + disc);
				//
				// float t1 = (float) (-b + Math.pow(disc, 0.5)) / (2 * a);
				// float t2 = (float) (-b - Math.pow(disc, 0.5)) / (2 * a);
				//
				// float t = 0;
				//
				// if (t1 < t2 && t1 > 0) {
				// t = t1;
				// } else if (t2 < t1 && t2 > 0) {
				// t = t2;
				// }
				//
				// float aimX = (t * ball.body.getLinearVelocity().x)
				// + ball.body.getWorldCenter().x;
				// float aimY = (t * ball.body.getLinearVelocity().y)
				// + ball.body.getWorldCenter().y;
				//
				// Vector2 aiPlayerMovement = new Vector2(aimX, aimY);
				// closestAwayPlayer.body.setLinearVelocity(aiPlayerMovement);

				setPlayerMovement(closestAwayPlayer);
			}
		} else { // defending, but AITeam are attacking
		}
	}

	// adapted from here
	// http://www.box2d.org/forum/viewtopic.php?f=3&t=8833

	public void setPlayerMovement(Player closestAwayPlayer) {
		Vector2 ballPosition = this.ball.body.getWorldCenter();
		Vector2 playerPosition = closestAwayPlayer.body.getWorldCenter();

		Vector2 ballMovementDirection = this.ball.body.getLinearVelocity();

		float ballSpeed = this.ball.body.getLinearVelocity().len();
		float playerSpeed = new Vector2(Player.PLAYER_SPEED,
				Player.PLAYER_SPEED).len();

		Vector2 offsetToBall = new Vector2(ballPosition.x - playerPosition.x,
				ballPosition.y - playerPosition.y);

		float distanceFromPlayerToBallAtInterception = Math
				.abs(ballMovementDirection.crs(offsetToBall));
		float distanceBallTravelsToPassPlayer = -ballMovementDirection
				.dot(offsetToBall);

		float ballSpeerSqr = (float) Math.pow(ballSpeed, 2);
		float playerSpeerSqr = (float) Math.pow(playerSpeed, 2);

		float timeUntilBallPassesPlayer = ballSpeed == 0.0f ? 0.0f
				: distanceBallTravelsToPassPlayer / ballSpeed;

		Vector2 interceptionPosition = ballPosition.add(new Vector2(
				ballMovementDirection.x * distanceBallTravelsToPassPlayer,
				ballMovementDirection.y * distanceBallTravelsToPassPlayer));

		float a = ballSpeerSqr - playerSpeerSqr;

		float b = -2.0f
				* (ballSpeerSqr * timeUntilBallPassesPlayer + playerSpeed);

		float c = ballSpeerSqr * timeUntilBallPassesPlayer
				* timeUntilBallPassesPlayer
				+ distanceFromPlayerToBallAtInterception
				* distanceFromPlayerToBallAtInterception;

		float disc = (float) Math.pow(b, 2) - 4 * a * c;

		Gdx.app.debug("Disc", "Disc" + disc);

		float t1 = (float) (-b + Math.pow(disc, 0.5)) / (2 * a);
		float t2 = (float) (-b - Math.pow(disc, 0.5)) / (2 * a);

		float t = 0;

		if (t1 < t2 && t1 > 0) {
			t = t1;
		} else if (t2 < t1 && t2 > 0) {
			t = t2;
		}

		float aimX = (t * ball.body.getLinearVelocity().x)
				+ ball.body.getWorldCenter().x;
		float aimY = (t * ball.body.getLinearVelocity().y)
				+ ball.body.getWorldCenter().y;

		Vector2 interceptionPos = new Vector2(aimX, aimY);

		Vector2 interceptDirection = getNormailesedMovementDirection(
				playerPosition, interceptionPos);

		closestAwayPlayer.body.setLinearVelocity(new Vector2(
				interceptDirection.x * Player.PLAYER_SPEED,
				interceptDirection.y * Player.PLAYER_SPEED));

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

	private void updateUserPlayerPositions(List<Player> listPlayers) {
		// if attacking, players should run to attacking target positions, one
		// per player
		// if defending, closest player should run to ball, the rest should
		// return to target position for defending,,
		// which is their starting positions

		for (Player player : listPlayers) {
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

			player.body.setLinearVelocity(
					UserPlayer.PLAYER_SPEED * linearVel.x,
					UserPlayer.PLAYER_SPEED * linearVel.y);

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

	private Player getClosestPlayer(ArrayList<Player> playerList) {
		for (Player player : playerList) {
			if (player.isClosestPlayerToBall()) {

				return player;
			}
		}
		return null;
	}

	// go through list of players on the field. Set the player that is closest
	// to the ball
	// as he will be the only one drawn towards the ball when defending.
	// This method will be used for both usrr and ai players
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

				player.setClosestPlayerToBall(true);

			}
		}
	}

	private void resetClosePlayerBools(ArrayList<Player> playerList) {
		for (Player player : playerList) {
			player.setClosestPlayerToBall(false);
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

		UserPlayer player9 = new UserPlayer(world, new Vector2(
				((this.screenWidth - 200) / 2) * 2 / PIXELS_PER_METER, 100),
				new Vector2(((this.screenWidth - 200) / 2) * 2
						/ PIXELS_PER_METER, 140), new Vector2(
						((this.screenWidth - 200) / 2) * 2 / PIXELS_PER_METER,
						100), 9, camera);

		UserPlayer player10 = new UserPlayer(world, new Vector2(
				(this.screenWidth + 100) / 2 / PIXELS_PER_METER, 120),
				new Vector2((this.screenWidth + 100) / 2 / PIXELS_PER_METER,
						160), new Vector2((this.screenWidth + 100) / 2
						/ PIXELS_PER_METER, 120), 10, camera);

		UserPlayer player11 = new UserPlayer(world, new Vector2(
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
		ball = new Ball(world, 30, 30);
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