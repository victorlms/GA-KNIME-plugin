package org.knime.mynode;

public class Individual {

	private String value;
	private Double fitness;
	private Double selectionProbability;
	
	public Individual(String value, Double fitness, Double selectionProbability) {
		super();
		this.value = value;
		this.fitness = fitness;
		this.selectionProbability = selectionProbability;
	}
	
	public Individual() {
		super();
		this.value = "";
		this.fitness = 0D;
		this.selectionProbability = 0D;
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
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
	
}
