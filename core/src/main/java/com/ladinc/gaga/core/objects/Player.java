package com.ladinc.gaga.core.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

public class Player {

	float playerSize = 3f;

	private static int PIXELS_PER_METER = 10;

	public Sprite sprite;

	public Body body;
	private World world;

	private OrthographicCamera camera;

	public Player(World world, Vector2 startPos,
			OrthographicCamera camera) {

		this.world = world;
		this.camera = camera;

		createBody(startPos);

		this.sprite = Player.getPlayerSprite();
	}
	
	private void createBody(Vector2 startPos) {
		// Dynamic Body
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;

		bodyDef.position
		.set(startPos.x, startPos.y);
		
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

		//this.body.setUserData(new CollisionInfo("", CollisionObjectType.Player, this));
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
}