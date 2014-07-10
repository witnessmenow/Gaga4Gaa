package com.ladinc.gaga.core.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.ladinc.gaga.core.collision.CollisionInfo;
import com.ladinc.gaga.core.collision.CollisionInfo.CollisionObjectType;

public class BoxProp {
	public float width, height;
	public Body body;

	public static enum Line {
		sideLine, endLine
	};

	public Line line;

	public BoxProp(World world, float width, float height, Vector2 position,
			Line line) {
		super();
		this.width = width;
		this.height = height;

		// initialize body
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(position);
		bodyDef.angle = 0;
		bodyDef.fixedRotation = true;
		this.body = world.createBody(bodyDef);

		this.line = line;

		// initialize shape
		FixtureDef fixtureDef = new FixtureDef();
		PolygonShape boxShape = new PolygonShape();
		boxShape.setAsBox(this.width / 2, this.height / 2);
		fixtureDef.shape = boxShape;
		fixtureDef.restitution = 0.4f; // positively bouncy!
		this.body.createFixture(fixtureDef);

		this.body.setUserData(new CollisionInfo("ball",
				CollisionObjectType.Wall, this));

		// this.body.setUserData(new CollisionInfo("Wall",
		// CollisionObjectType.wall));

		boxShape.dispose();
	}
}