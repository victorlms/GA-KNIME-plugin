package org.knime.geneticalgoritm;

import java.awt.List;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnFilter;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelDoubleBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelFilterString;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * <code>NodeDialog</code> for the "GeneticAlgorithm" Node.
 * Node will implement a generic Genetic Algorithm
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Victor
 */
public class GeneticAlgorithmNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring MyFirst node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected GeneticAlgorithmNodeDialog() {
        super();
        
        //INDIVIDUAL SETTINGS
        createNewGroup("Individual Settings:");
        
        //NUMBER OF GENES
        addDialogComponent(new DialogComponentNumber(
        		new SettingsModelIntegerBounded(
        				GeneticAlgorithmNodeModel.CROMOSSOMES_STR,
        				GeneticAlgorithmNodeModel.CROMOSSOMES_COUNT,
        				1, 1000),
        		"Cromossomes", 1));
        
        //GENE SYMBOLS
        addDialogComponent(new DialogComponentString(
        		new SettingsModelString(GeneticAlgorithmNodeModel.SCRIPT_STR, 
        				GeneticAlgorithmNodeModel.SCRIPT_PATH),
        				"Python script path:"));
        
        //POPULATION SETTING
        createNewGroup("Population Settings:");

        //INDIVIDUAL COUNT
        addDialogComponent(new DialogComponentNumber(
        		new SettingsModelIntegerBounded(
        				GeneticAlgorithmNodeModel.INDIVIDUAL_STR,
        				GeneticAlgorithmNodeModel.INDIVIDUAL_COUNT,
        				1, 10000),
        		"Individuals", 1 ));
        
        //ALGORITHM SETTINGS
        createNewGroup("Algorithm Settings:");
        
        //GENERATION COUNT
        addDialogComponent(new DialogComponentNumber(
        		new SettingsModelIntegerBounded(
        				GeneticAlgorithmNodeModel.GENERATION_STR,
        				GeneticAlgorithmNodeModel.GENERATION_COUNT,
        				1, 10000),
        		"Generations", 1));
        
        //CROSSOVER RATE
        addDialogComponent(new DialogComponentNumber(
        		new SettingsModelDoubleBounded(
        				GeneticAlgorithmNodeModel.CROSSOVER_STR,
        				GeneticAlgorithmNodeModel.CROSSOVER_COUNT,
        				0, 1),
        		"Crossover rate", 0.01));
        
        //MUTATION RATE
        addDialogComponent(new DialogComponentNumber(
        		new SettingsModelDoubleBounded(
        				GeneticAlgorithmNodeModel.MUTATION_STR,
        				GeneticAlgorithmNodeModel.MUTATION_COUNT,
        				0, 1),
        		"Mutation rate", 0.01));
        
        //USE OF ELITISM
        addDialogComponent(new DialogComponentBoolean(
        		new SettingsModelBoolean(
        				GeneticAlgorithmNodeModel.ELITISM_STR,
        				GeneticAlgorithmNodeModel.ELITISM_STATE),
        				"Elitism"));
        createNewGroup("Evaluation Function:");
        
        //PATH TO THE EVALUATION FUNCTION
        addDialogComponent(new DialogComponentString(
        		new SettingsModelString(GeneticAlgorithmNodeModel.SCRIPT_STR, 
        				GeneticAlgorithmNodeModel.SCRIPT_PATH),
        				"Python script path:"));
        
    }
}

