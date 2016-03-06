package topology.exampleTopologies;

import topology.topologyFoundationCode.Topology;

/**
 * Extend this abstract class to create and store defined topologies
 * @author ryanbrummet
 *
 */
public abstract class AbstractExampleTopology {
	
	protected boolean printTopologyToStdOut;
	protected String saveFileName;
	
	/**
	 * Use this constructor to optionally print the topology created by getNetInstanceOfThisTopology to stdout
	 * @param printTopologyToStdOut
	 */
	public AbstractExampleTopology(boolean printTopologyToStdOut) {
		this.printTopologyToStdOut = printTopologyToStdOut;
	}
	
	/**
	 * Use this constructor to save the topology created by getNewInstanceOfThisTopology and optionally print it to stdout
	 * @param printTopologyToStdOut
	 * @param saveFileName
	 */
	public AbstractExampleTopology(boolean printTopologyToStdOut, String saveFileName) {
		this.printTopologyToStdOut = printTopologyToStdOut;
		this.saveFileName = saveFileName;
	}

	/**
	 * Used to create a predefined topology
	 * @return
	 */
	public abstract Topology getNewInstanceOfThisTopology();
}
