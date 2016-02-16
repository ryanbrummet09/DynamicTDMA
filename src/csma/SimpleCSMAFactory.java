package csma;

import simulator.Node;
import simulator.NodeFactory;
import simulator.Simulator;
import topology.Vertex;

/**
 * Created by ochipara on 2/13/16.
 */
public class SimpleCSMAFactory extends NodeFactory{
    @Override
    public Node newNode(Vertex vertex, Simulator simulator, int seed) {
        return new SimpleCSMA(vertex, simulator, seed);
    }
}
