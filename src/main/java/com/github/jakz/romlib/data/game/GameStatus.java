package com.github.jakz.romlib.data.game;

import java.awt.Color;

public enum GameStatus
{
	MISSING("Missing",new Color(195,0,0)),
	INCOMPLETE("Incomplete", new Color(255,179,0)/*new Color(220,66,0)*/),
	UNORGANIZED("Unorganized", new Color(0,150,0)/*new Color(255,179,0)*/),
	FOUND("Found",new Color(0,150,0));
	
	public final String name;
	public final Color color;
	
	GameStatus(String name, Color color)
	{
		this.name = name;
		this.color = color;
	}
	
	public boolean isComplete() { return this == FOUND || this == UNORGANIZED; }
}
