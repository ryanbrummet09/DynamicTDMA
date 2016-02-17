package csma;

import simulator.Node;
import simulator.NodeFactory;
import simulator.Simulator;
import topology.Vertex;

/**
 * 
 * @author ryanbrummet
 * created 2/16/16
 *
 */
public class ExponentialBackoffCSMAFactory extends NodeFactory{
	
	private final int minContentionWindow;
	private final int maxContentionWindow;
	
	/**
	 * ExponentialBackoffCSMA constructor
	 * @param minContentionWindow
	 * @param maxContentionWindow
	 */
	public ExponentialBackoffCSMAFactory(int minContentionWindow, int maxContentionWindow) {
		this.minContentionWindow = minContentionWindow;
		this.maxContentionWindow = maxContentionWindow;
	}
	
	/**
	 * creates a new ExponentialBackoffCSMA node using the factories contention window settings
	 * @param vertex
	 * @param simulator
	 * @return
	 */
	@Override
	public Node newNode(Vertex vertex, Simulator simulator, int seed) {
        return new ExponentialBackoffCSMA(vertex, simulator, minContentionWindow, maxContentionWindow, seed);
    }

}
