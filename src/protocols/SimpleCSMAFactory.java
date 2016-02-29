package protocols;

import simulator.Node;
import simulator.NodeFactory;
import simulator.Simulator;
import topology.topologyFoundationCode.Vertex;

/**
 * Created by ochipara on 2/13/16.
 */
public class SimpleCSMAFactory extends NodeFactory{
	
	private final int contentionWindow;
	private final int maxQueueSize;
	
	/**
	 * SimpleCSMAFactory constructor
	 * @param maxQueueSize
	 * @param contentionWindow
	 */
	public SimpleCSMAFactory(int maxQueueSize, int contentionWindow) {
		this.contentionWindow = contentionWindow;
		this.maxQueueSize = maxQueueSize;
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
        return new SimpleCSMA(vertex, simulator, maxQueueSize, contentionWindow, seed);
    }
}
