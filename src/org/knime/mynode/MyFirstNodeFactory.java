package org.knime.mynode;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "MyFirst" Node.
 * Node teste para genética
 *
 * @author Victor
 */
public class MyFirstNodeFactory 
        extends NodeFactory<MyFirstNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public MyFirstNodeModel createNodeModel() {
        return new MyFirstNodeModel();
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
    public NodeView<MyFirstNodeModel> createNodeView(final int viewIndex,
            final MyFirstNodeModel nodeModel) {
        return new MyFirstNodeView(nodeModel);
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
        return new MyFirstNodeDialog();
    }

}

