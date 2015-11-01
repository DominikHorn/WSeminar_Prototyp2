package com.jufo2015.entities;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;

public class Collectable
{
	public static final float SIZE = 10f;
	
	private Vector2f position;
	private Image sprite;

	public Collectable(float x, float y)
	{
		this.position = new Vector2f(x, y);
		try
		{
			this.sprite = new Image("assets/Collectable.png");
		}
		catch (SlickException e)
		{
			e.printStackTrace();
		}
	}

	public void render(GameContainer container, Graphics g) throws SlickException
	{
		sprite.draw(this.position.x, this.position.y, SIZE, SIZE);
	}

	public float getSize()
	{
		return SIZE;
	}
	
	public Vector2f getPosition()
	{
		return this.position;
	}
}
