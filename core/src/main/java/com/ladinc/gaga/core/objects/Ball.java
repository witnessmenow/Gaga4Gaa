package com.ladinc.gaga.core.objects;

import com.badlogic.gdx.Gdx;
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
import com.ladinc.gaga.core.objects.BoxProp.Line;
import com.ladinc.gaga.screens.GameScreen;

public class Ball {
	public Body body;
	public float ballSize = 2f;
	protected float density = 0.25f;
	protected float slowDownMultiplier = 0.75f;
	protected float linDamp = 1f;
	public static final float ballOffsetX = 0f;
	public Sprite sprite;

	private double ballHeight = 0f;

	public double getBallHeight() {
		return ballHeight;
	}

	public void setBallHeight(double ballHeight) {
		this.ballHeight = ballHeight;
	}

	public Ball(World world, float x, float y, Sprite ballSprite, float ballSize) {
		this.ballSize = ballSize;
		createBallObject(world, x, y, ballSprite, false);
	}

	public Ball(World world, float x, float y, Sprite ballSprite,
			float ballSize, float density, float linDamp) {
		this.density = density;
		this.ballSize = ballSize;
		this.linDamp = linDamp;
		createBallObject(world, x, y, ballSprite, false);
	}

	public Ball(World world, float x, float y, Sprite ballSprite) {
		createBallObject(world, x, y, ballSprite, false);
	}

	public Ball(World world, float x, float y, Sprite ballSprite,
			boolean networked) {

		createBallObject(world, x, y, ballSprite, networked);
	}

	public void playerHasBall(UserPlayer player) {
		this.body.setLinearVelocity(new Vector2(0, 0));

		// attacking will be set to true unless the ball touches an
		// opponent
		GameScreen.attacking = true;
		GameScreen.ballAtFeet = true;
	}

	public void reverseBallDirection(Line wall) {
		if (wall == Line.sideLine) {
			this.body.setLinearVelocity(-this.body.getLinearVelocity().x,
					this.body.getLinearVelocity().y);
		} else {
			this.body.setLinearVelocity(this.body.getLinearVelocity().x,
					-this.body.getLinearVelocity().y);
		}
	}

	protected void createBallObject(World world, float x, float y,
			Sprite ballSprite, boolean networked) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(x, y);
		this.body = world.createBody(bodyDef);
		this.body.setLinearDamping(0.3f);
		CircleShape dynamicCircle = new CircleShape();
		dynamicCircle.setRadius(ballSize);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = dynamicCircle;
		fixtureDef.density = density;
		fixtureDef.friction = 0f;
		fixtureDef.restitution = 1f;

		// we want the ball to be a sensor so it will be able to go over the
		// player's heads etc.
		fixtureDef.isSensor = true;

		this.body.createFixture(fixtureDef);
		this.sprite = ballSprite;

		this.body.setUserData(new CollisionInfo("ball",
				CollisionObjectType.Ball, this));

		dynamicCircle.dispose();

		// this.body.setLinearDamping(BallSpeedPicker.getSlowDown());
	}

	public Vector2 getLocalVelocity() {
		/*
		 * returns balls's velocity vector relative to the car
		 */
		return this.body.getLocalVector(this.body
				.getLinearVelocityFromLocalPoint(new Vector2(0, 0)));
	}

	public void update() {

		Vector2 currentVelocity = this.getLocalVelocity();
		Vector2 position = this.getLocation();

		Gdx.app.debug("Ball Update", "Ball Position - " + position
				+ "Ball Velocity - " + currentVelocity);
	}

	public void resetPositionToStart(Vector2 startPoint) {
		this.body.setTransform(startPoint, 0f);
		this.body.setLinearVelocity(0f, 0f);
		this.body.setAngularVelocity(0f);
	}

	public void networkUpdate(Vector2 velocity, Vector2 position) {
		this.body.setTransform(position, 0);
		// this.body.
	}

	public Vector2 getLocation() {
		return this.body.getWorldCenter();
	}

	public void updateSprite(SpriteBatch spriteBatch, int PIXELS_PER_METER) {
		// Art.updateSprite(this.sprite, spriteBatch, PIXELS_PER_METER,
		// this.body);
	}

	public Vector2 getPosition() {
		return this.body.getPosition();
	}

	// Get distance of a player from the ball, can use this to determine if the
	// player has control of the ball
	// Use the formula 'root((x1-x2)2 + (y1-y2)2)' //TODO Confirm this!!
	public double getDistanceFromPlayer(Body playerBody) {
		double xDist = ((playerBody.getPosition().x - this.body.getPosition().x) * (playerBody
				.getPosition().x - this.body.getPosition().x));
		double yDist = ((playerBody.getPosition().y - this.body.getPosition().y) * (playerBody
				.getPosition().y - this.body.getPosition().y));

		double dist = Math.sqrt(xDist + yDist);

		return dist;
	}
}