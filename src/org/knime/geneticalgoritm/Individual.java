package org.knime.geneticalgoritm;

import java.util.ArrayList;

public class Individual implements Comparable<Individual>{

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
	
	@Override
	public int compareTo(Individual i) {
		if(i == null || this.fitness == null || i.fitness == null || i.fitness == this.fitness) {
			return 0;
		}
		return this.getFitness().compareTo(i.getFitness());
	}
	
	public ArrayList<String> getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value.add(value);
	}
	public void resetValue(ArrayList<String> value) {
		this.value = value;
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
	
	public String getStringValue() {
		String sValue = "";
		for(String s : this.value) {
			sValue = sValue.concat(s);
		}
		return sValue;
	}
	
}
