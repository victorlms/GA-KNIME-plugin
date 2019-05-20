package org.knime.geneticalgoritm;

import java.lang.reflect.Array;

import javax.swing.JFileChooser;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnFilter;import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelFilterString;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

public class IndividualSettingsDialog extends DefaultNodeSettingsPane {

	protected IndividualSettingsDialog(){
		super();
		
		createNewGroup("Individual Settings");
		//LENGTH
		addDialogComponent(new DialogComponentNumber(
				new SettingsModelIntegerBounded(
						IndividualSettingsModel.LENGTH_STRING,
						IndividualSettingsModel.INDIVIDUAL_LENGTH,
						1,1000
				), 
				"Individual length", 1));
		//SYMBOLS
		addDialogComponent(new DialogComponentString(
				new SettingsModelString(
						IndividualSettingsModel.SYMBOLS_STRING, 
	    				IndividualSettingsModel.SYMBOLS),
				"Symbols to represent each genoma"));
		
		//COUNT
		addDialogComponent(new DialogComponentNumber(
				new SettingsModelIntegerBounded(
						IndividualSettingsModel.COUNT_STRING,
						IndividualSettingsModel.POPULATION_COUNT,
						1,1000
				), 
				"Number of individuals for the population", 1));

		createNewGroup("First Population");
		
		addDialogComponent(new DialogComponentBoolean(
				new SettingsModelBoolean(
						IndividualSettingsModel.INITIAL_STRING,
						IndividualSettingsModel.INITIAL_POPULATION),
						"Generate random population"));
		
		addDialogComponent(new DialogComponentFileChooser(
				new SettingsModelString(IndividualSettingsModel.COLUMN_NAME_STRING,IndividualSettingsModel.COLUMN_NAME),
				"0",JFileChooser.OPEN_DIALOG, false,".xls|.txt"));
		
	}
	
}
