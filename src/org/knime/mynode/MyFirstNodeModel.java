package org.knime.mynode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.lang.String;


import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.container.ColumnRearranger;
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

import sun.text.normalizer.Replaceable;

import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;


/**
 * This is the model implementation of MyFirst.
 * Node teste para genética
 *
 * @author Victor
 */
public class MyFirstNodeModel extends NodeModel {
    
    // the logger instance
    private static final NodeLogger logger = NodeLogger
            .getLogger(MyFirstNodeModel.class);
        
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
	
    /** initial default count value. */
    static final int INDIVIDUAL_COUNT = 100;
    static final int GENERATION_COUNT = 200;
    static final int CROMOSSOMES_COUNT  = 8;
    static final boolean ELITISM_STATE = true;
    static final double MUTATION_COUNT = 0.01;
    static final double CROSSOVER_COUNT = 0.6;

    static final String SCRIPT_PATH = "";


    // example value: the models count variable filled from the dialog 
    // and used in the models execution method. The default components of the
    // dialog work with "SettingsModels".

    private final SettingsModelIntegerBounded individualCount = 
    		new SettingsModelIntegerBounded(MyFirstNodeModel.INDIVIDUAL_STR, 
    				MyFirstNodeModel.INDIVIDUAL_COUNT,
    				/*Minimum value*/0, /*Maximun value*/1000);

    private final SettingsModelIntegerBounded generationCount = 
    		new SettingsModelIntegerBounded(MyFirstNodeModel.INDIVIDUAL_STR, 
    				MyFirstNodeModel.INDIVIDUAL_COUNT,
    				Integer.MIN_VALUE, Integer.MAX_VALUE);

    private final SettingsModelIntegerBounded cromossomesCount = 
    		new SettingsModelIntegerBounded(MyFirstNodeModel.INDIVIDUAL_STR, 
    				MyFirstNodeModel.INDIVIDUAL_COUNT,
    				Integer.MIN_VALUE, Integer.MAX_VALUE);

    private final SettingsModelBoolean elitism = 
    		new SettingsModelBoolean(MyFirstNodeModel.ELITISM_STR,MyFirstNodeModel.ELITISM_STATE);

    private final SettingsModelDoubleBounded mutationCount = 
    		new SettingsModelDoubleBounded(MyFirstNodeModel.MUTATION_STR, MyFirstNodeModel.MUTATION_COUNT, 0, 1);

    private final SettingsModelDoubleBounded crossoverCount = 
    		new SettingsModelDoubleBounded(MyFirstNodeModel.MUTATION_STR, MyFirstNodeModel.MUTATION_COUNT, 0, 1);

	private List<String> lines = new ArrayList<>();
    
    

