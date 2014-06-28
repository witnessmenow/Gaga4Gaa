package com.ladinc.gaga.core;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.ladinc.gaga.core.controls.MyControllerManager;
import com.ladinc.gaga.core.screens.Pitch;

public class Gaga extends Game {

	public MyControllerManager controllerManager;
	public int screenWidth = 1920;
    public int screenHeight = 1080;

    public Pitch pitch;
    
	@Override
	public void create () 
	{
		//Gdx.app.setLogLevel(Application.LOG_DEBUG);
		Gdx.app.setLogLevel(Application.LOG_INFO);
		
		controllerManager = new MyControllerManager();
		
		createScreens();
		
		setScreen(pitch);
	}
	
	private void createScreens()
	{
		this.pitch = new Pitch(this);

	}

}
