package com.ladinc.gaga.core.objects;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.ladinc.gaga.core.collision.CollisionInfo;
import com.ladinc.gaga.core.collision.CollisionInfo.CollisionObjectType;

public class UserPlayer extends Player {

	public UserPlayer(World world, Vector2 startPos, Vector2 attackingPos,
			Vector2 defendingPos, int number, OrthographicCamera camera) {

		this.world = world;
		this.camera = camera;

		this.attackingPos = attackingPos;
		this.defendingPos = defendingPos;

		this.setPlayerNumber(number);

		createBody(startPos);

		this.sprite = UserPlayer.getPlayerSprite();
	}

	@Override
	public void createBody(Vector2 startPos) {
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

		this.body.setUserData(new CollisionInfo("player",
				CollisionObjectType.Player, this));
	}
}