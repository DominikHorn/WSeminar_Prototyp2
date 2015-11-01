package com.jufo2015.entities;

//import java.util.Random;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.*;

import com.jufo2015.main.SimulationController;
import com.jufo2015.neuronal.NeuronalNetwork;

public class Entity
{
	private final float FORWARD_SPEED = 3f;
	private final float TURN_SPEED = 4f;
	private final float SIZE = 10f;

	private SimulationController controller;
	private NeuronalNetwork brain;

	private Image sprite;
	private Vector2f position;
	private Vector2f lookAtVector;

	private float angle;
	private int collectedScore;
	// private int stepCount = 0;
	private boolean isCurrentBestEntity;

	// private boolean collided;

	public Entity(SimulationController controller, float x, float y, float angle, int[] numHiddenLayerNeurons)
	{
		// Random random = new Random();

		this.controller = controller;
		this.brain = new NeuronalNetwork(1, 2, numHiddenLayerNeurons.length, numHiddenLayerNeurons[0]);
		this.position = new Vector2f(x, y);
		this.angle = angle;
		this.lookAtVector = new Vector2f(0, 0);
		this.collectedScore = 0;
		this.isCurrentBestEntity = false;
		// this.collided = false;

		try
		{
			this.sprite = new Image("assets/Entity.png");
		}
		catch (SlickException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * create and initialize from parent entity.
	 * 
	 * @param controller
	 * @param parent
	 * @param x
	 * @param y
	 * @param angle2 
	 */
	public Entity(SimulationController controller, Entity parent, float mutationChance, float x, float y, float angle)
	{
		this.controller = controller;
		this.brain = parent.brain.evolve(mutationChance);
		this.position = new Vector2f(x, y);

		// Random random = new Random();
		this.angle = angle;
		this.lookAtVector = new Vector2f(0, 0);
		this.collectedScore = 0;
		this.isCurrentBestEntity = false;
		// this.collided = false;

		try
		{
			this.sprite = new Image("assets/Entity.png");
		}
		catch (SlickException e)
		{
			e.printStackTrace();
		}
	}

	public void increaseCollectedScore()
	{
		this.collectedScore++;
	}

	public int getCollectedScore()
	{
		return this.collectedScore;
	}

	public void setCollectedScore(int newScore)
	{
		this.collectedScore = newScore;
	}

	public Vector2f getPosition()
	{
		return this.position;
	}

	public void setPosition(float x, float y)
	{
		this.position.x = x;
		this.position.y = y;
	}

	public float getSize()
	{
		return this.SIZE;
	}

	public void setIsCurrentBestEntity(boolean value)
	{
		this.isCurrentBestEntity = value;
	}

	// public void setCollided(boolean value)
	// {
	// this.collided = value;
	// }

	public void render(GameContainer container, Graphics g) throws SlickException
	{
		// render sprite at position "position" with rotation based on speed
		g.pushTransform();
		g.rotate(this.position.x + SIZE / 2, this.position.y + SIZE / 2, this.angle);

		sprite.draw(this.position.x, this.position.y, SIZE, SIZE);
		g.popTransform();

		if (SimulationController.DEBUG_MODE)
		{
			g.setColor(Color.green);
			g.drawOval(this.position.x + SIZE / 2, this.position.y + SIZE / 2, 3, 3);
			g.setColor(Color.blue);
			g.drawRect(this.position.x, this.position.y, SIZE, SIZE);
		}

		// Render red oval around entity
		if (this.isCurrentBestEntity)
		{
			g.setColor(Color.green);
			g.setLineWidth(3);
			g.drawOval(this.position.x, this.position.y, this.SIZE, this.SIZE);

			Vector2f closestCollectable = this.controller.getClosestCollectableVector(this);
			g.setColor(Color.red);
			g.setLineWidth(1);
			lookAtVector.normalise();
			g.drawLine(this.position.x + this.SIZE / 2, this.position.y + this.SIZE / 2, this.position.x + (this.lookAtVector.x * 100f), this.position.y + (this.lookAtVector.y * 100f));

			closestCollectable.x += Collectable.SIZE / 2;
			closestCollectable.y += Collectable.SIZE / 2;
			g.setColor(Color.blue);
			g.drawLine(this.position.x + this.SIZE / 2, this.position.y + this.SIZE / 2, this.position.x + (closestCollectable.x), this.position.y + (closestCollectable.y));
		}

	}

	public void update(GameContainer container, int delta) throws SlickException
	{
		// /* Simulate brain step every 2 ticks */
		// if (stepCount % 2 == 0)
		// {
		// Set inputs for brain
		Vector2f closestCollectable = this.controller.getClosestCollectableVector(this);
		closestCollectable.x += Collectable.SIZE / 2f;
		closestCollectable.y += Collectable.SIZE / 2f;

		closestCollectable.normalise();
		lookAtVector.normalise();

//		 lookAtVector.x = (lookAtVector.x / 2.0f) + 0.5f;
//		 lookAtVector.y = (lookAtVector.y / 2.0f) + 0.5f;
//		 closestCollectable.x = (closestCollectable.x / 2.0f) + 0.5f;
//		 closestCollectable.y = (closestCollectable.y / 2.0f) + 0.5f;

		this.brain.setInput(0, (double) lookAtVector.dot(closestCollectable));
//		this.brain.setInput(0, (double) closestCollectable.x);
//		this.brain.setInput(1, (double) closestCollectable.y);
//		this.brain.setInput(2, (double) lookAtVector.x);
//		this.brain.setInput(3, (double) lookAtVector.y);
		
//		System.out.println("Inputs: (0) " + closestCollectable.x + "; (1) " + closestCollectable.y + "; (2) " + lookAtVector.x + "; (3) " + lookAtVector.y);

//		 this.brain.setInput(0, (double) this.angle / 180f);
//		 this.brain.setInput(1, (double) Math.atan2(closestCollectable.y, closestCollectable.x) / Math.PI);

		// this.brain.setInput(1, (double) Math.acos(closestCollectable.x / Math.sqrt(closestCollectable.x * closestCollectable.x + closestCollectable.y * closestCollectable.y)) / 180f);
		// System.out.println("; " + Math.atan2(closestCollectable.y, closestCollectable.x) / Math.PI);

		// Simulate brain
		this.brain.simulateStep();

		// Update wheel speed based on brain output
		// float leftWheelSpeed = (float) (brain.getOutput(0) - 0.5f) * 2.0f;
		// float rightWheelSpeed = (float) (brain.getOutput(1) - 0.5f) * 2.0f;
		float leftWheelSpeed = (float) brain.getOutput(0);
		float rightWheelSpeed = (float) brain.getOutput(1);
//		System.out.println("Outputs: (0) " + leftWheelSpeed + "; (1) " + rightWheelSpeed + "\n");
		
		// Calculate angle based on wheel speeds
		this.angle += (leftWheelSpeed - rightWheelSpeed) * TURN_SPEED;
		// float brainOutput = (float) this.brain.getOutput(0);
		// float turnAmount = brainOutput * TURN_SPEED;
		// this.angle += turnAmount;
		if (angle > 180f)
		{
			angle = -180f;
		}
		else if (angle < -180f)
		{
			angle = 180f;
		}

		// Calculate speed based on angle
		this.lookAtVector.x = (float) Math.cos(this.angle * Math.PI / 180.0);
		this.lookAtVector.y = (float) Math.sin(this.angle * Math.PI / 180.0);
		// }
		// stepCount++;

		// Update position based on speed
		this.position.x += (this.lookAtVector.x / 100.0f) * delta * FORWARD_SPEED;
		this.position.y += (this.lookAtVector.y / 100.0f) * delta * FORWARD_SPEED;
	}

	public void enableDebug()
	{
		this.isCurrentBestEntity = true;
	}

	public void disableDebug()
	{
		this.isCurrentBestEntity = false;
	}

	public void setAngle(float angle)
	{
		this.angle = angle;
	}
}
