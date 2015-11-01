package com.jufo2015.neuronal;

import java.util.*;

public class NeuronalNetwork
{
	private List<List<Neuron>> neuronLayers;
	private Random random;
	private Integer simulationStep;

	public NeuronalNetwork()
	{
		this.neuronLayers = new ArrayList<>();
		this.random = new Random();
		this.simulationStep = -1;
	}

	public NeuronalNetwork(Integer numInputs, Integer numOutputs, Integer numHiddenLayers, Integer numNeuronsPerHiddenLayer)
	{
		this.neuronLayers = new ArrayList<>();
		this.random = new Random();
		this.simulationStep = 0;

		/* generate input layer */
		this.neuronLayers.add(new ArrayList<Neuron>());
		for (int i = 0; i < numInputs; i++)
		{
			this.neuronLayers.get(0).add(new InputNeuron());
		}

		/* generate hidden layers */
		for (int i = 1; i <= numHiddenLayers; i++)
		{
			this.neuronLayers.add(new ArrayList<Neuron>());
			for (int j = 0; j < numNeuronsPerHiddenLayer; j++)
			{
				/* generate neuron */
				Neuron neuron = new Neuron((this.random.nextDouble() - 0.5) * 2);

				/* add input neurons */
				for (Neuron inputNeuron : this.neuronLayers.get(i - 1))
				{
					neuron.addInput(inputNeuron, (this.random.nextDouble() - 0.5) * 2);
				}

				/* add neuron to layer */
				this.neuronLayers.get(i).add(neuron);
			}
		}

		/* generate output layer */
		this.neuronLayers.add(new ArrayList<Neuron>());
		for (int i = 0; i < numOutputs; i++)
		{
			/* generate neuron */
			Neuron neuron = new Neuron((this.random.nextDouble() - 0.5) * 2);

			for (Neuron inputNeuron : this.neuronLayers.get(this.neuronLayers.size() - 2))
			{
				neuron.addInput(inputNeuron, (this.random.nextDouble() - 0.5) * 2);
			}

			this.neuronLayers.get(this.neuronLayers.size() - 1).add(neuron);
		}
		
//		this.printNeuronalNetwork();
	}

	public NeuronalNetwork evolve(Float mutationChance)
	{
		NeuronalNetwork clonedNeuronalNetwork = new NeuronalNetwork();

		/* clone input neurons */
		clonedNeuronalNetwork.neuronLayers.add(new ArrayList<Neuron>());
		for (Neuron neuron : this.neuronLayers.get(0))
		{
			clonedNeuronalNetwork.neuronLayers.get(0).add(neuron.clone());
		}

		/* clone hidden layer neurons */
		for (int i = 1; i < this.neuronLayers.size(); i++)
		{
			clonedNeuronalNetwork.neuronLayers.add(new ArrayList<Neuron>());

			for (int j = 0; j < this.neuronLayers.get(i).size(); j++)
			{
				/* clone neurons */
				Neuron clonedNeuron = this.neuronLayers.get(i).get(j).clone();

				/* add inputs */
				for (int k = 0; k < clonedNeuronalNetwork.neuronLayers.get(i - 1).size(); k++)
				{
					clonedNeuron.addInput(clonedNeuronalNetwork.neuronLayers.get(i - 1).get(k), this.neuronLayers.get(i).get(j).getWeight(this.neuronLayers.get(i - 1).get(k)));
				}

				/* add to clonedLayer */
				clonedNeuronalNetwork.neuronLayers.get(i).add(clonedNeuron);
			}
		}
		
		/* mutate each Neuron based on chance */
		for (List<Neuron> neuronLayer : clonedNeuronalNetwork.neuronLayers)
		{
			for (Neuron neuron : neuronLayer)
			{
				neuron.mutate(mutationChance);
			}
		}
		
		return clonedNeuronalNetwork;
	}
	
	public void printNeuronalNetwork()
	{
		System.out.println("==================================================================================================================");
		for (int i = 0; i < this.neuronLayers.size(); i++)
		{
			for (int j = 0; j < this.neuronLayers.get(i).size(); j++)
			{
				System.out.println("Neuron on layer " + i + ": " + this.neuronLayers.get(i).get(j).neuronToString());
				System.out.println("====================================================================================================================================");
			}
		}
		System.out.println("==================================================================================================================");
	}

	public void simulateStep()
	{
		this.simulationStep++;
	}

	public void setInput(Integer inputIndex, Double value)
	{
		((InputNeuron) this.neuronLayers.get(0).get(inputIndex)).setOutput(value);
	}

	public double getOutput(Integer outputIndex)
	{
		return this.neuronLayers.get(this.neuronLayers.size() - 1).get(outputIndex).getOutput(this.simulationStep);
	}
}
