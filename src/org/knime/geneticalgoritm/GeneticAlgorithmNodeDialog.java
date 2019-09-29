package org.knime.geneticalgoritm;

import java.awt.List;
import java.util.ArrayList;
import java.util.Arrays;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentButtonGroup;
import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.DialogComponentStringListSelection;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelDoubleBounded;
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
        
        createNewGroup("Population Settings:");
        setHorizontalPlacement(true);
        
        addDialogComponent(new DialogComponentNumber(
        		new SettingsModelIntegerBounded(
        				GeneticAlgorithmNodeModel.GENES_STR,
        				GeneticAlgorithmNodeModel.GENES_COUNT,
        				1, 1000),
        		"Genes", 1));
        addDialogComponent(new DialogComponentNumber(
        		new SettingsModelIntegerBounded(
        				GeneticAlgorithmNodeModel.INDIVIDUAL_STR,
        				GeneticAlgorithmNodeModel.INDIVIDUAL_COUNT,
        				1, 10000),
        		"Cromossomes", 1 ));
        setHorizontalPlacement(false);
        
        addDialogComponent(new DialogComponentBoolean(
        		new SettingsModelBoolean(
        				GeneticAlgorithmNodeModel.ORDER_BASED_STR,
        				GeneticAlgorithmNodeModel.ORDER_BASED),
        				"Order-based"));
        setHorizontalPlacement(true);
        
        addDialogComponent(new DialogComponentBoolean(
        		new SettingsModelBoolean(
        				GeneticAlgorithmNodeModel.ELITISM_STR,
        				GeneticAlgorithmNodeModel.ELITISM_STATE),
        				"Elitism"));
        
        addDialogComponent(new DialogComponentNumber(
        		new SettingsModelIntegerBounded(
        				GeneticAlgorithmNodeModel.BEST_STR,
        				GeneticAlgorithmNodeModel.BEST_COUNT,
        				1, 100),
        		"", 1));
        addDialogComponent(new DialogComponentStringSelection(
        		new SettingsModelString(GeneticAlgorithmNodeModel.ELITISM_TYPE_STR,
        				GeneticAlgorithmNodeModel.ELITISM_TYPE),
        		"", "Individual(s)","%"));

        
        setHorizontalPlacement(false);
        
        /*
		 * addDialogComponent(new DialogComponentString( new
		 * SettingsModelString(GeneticAlgorithmNodeModel.GENE_SYMBOLS_STR,
		 * GeneticAlgorithmNodeModel.GENE_SYMBOLS), "Gene Symbols"));
		 */
        createNewGroup("Operators Settings");
        setHorizontalPlacement(true);
        addDialogComponent(new DialogComponentNumber(
        		new SettingsModelDoubleBounded(
        				GeneticAlgorithmNodeModel.CROSSOVER_STR,
        				GeneticAlgorithmNodeModel.CROSSOVER_COUNT,
        				0, 1),
        		"Crossover rate", 0.01));
        addDialogComponent(new DialogComponentStringSelection(
        		new SettingsModelString(GeneticAlgorithmNodeModel.CROSSOVER_TYPE_STR,
        				GeneticAlgorithmNodeModel.CROSSOVER_TYPE),
        		"Crossover type", "Single point","Double point","Uniform"));

        setHorizontalPlacement(false);
        addDialogComponent(new DialogComponentNumber(
        		new SettingsModelDoubleBounded(
        				GeneticAlgorithmNodeModel.MUTATION_STR,
        				GeneticAlgorithmNodeModel.MUTATION_COUNT,
        				0, 1),
        		"Mutation rate", 0.01));
        
        createNewGroup("Stop Condition:");
        setHorizontalPlacement(true);
        addDialogComponent(new DialogComponentNumber(
        		new SettingsModelIntegerBounded(
        				GeneticAlgorithmNodeModel.GENERATION_STR,
        				GeneticAlgorithmNodeModel.GENERATION_COUNT,
        				1, 10000),
        		"", 1));
        
        addDialogComponent(new DialogComponentStringSelection(
        		new SettingsModelString(GeneticAlgorithmNodeModel.STOP_CONDITION_STR,
        				GeneticAlgorithmNodeModel.STOP_CONDITION),
        		"", "Generations","Minutes"));
        
        setHorizontalPlacement(false);
        
        createNewGroup("Evaluation Function:");
        addDialogComponent(new DialogComponentFileChooser(
        		new SettingsModelString(GeneticAlgorithmNodeModel.SCRIPT_STR, 
        				GeneticAlgorithmNodeModel.SCRIPT_PATH),
        				"Python script path:"));
        
//        createNewGroup("Evaluation Function:");
//        addDialogComponent(new DialogComponentString(
//        		new SettingsModelString(GeneticAlgorithmNodeModel.SCRIPT_STR, 
//        				GeneticAlgorithmNodeModel.SCRIPT_PATH),
//        				"Python script path:"));
        
    }
}

