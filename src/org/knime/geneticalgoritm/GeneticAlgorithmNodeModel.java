package org.knime.geneticalgoritm;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.knime.base.node.preproc.joiner.Joiner2NodeModel;
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
 * This is the model implementation of GeneticAlgorithm. Node will implement a
 * generic Genetic Algorithm
 *
 * @author Victor
 */
public class GeneticAlgorithmNodeModel extends NodeModel {

	// the logger instance
	private static final NodeLogger logger = NodeLogger.getLogger(GeneticAlgorithmNodeModel.class);

	protected final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	/**
	 * the settings key which is used to retrieve and store the settings (from the
	 * dialog or from a settings file) (package visibility to be usable from the
	 * dialog).
	 */
	static final String INDIVIDUAL_STR = "Number of individuals";
	static final String GENERATION_STR = "Number of generations";
	static final String GENES_STR = "Number of genes";
	static final String MUTATION_STR = "Mutation rate";
	static final String CROSSOVER_STR = "Crossover rate";
	static final String ELITISM_STR = "Elitism";
	static final String SCRIPT_STR = "Path to Python script";
	static final String CROSSOVER_TYPE_STR = "Crossover type";
	// static final String GENE_SYMBOLS_STR = "Gene symbols";
	static final String STOP_CONDITION_STR = "Stop condition";
	static final String ORDER_BASED_STR = "Order based cromossomes";
	static final String BEST_STR = "How many individuals will be considered the best";
	static final String ELITISM_TYPE_STR = "Type of individuals will be considered the best";

	/** initial default count value. */
	static final int INDIVIDUAL_COUNT = 30;
	static final int GENERATION_COUNT = 20;
	static final int GENES_COUNT = 8;
	static final boolean ELITISM_STATE = true;
	static final double MUTATION_COUNT = 0.05;
	static final double CROSSOVER_COUNT = 0.8;
	static final String CROSSOVER_TYPE = "single";
	// static final String GENE_SYMBOLS = "0,1";
	static final String STOP_CONDITION = "Minutes";
	static final boolean ORDER_BASED = false;
	static final String ELITISM_TYPE = "Individual(s)";

	static final int BEST_COUNT = 2;// "Individual or percent"
	static final String SCRIPT_PATH = "";
	private Long stopTime = 0L;

	// example value: the models count variable filled from the dialog
	// and used in the models execution method. The default components of the
	// dialog work with "SettingsModels".

	private final SettingsModelIntegerBounded individualCount = new SettingsModelIntegerBounded(
			GeneticAlgorithmNodeModel.INDIVIDUAL_STR, GeneticAlgorithmNodeModel.INDIVIDUAL_COUNT, 1, 10000);

	private final SettingsModelIntegerBounded generationCount = new SettingsModelIntegerBounded(
			GeneticAlgorithmNodeModel.GENERATION_STR, GeneticAlgorithmNodeModel.GENERATION_COUNT, 1, 10000);

	private final SettingsModelIntegerBounded genesCount = new SettingsModelIntegerBounded(
			GeneticAlgorithmNodeModel.GENES_STR, GeneticAlgorithmNodeModel.GENES_COUNT, 1, 1000);

	private final SettingsModelBoolean elitism = new SettingsModelBoolean(GeneticAlgorithmNodeModel.ELITISM_STR,
			GeneticAlgorithmNodeModel.ELITISM_STATE);

	private final SettingsModelDoubleBounded mutationCount = new SettingsModelDoubleBounded(
			GeneticAlgorithmNodeModel.MUTATION_STR, GeneticAlgorithmNodeModel.MUTATION_COUNT, 0, 1);

	private final SettingsModelDoubleBounded crossoverCount = new SettingsModelDoubleBounded(
			GeneticAlgorithmNodeModel.CROSSOVER_STR, GeneticAlgorithmNodeModel.CROSSOVER_COUNT, 0, 1);

	private final SettingsModelString path = new SettingsModelString(GeneticAlgorithmNodeModel.SCRIPT_STR,
			GeneticAlgorithmNodeModel.SCRIPT_PATH);

	private SettingsModelString crossoverType = new SettingsModelString(GeneticAlgorithmNodeModel.CROSSOVER_TYPE_STR,
			GeneticAlgorithmNodeModel.CROSSOVER_TYPE);

	/*
	 * private SettingsModelString geneSymbols = new
	 * SettingsModelString(GeneticAlgorithmNodeModel.GENE_SYMBOLS_STR,
	 * GeneticAlgorithmNodeModel.GENE_SYMBOLS);
	 */

	private final SettingsModelString stopCondition = new SettingsModelString(
			GeneticAlgorithmNodeModel.STOP_CONDITION_STR, GeneticAlgorithmNodeModel.STOP_CONDITION);

	private final SettingsModelBoolean orderBased = new SettingsModelBoolean(GeneticAlgorithmNodeModel.ORDER_BASED_STR,
			GeneticAlgorithmNodeModel.ORDER_BASED);

	private final SettingsModelString elitismType = new SettingsModelString(GeneticAlgorithmNodeModel.ELITISM_TYPE_STR,
			GeneticAlgorithmNodeModel.ELITISM_TYPE);

	private final SettingsModelIntegerBounded bestIndividualsCount = new SettingsModelIntegerBounded(
			GeneticAlgorithmNodeModel.BEST_STR, GeneticAlgorithmNodeModel.BEST_COUNT, 1, 100);

	/**
	 * Constructor for the node model.
	 */

