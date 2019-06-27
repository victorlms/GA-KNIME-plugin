package org.knime.geneticalgoritm;

import java.util.ArrayList;

public class Individual {

	private ArrayList<String> value;
	private Double fitness;
	private Double selectionProbability;
	
	public Individual(ArrayList<String> value, Double fitness, Double selectionProbability) {
		this.value = value;
		this.fitness = fitness;
		this.selectionProbability = selectionProbability;
	}
	
	public Individual() {
		this.value = new ArrayList<String>();
		this.fitness = 0D;
		this.selectionProbability = 0D;
	}
	
	public ArrayList<String> getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value.add(value);
	}
	public void resetValue() {
		this.value = new ArrayList<>();
	}
	public Double getFitness() {
		return fitness;
	}
	public void setFitness(Double fitness) {
		this.fitness = fitness;
	}

	public Double getSelectionProbability() {
		return selectionProbability;
	}

	public void setSelectionProbability(Double selectionProbability) {
		this.selectionProbability = selectionProbability;
	}
	
}
