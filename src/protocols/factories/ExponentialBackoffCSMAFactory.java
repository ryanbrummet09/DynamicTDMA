package protocols.factories;

import protocols.nodes.ExponentialBackoffCSMA;
import simulator.Node;
import simulator.NodeFactory;
import simulator.Simulator;
import topology.topologyFoundationCode.Vertex;

/**
 * 
 * @author ryanbrummet
 * created 2/16/16
 *
 */
public class ExponentialBackoffCSMAFactory extends NodeFactory{
	
	private final int minContentionWindow;
	private final int maxContentionWindow;
	private final int maxQueueSize;
	
	/**
	 * ExponentialBackoffCSMAFactory constructor
	 * @param maxQueueSize
	 * @param minContentionWindow
	 * @param maxContentionWindow
	 */
	public ExponentialBackoffCSMAFactory(int maxQueueSize, int minContentionWindow, int maxContentionWindow) {
		this.minContentionWindow = minContentionWindow;
		this.maxContentionWindow = maxContentionWindow;
		this.maxQueueSize = maxQueueSize;
	}
	
	/**
	 * creates a new ExponentialBackoffCSMA node using the factories contention window settings
	 * @param vertex
	 * @param oldSimulator
	 * @return
	 */
	@Override
	public Node newNode(Vertex vertex, Simulator simulator, int seed) {
        return new ExponentialBackoffCSMA(vertex, simulator, maxQueueSize, minContentionWindow, maxContentionWindow, seed);
    }

}