	protected GeneticAlgorithmNodeModel() {

		// TODO one incoming port and one outgoing port is assumed
		super(1, 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
			throws Exception {

		String[] symbols = new String[2];
		symbols[0] = "0";
		symbols[1] = "1";// this.geneSymbols.getStringValue().split(",");
		if (this.path.getStringValue().equals("")) {
			throw new Exception("It is required an evaluation function. Check node settings.");
		}
		// if(this.orderBased.getBooleanValue()
		// && this.genesCount.getIntValue() != symbols.length) {
		// throw new Exception("It was provided less symbols than the number of genes
		// available" +
		// "which is an invalid setting for order-based algorithms.");
		// }
		Long startTime = System.currentTimeMillis() / 1000;
		List<Population> populations = new ArrayList<Population>();
		Population firstPopulation = new Population();
		Random random = new Random();
		Integer index = 1;

		DataColumnSpec[] allColSpecs = new DataColumnSpec[3];

		allColSpecs[0] = new DataColumnSpecCreator("Average Fitness", DoubleCell.TYPE).createSpec();
		allColSpecs[1] = new DataColumnSpecCreator("Best Individual", StringCell.TYPE).createSpec();
		allColSpecs[2] = new DataColumnSpecCreator("Best Fitness", DoubleCell.TYPE).createSpec();
//		allColSpecs[3] = new DataColumnSpecCreator("Execution Time", DoubleCell.TYPE).createSpec();
		DataTableSpec outputSpec = new DataTableSpec(allColSpecs);
		// the execution context will provide us with storage capacity, in this
		// case a data container to which we will add rows sequentially
		// Note, this container can also handle arbitrary big data tables, it
		// will buffer to disc if necessary.
		BufferedDataContainer container = exec.createDataContainer(outputSpec);
		BufferedDataTable table = inData[0];
		// populate with provided individuals
		if (table.size() > 0) {
			for (DataRow row : table) {
				Individual definedIndividual = new Individual();
				// String str = inData[0].toString();
				if (row.getNumCells() != genesCount.getIntValue()) {
					throw new Exception(
							"The provided individual: " + row.toString() + " do not match the defined settings.");
				}
				for (int i = 0; i < row.getNumCells(); i++) {
					if (this.orderBased.getBooleanValue()) {
						if (Character.isDigit(row.getCell(i).toString().charAt(0))) {
							throw new Exception("The provided individual: " + row.toString()
									+ " do not match the defined settings.");
						}
					}
					definedIndividual.setValue(row.getCell(i).toString());
				}
				firstPopulation.getIndividuals().add(definedIndividual);
			}
		}
		// Generates an initial population
		do {
			exec.checkCanceled();
			Individual individual = new Individual();

			if (!this.orderBased.getBooleanValue()) {

				do {
					// individual.setValue(individual.getValue().concat(String.valueOf(random.nextInt(2))));
					individual.getValue().add(symbols[random.nextInt(symbols.length)]);
				} while (individual.getValue().size() < this.genesCount.getIntValue());

				firstPopulation.getIndividuals().add(individual);
			} else {
				do {
					String str = String.valueOf(this.ALPHABET.charAt(random.nextInt(this.genesCount.getIntValue())));
					// individual.setValue(individual.getValue().concat(String.valueOf(random.nextInt(2))));
					if (!individual.getValue().contains(str)) {
						individual.getValue().add(str);
					}
				} while (individual.getValue().size() < this.genesCount.getIntValue());

				firstPopulation.getIndividuals().add(individual);
			}

		} while (firstPopulation.getIndividuals().size() < this.individualCount.getIntValue());

		exec.setProgress(0, "Generation " + 0);
		firstPopulation = evaluate(firstPopulation, exec);
		firstPopulation.defineBest(this.bestIndividualsCount.getIntValue());
		Long endTime = System.currentTimeMillis() / 1000;

		firstPopulation.setExecutionTime(endTime - startTime);
		populations.add(firstPopulation);

		RowKey key = new RowKey("Population " + index);
		DataCell[] cell = new DataCell[3];
		cell[0] = new DoubleCell(populations.get(0).getAverageFitness());
		cell[1] = new StringCell(populations.get(0).getBestIndividuals().get(0).getValue().toString());
		cell[2] = new DoubleCell(populations.get(0).getBestIndividuals().get(0).getFitness());
//		cell[3] = new DoubleCell(populations.get(0).getExecutionTime().doubleValue());
		DataRow row = new DefaultRow(key, cell);
		// DataRow fitnessRow = new DefaultRow(fitnessKey, cells);
		container.addRowToTable(row);
		// container.addRowToTable(fitnessRow);
		index++;

		if (!stopCondition.getStringValue().equalsIgnoreCase("Minutes")) {
			for (int i = 0; i < this.generationCount.getIntValue() - 1; i++) {
				exec.checkCanceled();

				populations.add(geneticAlgorithm(exec, populations.get(i)));

				RowKey individualKey = new RowKey("Population " + index);
				DataCell[] cells = new DataCell[3];
				cells[0] = new DoubleCell(populations.get(i + 1).getAverageFitness());
				cells[1] = new StringCell(populations.get(i + 1).getBestIndividuals().get(0).getValue().toString());
				cells[2] = new DoubleCell(populations.get(i + 1).getBestIndividuals().get(0).getFitness());
//				cells[3] = new DoubleCell(populations.get(i + 1).getExecutionTime().doubleValue());
				DataRow individualRow = new DefaultRow(individualKey, cells);
				// DataRow fitnessRow = new DefaultRow(fitnessKey, cells);
				container.addRowToTable(individualRow);
				// container.addRowToTable(fitnessRow);
				exec.setProgress(i / (double) generationCount.getIntValue(), "Generation " + (i + 1));

				index++;
			}
		} else {
			int i = 0;
			do {
				exec.checkCanceled();

				populations.add(geneticAlgorithm(exec, populations.get(i)));

				RowKey individualKey = new RowKey("Population " + index);
				DataCell[] cells = new DataCell[3];
				cells[0] = new DoubleCell(populations.get(i + 1).getAverageFitness());
				cells[1] = new StringCell(populations.get(i + 1).getBestIndividuals().get(0).getValue().toString());
				cells[2] = new DoubleCell(populations.get(i + 1).getBestIndividuals().get(0).getFitness());
//				cells[3] = new DoubleCell(populations.get(i + 1).getExecutionTime().doubleValue());
				DataRow individualRow = new DefaultRow(individualKey, cells);
				// DataRow fitnessRow = new DefaultRow(fitnessKey, cells);
				container.addRowToTable(individualRow);
				// container.addRowToTable(fitnessRow);
				exec.setProgress(this.stopTime / ((double) generationCount.getIntValue() * 60) * 100,
						"Generation " + (i + 1));
				i++;
				index++;
			} while (!checkStopTime(populations.get(i).getExecutionTime()));
		}
		this.stopTime = 0L;

		exec.checkCanceled();
		/*
		 * exec.setProgress(i / (double)m_count.getIntValue(), "Adding row " + i);
		 */
		// once we are done, we close the container and return its table
		container.close();
		BufferedDataTable out = container.getTable();
		return new BufferedDataTable[] { out };
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
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {

		// TODO: check if user settings are available, fit to the incoming
		// table structure, and the incoming types are feasible for the node
		// to execute. If the node can execute in its current state return
		// the spec of its output data table(s) (if you can, otherwise an array
		// with null elements), or throw an exception with a useful user message
		return new DataTableSpec[] { null };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {

		// TODO save user settings to the config object.

		individualCount.saveSettingsTo(settings);
		generationCount.saveSettingsTo(settings);
		genesCount.saveSettingsTo(settings);
		crossoverCount.saveSettingsTo(settings);
		mutationCount.saveSettingsTo(settings);
		elitism.saveSettingsTo(settings);
		path.saveSettingsTo(settings);
		crossoverType.saveSettingsTo(settings);
		// geneSymbols.saveSettingsTo(settings);
		stopCondition.saveSettingsTo(settings);
		orderBased.saveSettingsTo(settings);
		bestIndividualsCount.saveSettingsTo(settings);
		elitismType.saveSettingsTo(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {

		// TODO load (valid) settings from the config object.
		// It can be safely assumed that the settings are valided by the
		// method below.

		individualCount.loadSettingsFrom(settings);
		generationCount.loadSettingsFrom(settings);
		genesCount.loadSettingsFrom(settings);
		crossoverCount.loadSettingsFrom(settings);
		mutationCount.loadSettingsFrom(settings);
		elitism.loadSettingsFrom(settings);
		path.loadSettingsFrom(settings);
		crossoverType.loadSettingsFrom(settings);
		// geneSymbols.loadSettingsFrom(settings);
		stopCondition.loadSettingsFrom(settings);
		orderBased.loadSettingsFrom(settings);
		bestIndividualsCount.loadSettingsFrom(settings);
		elitismType.loadSettingsFrom(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {

		// TODO check if the settings could be applied to our model
		// e.g. if the count is in a certain range (which is ensured by the
		// SettingsModel).
		// Do not actually set any values of any member variables.

		individualCount.validateSettings(settings);
		generationCount.validateSettings(settings);
		genesCount.validateSettings(settings);
		crossoverCount.validateSettings(settings);
		mutationCount.validateSettings(settings);
		elitism.validateSettings(settings);
		path.validateSettings(settings);
		crossoverType.validateSettings(settings);
		// geneSymbols.validateSettings(settings);
		stopCondition.validateSettings(settings);
		orderBased.validateSettings(settings);
		bestIndividualsCount.validateSettings(settings);
		elitismType.validateSettings(settings);

		Boolean order = settings.getBoolean(ORDER_BASED_STR);
		Integer genesCount = settings.getInt(GENES_STR);
		// String genes = settings.getString(GENE_SYMBOLS_STR);
		// String[] symbols = genes.split(",");
		// if(order && symbols.length < genesCount) {
		// throw setValidadeException("It was provided less symbols than the number of
		// genes available "
		// + "which is an invalid setting for order-based algorithms.");
		// }
		String filePath = settings.getString(SCRIPT_STR);
		if (filePath.equals("")) {
			throw setValidadeException("It is required an evaluation function.");
		}
		File file = new File(filePath);
		if (!file.exists()) {
			throw setValidadeException("Evaluation function file could not be founded.");
		}
	}

	private InvalidSettingsException setValidadeException(String message) {
		return new InvalidSettingsException(message);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadInternals(final File internDir, final ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {

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
	protected void saveInternals(final File internDir, final ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {

		// TODO save internal models.
		// Everything written to output ports is saved automatically (data
		// returned by the execute method, models saved in the saveModelContent,
		// and user settings saved through saveSettingsTo - is all taken care
		// of). Save here only the other internals that need to be preserved
		// (e.g. data used by the views).

	}

	protected Boolean checkStopTime(Long time) {
		this.stopTime += time;

		if (this.stopTime.intValue() >= (this.generationCount.getIntValue() * 60)) {
			return true;
		}
		return false;

	}

	/**
	 * 
	 * Method return a random Char defined in alphabet constant
	 */
	protected String nextChar() {
		return Character.toString(ALPHABET.charAt(new Random().nextInt(ALPHABET.length())));
	}

	// protected Individual geneticAlgorithm(final ExecutionContext exec) throws
	// CanceledExecutionException {
	protected Population geneticAlgorithm(final ExecutionContext exec, Population population)
			throws CanceledExecutionException {

		Individual bestIndividual = new Individual();

		// List<Population> populationList = new ArrayList<Population>();

		Population newPopulation = (Population) population.clone();
		newPopulation = selection(newPopulation, exec);

		// int generation = 0;
		// String[] symbols = this.geneSymbols.getStringValue().split(",");

		Long startTime;
		Long endTime;

		exec.checkCanceled();
		// Selection
		startTime = System.currentTimeMillis() / 1000;
		// populationList.add(selection(populationList.get(generation)));
		// generation++;

		// crossover
		if (this.orderBased.getBooleanValue()) {
				newPopulation = shuffleCrossOver(newPopulation, exec);
				
		} else {
			switch (this.crossoverType.getStringValue().toUpperCase()) {
			case "SINGLE POINT":
				newPopulation = singlePointCrossover(newPopulation, exec);
				break;
			case "DOUBLE POINT":
				newPopulation = doublePointCrossover(newPopulation, exec);
				break;
			case "UNIFORM":
				newPopulation = uniformCrossover(newPopulation, exec);
				break;
			default:
				newPopulation = singlePointCrossover(newPopulation, exec);
				break;
			}
		}

		// mutation
		if (this.orderBased.getBooleanValue()) {
//			newPopulation = mutationOrderBased(newPopulation, exec);
		} else {
			newPopulation = mutation(newPopulation, exec);
		}
		// evaluation
		newPopulation = evaluate(newPopulation, exec);
		endTime = System.currentTimeMillis() / 1000;
		newPopulation.setExecutionTime(endTime - startTime);
		endTime = startTime = 0L;
		newPopulation.defineBest(this.bestIndividualsCount.getIntValue());
		bestIndividual = newPopulation.getBestIndividual().get(0);
		NodeLogger logger = NodeLogger.getLogger(Joiner2NodeModel.class);

		logger.warn("Population's best Individual: " + bestIndividual.getValue());
		logger.warn("fitness: " + bestIndividual.getFitness());
		bestIndividual = new Individual();
		// return bestIndividual;
		return newPopulation;

	}

	Population evaluate(Population population, final ExecutionContext exec) throws CanceledExecutionException {

		if (this.elitismType.getStringValue().equals("%")) {
			int i = (this.bestIndividualsCount.getIntValue() * population.getIndividuals().size()) / 100;
			population.defineBest(i);
		} else {
			population.defineBest(this.bestIndividualsCount.getIntValue());
		}

		if (!population.getBestIndividuals().isEmpty()) {
			System.out.println("EVALUATE BEST INDIVIDUAL START");
			System.out.println(population.getBestIndividuals().get(0).getStringValue());
			System.out.println(population.getBestIndividuals().get(0).getFitness());
			System.out.println(population.getIndividuals().indexOf(population.getBestIndividuals().get(0)));

		}
		Long startEvaluation = System.nanoTime();
		try {

			for (Individual individual : population.getIndividuals()) {
				// System.out.println("INDIVIDUAL START");
				// System.out.println(individual.getStringValue());
				// System.out.println(individual.getFitness());
				/*
				 * Path filePath; //Creates a new temp file that will actually be executed
				 * filePath = Files.createTempFile("script", ".py"); Files.write(filePath,
				 * lines, Charset.forName("UTF-8")); //Writes the Python code down in the file
				 */
				String value = "";
				// List<String> valueList = individual.getValue();
				value = individual.getStringValue();

				// if(valueList.size() > 1) {
				// for(String s : valueList) {
				// value = value.concat(s);
				// }
				// }else {
				// value = valueList.get(0);
				// }
				exec.checkCanceled();
				ProcessBuilder processBuilder = new ProcessBuilder("python", this.path.getStringValue(), value); // Builds
																													// the
																													// process
																													// that
																													// will
																													// execute
																													// the
																													// script
				Process process = processBuilder.start();
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream())); // Reads
																												// the
																												// output
																												// from
																												// the
																												// script
				String str = reader.readLine();
				if (str != null) {
					individual.setFitness(Double.parseDouble(str)); // Set individual fitness
				} else {
					individual.setFitness(-1D);
				}
				// Files.deleteIfExists(filePath);
				reader.close();
				reader = null;
				process.destroy();
				processBuilder = null;
				value = null;
				str = null;
				//
				// System.out.println("INDIVIDUAL END");
				// System.out.println(individual.getStringValue());
				// System.out.println(individual.getFitness());

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Long endEvaluation = System.nanoTime();

		System.out.println("Evaluation Time:");
		System.out.println((endEvaluation - startEvaluation));
		if (this.elitismType.getStringValue().equals("%")) {
			int i = (this.bestIndividualsCount.getIntValue() * population.getIndividuals().size()) / 100;
			population.defineBest(i);
		} else {
			population.defineBest(this.bestIndividualsCount.getIntValue());
		}
		System.out.println("EVALUATE BEST INDIVIDUAL");
		System.out.println(population.getBestIndividual().get(0).getStringValue());
		System.out.println(population.getBestIndividual().get(0).getFitness());
		return population; // return population with its fitness and individuals evaluated
	}

	Population selection(Population p, final ExecutionContext exec) throws CanceledExecutionException {

		Population population = new Population();
		population.setIndividuals(new ArrayList<>());
		for (int i = 0; i < p.getIndividuals().size(); i++) {
			population.getIndividuals().add(new Individual());
		}
		// sortedList.addAll(this.individuals);

		Collections.copy(population.getIndividuals(), p.getIndividuals());

		if (this.elitismType.getStringValue().equals("%")) {
			int i = (this.bestIndividualsCount.getIntValue() * population.getIndividuals().size()) / 100;
			population.defineBest(i);
		} else {
			population.defineBest(this.bestIndividualsCount.getIntValue());
		}

		if (!population.getBestIndividual().isEmpty()) {
			System.out.println("SELECTION BEST INDIVIDUAL START");
			System.out.println(population.getBestIndividual().get(0).getStringValue());
			System.out.println(population.getBestIndividual().get(0).getFitness());
		}
		Long startSelection = System.nanoTime();
		Double cumulative = 0D;
		Double rand = Math.random();
		Population returnPopulation = new Population();
		List<Individual> individualList = new ArrayList<Individual>();
		List<Double> wheelList = new ArrayList<Double>();

		// generates a random number list
		// for(Individual individual : population.getIndividuals()) {
		// rand = Math.random();
		// wheelList.add(rand);
		// }

		// Calculates the probability of the individual to go to the next generation
		// for(Individual individual : population.getIndividuals()) {
		// exec.checkCanceled();
		// cumulative += (individual.getFitness()/population.getSumFitness());
		// individual.setSelectionProbability(cumulative);
		// }
		//
		// //check the choosen individuals
		// for(Double d : wheelList) {
		// int index = 0;
		// for(Individual individual : population.getIndividuals()) {
		//
		// if(Math.abs(individual.getSelectionProbability() - d) <
		// Math.abs(population.getIndividuals().get(index).getSelectionProbability() -
		// d)) {
		// index = population.getIndividuals().indexOf(individual);
		// }
		//
		// }
		//
		// individualList.add(population.getIndividuals().get(index));
		//
		// }
		Double sum = population.getSumFitness();
		for (int i = 0; i < (this.elitism.getBooleanValue()
				? (this.individualCount.getIntValue() - this.bestIndividualsCount.getIntValue())
				: this.individualCount.getIntValue()); i++) { // ITERATE THE INDIVIDUALS COUNT TIMES TO THE NEW
																// POPULATION
			Double floor = 0D;
			exec.checkCanceled();
			rand = sum * Math.random();
			// Random ran;
			int index = 0;
			int last = 0;
			while (floor < rand) { // FIND THE INDIVIDUAL CHOOSEN
				floor += population.getIndividuals().get(index).getFitness();
				last = index;
				index++;
			}
			// individualList.add(new Individual(
			// population.getIndividuals().get(last).getValue(),
			// population.getIndividuals().get(last).getFitness(),
			// population.getIndividuals().get(last).getSelectionProbability()));

			individualList.add((Individual) population.getIndividuals().get(last).clone());
			rand = Math.random();

		}

		if (this.elitismType.getStringValue().equals("%")) {
			int i = (this.bestIndividualsCount.getIntValue() * population.getIndividuals().size()) / 100;
			population.defineBest(i);
		} else {
			population.defineBest(this.bestIndividualsCount.getIntValue());
		}

		if (this.elitism.getBooleanValue()) {
			individualList.addAll(population.getBestIndividuals());
		}

		returnPopulation.setIndividuals(individualList);

		if (this.elitismType.getStringValue().equals("%")) {
			int i = (this.bestIndividualsCount.getIntValue() * returnPopulation.getIndividuals().size()) / 100;
			returnPopulation.defineBest(i);
		} else {
			returnPopulation.defineBest(this.bestIndividualsCount.getIntValue());
		}

		if (!population.getBestIndividual().isEmpty()) {
			System.out.println("SELECTION BEST INDIVIDUAL START");
			System.out.println(population.getBestIndividual().get(0).getStringValue());
			System.out.println(population.getBestIndividual().get(0).getFitness());
		}

		System.out.println("SELECTION'S BEST INDIVIDUAL");
		System.out.println(returnPopulation.getBestIndividuals().get(0).getStringValue());
		System.out.println(returnPopulation.getBestIndividuals().get(0).getFitness());

		return returnPopulation;
	}

	Population uniformCrossoverOrderBased(Population population, final ExecutionContext exec)
			throws CanceledExecutionException {
		return new Population();
	}

	Population uniformCrossover(Population population, final ExecutionContext exec) throws CanceledExecutionException {

		Population returnPopulation = new Population();

		int count = 0; // VERIFIES IF THERE'S ALREADY AN INDIVIDUAL SELECTED TO THE CROSSOVER

		Individual previousIndividual = new Individual(); // STORES THE FIRST INDIVIDUAL TO BE CROSSOVERED

		for (Individual ind : population.getIndividuals()) {
			exec.checkCanceled();

			if (this.elitism.getBooleanValue() && (returnPopulation.getIndividuals()
					.size() >= population.getIndividuals().size() - this.bestIndividualsCount.getIntValue())) {
				break;
			}
			if (returnPopulation.getIndividuals().size() == population.getIndividuals().size()) {
				break;
			}
			Individual individual = new Individual();
			individual = (Individual) ind.clone();
			if (Math.random() < this.crossoverCount.getDoubleValue()) {
				if (count < 1) { // IF THE INDIVIDUAL IS CHOOSEN, AND THERE'S NO ONE STORED
					previousIndividual = (Individual) individual.clone();
					count++;
				} else { // IF THE INDIVIDUAL IS CHOOSEN, AND THERE'S ALREADY ONE STORED
					List<String> mask = new ArrayList<>();

					for (int i = 0; i < this.genesCount.getIntValue(); i++) {
						mask.add(String.valueOf(new Random().nextInt(2)));
					}

					String[] previousIndividualChars = new String[previousIndividual.getValue().size()];
					for (int i = 0; i < previousIndividual.getValue().size(); i++) {
						previousIndividualChars[i] = previousIndividual.getValue().get(i);
					}

					String[] individualChars = new String[individual.getValue().size()];
					for (int i = 0; i < individual.getValue().size(); i++) {
						individualChars[i] = individual.getValue().get(i);
					}

					String aux;
					for (int i = 0; i < mask.size(); i++) { // ITERATE OVER THE GENES
						if (mask.get(i).equals("1")) {
							aux = previousIndividualChars[i];
							previousIndividualChars[i] = individualChars[i];
							individualChars[i] = aux;
						}
					}

					previousIndividual.resetValue(new ArrayList<>(Arrays.asList(previousIndividualChars)));
					// previousIndividual.setValue(String.valueOf(previousIndividualChars));
					individual.resetValue(new ArrayList<>(Arrays.asList(individualChars)));
					// individual.setValue(String.valueOf(individualChars));

					// ADDS THE NEW INDIVIDUALS TO THE NEW POPULATION
					returnPopulation.getIndividuals().add(previousIndividual);
					if (returnPopulation.getIndividuals().size() == population.getIndividuals().size()) {
						break;
					}
					returnPopulation.getIndividuals().add(individual);
					previousIndividual = new Individual();
					count = 0;
				}
			} else {
				// IF THE INDIVIDUAL IS NOT CHOOSEN TO BE CROSSOVERED, IT GOES RIGHT TO THE NEXT
				// POPULATION

				returnPopulation.getIndividuals().add(individual);
			}

		}

		if (this.elitismType.getStringValue().equals("%")) {
			int i = (this.bestIndividualsCount.getIntValue() * population.getIndividuals().size()) / 100;
			population.defineBest(i);

			if (this.elitism.getBooleanValue()) {
				returnPopulation.getIndividuals().addAll(population.getBestIndividuals());
			}

			returnPopulation.defineBest(i);
		} else {
			population.defineBest(this.bestIndividualsCount.getIntValue());

			if (this.elitism.getBooleanValue()) {
				returnPopulation.getIndividuals().addAll(population.getBestIndividuals());
			}

			returnPopulation.defineBest(this.bestIndividualsCount.getIntValue());
		}

		System.out.println("UNIFORM CROSSOVER BEST INDIVIDUAL");
		System.out.println(returnPopulation.getBestIndividual().get(0).getStringValue());
		System.out.println(returnPopulation.getBestIndividuals().get(0).getFitness());
		return returnPopulation;
	}

	Population doublePointCrossoverOrderBased(Population population, final ExecutionContext exec)
			throws CanceledExecutionException {
		return new Population();
	}

	Population doublePointCrossover(Population population, final ExecutionContext exec)
			throws CanceledExecutionException {

		Population returnPopulation = new Population();

		int count = 0; // VERIFIES IF THERE'S ALREADY A INDIVIDUAL SELECTED TO THE CROSSOVER

		Individual previousIndividual = new Individual(); // STORES THE FIRST INDIVIDUAL TO BE CROSSOVERED

		for (Individual ind : population.getIndividuals()) {
			exec.checkCanceled();

			if (this.elitism.getBooleanValue() && (returnPopulation.getIndividuals()
					.size() == population.getIndividuals().size() - this.bestIndividualsCount.getIntValue())) {
				break;
			}
			if (returnPopulation.getIndividuals().size() == population.getIndividuals().size()) {
				break;
			}
			Individual individual = new Individual();
			individual = (Individual) ind.clone();
			if (Math.random() < this.crossoverCount.getDoubleValue()) {
				if (count < 1) { // IF THE INDIVIDUAL IS CHOOSEN, AND THERE'S NO ONE STORED
					previousIndividual = (Individual) individual.clone();
					count++;
				} else { // IF THE INDIVIDUAL IS CHOOSEN, AND THERE'S ALREADY ONE STORED

					// String str = "";
					//
					// if(previousIndividual.getValue().size()>1) {
					// for(String string : previousIndividual.getValue()) {
					// str = str.concat(string);
					// }
					// }else {
					// str = previousIndividual.getValue().get(0);
					// }
					// char[] previousIndividualChars = str.toCharArray();
					//
					// if(individual.getValue().size()>1) {
					// for(String string : individual.getValue()) {
					// str = str.concat(string);
					// }
					// }else {
					// str = individual.getValue().get(0);
					// }
					//
					// char[] individualChars = str.toCharArray();
					String[] previousIndividualChars = new String[previousIndividual.getValue().size()];
					for (int i = 0; i < previousIndividual.getValue().size(); i++) {
						previousIndividualChars[i] = previousIndividual.getValue().get(i);
					}

					String[] individualChars = new String[individual.getValue().size()];
					for (int i = 0; i < individual.getValue().size(); i++) {
						individualChars[i] = individual.getValue().get(i);
					}

					String aux;
					int firstSlice = -1;
					int secondSlice = -1;

					do {
						firstSlice = (int) Math.random(); // DEFINES WHERE THE SLICE WILL HAPPEN
					} while (firstSlice > individualChars.length - 3);

					do {
						secondSlice = (int) Math.random();
					} while (secondSlice > individualChars.length - 1 && secondSlice <= firstSlice + 1);

					for (int i = 1; i < individualChars.length; i++) { // ITERATE OVER THE CROMOSSOMES | IT GOES AS
																		// "X|XX...|X"
						if (i >= firstSlice && i <= secondSlice) { // SWAP THE CROMOSSOMES AFTER THE SLICE
							aux = previousIndividualChars[i];
							previousIndividualChars[i] = individualChars[i];
							individualChars[i] = aux;
						}
					}

					previousIndividual.resetValue(new ArrayList<>(Arrays.asList(previousIndividualChars)));
					// previousIndividual.setValue(String.valueOf(previousIndividualChars));
					individual.resetValue(new ArrayList<>(Arrays.asList(individualChars)));
					// individual.setValue(String.valueOf(individualChars));

					// ADDS THE NEW INDIVIDUALS TO THE NEW POPULATION
					returnPopulation.getIndividuals().add(previousIndividual);
					if (returnPopulation.getIndividuals().size() == population.getIndividuals().size()) {
						break;
					}
					returnPopulation.getIndividuals().add(individual);
					previousIndividual = new Individual();
					count = 0;
				}
			} else {
				// IF THE INDIVIDUAL IS NOT CHOOSEN TO BE CROSSOVERED, IT GOES RIGHT TO THE NEXT
				// POPULATION

				returnPopulation.getIndividuals().add(individual);
			}

		}

		if (this.elitismType.getStringValue().equals("%")) {
			int i = (this.bestIndividualsCount.getIntValue() * population.getIndividuals().size()) / 100;
			population.defineBest(i);

			if (this.elitism.getBooleanValue()) {
				returnPopulation.getIndividuals().addAll(population.getBestIndividuals());
			}

			returnPopulation.defineBest(i);
		} else {
			population.defineBest(this.bestIndividualsCount.getIntValue());

			if (this.elitism.getBooleanValue()) {
				returnPopulation.getIndividuals().addAll(population.getBestIndividuals());
			}

			returnPopulation.defineBest(this.bestIndividualsCount.getIntValue());
		}

		System.out.println("DOUBLE POINT CROSSOVER BEST INDIVIDUAL");
		System.out.println(returnPopulation.getBestIndividuals().get(0).getStringValue());
		System.out.println(returnPopulation.getBestIndividuals().get(0).getFitness());
		return returnPopulation;
	}

	Population singlePointCrossoverOrderBased(Population population, final ExecutionContext exec)
			throws CanceledExecutionException {
		return new Population();
	}

	Population singlePointCrossover(Population p, final ExecutionContext exec) throws CanceledExecutionException {

		Population population = new Population();
		population.setIndividuals(new ArrayList<>());
		for (int i = 0; i < p.getIndividuals().size(); i++) {
			population.getIndividuals().add(new Individual());
		}
		// sortedList.addAll(this.individuals);

		Collections.copy(population.getIndividuals(), p.getIndividuals());

		if (this.elitismType.getStringValue().equals("%")) {
			int i = (this.bestIndividualsCount.getIntValue() * population.getIndividuals().size()) / 100;
			population.defineBest(i);
		} else {
			population.defineBest(this.bestIndividualsCount.getIntValue());
		}

		System.out.println("CROSSOVER BEST INDIVIDUAL START");
		System.out.println(population.getBestIndividuals().get(0).getStringValue());
		System.out.println(population.getBestIndividuals().get(0).getFitness());
		Population returnPopulation = new Population();

		int count = 0; // VERIFIES IF THERE'S ALREADY A INDIVIDUAL SELECTED TO THE CROSSOVER

		Individual previousIndividual = new Individual(); // STORES THE FIRST INDIVIDUAL TO BE CROSSOVERED

		for (Individual ind : population.getIndividuals()) {

			exec.checkCanceled();
			if (this.elitism.getBooleanValue() && (returnPopulation.getIndividuals()
					.size() == population.getIndividuals().size() - this.bestIndividualsCount.getIntValue())) {
				break;
			}
			if (returnPopulation.getIndividuals().size() == population.getIndividuals().size()) {
				break;
			}
			Individual individual = (Individual) ind.clone();
			individual.resetValue(individual.getValue());
			if (Math.random() < this.crossoverCount.getDoubleValue()) {
				if (count < 1) { // IF THE INDIVIDUAL IS CHOOSEN, AND THERE'S NO ONE STORED
					previousIndividual.resetValue(individual.getValue());
					count++;
				} else { // IF THE INDIVIDUAL IS CHOOSEN, AND THERE'S ALREADY ONE STORED

					String[] previousIndividualChars = new String[previousIndividual.getValue().size()];
					for (int i = 0; i < previousIndividual.getValue().size(); i++) {
						previousIndividualChars[i] = previousIndividual.getValue().get(i);
					}

					String[] individualChars = new String[individual.getValue().size()];
					for (int i = 0; i < individual.getValue().size(); i++) {
						individualChars[i] = individual.getValue().get(i);
					}
					// ArrayList<String> previousIndividualChars = previousIndividual.getValue();
					// ArrayList<String> individualChars = individual.getValue();

					String aux;

					int slice = individualChars.length / 2 + 1; // DEFINES WHERE THE SLICE WILL HAPPEN
					for (int i = 1; i < individualChars.length - 1; i++) { // ITERATE OVER THE CROMOSSOMES -> IT GOES AS
																			// "X|XX...X" OR "X...XX|X"
						if (i >= slice) { // SWAP THE CROMOSSOMES AFTER THE SLICE
							aux = previousIndividualChars[i];
							previousIndividualChars[i] = individualChars[i];
							individualChars[i] = aux;
						}
					}

					previousIndividual.resetValue(new ArrayList<>(Arrays.asList(previousIndividualChars)));
					// previousIndividual.setValue(String.valueOf(previousIndividualChars));
					individual.resetValue(new ArrayList<>(Arrays.asList(individualChars)));
					// individual.setValue(String.valueOf(individualChars));

					// ADDS THE NEW INDIVIDUALS TO THE NEW POPULATION
					returnPopulation.getIndividuals().add(previousIndividual);
					if (returnPopulation.getIndividuals().size() == population.getIndividuals().size()) {
						break;
					}
					returnPopulation.getIndividuals().add(individual);
					previousIndividual = new Individual();
					individual = null;
					count = 0;
				}
			} else {
				// IF THE INDIVIDUAL IS NOT CHOOSEN TO BE CROSSOVERED, IT GOES RIGHT TO THE NEXT
				// POPULATION
				returnPopulation.getIndividuals().add(individual);
			}

		}

		if (this.elitism.getBooleanValue()) {
			returnPopulation.getIndividuals().addAll(population.getBestIndividuals());
		}

		returnPopulation.defineBest(this.bestIndividualsCount.getIntValue());

		System.out.println("CROSSOVER BEST INDIVIDUAL");
		System.out.println(returnPopulation.getBestIndividuals().get(0).getStringValue());
		System.out.println(returnPopulation.getBestIndividuals().get(0).getFitness());
		return returnPopulation;
	}

	Population shuffleCrossOver(Population population, final ExecutionContext exec) throws CanceledExecutionException {

		Population returnPopulation = new Population();
		int count = 0; // VERIFIES IF THERE'S ALREADY AN INDIVIDUAL SELECTED TO THE CROSSOVER

		Individual previousIndividual = new Individual(); // STORES THE FIRST INDIVIDUAL TO BE CROSSOVERED

		for (Individual ind : population.getIndividuals()) {
			exec.checkCanceled();

			if (this.elitism.getBooleanValue() && (returnPopulation.getIndividuals()
					.size() >= population.getIndividuals().size() - this.bestIndividualsCount.getIntValue())) {
				break;
			}
			if (returnPopulation.getIndividuals().size() == population.getIndividuals().size()) {
				break;
			}
			Individual individual = new Individual();
			individual = (Individual) ind.clone();
			if (Math.random() < this.crossoverCount.getDoubleValue()) {
				if (count < 1) { // IF THE INDIVIDUAL IS CHOOSEN, AND THERE'S NO ONE STORED
					previousIndividual = (Individual) individual.clone();
					count++;
				} else { // IF THE INDIVIDUAL IS CHOOSEN, AND THERE'S ALREADY ONE STORED
					List<String> mask = new ArrayList<>();

					for (int i = 0; i < this.genesCount.getIntValue(); i++) {
						mask.add(String.valueOf(new Random().nextInt(2)));
					}

					String[] previousIndividualChars = new String[previousIndividual.getValue().size()];
					for (int i = 0; i < previousIndividual.getValue().size(); i++) {
						previousIndividualChars[i] = previousIndividual.getValue().get(i);
					}

					String[] individualChars = new String[individual.getValue().size()];
					for (int i = 0; i < individual.getValue().size(); i++) {
						individualChars[i] = individual.getValue().get(i);
					}

					String aux;
					String[] child1 = new String[individualChars.length];
					List<String> child1Missing = new ArrayList<>();
					String[] child2 = new String[previousIndividualChars.length];
					List<String> child2Missing = new ArrayList<>();
					
					for (int i = 0; i < mask.size(); i++) {
						if (mask.get(i).equals("1")) {
							child1[i] = individualChars[i];
							child2[i] = previousIndividualChars[i];
						} else {
							child1[i] = "0";
							child2[i] = "0";
						}
//						child1Missing.add("0");
//						child2Missing.add("0");
					}

					for (int i = 0; i < mask.size(); i++) {
						if (!child1.toString().contains(individualChars[i])) {
							child1Missing.add(individualChars[i]);
						}
						if (!child2.toString().contains(previousIndividualChars[i])) {
							child2Missing.add(previousIndividualChars[i]);
						}
					}
					
					for(int i = 0; i < individualChars.length; i++) {
						if(child1Missing.contains(individualChars[i].toString())) {
							for(int j = 0;j<child1.length;j++) {
								if(child1[i].equals("0")) {
									child1[i] = individualChars[i];
									break;
								}
							}
						}
					}
					
					for(int i = 0; i < previousIndividualChars.length; i++) {
						if(child2Missing.contains(previousIndividualChars[i].toString())) {
							for(int j = 0;j<child2.length;j++) {
								if(child2[i].equals("0")) {
									child2[i] = previousIndividualChars[i];
									break;
								}
							}
						}
					}

					previousIndividual.resetValue(new ArrayList<>(Arrays.asList(previousIndividualChars)));
					// previousIndividual.setValue(String.valueOf(previousIndividualChars));
					individual.resetValue(new ArrayList<>(Arrays.asList(individualChars)));
					// individual.setValue(String.valueOf(individualChars));

					// ADDS THE NEW INDIVIDUALS TO THE NEW POPULATION
					returnPopulation.getIndividuals().add(previousIndividual);
					if (returnPopulation.getIndividuals().size() == population.getIndividuals().size()) {
						break;
					}
					returnPopulation.getIndividuals().add(individual);
					previousIndividual = new Individual();
					count = 0;
				}
			} else {
				// IF THE INDIVIDUAL IS NOT CHOOSEN TO BE CROSSOVERED, IT GOES RIGHT TO THE NEXT
				// POPULATION

				returnPopulation.getIndividuals().add(individual);
			}

		}

		if (this.elitismType.getStringValue().equals("%")) {
			int i = (this.bestIndividualsCount.getIntValue() * population.getIndividuals().size()) / 100;
			population.defineBest(i);

			if (this.elitism.getBooleanValue()) {
				returnPopulation.getIndividuals().addAll(population.getBestIndividuals());
			}

			returnPopulation.defineBest(i);
		} else {
			population.defineBest(this.bestIndividualsCount.getIntValue());

			if (this.elitism.getBooleanValue()) {
				returnPopulation.getIndividuals().addAll(population.getBestIndividuals());
			}

			returnPopulation.defineBest(this.bestIndividualsCount.getIntValue());
		}

		System.out.println("SHUFFLE CROSSOVER BEST INDIVIDUAL");
		System.out.println(returnPopulation.getBestIndividual().get(0).getStringValue());
		System.out.println(returnPopulation.getBestIndividuals().get(0).getFitness());
		return returnPopulation;

	}

	Population mutationOrderBased(Population population, final ExecutionContext exec)
			throws CanceledExecutionException {
		return new Population();
	}

	Population mutation(Population p, final ExecutionContext exec) throws CanceledExecutionException {
		Population population = new Population();
		population.setIndividuals(new ArrayList<>());
		for (int i = 0; i < p.getIndividuals().size(); i++) {
			population.getIndividuals().add(new Individual());
		}
		// sortedList.addAll(this.individuals);

		Collections.copy(population.getIndividuals(), p.getIndividuals());

		if (this.elitismType.getStringValue().equals("%")) {
			int i = (this.bestIndividualsCount.getIntValue() * population.getIndividuals().size()) / 100;
			population.defineBest(i);
		} else {
			population.defineBest(this.bestIndividualsCount.getIntValue());
		}

		System.out.println("MUTATION BEST INDIVIDUAL START");
		System.out.println(population.getBestIndividuals().get(0).getStringValue());
		System.out.println(population.getBestIndividuals().get(0).getFitness());

		Population returnPopulation = new Population();

		for (Individual ind : population.getIndividuals()) {
			exec.checkCanceled();
			if (this.elitism.getBooleanValue() && (returnPopulation.getIndividuals()
					.size() == population.getIndividuals().size() - this.bestIndividualsCount.getIntValue())) {
				break;
			}
			if (returnPopulation.getIndividuals().size() >= population.getIndividuals().size()) {
				break;
			}
			Individual individual = new Individual();
			individual = (Individual) ind.clone();

			if (Math.random() < this.mutationCount.getDoubleValue()) {

				String[] individualChars = new String[individual.getValue().size()];

				for (int i = 0; i < individual.getValue().size(); i++) {
					individualChars[i] = individual.getValue().get(i);
				}

				for (int i = 0; i < individualChars.length; i++) {
					if (Math.random() < this.mutationCount.getDoubleValue()) { // SWAP THE CROMOSSOMES
						if (individualChars[i].equals("0")) {
							individualChars[i] = "1";
						} else {
							individualChars[i] = "0";
						}
					}
				}

				individual.resetValue(new ArrayList<>(Arrays.asList(individualChars)));

				returnPopulation.getIndividuals().add(individual);
				if (returnPopulation.getIndividuals().size() == population.getIndividuals().size()) {
					break;
				}
			} else {
				returnPopulation.getIndividuals().add(individual);
			}
		}

		if (this.elitismType.getStringValue().equals("%")) {
			int i = (this.bestIndividualsCount.getIntValue() * population.getIndividuals().size()) / 100;
			population.defineBest(i);

			if (this.elitism.getBooleanValue()) {
				returnPopulation.getIndividuals().addAll(population.getBestIndividuals());
			}

			returnPopulation.defineBest(i);
		} else {
			population.defineBest(this.bestIndividualsCount.getIntValue());

			if (this.elitism.getBooleanValue()) {
				returnPopulation.getIndividuals().addAll(population.getBestIndividuals());
			}

			returnPopulation.defineBest(this.bestIndividualsCount.getIntValue());
		}

		for (Individual i : returnPopulation.getIndividuals()) {
			if (i.getFitness() > 0) {
				System.out.println("AQUI");
				System.out.println(i.getStringValue());
				System.out.println(i.getFitness());
				System.out.println(returnPopulation.getIndividuals().indexOf(i));
			}
		}

		for (Individual i : population.getBestIndividuals()) {
			if (i.getFitness() > 0) {
				System.out.println("AQUI 2");
				System.out.println(i.getStringValue());
				System.out.println(i.getFitness());
			}
		}

		System.out.println("MUTATION BEST INDIVIDUAL");
		System.out.println(returnPopulation.getBestIndividuals().get(0).getStringValue());
		System.out.println(returnPopulation.getBestIndividuals().get(0).getFitness());
		return returnPopulation;
	}

}
