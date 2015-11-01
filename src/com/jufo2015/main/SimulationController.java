package com.jufo2015.main;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import com.jufo2015.entities.Collectable;
import com.jufo2015.entities.Entity;
import com.jufo2015.gui.ParameterWindow;
import com.jufo2015.gui.ParameterWindowDelegate;

public class SimulationController extends BasicGame implements ParameterWindowDelegate
{
	public static final int SCREEN_WIDTH = 1024;
	public static final int SCREEN_HEIGHT = 720;
	public static final int UPDATE_JUMP_AMOUNT = 50000;
	public static final int EVOLUTION_UPDATE_STEP_TIMEOUT = 1000000; // Amount of time each entity has before death, measured in updates TODO: make GUI Element
	public static final float EVOLUTION_MUTATION_CHANCE = 0.6f; // Chance that a mutation occurs TODO: make GUI Element
	public static final boolean DEBUG_MODE = false;

	public static void main(String[] args)
	{
		try
		{
			AppGameContainer app = new AppGameContainer(new SimulationController());
			app.setDisplayMode(SCREEN_WIDTH, SCREEN_HEIGHT, false);
			app.start();
		}
		catch (SlickException e)
		{
			e.printStackTrace();
		}
	}

	private ArrayList<Entity> simulationEntities;
	private ArrayList<Collectable> simulationCollectables;
	private ParameterWindow parameterWindow;
	private CountDownLatch latch;
	private Random random;
	private Entity previousBestEntity;
	private Entity currentBestEntityInSimulation;
	private int evolutionUpdateStepCount;
	private int evolutionGenerations;
	private boolean jumping;
	private boolean rendering;

	public SimulationController()
	{
		super("Jufo2015-Dhorn");

		this.simulationEntities = new ArrayList<>();
		this.simulationCollectables = new ArrayList<>();
		this.parameterWindow = new ParameterWindow(this);
		this.random = new Random();
		this.evolutionUpdateStepCount = 0;
		this.evolutionGenerations = 0;
		this.jumping = false;
		this.rendering = true;
	}

	@Override
	public void okButtonPressed()
	{
		this.latch.countDown();
	}

	public Vector2f getClosestCollectableVector(Entity entity)
	{
		Vector2f closestCollectableVector = new Vector2f(0, 0);
		if (this.simulationCollectables.size() > 0)
		{
			Vector2f collectablePosition = this.simulationCollectables.get(0).getPosition();
			Vector2f entityPosition = entity.getPosition();

			Collectable closestCollectable = this.simulationCollectables.get(0);
			float closestDistanceSquared = (collectablePosition.x - entityPosition.x) * (collectablePosition.x - entityPosition.x) + (collectablePosition.y - entityPosition.y) * (collectablePosition.y - entityPosition.y);
			for (int i = 1; i < this.simulationCollectables.size(); i++)
			{
				collectablePosition = this.simulationCollectables.get(i).getPosition();

				float distance = (collectablePosition.x - entityPosition.x) * (collectablePosition.x - entityPosition.x) + (collectablePosition.y - entityPosition.y) * (collectablePosition.y - entityPosition.y);
				if (distance < closestDistanceSquared)
				{
					closestDistanceSquared = distance;
					closestCollectable = this.simulationCollectables.get(i);
				}
			}

			// create vector from aquired data
			closestCollectableVector = new Vector2f(closestCollectable.getPosition().x - entity.getPosition().x, closestCollectable.getPosition().y - entity.getPosition().y);
		}

		return closestCollectableVector;
	}

	/* Slick2D */

	@Override
	public void render(GameContainer container, Graphics g) throws SlickException
	{
		// Toggle rendering
		if (container.getInput().isKeyPressed(Input.KEY_F2))
		{
			this.rendering = this.rendering ? false : true;
		}

		if (this.rendering)
		{
			container.getGraphics().setBackground(new Color(0.5f, 0.5f, 0.5f, 1.0f));

			/* render collectables */
			for (Collectable collectable : this.simulationCollectables)
			{
				collectable.render(container, g);
			}

			/* render entites on top */
			for (Entity entity : this.simulationEntities)
			{
				entity.render(container, g);
			}
		}
		else
		{
			container.getGraphics().setBackground(new Color(0.0f, 0.0f, 0.0f, 1.0f));
		}

		/* render remaining EVOLUTION_UPDATE_STEP_TIMEOUT */
		g.setColor(Color.white);
		g.drawString(new String("remaining: " + this.evolutionUpdateStepCount), SCREEN_WIDTH - 150, SCREEN_HEIGHT - 20);
		g.drawString(new String("generations: " + this.evolutionGenerations), 10, SCREEN_HEIGHT - 20);

	}

