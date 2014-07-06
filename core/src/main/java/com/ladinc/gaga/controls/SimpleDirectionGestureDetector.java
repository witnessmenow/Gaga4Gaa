package com.ladinc.gaga.controls;

import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.ladinc.gaga.screens.GameScreen;

public class SimpleDirectionGestureDetector extends GestureDetector {
	public interface DirectionListener {
		void onLeft();

		void onRight();

		void onUp();

		void onDown();
	}

	public SimpleDirectionGestureDetector(DirectionListener directionListener) {
		super(new DirectionGestureListener(directionListener));
	}
	
	private static class DirectionGestureListener extends GestureAdapter{
		DirectionListener directionListener;
		
		public DirectionGestureListener(DirectionListener directionListener){
			this.directionListener = directionListener;
		}
		
		@Override
        public boolean fling(float velocityX, float velocityY, int button) {
			if(GameScreen.ballAtFeet)
			{
				if(Math.abs(velocityX)>Math.abs(velocityY)){
				if(velocityX>0){
						directionListener.onRight();
				}else{
						directionListener.onLeft();
				}
				}else{
					if(velocityY>0){
							directionListener.onDown();
					}else{                                  
							directionListener.onUp();
					}
				}
				
				GameScreen.moveBall(new Vector2(velocityX, -velocityY));
				return super.fling(velocityX, velocityY, button);
			}
			return true;
			
        }

	}

}