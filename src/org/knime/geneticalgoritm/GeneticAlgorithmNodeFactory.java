package org.knime.geneticalgoritm;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "GeneticAlgorithm" Node.
 * Node will implement a generic Genetic Algorithm
 *
 * @author Victor
 */
public class GeneticAlgorithmNodeFactory 
        extends NodeFactory<GeneticAlgorithmNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public GeneticAlgorithmNodeModel createNodeModel() {
        return new GeneticAlgorithmNodeModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrNodeViews() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<GeneticAlgorithmNodeModel> createNodeView(final int viewIndex,
            final GeneticAlgorithmNodeModel nodeModel) {
        return new GeneticAlgorithmNodeView(nodeModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDialog() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeDialogPane createNodeDialogPane() {
        return new GeneticAlgorithmNodeDialog();
    }

}

