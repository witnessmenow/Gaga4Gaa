package com.ladinc.gaga.core.objects;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.ladinc.gaga.core.collision.CollisionInfo;
import com.ladinc.gaga.core.collision.CollisionInfo.CollisionObjectType;

public class Player {

	public static final double PLAYER_HEIGHT = 10;

	float playerSize = 3f;

	private static int PIXELS_PER_METER = 10;

	public Sprite sprite;

	public Body body;
	private final World world;

	private final OrthographicCamera camera;

	public static float PLAYER_SPEED = 10;

	private final Vector2 leftMovement = new Vector2(0, 0);
	private boolean isClosestPlayerToBall = false;

	public double distFromBall;

	private boolean hasBall = false;

	private Vector2 attackingPos;
	private Vector2 defendingPos;

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

	public double getDistFromBall() {
		return distFromBall;
	}

	public void setDistFromBall(double d) {
		this.distFromBall = d;
	}

	public boolean getIsClosestPlayerToBall() {
		return isClosestPlayerToBall;
	}

	public void setIsClosestPlayerToBall(boolean closestPlayerToBall) {
		this.isClosestPlayerToBall = closestPlayerToBall;
	}

	public Player(World world, Vector2 startPos, Vector2 attackingPos,
			Vector2 defendingPos, OrthographicCamera camera) {

		this.world = world;
		this.camera = camera;

		this.attackingPos = attackingPos;
		this.defendingPos = defendingPos;

		createBody(startPos);

		this.sprite = Player.getPlayerSprite();
	}

	private void createBody(Vector2 startPos) {
		// Dynamic Body
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody; // TODO Should this be dynamic so
												// that it can 'hit' the ball

		bodyDef.position.set(startPos.x, startPos.y);

		// This keeps it that the force up is applied relative to the screen,
		// rather than the direction that the player is facing
		bodyDef.fixedRotation = true;
		this.body = world.createBody(bodyDef);

		CircleShape dynamicCircle = new CircleShape();
		dynamicCircle.setRadius(playerSize);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = 10.0f;
		fixtureDef.friction = 0.3f;
		fixtureDef.restitution = 0.5f;
		fixtureDef.shape = dynamicCircle;

		this.body.createFixture(fixtureDef);

		this.body.setUserData(new CollisionInfo("ball",
				CollisionObjectType.Player, this));

		// this.body.setUserData(new CollisionInfo("",
		// CollisionObjectType.Player, this));
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
		//
		// Vector2 temp = new Vector2(playerLocation.x - aiLocation.x,
		// (playerLocation.y - aiLocation.y));
		//
		// int direcitonX = 1;
		// int directionY = 1;
		//
		// if (temp.x < 0) {
		// direcitonX = -1;
		// }
		//
		// if (temp.y < 1) {
		// directionY = -1;
		// }
		//
		// float absX = Math.abs(temp.x);
		// float absY = Math.abs(temp.y);
		//
		// if (absX > absY) {
		// temp.x = direcitonX * 1;
		// temp.y = directionY * (absY / absX);
		// } else {
		//
		// temp.y = directionY * 1;
		// temp.x = direcitonX * (absX / absY);
		//
		// }
		// return temp;

	}

	public float normalizeFloat(float value, float limit) {
		if (value < 0) {
			return Math.max(value, -limit);
		} else {
			return Math.min(value, limit);
		}

	}
}