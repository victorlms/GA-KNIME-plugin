package org.knime.geneticalgoritm;

import java.util.ArrayList;
import java.util.List;

public class Population {

	private List<Individual> individuals;
	private Long executionTime;
	
	public Population(List<Individual> individuals) {
		super();
		this.individuals = individuals;
		this.executionTime = 0L;
	}
	
	public Population() {
		super();
		this.individuals = new ArrayList<Individual>();
		this.executionTime = 0L;
	}
	
	public List<Individual> getIndividuals() {
		return individuals;
	}
	
	public void setIndividuals(List<Individual> individuals) {
		this.individuals = individuals;
	}
	
	public Individual getBestIndividual() {
		Individual bestIndividual = new Individual();
		for(Individual individual : this.individuals) {
			if(individual.getFitness()>bestIndividual.getFitness()) {
				bestIndividual = individual;
			}
		}
		return bestIndividual;
	}
		
	public Double getSumFitness() {
		Double sum = 0D;
		for(Individual individual : individuals) {
			sum += individual.getFitness();
		}
		return sum;
	}
	
	public Double getAverageFitness() {
		return getSumFitness()/individuals.size();
	}
	
	public Long getExecutionTime() {
		return this.executionTime;
	}
	
	public void setExecutionTime(Long time) {
		this.executionTime = time;
	}
	
}
