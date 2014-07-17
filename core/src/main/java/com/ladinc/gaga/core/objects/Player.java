package com.ladinc.gaga.core.objects;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public abstract class Player {

	public static final double PLAYER_HEIGHT = 10;

	float playerSize = 3f;

	private static int PIXELS_PER_METER = 10;

	public Sprite sprite;

	public Body body;
	public World world;

	protected OrthographicCamera camera;

	private int playerNumber;

	public int getPlayerNumber() {
		return playerNumber;
	}

	public void setPlayerNumber(int playerNumber) {
		this.playerNumber = playerNumber;
	}

	public static float PLAYER_SPEED = 10;

	private final Vector2 leftMovement = new Vector2(0, 0);
	private boolean isClosestPlayerToBall = false;

	public void setClosestPlayerToBall(boolean isClosestPlayerToBall) {
		this.isClosestPlayerToBall = isClosestPlayerToBall;
	}

	public boolean isClosestPlayerToBall() {
		return isClosestPlayerToBall;
	}

	public double getDistFromBall() {
		return distFromBall;
	}

	public double distFromBall;

	private boolean hasBall = false;

	protected Vector2 attackingPos;
	protected Vector2 defendingPos;

	public Vector2 getAttackingPos() {
		return attackingPos;
	}

	public void setAttackingPos(Vector2 attackingPos) {
		this.attackingPos = attackingPos;
	}

	public Vector2 getDefendingPos() {
		return defendingPos;
	}

	public void setDefendingPos(Vector2 defendingPos) {
		this.defendingPos = defendingPos;
	}

	public boolean getHasBall() {
		return hasBall;
	}

	public void setHasBall(boolean hasBall) {
		this.hasBall = hasBall;
	}

	public void updateSprite(SpriteBatch spriteBatch) {
		setSpritePosition(sprite, PIXELS_PER_METER, body);
		sprite.draw(spriteBatch);
	}

	public void setSpritePosition(Sprite spr, int PIXELS_PER_METER,
			Body forLocation) {

		spr.setPosition(
				PIXELS_PER_METER * forLocation.getPosition().x - spr.getWidth()
						/ 2, PIXELS_PER_METER * forLocation.getPosition().y
						- spr.getHeight() / 2);
	}

	public static Sprite getPlayerSprite() {
		Texture playerTexture;
		return null;
	}

	// When attacking, this method will get the movement of the player towards
	// some target position on the pithc
	// When defending, this will either get the moveemnt of the player to the
	// ball if that player is the closest,
	// or it will get the movement towards the players target defending position
	public Vector2 getMovemenOfPlayerTowardsTargetDest(Vector2 aiLocation,
			Vector2 playerLocation) {

		Vector2 relativeVector = new Vector2();

		relativeVector.x = playerLocation.x - aiLocation.x;
		relativeVector.y = playerLocation.y - aiLocation.y;

		relativeVector.x = normalizeFloat(relativeVector.x, 1f);
		relativeVector.y = normalizeFloat(relativeVector.y, 1f);

		return relativeVector;
	}

	public float normalizeFloat(float value, float limit) {
		if (value < 0) {
			return Math.max(value, -limit);
		} else {
			return Math.min(value, limit);
		}

	}

	public void setDistFromBall(double d) {
		this.distFromBall = d;
	}

	public abstract void createBody(Vector2 startPos);
}
