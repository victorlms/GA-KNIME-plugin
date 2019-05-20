package org.knime.geneticalgoritm;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

public class IndividualSettingsFactory extends NodeFactory<IndividualSettingsModel> {

	@Override
	public IndividualSettingsModel createNodeModel() {
		// TODO Auto-generated method stub
		return new IndividualSettingsModel();
	}

	@Override
	protected int getNrNodeViews() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public NodeView<IndividualSettingsModel> createNodeView(int viewIndex, IndividualSettingsModel nodeModel) {
		// TODO Auto-generated method stub
		return new IndividualSettingsView(nodeModel);
	}

	@Override
	protected boolean hasDialog() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected NodeDialogPane createNodeDialogPane() {
		// TODO Auto-generated method stub
		return new IndividualSettingsDialog();
	}

}
