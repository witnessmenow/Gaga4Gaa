package com.ladinc.gaga.core.collision;

import com.ladinc.gaga.core.objects.Player;
import com.ladinc.gaga.core.utilities.GenericEnums.Side;

public class CollisionInfo {
	
	public String text;
	public CollisionObjectType type;
	
	public Player player;
	
	public Side side;
	
	
	public CollisionInfo(String text, CollisionObjectType type)
	{
		this.text = text;		
		this.type = type;
	}
	
	public CollisionInfo(String text, CollisionObjectType type, Side side)
	{
		this.text = text;		
		this.type = type;
		this.side = side;
	}
	
	public CollisionInfo(String text, CollisionObjectType type, Player player)
	{
		this.text = text;		
		this.type = type;
		this.player = player;
	}
	
	public static enum CollisionObjectType{Rink, Goal, Player, ScoreZone, Puck};

}

