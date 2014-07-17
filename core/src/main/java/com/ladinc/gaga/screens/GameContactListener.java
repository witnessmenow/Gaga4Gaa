package com.ladinc.gaga.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.ladinc.gaga.core.collision.CollisionInfo;
import com.ladinc.gaga.core.collision.CollisionInfo.CollisionObjectType;
import com.ladinc.gaga.core.objects.Ball;
import com.ladinc.gaga.core.objects.BoxProp;
import com.ladinc.gaga.core.objects.UserPlayer;

public class GameContactListener implements ContactListener {

	@Override
	public void beginContact(Contact contact) {

		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();

		CollisionInfo bodyAInfo = getCollisionInfoFromFixture(fixtureA);
		CollisionInfo bodyBInfo = getCollisionInfoFromFixture(fixtureB);

		Ball ball;
		UserPlayer player;
		BoxProp wall;

		if (bodyAInfo != null && bodyBInfo != null) {

			Gdx.app.debug("beginContact",
					"between " + bodyAInfo.type.toString() + " and "
							+ bodyBInfo.type.toString());

			if (GameContactListener.checkIfCollisionIsOfCertainBodies(
					bodyAInfo, bodyBInfo, CollisionObjectType.Player,
					CollisionObjectType.Ball)) {
				if (bodyAInfo.type == CollisionObjectType.Ball) {
					ball = (Ball) bodyAInfo.object;
					player = (UserPlayer) bodyBInfo.object;
				} else {
					ball = (Ball) bodyBInfo.object;
					player = (UserPlayer) bodyAInfo.object;
				}

				ball.playerHasBall(player);

				Gdx.app.log("beginContact", "player got ball");

			} else if (GameContactListener.checkIfCollisionIsOfCertainBodies(
					bodyAInfo, bodyBInfo, CollisionObjectType.Wall,
					CollisionObjectType.Ball)) {

				if (bodyAInfo.type == CollisionObjectType.Ball) {
					ball = (Ball) bodyAInfo.object;
					wall = (BoxProp) bodyBInfo.object;
				} else {
					ball = (Ball) bodyBInfo.object;
					wall = (BoxProp) bodyAInfo.object;
				}

				if (this.ballPassingGoalLine()) {
					GameScreen.goalScored();
				} else {
					ball.reverseBallDirection(wall.line);
				}
			}
		}

	}

	// TODO Need to use collision detection instead of co-ordinates to check if
	// ball is passing goalline
	private boolean ballPassingGoalLine() {
		boolean goalScored = false;
		if (passGoalLineX() && passGoalLineY()) {
			goalScored = true;
		}
		return goalScored;
	}

	private boolean passGoalLineY() {
		return GameScreen.ball.body.getWorldCenter().y < (GameScreen.screenHeight / 6 + 4)
				&& GameScreen.ball.body.getWorldCenter().y > -1;
	}

	private boolean passGoalLineX() {
		return GameScreen.ball.body.getWorldCenter().x > (GameScreen.center.x - 30)
				&& GameScreen.ball.body.getWorldCenter().x < (GameScreen.center.x + 30);
	}

	@Override
	public void endContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();

		CollisionInfo bodyAInfo = getCollisionInfoFromFixture(fixtureA);
		CollisionInfo bodyBInfo = getCollisionInfoFromFixture(fixtureB);

		Ball ball;
		UserPlayer player;

		if (bodyAInfo != null && bodyBInfo != null) {

			Gdx.app.debug("endContact", "between " + bodyAInfo.type.toString()
					+ " and " + bodyBInfo.type.toString());

			if (GameContactListener.checkIfCollisionIsOfCertainBodies(
					bodyAInfo, bodyBInfo, CollisionObjectType.Player,
					CollisionObjectType.Ball)) {

				GameScreen.ballAtFeet = false;

				Gdx.app.log("endContact", "player kicked ball");
			}
		}

	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub

	}

	private CollisionInfo getCollisionInfoFromFixture(Fixture fix) {
		CollisionInfo colInfo = null;

		if (fix != null) {
			Body body = fix.getBody();

			if (body != null) {
				colInfo = (CollisionInfo) body.getUserData();
			}
		}

		return colInfo;
	}

	public static boolean checkIfCollisionIsOfCertainBodies(
			CollisionInfo bodyAInfo, CollisionInfo bodyBInfo,
			CollisionObjectType type1, CollisionObjectType type2) {
		return (bodyAInfo.type == type1 && bodyBInfo.type == type2)
				|| (bodyAInfo.type == type2 && bodyBInfo.type == type1);
	}

}