	@Override
	public void init(GameContainer container) throws SlickException
	{
		this.populateSimulation();

		container.getGraphics().setBackground(new Color(0.4f, 0.4f, 0.4f, 1.0f));
		container.setAlwaysRender(true);
		container.setUpdateOnlyWhenVisible(false);
		container.setTargetFrameRate(60);
		container.setVSync(true);
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException
	{
		// Prevent dumb errors
		if (delta > 30)
			return;

		// Jump ahead UPDATE_JUMP_AMOUNT Update steps
		if (!this.jumping)
		{
			if (container.getInput().isKeyPressed(Input.KEY_F1))
			{
				this.jumping = true;
				container.pause();
				for (int i = 0; i < UPDATE_JUMP_AMOUNT; i++)
				{
					this.update(null, 16);
				}
				container.resume();
				this.jumping = false;
			}
			else if (container.getInput().isKeyDown(Input.KEY_F3))
			{
				this.jumping = true;
				// container.pause();

				for (int i = 0; i < 1000; i++)
				{
					this.update(null, 10);
				}

				// container.resume();
				this.jumping = false;
			}
		}

		// have we reached the timeout? if so - reset simulation + generate offspring of best entity
		if (this.evolutionUpdateStepCount <= 0 && currentBestEntityInSimulation.getCollectedScore() > 0)
		{
			evolveAndResetSimulation();
			return;
		}
		this.evolutionUpdateStepCount -= delta;

		if (evolutionUpdateStepCount < -SimulationController.EVOLUTION_UPDATE_STEP_TIMEOUT) {
			System.err.println("Evolutional error encountered");
			evolveAndResetSimulation();
			return;
		}
		
		currentBestEntityInSimulation = this.simulationEntities.get(0);
		for (Entity entity : this.simulationEntities)
		{
			// Update every game entity
			entity.update(container, delta);
			entity.disableDebug();
			// entity.setCollided(false);

			/* if entity is out of bounds place back in bounds */
			if (entity.getPosition().x < 0)
			{
				entity.setPosition(0, entity.getPosition().y);
				// entity.setCollided(true);
			}
			else if (entity.getPosition().x + entity.getSize() > SCREEN_WIDTH)
			{
				entity.setPosition(SCREEN_WIDTH - entity.getSize(), entity.getPosition().y);
				// entity.setCollided(true);
			}

			if (entity.getPosition().y < 0)
			{
				entity.setPosition(entity.getPosition().x, 0);
				// entity.setCollided(true);
			}
			else if (entity.getPosition().y + entity.getSize() > SCREEN_HEIGHT)
			{
				entity.setPosition(entity.getPosition().x, SCREEN_HEIGHT - entity.getSize());
				// entity.setCollided(true);
			}

			// for every entity check if it collides with collectable
			for (Collectable collectable : this.simulationCollectables)
			{
				if (collides(entity.getPosition(), collectable.getPosition(), entity.getSize(), collectable.getSize()))
				{
					// Increase entity score
					entity.increaseCollectedScore();

					// remove collectable
					this.simulationCollectables.remove(collectable);

					// Add new collectable
					this.simulationCollectables.add(new Collectable(SCREEN_WIDTH * 0.15f + this.random.nextFloat() * (SCREEN_WIDTH * 0.7f), SCREEN_HEIGHT * 0.15f + this.random.nextFloat() * (SCREEN_HEIGHT * 0.7f)));
					break;
				}
			}

			if (entity.getCollectedScore() > currentBestEntityInSimulation.getCollectedScore())
			{
				currentBestEntityInSimulation = entity;
			}
		}

		if (currentBestEntityInSimulation.getCollectedScore() > 0) {
			currentBestEntityInSimulation.enableDebug();
		}
	}

	/**
	 * Populate Simulation
	 */
	private void populateSimulation()
	{
		this.latch = new CountDownLatch(1);

		// remove all entites and collectables
		this.simulationEntities.clear();
		this.simulationCollectables.clear();

		// fetch parameters for simulation
		this.parameterWindow.displayWindow();
		try
		{
			this.latch.await();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		this.parameterWindow.hideWindow();

		float posX = 100f;
		float posY = 100f;
		float angle = 225f;
		switch (random.nextInt(4))
		{
		case 0:
			// Top left edge
			break;
		case 1:
			// Top right edge
			posX = SCREEN_WIDTH - 100f;
			angle = 315f;
			break;
		case 2:
			// Bottom right edge
			posX = SCREEN_WIDTH - 100f;
			posY = SCREEN_HEIGHT - 100f;
			angle = 45f;
			break;
		case 3:
			// Bottom left edge
			posY = SCREEN_HEIGHT - 100f;
			angle = 135f;
			break;
		}

		// populate with new random entites
		for (int i = 0; i < this.parameterWindow.getSimulationEntityAmount(); i++)
		{
			int[] hiddenLayerNeuronCount = new int[this.parameterWindow.getSimulationHiddenNeuronLayerCount()];
			int neuronsPerHiddenLayer = this.parameterWindow.getSimulationHiddenNeuronLayerNeuronCount();
			for (int j = 0; j < hiddenLayerNeuronCount.length; j++)
			{
				hiddenLayerNeuronCount[j] = neuronsPerHiddenLayer;
			}

			this.simulationEntities.add(new Entity(this, posX, posY, angle, hiddenLayerNeuronCount));
			// this.simulationEntities.add(new Entity(this, (float) SCREEN_WIDTH * this.random.nextFloat(), (float) SCREEN_HEIGHT * this.random.nextFloat(), hiddenLayerNeuronCount));
		}

		// generate randomly spread collectables
		for (int i = 0; i < this.parameterWindow.getSimulationCollectableCount(); i++)
		{
			this.simulationCollectables.add(new Collectable(SCREEN_WIDTH * 0.15f + this.random.nextFloat() * (SCREEN_WIDTH * 0.7f), SCREEN_HEIGHT * 0.15f + this.random.nextFloat() * (SCREEN_HEIGHT * 0.7f)));
		}

		this.evolutionUpdateStepCount = EVOLUTION_UPDATE_STEP_TIMEOUT;
	}

	private void evolveAndResetSimulation()
	{
		// Save some simulation information
		int simulationEntityCount = this.simulationEntities.size();

		// find best Entity (based on collectedScore)
		Entity bestEntity = this.simulationEntities.get(0);
		for (Entity entity : this.simulationEntities)
		{
			if (entity.getCollectedScore() > bestEntity.getCollectedScore())
			{
				bestEntity = entity;
			}
		}
		
		if (this.previousBestEntity != null && this.previousBestEntity.getCollectedScore() > bestEntity.getCollectedScore())
		{
			System.err.println("No better Entity could be found!");
			bestEntity = this.previousBestEntity;
		} else {
			System.out.println("New best Entity collected: " + bestEntity.getCollectedScore() + " units!");
		}

		// clean up simulation
		this.simulationEntities.clear();
		this.simulationCollectables.clear();

		// generate offspring from evolving entity
		float posX = 100f;
		float posY = 100f;
		float angle = 225f;
		switch (random.nextInt(4))
		{
		case 0:
			// Top left edge
			break;
		case 1:
			// Top right edge
			posX = SCREEN_WIDTH - 100f;
			angle = 315f;
			break;
		case 2:
			// Bottom right edge
			posX = SCREEN_WIDTH - 100f;
			posY = SCREEN_HEIGHT - 100f;
			angle = 45f;
			break;
		case 3:
			// Bottom left edge
			posY = SCREEN_HEIGHT - 100f;
			angle = 135f;
			break;
		}

		for (int i = 0; i < simulationEntityCount - 1; i++)
		{
			this.simulationEntities.add(new Entity(this, bestEntity, SimulationController.EVOLUTION_MUTATION_CHANCE, posX, posY, angle));
		}
		this.simulationEntities.add(new Entity(this, bestEntity, 0, posX, posY, angle));	// readd best entity without evolving it

		// repopulate collectables array
		for (int i = 0; i < this.parameterWindow.getSimulationCollectableCount(); i++)
		{
			this.simulationCollectables.add(new Collectable(SCREEN_WIDTH * 0.15f + this.random.nextFloat() * (SCREEN_WIDTH * 0.7f), SCREEN_HEIGHT * 0.15f + this.random.nextFloat() * (SCREEN_HEIGHT * 0.7f)));
		}

		this.evolutionUpdateStepCount = EVOLUTION_UPDATE_STEP_TIMEOUT;
		this.evolutionGenerations++;

		this.previousBestEntity = bestEntity;
	}

	private boolean collides(Vector2f position1, Vector2f position2, float size1, float size2)
	{
		// Check if they are not intersecting
		if ((position1.x) > (position2.x + size2) || (position2.x) > (position1.x + size1) || (position1.y) > (position2.y + size2) || (position2.y) > (position1.y + size1))
		{
			return false;
		}

		// They are not not intersecting => thusly intersecting
		return true;
	}
}