package org.knime.geneticalgoritm;

import java.awt.List;
import java.util.ArrayList;
import java.util.Arrays;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentButtonGroup;
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
        addDialogComponent(new DialogComponentNumber(
        		new SettingsModelIntegerBounded(
        				GeneticAlgorithmNodeModel.CROMOSSOMES_STR,
        				GeneticAlgorithmNodeModel.CROMOSSOMES_COUNT,
        				1, 1000),
        		"Cromossomes", 1));
        addDialogComponent(new DialogComponentNumber(
        		new SettingsModelIntegerBounded(
        				GeneticAlgorithmNodeModel.INDIVIDUAL_STR,
        				GeneticAlgorithmNodeModel.INDIVIDUAL_COUNT,
        				1, 10000),
        		"Individuals", 1 ));
        addDialogComponent(new DialogComponentString(
        		new SettingsModelString(GeneticAlgorithmNodeModel.GENE_SYMBOLS_STR, 
        				GeneticAlgorithmNodeModel.GENE_SYMBOLS),
        		"Gene Symbols"));
        
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
        createNewGroup("Algorithm Settings:");
        addDialogComponent(new DialogComponentNumber(
        		new SettingsModelIntegerBounded(
        				GeneticAlgorithmNodeModel.GENERATION_STR,
        				GeneticAlgorithmNodeModel.GENERATION_COUNT,
        				1, 10000),
        		"Generations", 1));
        addDialogComponent(new DialogComponentBoolean(
        		new SettingsModelBoolean(
        				GeneticAlgorithmNodeModel.ELITISM_STR,
        				GeneticAlgorithmNodeModel.ELITISM_STATE),
        				"Elitism"));
        
        createNewGroup("Evaluation Function:");
        addDialogComponent(new DialogComponentString(
        		new SettingsModelString(GeneticAlgorithmNodeModel.SCRIPT_STR, 
        				GeneticAlgorithmNodeModel.SCRIPT_PATH),
        				"Python script path:"));
        
    }
}

