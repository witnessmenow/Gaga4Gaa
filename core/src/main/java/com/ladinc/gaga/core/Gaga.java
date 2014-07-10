package com.ladinc.gaga.core;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.ladinc.gaga.controls.SimpleDirectionGestureDetector;
import com.ladinc.gaga.screens.GameScreen;

public class Gaga extends Game {
	public static float delta;

	private GameScreen gameScreen;

	public int screenWidth = 1920;
	public int screenHeight = 1080;

	@Override
	public void create() {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		Gdx.input.setInputProcessor(new SimpleDirectionGestureDetector(
				new SimpleDirectionGestureDetector.DirectionListener() {

					@Override
					public void onUp() {
						// TODO Auto-generated method stub
					}

					@Override
					public void onRight() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLeft() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onDown() {
						// TODO Auto-generated method stub

					}
				}));

		createScreens();
		setScreen(gameScreen);
	}

	private void createScreens() {
		this.gameScreen = new GameScreen(this);
	}
}
