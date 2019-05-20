package org.knime.geneticalgoritm;

import java.io.File;
import java.io.IOException;

import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

public class IndividualSettingsModel extends NodeModel {
	
	private static final NodeLogger logger = NodeLogger
            .getLogger(IndividualSettingsModel.class);
	
	public static final String COLUMN_NAME_STRING = "Individual";
	public static final String SYMBOLS_STRING = "Símbolos de representação";
	public static final String LENGTH_STRING = "Quantidade de genomas do cromossomo do indivíduo";
	public static final String COUNT_STRING = "Quantidade de indivíduos da população";
	public static final String INITIAL_STRING = "Se a população será gerada ou não";

	public static final String COLUMN_NAME = " ";
	public static final String SYMBOLS = "0 1";
	public static final Integer INDIVIDUAL_LENGTH = 4;
	public static final Integer POPULATION_COUNT = 100;
	public static final Boolean INITIAL_POPULATION = false;

	private final SettingsModelString columnName = 
    		new SettingsModelString(IndividualSettingsModel.COLUMN_NAME_STRING, 
    				IndividualSettingsModel.COLUMN_NAME);
	
	private final SettingsModelString individualSymbols = 
    		new SettingsModelString(IndividualSettingsModel.SYMBOLS_STRING, 
    				IndividualSettingsModel.SYMBOLS);
	
	private final SettingsModelIntegerBounded individualLength = 
    		new SettingsModelIntegerBounded(IndividualSettingsModel.LENGTH_STRING, 
    				IndividualSettingsModel.INDIVIDUAL_LENGTH,
    				1, 10000);
	
	private final SettingsModelIntegerBounded populationCount = 
    		new SettingsModelIntegerBounded(IndividualSettingsModel.COUNT_STRING, 
    				IndividualSettingsModel.POPULATION_COUNT,
    				1, 10000);
	
	private final SettingsModelBoolean initialPopulation = 
    		new SettingsModelBoolean(IndividualSettingsModel.INITIAL_STRING, 
    				IndividualSettingsModel.INITIAL_POPULATION);
	
	protected IndividualSettingsModel() {
	    
        // TODO one incoming port and one outgoing port is assumed
        super(0, 1);
    }
	
	 @Override
	    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
	            final ExecutionContext exec) throws Exception {
		
		 String testeString = columnName.getStringValue();
				 System.out.println(testeString);
		 return inData;
	
	 }
	
	@Override
	protected void loadInternals(File nodeInternDir, ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void saveInternals(File nodeInternDir, ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		// TODO Auto-generated method stub

	}

	 @Override
	    protected void saveSettingsTo(final NodeSettingsWO settings) {

	        // TODO save user settings to the config object.
	        
	        columnName.saveSettingsTo(settings);
	        individualSymbols.saveSettingsTo(settings);
	        individualLength.saveSettingsTo(settings);
	        populationCount.saveSettingsTo(settings);
	        initialPopulation.saveSettingsTo(settings);

	    }

	@Override
	protected void validateSettings(NodeSettingsRO settings) throws InvalidSettingsException {
		// TODO Auto-generated method stub
		columnName.validateSettings(settings);
        individualSymbols.validateSettings(settings);
        individualLength.validateSettings(settings);
        populationCount.validateSettings(settings);
        initialPopulation.validateSettings(settings);

	}

	@Override
	protected void loadValidatedSettingsFrom(NodeSettingsRO settings) throws InvalidSettingsException {
		// TODO Auto-generated method stub
		 columnName.loadSettingsFrom(settings);
		individualSymbols.loadSettingsFrom(settings);
		individualLength.loadSettingsFrom(settings);
		populationCount.loadSettingsFrom(settings);
		initialPopulation.loadSettingsFrom(settings);
	}

	@Override
	protected void reset() {
		// TODO Auto-generated method stub

	}
	
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


}
