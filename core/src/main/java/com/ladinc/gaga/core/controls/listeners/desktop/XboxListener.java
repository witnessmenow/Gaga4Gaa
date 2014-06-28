package com.ladinc.gaga.core.controls.listeners.desktop;

import com.ladinc.gaga.core.controls.listeners.GenericControllerListener;
import com.ladinc.gaga.core.controls.mapping.Xbox360WindowsMapper;

public class XboxListener  extends GenericControllerListener{
	
	public XboxListener()
	{
		this.DiveButton = Xbox360WindowsMapper.A_BUTTON;
		
		this.LeftAxisX = Xbox360WindowsMapper.LEFT_ANALOG_X;
		this.LeftAxisY = Xbox360WindowsMapper.LEFT_ANALOG_Y;
		
		//Fix this!
		this.RightAxisX = Xbox360WindowsMapper.RIGHT_ANALOG_X;
		this.RightAxisY= Xbox360WindowsMapper.RIGHT_ANALOG_Y;
	}

}
