package org.knime.mynode;

import java.util.ArrayList;
import java.util.List;

public class Population {

	private List<Individual> individuals;
	private Individual bestIndividual;
	
	
	
	public Population(List<Individual> individuals) {
		super();
		this.individuals = individuals;
	}
	
	public Population() {
		super();
		this.individuals = new ArrayList<Individual>();
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
	
	
}
