package com.jufo2015.neuronal;

public class InputNeuron extends Neuron
{
	private Double output;
	
	public InputNeuron()
	{
		this.output = 0.0;
	}
	
	public void setOutput(Double output)
	{
		this.output = output;
	}
	
	@Override
	public InputNeuron clone()
	{
		InputNeuron neuron = new InputNeuron();
		neuron.setOutput(this.output);
		return neuron;
	}
	
	@Override
	public double getOutput(Integer simulationStep)
	{
		return this.output;
	}
	
	@Override
	public String neuronToString()
	{
		return new String("InputNeuronToString: " + this.getOutput(0));
	}
	
	@Override
	protected Double getBiasWeight()
	{
		return 0.0;
	}
}