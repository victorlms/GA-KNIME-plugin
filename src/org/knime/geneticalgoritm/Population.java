package org.knime.geneticalgoritm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Population implements Cloneable {

	private List<Individual> individuals;
	private Long executionTime;
	
	private List<Individual> bestIndividuals;
	
	public Population(List<Individual> individuals) {
		super();
		this.individuals = individuals;
		this.executionTime = 0L;
		this.bestIndividuals = new ArrayList<>();
	}
	
	public Population() {
		super();
		this.individuals = new ArrayList<Individual>();
		this.executionTime = 0L;
		this.bestIndividuals = new ArrayList<>();
	}
	
	public List<Individual> getIndividuals() {
		return individuals;
	}
	
	public void setIndividuals(List<Individual> individuals) {
		this.individuals = individuals;
	}
	
	public List<Individual> getBestIndividual() {
		return this.bestIndividuals;		
	}
	
	public void defineBest(Integer count) {
		List<Individual> bestList = new ArrayList<>();
		
		List<Individual> sortedList = new ArrayList<>();

		for(int i = 0; i<this.individuals.size(); i++) {
			sortedList.add(new Individual());
		}
		
		Collections.copy(sortedList, this.individuals);
		
		Collections.sort(sortedList, Collections.reverseOrder());
		
		
		for(int i = 0;i<count;i++) {
			bestList.add((Individual) sortedList.get(i).clone());
		}
		
		Collections.sort(bestList, Collections.reverseOrder());
		this.bestIndividuals = bestList;
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
	
	public List<Individual> getBestIndividuals() {
		return this.bestIndividuals;
	}
	
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
}
