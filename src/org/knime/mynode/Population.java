package org.knime.mynode;

import java.util.ArrayList;
import java.util.List;

public class Population {

	private List<Individual> individuals;
	private Individual bestIndividual;
	
	
	
	public Population(List<Individual> individuals, Individual bestIndividual) {
		super();
		this.individuals = individuals;
		this.bestIndividual = bestIndividual;
	}
	
	public Population() {
		super();
		this.individuals = new ArrayList<Individual>();
		this.bestIndividual = new Individual();
	}
	
	public List<Individual> getIndividuals() {
		return individuals;
	}
	
	public void setIndividuals(List<Individual> individuals) {
		this.individuals = individuals;
	}
	
	public Individual getBestIndividual() {
		return bestIndividual;
	}
	
	public void setBestIndividual(Individual bestIndividual) {
		this.bestIndividual = bestIndividual;
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
	
	
}
