package com.ladinc.gaga.core.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.ladinc.gaga.core.collision.CollisionInfo;
import com.ladinc.gaga.core.collision.CollisionInfo.CollisionObjectType;
import com.ladinc.gaga.core.objects.AIPlayer;
import com.ladinc.gaga.core.objects.Ball;
import com.ladinc.gaga.core.objects.BoxProp;
import com.ladinc.gaga.core.objects.Player;
import com.ladinc.gaga.core.objects.UserPlayer;

public class GameContactListener implements ContactListener {

	public static boolean checkIfCollisionIsOfCertainBodies(
			CollisionInfo bodyAInfo, CollisionInfo bodyBInfo,
			CollisionObjectType type1, CollisionObjectType type2) {
		return (bodyAInfo.type == type1 && bodyBInfo.type == type2)
				|| (bodyAInfo.type == type2 && bodyBInfo.type == type1);
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

	@Override
	public void beginContact(Contact contact) {

		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();

		CollisionInfo bodyAInfo = getCollisionInfoFromFixture(fixtureA);
		CollisionInfo bodyBInfo = getCollisionInfoFromFixture(fixtureB);

		Ball ball;
		Player player;
		BoxProp wall;

		if (bodyAInfo != null && bodyBInfo != null) {

			Gdx.app.debug("beginContact",
					"between " + bodyAInfo.type.toString() + " and "
							+ bodyBInfo.type.toString());

			if (GameContactListener.checkIfCollisionIsOfCertainBodies(
					bodyAInfo, bodyBInfo, CollisionObjectType.UserPlayer,
					CollisionObjectType.Ball)
					|| GameContactListener.checkIfCollisionIsOfCertainBodies(
							bodyAInfo, bodyBInfo, CollisionObjectType.AIPlayer,
							CollisionObjectType.Ball)) {
				if (bodyAInfo.type == CollisionObjectType.Ball) {
					ball = (Ball) bodyAInfo.object;
					setAttackingOrDefending(bodyBInfo);
					player = (Player) bodyBInfo.object;
				} else {
					ball = (Ball) bodyBInfo.object;
					setAttackingOrDefending(bodyAInfo);
					player = (Player) bodyAInfo.object;
				}

				player.setHasBall(true);
				GameScreen.ballAtFeet = true;
				GameScreen.ball.body.setLinearVelocity(new Vector2(0, 0));
				GameScreen.delta = 0f;
				GameScreen.playerWithBall = player;

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

	@Override
	public void endContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();

		CollisionInfo bodyAInfo = getCollisionInfoFromFixture(fixtureA);
		CollisionInfo bodyBInfo = getCollisionInfoFromFixture(fixtureB);

		if (bodyAInfo != null && bodyBInfo != null) {

			Gdx.app.debug("endContact", "between " + bodyAInfo.type.toString()
					+ " and " + bodyBInfo.type.toString());

			if (checkIfCollisionIsOfCertainBodies(bodyAInfo, bodyBInfo,
					CollisionObjectType.UserPlayer, CollisionObjectType.Ball)
					|| GameContactListener.checkIfCollisionIsOfCertainBodies(
							bodyAInfo, bodyBInfo, CollisionObjectType.AIPlayer,
							CollisionObjectType.Ball)) {

				setHasBallAtFeetFalse(bodyAInfo, bodyBInfo);
				Gdx.app.log("endContact", "player kicked ball");
			}
		}

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

	private boolean passGoalLineX() {
		return GameScreen.ball.body.getWorldCenter().x > (GameScreen.center.x - 30)
				&& GameScreen.ball.body.getWorldCenter().x < (GameScreen.center.x + 30);
	}

	private boolean passGoalLineY() {
		return GameScreen.ball.body.getWorldCenter().y < (GameScreen.screenHeight / 6 + 4)
				&& GameScreen.ball.body.getWorldCenter().y > -1;
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub

	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub

	}

	private void setAttackingOrDefending(CollisionInfo bodyBInfo) {
		if (bodyBInfo.object.getClass() == UserPlayer.class)
			GameScreen.attacking = true;
		else if (bodyBInfo.object.getClass() == AIPlayer.class) {
			GameScreen.attacking = false;
		}
	}

	private void setHasBallAtFeetFalse(CollisionInfo bodyAInfo,
			CollisionInfo bodyBInfo) {
		if (bodyAInfo.object.getClass() == UserPlayer.class
				|| bodyAInfo.object.getClass() == AIPlayer.class) {
			Player player = (Player) bodyAInfo.object;
			Gdx.app.log("hasBall", "setting has ball to false");
			player.setHasBall(false);
		} else if (bodyBInfo.object.getClass() == UserPlayer.class
				|| bodyBInfo.object.getClass() == AIPlayer.class) {
			Player player = (Player) bodyAInfo.object;
			Gdx.app.log("hasBall", "setting has ball to false");
			player.setHasBall(false);
		}
	}
}
