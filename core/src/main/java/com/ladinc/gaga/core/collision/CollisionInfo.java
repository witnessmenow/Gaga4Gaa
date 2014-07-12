package com.ladinc.gaga.core.collision;

public class CollisionInfo {

	public String text;
	public CollisionObjectType type;
	public Object object;

	public Object team;

	public CollisionInfo(String text, CollisionObjectType type) {
		this.text = text;
		this.type = type;
	}

	// public CollisionInfo(String text, CollisionObjectType type, Object side)
	// {
	// this.text = text;
	// this.type = type;
	// this.team = side;
	// }

	public CollisionInfo(String text, CollisionObjectType type, Object object) {
		this.text = text;
		this.type = type;
		this.object = object;
	}

	public static enum CollisionObjectType {
		Wall, Player, VehicleSensor, ScoreZone, Ball, Pocket, BallSensor, FloorSensor, AIPLayer
	};

}
