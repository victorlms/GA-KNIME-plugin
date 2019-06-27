package org.knime.geneticalgoritm;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelDoubleBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;


/**
 * This is the model implementation of GeneticAlgorithm.
 * Node will implement a generic Genetic Algorithm
 *
 * @author Victor
 */
public class GeneticAlgorithmNodeModel extends NodeModel {
    
    // the logger instance
    private static final NodeLogger logger = NodeLogger
            .getLogger(GeneticAlgorithmNodeModel.class);
        
    /** the settings key which is used to retrieve and 
        store the settings (from the dialog or from a settings file)    
       (package visibility to be usable from the dialog). */
    static final String INDIVIDUAL_STR = "Number of individuals";
    static final String GENERATION_STR = "Number of generations";
    static final String CROMOSSOMES_STR  = "Number of cromossomes";
    static final String MUTATION_STR = "Mutation rate";
    static final String CROSSOVER_STR = "Crossover rate";
    static final String ELITISM_STR = "Elitism";
    static final String SCRIPT_STR = "Path to Python script";
    static final String CROSSOVER_TYPE_STR = "Crossover type";
    static final String GENE_SYMBOLS_STR = "Gene symbols";
	
    /** initial default count value. */
    static final int INDIVIDUAL_COUNT = 100;
    static final int GENERATION_COUNT = 200;
    static final int CROMOSSOMES_COUNT  = 8;
    static final boolean ELITISM_STATE = true;
    static final double MUTATION_COUNT = 0.01;
    static final double CROSSOVER_COUNT = 0.6;
    static final String CROSSOVER_TYPE = "single";
    static final String GENE_SYMBOLS = "0,1";
    
    static final String SCRIPT_PATH = "";


    // example value: the models count variable filled from the dialog 
    // and used in the models execution method. The default components of the
    // dialog work with "SettingsModels".

    private final SettingsModelIntegerBounded individualCount = 
    		new SettingsModelIntegerBounded(GeneticAlgorithmNodeModel.INDIVIDUAL_STR, 
    				GeneticAlgorithmNodeModel.INDIVIDUAL_COUNT,
    				1, 10000);

    private final SettingsModelIntegerBounded generationCount = 
    		new SettingsModelIntegerBounded(GeneticAlgorithmNodeModel.GENERATION_STR, 
    				GeneticAlgorithmNodeModel.GENERATION_COUNT,
    				1, 10000);

    private final SettingsModelIntegerBounded cromossomesCount = 
    		new SettingsModelIntegerBounded(GeneticAlgorithmNodeModel.CROMOSSOMES_STR, 
    				GeneticAlgorithmNodeModel.CROMOSSOMES_COUNT,
    				1, 1000);

    private final SettingsModelBoolean elitism = 
    		new SettingsModelBoolean(GeneticAlgorithmNodeModel.ELITISM_STR,GeneticAlgorithmNodeModel.ELITISM_STATE);

    private final SettingsModelDoubleBounded mutationCount = 
    		new SettingsModelDoubleBounded(GeneticAlgorithmNodeModel.MUTATION_STR, GeneticAlgorithmNodeModel.MUTATION_COUNT, 0, 1);

    private final SettingsModelDoubleBounded crossoverCount = 
    		new SettingsModelDoubleBounded(GeneticAlgorithmNodeModel.CROSSOVER_STR, GeneticAlgorithmNodeModel.CROSSOVER_COUNT, 0, 1);
    
    private final SettingsModelString path = 
    		new SettingsModelString(GeneticAlgorithmNodeModel.SCRIPT_STR, GeneticAlgorithmNodeModel.SCRIPT_PATH);

    private SettingsModelString crossoverType = 
    		new SettingsModelString(GeneticAlgorithmNodeModel.CROSSOVER_TYPE_STR, GeneticAlgorithmNodeModel.CROSSOVER_TYPE);
    
