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
import com.ladinc.gaga.core.objects.Player;

public class GameContactListener implements ContactListener {

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
					bodyAInfo, bodyBInfo, CollisionObjectType.Player,
					CollisionObjectType.Ball)) {
				if (bodyAInfo.type == CollisionObjectType.Ball) {
					ball = (Ball) bodyAInfo.object;
					player = (Player) bodyBInfo.object;
				} else {
					ball = (Ball) bodyBInfo.object;
					player = (Player) bodyAInfo.object;
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

				ball.reverseBallDirection(wall.line);
				Gdx.app.log("beginContact", "player got ball");
			}
		}

	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub

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
