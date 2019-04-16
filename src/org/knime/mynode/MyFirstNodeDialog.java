package org.knime.mynode;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelDoubleBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * <code>NodeDialog</code> for the "MyFirst" Node.
 * Node teste para genética
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Victor
 */
public class MyFirstNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring MyFirst node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected MyFirstNodeDialog() {
        super();
        
        createNewGroup("Population Settings:");
        addDialogComponent(new DialogComponentNumber(
        		new SettingsModelIntegerBounded(
        				MyFirstNodeModel.CROMOSSOMES_STR,
        				MyFirstNodeModel.CROMOSSOMES_COUNT,
        				0, 500),
        		"Cromossomes", 1, 5));
        addDialogComponent(new DialogComponentNumber(
        		new SettingsModelIntegerBounded(
        				MyFirstNodeModel.INDIVIDUAL_STR,
        				MyFirstNodeModel.INDIVIDUAL_COUNT,
        				0, 500),
        		"Individuals", 1, 5));
        createNewGroup("Algorithm Settings:");
        addDialogComponent(new DialogComponentNumber(
        		new SettingsModelIntegerBounded(
        				MyFirstNodeModel.GENERATION_STR,
        				MyFirstNodeModel.GENERATION_COUNT,
        				0, 500),
        		"Generations", 1, 5));
        addDialogComponent(new DialogComponentNumber(
        		new SettingsModelDoubleBounded(
        				MyFirstNodeModel.CROSSOVER_STR,
        				MyFirstNodeModel.CROSSOVER_COUNT,
        				0, 500),
        		"Crossover rate", 1, 5));
        addDialogComponent(new DialogComponentNumber(
        		new SettingsModelDoubleBounded(
        				MyFirstNodeModel.MUTATION_STR,
        				MyFirstNodeModel.MUTATION_COUNT,
        				0, 500),
        		"Mutation rate", 1, 5));
        addDialogComponent(new DialogComponentBoolean(
        		new SettingsModelBoolean(
        				MyFirstNodeModel.ELITISM_STR,
        				MyFirstNodeModel.ELITISM_STATE),
        				"Elitism"));
        createNewGroup("Evaluation Function:");
        addDialogComponent(new DialogComponentString(
        		new SettingsModelString(MyFirstNodeModel.SCRIPT_STR, 
        				MyFirstNodeModel.SCRIPT_PATH),
        				"Python script path:"));
        
    }
}

