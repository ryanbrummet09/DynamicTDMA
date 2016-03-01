package simulator;

import topology.topologyFoundationCode.Vertex;

/**
 * Created by ochipara on 2/13/16.
 */
public abstract class NodeFactory {
    public abstract Node newNode(Vertex vertex, Simulator imulator, int seed);
}
