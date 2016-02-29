package simulator;

import java.util.List;

import topology.topologyFoundationCode.Topology;

/**
 * 
 * @author ryanbrummet
 *
 */
public class AggregateRunStatistics {
	
	private final RunStatistics simulationResults[]; 
	private int numOfSlots;
	
	/**
	 * Constructor for AggregateRunStatistics object
	 * @param numOfSimulations
	 */
	public AggregateRunStatistics(int numOfSimulations) {
		simulationResults = new RunStatistics[numOfSimulations];
		numOfSlots = 0;
	}
	
	/**
	 * Saves/Stores the passed RunStatistics object at the given location in the simulationResults array associated with 
	 * this object.  It is assumed that the first index value will be zero, the second will be one, etc (if this is not true,
	 * this function needs to be redefined)
	 * @param index
	 * @param stats
	 */
	public void addRunStatistics(int index, RunStatistics stats) {
		if(index == 0) {
			numOfSlots = stats.numSlotsUsedInSimulation();
			simulationResults[index] = stats;
		} else {
			if(simulationResults[index - 1].numSlotsUsedInSimulation() == stats.numSlotsUsedInSimulation()) {
				simulationResults[index] = stats;
			} else {
				throw new IllegalArgumentException("Each RunStatistics object associated with an AggregateRunStatistics object must have the same number of slots");
			}
		}
	}
	
	/**
	 * returns the number of packets received at the passed node in the given topology during the execution of the passed simulator
	 * @param targetNodeName
	 * @param topology
	 * @param simulator
	 * @return
	 */
	public static int getNumPacketsReceivedByNode(String targetNodeName, Topology topology, Simulator simulator) {
		List<Node> nodes = simulator.getNodes();
		int numPacketsReceievedAtNode = -1;
		for(Node node : nodes) {
			if(topology.getVertexByName(targetNodeName) == node.getVertex()) {
				numPacketsReceievedAtNode += node.getStatistics().getRx();
				break;
			}
		}
		if(numPacketsReceievedAtNode == -1) {
			throw new IllegalStateException("The targetNodeName passed does not exist in the simulators list of nodes");
		}
		return numPacketsReceievedAtNode;
	}
	
	/**
	 * returns the number of packets sent by the passed node in the given topology during the execution of the passed simulator
	 * @param targetNodeName
	 * @param topology
	 * @param simulator
	 * @return
	 */
	public static int getNumPacketsSentByNode(String targetNodeName, Topology topology, Simulator simulator) {
		List<Node> nodes = simulator.getNodes();
		int numPacketsSentByNode = -1;
		for(Node node : nodes) {
			if(topology.getVertexByName(targetNodeName) == node.getVertex()) {
				numPacketsSentByNode += node.getStatistics().getTx();
				break;
			}
		}
		if(numPacketsSentByNode == -1) {
			throw new IllegalStateException("The targetNodeName passed does not exist in the simulators list of nodes");
		}
		return numPacketsSentByNode;
	}
	
	/**
	 * returns the chance the channel was idle for each slot over each simulation 
	 * (ie the ratio or idle to not idle for every slot over every simulation)
	 * @return
	 */
	public double[] getChannalIdleChanceBySlot() {
		double[] channelIdleSlotPercent = new double[numOfSlots];
		for(int i = 0; i < numOfSlots; i++) {
			int counter = 0;
			for(RunStatistics stats : simulationResults) {
				if(stats.getSuccessfulTransmissions()[i] == 0) {
					counter++;
				}
			}
			channelIdleSlotPercent[i] = counter / simulationResults.length;
		}
		return channelIdleSlotPercent;
	}
	
	/**
	 * returns the chance the channel was under contention for each slot over each simulation
	 * (ie the ratio of under contention to not under contention for every slot over every simulation)
	 * @return
	 */
	public double[] getChannelContentionChanceBySlot() {
		double[] channelContentionSlotPercent = new double[numOfSlots];
		for(int i = 0; i < numOfSlots; i++) {
			int counter = 0;
			for(RunStatistics stats : simulationResults) {
				if(stats.getSuccessfulTransmissions()[i] == 1) {
					counter++;
				}
			}
			channelContentionSlotPercent[i] = counter / simulationResults.length;
		}
		return channelContentionSlotPercent;
	}
	
	/**
	 * returns the chance the channel was used for a successful transmission for each slot over each simulation
	 * (ie the ratio of successful transmissions to either unsuccessful or no transmissions for every slot over every simulation)
	 * @return
	 */
	public double[] getChannelRealUsageChanceBySlot() {
		double[] channelRealUsageSlotPercent = new double[numOfSlots];
		for(int i = 0; i < numOfSlots; i++) {
			int counter = 0;
			for(RunStatistics stats : simulationResults) {
				if(stats.getSuccessfulTransmissions()[i] == 2) {
					counter++;
				}
			}
			channelRealUsageSlotPercent[i] = counter / simulationResults.length;
		}
		return channelRealUsageSlotPercent;
	}
	
	/**
	 * returns the chance the channel will drop a packet for each slot over each simulation
	 * (ie the ratio of packet drops to no packet drops for every slot over every simulation)
	 * @return
	 */
	public double[] getChannelPacketDropChanceBySlot() {
		double[] channelPacketDropSlotPercent = new double[numOfSlots];
		for(int i = 0; i < numOfSlots; i++) {
			int counter = 0;
			for(RunStatistics stats : simulationResults) {
				if(stats.getSuccessfulTransmissions()[i] == 3) {
					counter++;
				}
			}
			channelPacketDropSlotPercent[i] = counter / simulationResults.length;
		}
		return channelPacketDropSlotPercent;
	}
	
	/**
	 * returns the chance a transmission will fail for each slot over each simulation
	 * (ie the ratio of failed transmissions to no failed transmissions for every slot over every simulation)
	 * @return
	 */
	public double[] getChannelFailureChanceBySlot() {
		double[] channelFailureSlotPercent = new double[numOfSlots];
		for(int i = 0; i < numOfSlots; i++) {
			int counter = 0;
			for(RunStatistics stats : simulationResults) {
				if(stats.getSuccessfulTransmissions()[i] == 4) {
					counter++;
				}
			}
			channelFailureSlotPercent[i] = counter / simulationResults.length;
		}
		return channelFailureSlotPercent;
	}
}
