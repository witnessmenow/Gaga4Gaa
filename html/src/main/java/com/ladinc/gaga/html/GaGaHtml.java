package com.ladinc.gaga.html;

import com.ladinc.gaga.core.GaGa;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

public class GaGaHtml extends GwtApplication {
	@Override
	public ApplicationListener getApplicationListener () {
		return new GaGa();
	}
	
	@Override
	public GwtApplicationConfiguration getConfig () {
		return new GwtApplicationConfiguration(480, 320);
	}
}
