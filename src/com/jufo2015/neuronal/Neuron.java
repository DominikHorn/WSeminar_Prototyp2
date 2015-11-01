package com.jufo2015.neuronal;

import java.util.*;

public class Neuron
{
	private Map<Neuron, Double> inputs;	// Keys are input neurons, object their connection's weight
	private Random random;				// Random number source for this neuron
	private Double calculatedOutput;	// Optimization, stores calculatedOutput for simulation step
	private Integer simulationStep;		// Optimization, Stores simulationStep
	
	public Neuron()
	{
		this.inputs = new Hashtable<>();
		this.random = new Random();
		this.calculatedOutput = 0.0;
		this.simulationStep = -1;
	}
	
	public Neuron(Double biasWeight)
	{
		this.inputs = new Hashtable<>();
		this.random = new Random();
		this.calculatedOutput = 0.0;
		this.simulationStep = -1;
		
		/* Add bias weight */
		this.inputs.put(new BiasWeightNeuron(), biasWeight);
	}
	
	public Neuron clone()
	{
		return new Neuron(this.getBiasWeight());
	}
	
	public void mutate(Float mutationChance)
	{
		for (Neuron neuron : this.inputs.keySet())
		{
			if (this.random.nextFloat() < mutationChance)
			{
				Double weight = this.inputs.get(neuron);
				Double mutationAmount = (this.random.nextDouble() - 0.5) / 2.5;
				
				weight += mutationAmount;
				if (weight < -1.0)
				{
					weight -= 2 * mutationAmount;
				} else if (weight > 1.0)
				{
					weight -= 2 * mutationAmount;
				}
				
				this.inputs.put(neuron, weight);
			}
		}
	}
	
	public double getOutput(Integer simulationStep)
	{
		double output = 0.0;
		if (this.simulationStep == simulationStep)
		{
			output = this.calculatedOutput;
		}
		else 
		{
			this.simulationStep = simulationStep;
			
			/* add output from each input (Bias weight is included) */
			for (Neuron neuron : inputs.keySet())
			{
				output += neuron.getOutput(simulationStep) * this.inputs.get(neuron);
			}
			
			/* run through sigma function */
			output = (1.0 / (1.0 + Math.exp(-output)));
			
			this.calculatedOutput = output;
		}
		
		return output;
	}
	
	public void addInput(Neuron neuron, Double weight)
	{
		this.inputs.put(neuron, weight);
	}
	
	public double getWeight(Neuron neuron)
	{
		return this.inputs.get(neuron);
	}
	
	public String neuronToString()
	{
		String string = new String("Neuron to String: " + this.getOutput(this.simulationStep) + "\n\t");
		for (Neuron neuron : this.inputs.keySet())
		{
			string = string + "Neuron: " + neuron + " with output: " + neuron.getOutput(this.simulationStep) + " has weight: " + this.inputs.get(neuron) + "\n\t";
		}
		
		return string;
	}
	
	protected Double getBiasWeight()
	{
		return this.inputs.get(this.inputs.keySet().toArray()[0]);
	}
}