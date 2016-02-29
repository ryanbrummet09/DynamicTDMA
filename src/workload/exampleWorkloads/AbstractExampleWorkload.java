package workload.exampleWorkloads;

import topology.topologyFoundationCode.Topology;
import workload.workloadFoundationCode.Workload;

/**
 * 
 * @author ryanbrummet
 *
 */
public abstract class AbstractExampleWorkload {
	
	protected Topology topology;
	protected final boolean printWorkload;
	protected final int numOfSlotsNeededToSendPacket;
	
	/**
	 * Use this constructor to define the topology and numOfSlotsNeededToSendPackets to create predefined workflows
	 * (via classes that extend this one).  If printWorkload is true, the created workload will be printed to stdout
	 * @param topology
	 * @param numOfSlotsNeededToSendPackets
	 * @param printWorkload
	 */
	public AbstractExampleWorkload(Topology topology, int numOfSlotsNeededToSendPacket, boolean printWorkload) {
		this.topology = topology;
		this.numOfSlotsNeededToSendPacket = numOfSlotsNeededToSendPacket;
		this.printWorkload = printWorkload;
	}
	
	/**
	 * Used to create a predefined workload for the topology and numOfSlotsNeededToSendPackets associated with this object
	 * @return
	 */
	abstract public Workload getNewInstanceOfThisWorkload();

}
