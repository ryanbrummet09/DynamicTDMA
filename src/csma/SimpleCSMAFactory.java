package csma;

import simulator.Node;
import simulator.NodeFactory;
import simulator.Simulator;
import topology.Vertex;

/**
 * Created by ochipara on 2/13/16.
 */
public class SimpleCSMAFactory extends NodeFactory{
	
	private final int contentionWindow;
	
	/**
	 * SimpleCSMAFactory constructor
	 * @param contentionWindow
	 */
	public SimpleCSMAFactory(int contentionWindow) {
		this.contentionWindow = contentionWindow;
	}
	
    /**
     * Creates a new SimpleCSMA node using the factories contention window settings
     * @param vertex
     * @param simulator
     * @param seed
     * @return
     */
	@Override
    public Node newNode(Vertex vertex, Simulator simulator, int seed) {
        return new SimpleCSMA(vertex, simulator, contentionWindow, seed);
    }
}
