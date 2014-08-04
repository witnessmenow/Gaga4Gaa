package com.ladinc.gaga.core.controls;

import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.ladinc.gaga.core.screens.GameScreen;

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

	private static class DirectionGestureListener extends GestureAdapter {
		DirectionListener directionListener;

		public DirectionGestureListener(DirectionListener directionListener) {
			this.directionListener = directionListener;
		}

		@Override
		public boolean fling(float velocityX, float velocityY, int button) {
			if (GameScreen.attacking && GameScreen.ballAtFeet) {

				GameScreen
						.moveBall(new Vector2(velocityX * 10, -velocityY * 10));
				return super.fling(velocityX, velocityY, button);
			}
			return true;

		}

	}

}