    /**
     * Constructor for the node model.
     */
    protected MyFirstNodeModel() {
    
        // TODO one incoming port and one outgoing port is assumed
        super(1, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {


    	BufferedDataTable table = inData[0];
		int rowCount = (int) table.size();
    	int currentRow = 0;

    	
    	
    	for(DataRow row : table) {
    		exec.checkCanceled();
    		exec.setProgress((double) currentRow/rowCount, 
    				"Processing now " + currentRow);
    		for(int i = 0; i < row.getNumCells(); i++) {
    			DataCell cell = row.getCell(i);
    			if(!cell.isMissing()) {
    				lines.add(cell.toString());
    			}
    		}
    		currentRow++;
    	}
    	
    	Individual bestIndividual = geneticAlgorithm();
    	
    	//String testeString = reader.readLine();
    	//reader.close();
        // the data table spec of the single output table, 
        // the table will have three columns:
        DataColumnSpec[] allColSpecs = new DataColumnSpec[1];
        allColSpecs[0] = 
            new DataColumnSpecCreator("Column 0", StringCell.TYPE).createSpec();
        DataTableSpec outputSpec = new DataTableSpec(allColSpecs);
        // the execution context will provide us with storage capacity, in this
        // case a data container to which we will add rows sequentially
        // Note, this container can also handle arbitrary big data tables, it
        // will buffer to disc if necessary.
        BufferedDataContainer container = exec.createDataContainer(outputSpec);
        RowKey key = new RowKey("Best individual");
        DataCell[] cells = new DataCell[1];
        cells[0] = new StringCell(bestIndividual.getValue());
        DataRow row = new DefaultRow(key, cells);
        container.addRowToTable(row);
        
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
    
    protected Individual geneticAlgorithm() {
    	
    	Individual bestIndividual = new Individual();
    	
    	List<Population> populationList = new ArrayList<Population>();
    	
    	Population firstPopulation = new Population();
    	
    	Random random = new Random();
    	
    	int generation = 0;
    	
    	//Generates an initial population
    	do {
    		
    		Individual individual = new Individual();
    	
    		do {
    			individual.setValue(individual.getValue().concat(String.valueOf(random.nextInt(2))));
    		}while(individual.getValue().length()<CROMOSSOMES_COUNT);
    		
    		firstPopulation.getIndividuals().add(individual);
    	
    	}while(firstPopulation.getIndividuals().size() < INDIVIDUAL_COUNT);
    	
    	firstPopulation = evaluate(firstPopulation);
    	
    	populationList.add(firstPopulation);

		
		while (generation < GENERATION_COUNT) {
    		//Selection
    		populationList.add(selection(populationList.get(generation)));
    		generation++;
    		
    		//crossover
    		populationList.set(generation, crossover(populationList.get(generation)));
    		
    		//mutation
    		populationList.set(generation, mutation(populationList.get(generation)));
    		
    		//evaluation
    		populationList.set(generation, evaluate(populationList.get(generation)));
    	}
		
		for(Population population : populationList) {
			if(population.getBestIndividual().getFitness() > bestIndividual.getFitness()) {
				bestIndividual = population.getBestIndividual();
			}
		}
    	
    	return bestIndividual;
    	
    }
    
    Population evaluate(Population population) {
    	try {
    		for(Individual individual : population.getIndividuals()) {
    			/*Path filePath; 	//Creates a new temp file that will actually be executed
        		filePath = Files.createTempFile("script", ".py");
            	Files.write(filePath, lines, Charset.forName("UTF-8")); //Writes the Python code down in the file*/
    			ProcessBuilder processBuilder = new ProcessBuilder("python",SCRIPT_PATH,individual.getValue());  //Builds the process that will execute the script
        		Process process = processBuilder.start(); 
        		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream())); //Reads the output from the script
        		String str = reader.readLine();
        		individual.setFitness(Double.parseDouble(str)); //Set individual fitness
        		//Files.deleteIfExists(filePath);
        		reader.close();
        		
        		PythonInterpreter python = new PythonInterpreter();
        		python.exec(lines.toString());
    		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return population; //return population with its fitness and individuals evaluated
    }
    
    Population selection(Population population) {
    	Double cumulative = 0D;
    	//Calculates the probability of the individual to go to the next generation
    	for(Individual individual : population.getIndividuals()) {
    		cumulative += (individual.getFitness()/population.getSumFitness());
    		individual.setSelectionProbability(cumulative);
    	}
    	
    	Random random = new Random();
    	Population returnPopulation = new Population();
    	
    	//pick the  individuals for the next generation
    	while(returnPopulation.getIndividuals().size()!=population.getIndividuals().size()){
    		Double choosen = random.nextDouble();
    		for(Individual individual : population.getIndividuals()) {
    			if(individual.getSelectionProbability()<=choosen) {
    				returnPopulation.getIndividuals().add(individual);
    				break;
    			}
    		}
    	}
    	
    	return returnPopulation;
    }
    
    Population crossover(Population population) {
    	Population returnPopulation = new Population();
    	Random random = new Random();
    	int count = 0; //VERIFIES IF THERE'S ALREADY A INDIVIDUAL SELECTED TO THE CROSSOVER
    	Individual previousIndividual = new Individual(); 	//STORES THE FIRST INDIVIDUAL TO BE CROSSOVERED
    	for(Individual individual : population.getIndividuals()) {
    		
    		if(Math.random()<CROSSOVER_COUNT) {
    			if(count <1) {								//IF THE INDIVIDUAL IS CHOOSEN, AND THERE'S NO ONE STORED
    				previousIndividual = individual;
    				count++;
    			}else {										//IF THE INDIVIDUAL IS CHOOSEN, AND THERE'S ALREADY ONE STORED
					//TODO
					char[] previousIndividualChars = previousIndividual.getValue().toCharArray();
					char[] individualChars = individual.getValue().toCharArray();
					char aux;
					int slice = individualChars.length/2+1; //DEFINES WHERE THE SLICE WILL HAPPEN
					for(int i = 0; i < individualChars.length; i++) { 	//ITERATE OVER THE CROMOSSOMES
						if(i >= slice) {								//SWAP THE CROMOSSOMES AFTER THE SLICE
							aux = previousIndividualChars[i];
							previousIndividualChars[i] = individualChars[i];
							individualChars[i] = aux;
						}
					}
					previousIndividual.setValue(previousIndividualChars.toString());
					individual.setValue(individualChars.toString());
					
					//ADDS THE NEW INDIVIDUALS TO THE NEW POPULATION
					returnPopulation.getIndividuals().add(previousIndividual);
					returnPopulation.getIndividuals().add(individual);		
    			}
    			individual = new Individual();
				count = 0;
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
    		
    		if(Math.random()<MUTATION_COUNT) {
    			
				char[] individualChars = individual.getValue().toCharArray();
				
				for(char c : individualChars) { 			//ITERATE OVER THE CROMOSSOMES
					if(Math.random()<MUTATION_COUNT) {		//SWAP THE CROMOSSOMES
						if(c == '0') {
							c = '1';
						}else {
							c = '0';
						}
					}
				}
				
				individual.setValue(individualChars.toString());
				returnPopulation.getIndividuals().add(individual);
    		}else {
    			returnPopulation.getIndividuals().add(individual);
    		}
    	}
    	
    	return returnPopulation; 
    }

}