    private SettingsModelString geneSymbols = 
    		new SettingsModelString(GeneticAlgorithmNodeModel.GENE_SYMBOLS_STR, GeneticAlgorithmNodeModel.GENE_SYMBOLS);
    /**
     * Constructor for the node model.
     */
    protected GeneticAlgorithmNodeModel() {
    
        // TODO one incoming port and one outgoing port is assumed
        super(0, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
    
    	List<Population> populations = new ArrayList<Population>();
    	//Individual bestIndividual = geneticAlgorithm(exec);
    	populations = geneticAlgorithm(exec);
    	//String testeString = reader.readLine();
    	//reader.close();
        // the data table spec of the single output table, 
        // the table will have three columns:
//        DataColumnSpec[] allColSpecs = new DataColumnSpec[2];
        DataColumnSpec[] allColSpecs = new DataColumnSpec[3];
        
        allColSpecs[0] = 
                new DataColumnSpecCreator("Average Fitness", DoubleCell.TYPE).createSpec();
        allColSpecs[1] = 
        		new DataColumnSpecCreator("Best Individual", StringCell.TYPE).createSpec();
        allColSpecs[2] = 
        		new DataColumnSpecCreator("Best Fitness", DoubleCell.TYPE).createSpec();
        DataTableSpec outputSpec = new DataTableSpec(allColSpecs);
        // the execution context will provide us with storage capacity, in this
        // case a data container to which we will add rows sequentially
        // Note, this container can also handle arbitrary big data tables, it
        // will buffer to disc if necessary.
        BufferedDataContainer container = exec.createDataContainer(outputSpec);
        
        //RowKey fitnessKey = new RowKey("Fitness");
        Integer index = 1;
        
        for(Population population : populations) {
        	exec.checkCanceled();
        	RowKey individualKey = new RowKey("Population "+index+" average fitness");
        	DataCell[] cells = new DataCell[3];
        	//cells[0] = new StringCell((index).toString());
        	cells[0] = new DoubleCell(population.getAverageFitness());
        	cells[1] = new StringCell(population.getBestIndividual().getValue().toString());
        	cells[2] = new DoubleCell(population.getBestIndividual().getFitness());
        	DataRow individualRow = new DefaultRow(individualKey, cells);
        	//DataRow fitnessRow = new DefaultRow(fitnessKey, cells);
        	container.addRowToTable(individualRow);
        	//container.addRowToTable(fitnessRow);
        	index++;
        }
        
        exec.checkCanceled();
        /*exec.setProgress(i / (double)m_count.getIntValue(), 
                "Adding row " + i);*/
        // once we are done, we close the container and return its table
        container.close();
        BufferedDataTable out = container.getTable();
        return new BufferedDataTable[]{out};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        // TODO Code executed on reset.
        // Models build during execute are cleared here.
        // Also data handled in load/saveInternals will be erased here.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {
        
        // TODO: check if user settings are available, fit to the incoming
        // table structure, and the incoming types are feasible for the node
        // to execute. If the node can execute in its current state return
        // the spec of its output data table(s) (if you can, otherwise an array
        // with null elements), or throw an exception with a useful user message
    	return new DataTableSpec[]{null};
    }


	/**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {

        // TODO save user settings to the config object.
        
        individualCount.saveSettingsTo(settings);
        generationCount.saveSettingsTo(settings);
        cromossomesCount.saveSettingsTo(settings);
        crossoverCount.saveSettingsTo(settings);
        mutationCount.saveSettingsTo(settings);
        elitism.saveSettingsTo(settings);
        path.saveSettingsTo(settings);
        crossoverType.saveSettingsTo(settings);
        geneSymbols.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
            
        // TODO load (valid) settings from the config object.
        // It can be safely assumed that the settings are valided by the 
        // method below.
        
        individualCount.loadSettingsFrom(settings);
        generationCount.loadSettingsFrom(settings);
        cromossomesCount.loadSettingsFrom(settings);
        crossoverCount.loadSettingsFrom(settings);
        mutationCount.loadSettingsFrom(settings);
        elitism.loadSettingsFrom(settings);
        path.loadSettingsFrom(settings);
        crossoverType.loadSettingsFrom(settings);
        geneSymbols.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
            
        // TODO check if the settings could be applied to our model
        // e.g. if the count is in a certain range (which is ensured by the
        // SettingsModel).
        // Do not actually set any values of any member variables.

        individualCount.validateSettings(settings);
        generationCount.validateSettings(settings);
        cromossomesCount.validateSettings(settings);
        crossoverCount.validateSettings(settings);
        mutationCount.validateSettings(settings);
        elitism.validateSettings(settings);
        path.validateSettings(settings);
        crossoverType.validateSettings(settings);
        geneSymbols.validateSettings(settings);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        
        // TODO load internal data. 
        // Everything handed to output ports is loaded automatically (data
        // returned by the execute method, models loaded in loadModelContent,
        // and user settings set through loadSettingsFrom - is all taken care 
        // of). Load here only the other internals that need to be restored
        // (e.g. data used by the views).

    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
       
        // TODO save internal models. 
        // Everything written to output ports is saved automatically (data
        // returned by the execute method, models saved in the saveModelContent,
        // and user settings saved through saveSettingsTo - is all taken care 
        // of). Save here only the other internals that need to be preserved
        // (e.g. data used by the views).

    }
    
//    protected Individual geneticAlgorithm(final ExecutionContext exec) throws CanceledExecutionException {
    	protected List<Population>geneticAlgorithm(final ExecutionContext exec) throws CanceledExecutionException {
    	
    	Individual bestIndividual = new Individual();
    	
    	List<Population> populationList = new ArrayList<Population>();
    	
    	Population firstPopulation = new Population();
    	
    	Random random = new Random();
    	
    	int generation = 0;
    	String[] symbols = this.geneSymbols.getStringValue().split(",");
    
    	//Generates an initial population
    	do {
    		exec.checkCanceled();
    		Individual individual = new Individual();
    	
    		do {
//    			individual.setValue(individual.getValue().concat(String.valueOf(random.nextInt(2))));
    			individual.getValue().add(symbols[random.nextInt(symbols.length)]);
    		}while(individual.getValue().size() < this.cromossomesCount.getIntValue());
    		
    		firstPopulation.getIndividuals().add(individual);
    		
    	}while(firstPopulation.getIndividuals().size() < this.individualCount.getIntValue());
    	
    	firstPopulation = evaluate(firstPopulation);
    	
    	populationList.add(firstPopulation);

		
		while (generation < this.generationCount.getIntValue()) {
			exec.checkCanceled();
    		//Selection
    		populationList.add(selection(populationList.get(generation)));
    		generation++;
    		
    		//crossover
    		switch(this.crossoverType.getStringValue()) {
    		case "Single Point":
        		populationList.set(generation, singlePointCrossover(populationList.get(generation)));
    			break;
    		case "Double Point":
    			populationList.set(generation, doublePointCrossover(populationList.get(generation)));
    			break;
    		case "uniform":
    			populationList.set(generation, uniformCrossover(populationList.get(generation)));
    			break;
    		}
    		
    		//mutation
    		populationList.set(generation, mutation(populationList.get(generation)));
    		
    		//evaluation
    		populationList.set(generation, evaluate(populationList.get(generation)));
		}
		
		for(Population population : populationList) {
			exec.checkCanceled();
			if(population.getBestIndividual().getFitness() > bestIndividual.getFitness()) {
				bestIndividual = population.getBestIndividual();
			}
		}
    	
//    	return bestIndividual;
    	return populationList;
    	
    }
    
    Population evaluate(Population population) {
    	try {
    		for(Individual individual : population.getIndividuals()) {
    			/*Path filePath; 	//Creates a new temp file that will actually be executed
        		filePath = Files.createTempFile("script", ".py");
            	Files.write(filePath, lines, Charset.forName("UTF-8")); //Writes the Python code down in the file*/
    			String value = "";
    			List<String> valueList = individual.getValue();
    			if(valueList.size() > 1) {
	    			for(String s : valueList) {
	    				value = value.concat(s);
	    			}
    			}else {
    				value = valueList.get(0);
    			}
    			ProcessBuilder processBuilder = new ProcessBuilder("python",this.path.getStringValue(),value);  //Builds the process that will execute the script
        		Process process = processBuilder.start(); 
        		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream())); //Reads the output from the script
        		String str = reader.readLine();
        		
        		individual.setFitness(Double.parseDouble(str)); //Set individual fitness
        		//Files.deleteIfExists(filePath);
        		reader.close();
        		
    		}
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return population; //return population with its fitness and individuals evaluated
    }
    
    Population selection(Population population) {
    	Double cumulative = 0D;
    	
    	Population returnPopulation = new Population();
    	List<Individual> individualList = new ArrayList<Individual>();
    	List<Double> wheelList = new ArrayList<Double>();
    	
    	if(this.elitism.getBooleanValue()) {
    		returnPopulation.getIndividuals().add(population.getBestIndividual());
    	}
    	//Calculates the probability of the individual to go to the next generation
    	for(Individual individual : population.getIndividuals()) {
    		cumulative += (individual.getFitness()/population.getSumFitness());
    		individual.setSelectionProbability(cumulative);
    	}
    	
    	/*while(wheelList.size()<this.individualCount.getIntValue()) {
    		wheelList.add(Math.random());
    	}
    	while(individualList.size()!= population.getIndividuals().size()) {
	    	for(Double d : wheelList) {
	    		for(Individual individual : population.getIndividuals()) {
	    			if(individual.getSelectionProbability() > floor && individual.getSelectionProbability() < d) {
	    				Individual newIndividual = new Individual(individual.getValue(),individual.getFitness(),individual.getSelectionProbability());
	    				individualList.add(newIndividual);
	    				floor = individual.getSelectionProbability();
	    				break;
	    			}
	    		}
	    		if(individualList.size() == population.getIndividuals().size()) {
	    			break;
	    		}
	    	}
    	}*/
    	
    	Double floor = 0D;
    	for(int i = 0; i < this.individualCount.getIntValue(); i++) { //ITERATE THE INDIVIDUALS COUNT TIMES TO THE NEW POPULATION
	    	Double rand = Math.random();
	    	int index = 0;
	    	int last = 0;
	    	while(floor < rand) { //FIND THE INDIVIDUAL CHOOSEN
	    		floor += population.getIndividuals().get(index).getSelectionProbability();
	    		last = index;
	    		index++;
	    	}
	    	individualList.add(new Individual(
    							population.getIndividuals().get(last).getValue(),
    							population.getIndividuals().get(last).getFitness(), 
    							population.getIndividuals().get(last).getSelectionProbability()));
	    	
    	}
    	
    	returnPopulation.setIndividuals(individualList);
    	return returnPopulation;
    }
    
    Population uniformCrossover(Population population) {
    	
    	Population returnPopulation = new Population();
    	
    	int count = 0; //VERIFIES IF THERE'S ALREADY AN INDIVIDUAL SELECTED TO THE CROSSOVER
    	
    	Individual previousIndividual = new Individual(); 	//STORES THE FIRST INDIVIDUAL TO BE CROSSOVERED
    	
    	for(Individual individual : population.getIndividuals()) {
    		
    		if(Math.random()<this.crossoverCount.getDoubleValue()) {
    			if(count <1) {								//IF THE INDIVIDUAL IS CHOOSEN, AND THERE'S NO ONE STORED
    				previousIndividual = individual;
    				count++;
    			}else {										//IF THE INDIVIDUAL IS CHOOSEN, AND THERE'S ALREADY ONE STORED
    				
    				String str = "";
    				
    				if(previousIndividual.getValue().size()>1) {
	    				for(String string : previousIndividual.getValue()) {
	    					str = str.concat(string);
	    				}
    				}else {
    					str = previousIndividual.getValue().get(0);
    				}
    				char[] previousIndividualChars = str.toCharArray();
    				
    				if(individual.getValue().size()>1) {
    					for(String string : individual.getValue()) {
        					str = str.concat(string);
        				}
    				}else {
    					str = individual.getValue().get(0);
    				}
    				    				
					char[] individualChars = str.toCharArray();
					
					char aux;
					int slice = individualChars.length/2+1; //DEFINES WHERE THE SLICE WILL HAPPEN
					for(int i = 0; i < individualChars.length; i++) { 	//ITERATE OVER THE CROMOSSOMES
						if(i >= this.crossoverCount.getDoubleValue()) {								//SWAP THE CROMOSSOMES AFTER THE SLICE
							aux = previousIndividualChars[i];
							previousIndividualChars[i] = individualChars[i];
							individualChars[i] = aux;
						}
					}
					previousIndividual.resetValue();
					previousIndividual.setValue(String.valueOf(previousIndividualChars));
					individual.resetValue();
					individual.setValue(String.valueOf(individualChars));
					
					//ADDS THE NEW INDIVIDUALS TO THE NEW POPULATION
					returnPopulation.getIndividuals().add(previousIndividual);
					returnPopulation.getIndividuals().add(individual);		
					previousIndividual= new Individual();
					count = 0;
    			}
    		}else {
    			//IF THE INDIVIDUAL IS NOT CHOOSEN TO BE CROSSOVERED, IT GOES RIGHT TO THE NEXT POPULATION
    			returnPopulation.getIndividuals().add(individual); 	
    		}
    		
    	}
    	return returnPopulation;
    }
    
	Population doublePointCrossover(Population population) {
    	
    	Population returnPopulation = new Population();
    	
    	int count = 0; //VERIFIES IF THERE'S ALREADY A INDIVIDUAL SELECTED TO THE CROSSOVER
    	
    	Individual previousIndividual = new Individual(); 	//STORES THE FIRST INDIVIDUAL TO BE CROSSOVERED
    	
    	for(Individual individual : population.getIndividuals()) {
    		
    		if(Math.random()<this.crossoverCount.getDoubleValue()) {
    			if(count <1) {								//IF THE INDIVIDUAL IS CHOOSEN, AND THERE'S NO ONE STORED
    				previousIndividual = individual;
    				count++;
    			}else {										//IF THE INDIVIDUAL IS CHOOSEN, AND THERE'S ALREADY ONE STORED
    				
    				String str = "";
    				
    				if(previousIndividual.getValue().size()>1) {
	    				for(String string : previousIndividual.getValue()) {
	    					str = str.concat(string);
	    				}
    				}else {
    					str = previousIndividual.getValue().get(0);
    				}
    				char[] previousIndividualChars = str.toCharArray();
    				
    				if(individual.getValue().size()>1) {
    					for(String string : individual.getValue()) {
        					str = str.concat(string);
        				}
    				}else {
    					str = individual.getValue().get(0);
    				}
    				    				
					char[] individualChars = str.toCharArray();
					
					char aux;
					int firstSlice = -1;
					int secondSlice = -1;
					do {
						firstSlice = (int) Math.random(); //DEFINES WHERE THE SLICE WILL HAPPEN
					}while(firstSlice > individualChars.length-1);
					do {
						secondSlice = (int) Math.random();
					}while (secondSlice>individualChars.length && secondSlice <= firstSlice + 1);
					for(int i = 0; i < individualChars.length; i++) { 	//ITERATE OVER THE CROMOSSOMES
						if(i >= firstSlice && i <= secondSlice ) {								//SWAP THE CROMOSSOMES AFTER THE SLICE
							aux = previousIndividualChars[i];
							previousIndividualChars[i] = individualChars[i];
							individualChars[i] = aux;
						}
					}
					previousIndividual.resetValue();
					previousIndividual.setValue(String.valueOf(previousIndividualChars));
					individual.resetValue();
					individual.setValue(String.valueOf(individualChars));
					
					//ADDS THE NEW INDIVIDUALS TO THE NEW POPULATION
					returnPopulation.getIndividuals().add(previousIndividual);
					returnPopulation.getIndividuals().add(individual);		
					previousIndividual= new Individual();
					count = 0;
    			}
    		}else {
    			//IF THE INDIVIDUAL IS NOT CHOOSEN TO BE CROSSOVERED, IT GOES RIGHT TO THE NEXT POPULATION
    			returnPopulation.getIndividuals().add(individual); 	
    		}
    		
    	}
    	return returnPopulation;
    }

    Population singlePointCrossover(Population population) {
    	
    	Population returnPopulation = new Population();
    	
    	int count = 0; //VERIFIES IF THERE'S ALREADY A INDIVIDUAL SELECTED TO THE CROSSOVER
    	
    	Individual previousIndividual = new Individual(); 	//STORES THE FIRST INDIVIDUAL TO BE CROSSOVERED
    	
    	for(Individual individual : population.getIndividuals()) {
    		
    		if(Math.random()<this.crossoverCount.getDoubleValue()) {
    			if(count <1) {								//IF THE INDIVIDUAL IS CHOOSEN, AND THERE'S NO ONE STORED
    				previousIndividual = individual;
    				count++;
    			}else {										//IF THE INDIVIDUAL IS CHOOSEN, AND THERE'S ALREADY ONE STORED
    				
    				String str = "";
    				
    				if(previousIndividual.getValue().size()>1) {
	    				for(String string : previousIndividual.getValue()) {
	    					str = str.concat(string);
	    				}
    				}else {
    					str = previousIndividual.getValue().get(0);
    				}
    				char[] previousIndividualChars = str.toCharArray();
    				
    				if(individual.getValue().size()>1) {
    					for(String string : individual.getValue()) {
        					str = str.concat(string);
        				}
    				}else {
    					str = individual.getValue().get(0);
    				}
    				    				
					char[] individualChars = str.toCharArray();
					char aux;
					int slice = individualChars.length/2+1; //DEFINES WHERE THE SLICE WILL HAPPEN
					for(int i = 0; i < individualChars.length; i++) { 	//ITERATE OVER THE CROMOSSOMES
						if(i >= slice) {								//SWAP THE CROMOSSOMES AFTER THE SLICE
							aux = previousIndividualChars[i];
							previousIndividualChars[i] = individualChars[i];
							individualChars[i] = aux;
						}
					}
					previousIndividual.resetValue();
					individual.resetValue();
					previousIndividual.setValue(String.valueOf(previousIndividualChars));
					individual.setValue(String.valueOf(individualChars));
					
					//ADDS THE NEW INDIVIDUALS TO THE NEW POPULATION
					returnPopulation.getIndividuals().add(previousIndividual);
					returnPopulation.getIndividuals().add(individual);		
					previousIndividual= new Individual();
					count = 0;
    			}
    		}else {
    			//IF THE INDIVIDUAL IS NOT CHOOSEN TO BE CROSSOVERED, IT GOES RIGHT TO THE NEXT POPULATION
    			returnPopulation.getIndividuals().add(individual); 	
    		}
    		
    	}
    	return returnPopulation;
    }
    
    Population mutation(Population population) {
    	
    	Population returnPopulation = new Population();
    	
    	for(Individual individual : population.getIndividuals()) {
    		
    		if(Math.random()<this.mutationCount.getDoubleValue()) {
    			
    			String str = "";
				
				
				if(individual.getValue().size()>1) {
					for(String string : individual.getValue()) {
    					str = str.concat(string);
    				}
				}else {
					str = individual.getValue().get(0);
				}
				    				
				char[] individualChars = str.toCharArray();
				
				
				for(char c : individualChars) { 			//ITERATE OVER THE CROMOSSOMES
					if(Math.random()<this.mutationCount.getDoubleValue()) {		//SWAP THE CROMOSSOMES
						if(c == '0') {
							c = '1';
						}else {
							c = '0';
						}
					}
				}
				individual.resetValue();
				individual.setValue(String.valueOf(individualChars));
				returnPopulation.getIndividuals().add(individual);
    		}else {
    			returnPopulation.getIndividuals().add(individual);
    		}
    	}
    	
    	return returnPopulation; 
    }

}

