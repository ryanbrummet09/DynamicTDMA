package simulator;

import topology.Vertex;

/**
 * Created by ochipara on 2/13/16.
 */
public abstract class NodeFactory {
    public abstract Node newNode(Vertex vertex, Simulator simulator);
}
