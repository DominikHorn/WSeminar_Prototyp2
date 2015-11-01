package com.jufo2015.neuronal;

public class BiasWeightNeuron extends Neuron
{
	public BiasWeightNeuron()
	{
		/* empty class used as key for biasweight */
	}

	@Override
	public BiasWeightNeuron clone()
	{
		return new BiasWeightNeuron();
	}

	@Override
	public String neuronToString()
	{
		return new String("BiasWeightNeuron to String: " + this.getOutput(0));
	}

	@Override
	public double getOutput(Integer simulationStep)
	{
		return -1.0;
	}
}