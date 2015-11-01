package com.jufo2015.gui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ParameterWindow
{
	private JFrame frmParametersForSimulation;
	private JLabel simulationEntityAmount;
	private JLabel simulationHiddenNeuronLayerCount;
	private JLabel simulationHiddenNeuronLayerNeuronCount;
	private JLabel simulationCollectablesAmount;

	private ParameterWindowDelegate delegate;

	public ParameterWindow(ParameterWindowDelegate delegate)
	{
		this.delegate = delegate;

		initialize();
	}

	public void displayWindow()
	{
		this.frmParametersForSimulation.setVisible(true);
	}

	public void hideWindow()
	{
		this.frmParametersForSimulation.setVisible(false);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize()
	{
		frmParametersForSimulation = new JFrame();
		frmParametersForSimulation.setVisible(false);
		frmParametersForSimulation.setResizable(false);
		frmParametersForSimulation.setTitle("Parameters for Simulation\r\n");
		frmParametersForSimulation.setAlwaysOnTop(true);
		frmParametersForSimulation.setBounds(100, 100, 800, 640);
		frmParametersForSimulation.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmParametersForSimulation.getContentPane().setLayout(null);

		simulationEntityAmount = new JLabel("80");
		simulationEntityAmount.setHorizontalAlignment(SwingConstants.CENTER);
		simulationEntityAmount.setBounds(198, 11, 58, 20);
		frmParametersForSimulation.getContentPane().add(simulationEntityAmount);

		simulationHiddenNeuronLayerCount = new JLabel("2");
		simulationHiddenNeuronLayerCount.setHorizontalAlignment(SwingConstants.CENTER);
		simulationHiddenNeuronLayerCount.setAlignmentY(0.0f);
		simulationHiddenNeuronLayerCount.setAlignmentX(0.5f);
		simulationHiddenNeuronLayerCount.setBounds(198, 42, 58, 20);
		frmParametersForSimulation.getContentPane().add(simulationHiddenNeuronLayerCount);

		simulationHiddenNeuronLayerNeuronCount = new JLabel("6");
		simulationHiddenNeuronLayerNeuronCount.setHorizontalAlignment(SwingConstants.CENTER);
		simulationHiddenNeuronLayerNeuronCount.setAlignmentY(0.0f);
		simulationHiddenNeuronLayerNeuronCount.setAlignmentX(0.5f);
		simulationHiddenNeuronLayerNeuronCount.setBounds(198, 73, 58, 20);
		frmParametersForSimulation.getContentPane().add(simulationHiddenNeuronLayerNeuronCount);

		simulationCollectablesAmount = new JLabel("50");
		simulationCollectablesAmount.setHorizontalAlignment(SwingConstants.CENTER);
		simulationCollectablesAmount.setAlignmentY(0.0f);
		simulationCollectablesAmount.setAlignmentX(0.5f);
		simulationCollectablesAmount.setBounds(198, 104, 58, 20);
		frmParametersForSimulation.getContentPane().add(simulationCollectablesAmount);

		JSlider slider_1 = new JSlider();
		slider_1.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				simulationHiddenNeuronLayerCount.setText("" + ((JSlider) e.getSource()).getValue());
			}
		});
		slider_1.setMinimum(1);
		slider_1.setMaximum(6);
		slider_1.setBounds(266, 42, 518, 23);
		slider_1.setValue(1);
		frmParametersForSimulation.getContentPane().add(slider_1);

		JSlider slider_2 = new JSlider();
		slider_2.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				simulationHiddenNeuronLayerNeuronCount.setText("" + ((JSlider) e.getSource()).getValue());
			}
		});
		slider_2.setMinimum(1);
		slider_2.setMaximum(50);
		slider_2.setBounds(266, 73, 518, 23);
		slider_2.setValue(6);
		frmParametersForSimulation.getContentPane().add(slider_2);

		JSlider slider = new JSlider();
		slider.setValue(40);
		slider.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent event)
			{
				simulationEntityAmount.setText("" + ((JSlider) event.getSource()).getValue());
			}
		});
		slider.setMinimum(1);
		slider.setMaximum(1000);
		slider.setBounds(266, 8, 518, 23);
		frmParametersForSimulation.getContentPane().add(slider);

		JSlider slider_3 = new JSlider();
		slider_3.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				simulationCollectablesAmount.setText("" + ((JSlider) e.getSource()).getValue());
			}
		});
		slider_3.setMinimum(1);
		slider_3.setMaximum(500);
		slider_3.setBounds(266, 104, 518, 23);
		slider_3.setValue(150);
		frmParametersForSimulation.getContentPane().add(slider_3);

		JButton btnDone = new JButton("Done");
		btnDone.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				delegate.okButtonPressed();
			}
		});
		btnDone.setBounds(695, 578, 89, 23);
		frmParametersForSimulation.getContentPane().add(btnDone);

		JLabel lblAmountOfCollectables = new JLabel("Amount of collectables:");
		lblAmountOfCollectables.setHorizontalAlignment(SwingConstants.TRAILING);
		lblAmountOfCollectables.setAlignmentY(0.0f);
		lblAmountOfCollectables.setAlignmentX(0.5f);
		lblAmountOfCollectables.setBounds(10, 104, 178, 20);
		frmParametersForSimulation.getContentPane().add(lblAmountOfCollectables);

		JLabel label = new JLabel("Amount of simulation entities:");
		label.setHorizontalAlignment(SwingConstants.TRAILING);
		label.setAlignmentY(0.0f);
		label.setAlignmentX(0.5f);
		label.setBounds(10, 11, 178, 20);
		frmParametersForSimulation.getContentPane().add(label);

		JLabel label_1 = new JLabel("Amount of hidden layers:");
		label_1.setHorizontalAlignment(SwingConstants.TRAILING);
		label_1.setAlignmentY(0.0f);
		label_1.setAlignmentX(0.5f);
		label_1.setBounds(10, 42, 178, 20);
		frmParametersForSimulation.getContentPane().add(label_1);

		JLabel label_4 = new JLabel("Amount of hidden neurons:");
		label_4.setHorizontalAlignment(SwingConstants.TRAILING);
		label_4.setAlignmentY(0.0f);
		label_4.setAlignmentX(0.5f);
		label_4.setBounds(10, 73, 178, 20);
		frmParametersForSimulation.getContentPane().add(label_4);
	}

	/**
	 * getters for gui values
	 */
	public int getSimulationEntityAmount()
	{
		return new Integer(this.simulationEntityAmount.getText());
	}

	public int getSimulationHiddenNeuronLayerCount()
	{
		return new Integer(this.simulationHiddenNeuronLayerCount.getText());
	}

	public int getSimulationHiddenNeuronLayerNeuronCount()
	{
		return new Integer(this.simulationHiddenNeuronLayerNeuronCount.getText());
	}
	
	public int getSimulationCollectableCount()
	{
		return new Integer(this.simulationCollectablesAmount.getText());
	}
}
