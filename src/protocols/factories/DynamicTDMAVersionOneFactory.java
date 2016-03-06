package protocols.factories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import protocols.nodes.DynamicTDMAVersionOne;
import simulator.Node;
import simulator.NodeFactory;
import simulator.Simulator;
import topology.topologyFoundationCode.Topology;
import topology.topologyFoundationCode.Vertex;
import workload.workloadFoundationCode.Workload;

/**
 * 
 * @author ryanbrummet
 *
 */
public class DynamicTDMAVersionOneFactory extends NodeFactory {
	
	private int maxQueueSize;
	private int contentionWindow;
	private int[] schedule;

	/**
	 * DynamicTDMAVersionOneFactory constructor, assuming a schedule exists
	 * @param maxQueueSize
	 * @param contentionWindow
	 * @param schedule
	 */
	public DynamicTDMAVersionOneFactory(int maxQueueSize, int contentionWindow, int[] schedule) {
		this.maxQueueSize = maxQueueSize;
		this.contentionWindow = contentionWindow;
		this.schedule = schedule;
	}
	
	/**
	 * DynamicTDMAVersionOneFactory constructor, assuming no schedule exists and must be made
	 * @param maxQueueSize
	 * @param contentionWindow
	 * @param topology
	 * @param workload
	 * @param failureChance
	 * @param searchDepth
	 */
	public DynamicTDMAVersionOneFactory(int maxQueueSize, int contentionWindow, Topology topology, Workload workload, double failureChance, int searchDepth) {
		this.maxQueueSize = maxQueueSize;
		this.contentionWindow = contentionWindow;
		this.schedule = buildSchedule(topology, workload, failureChance, searchDepth);
	}
	
	/**
	 * creates a new DynamicTDMAVersionOne node using the factories contention window settings
	 * @param vertex
	 * @param oldSimulator
	 * @return
	 */
	@Override
	public Node newNode(Vertex vertex, Simulator simulator, int seed) {
        return new DynamicTDMAVersionOne(vertex, simulator, maxQueueSize, contentionWindow, schedule, seed);
    }
	
	/**
	 * builds and returns a schedule given the passed workload
	 * 
	 * The scheduler repeatedly runs searchDepth simulations.  After searchDepth simulations have been run
	 * the schedule is updated in the following fashion:
	 * 
	 * each node can only be assigned one new additional TDMA slot for each set of searchDepth simulations
	 * 	This is because adding a TDMA slot can have drastic changes on the % chance a node has a packet to send
	 * 	in all following time slots.
	 * 
	 * In a particular time slot, only one node may be assigned a TDMA slot to transmit (we don't have to worry about
	 * reception since this simulator handles that, at least for now)
	 * 
	 * If a node has 100% of having a packet to send in a particular time slot it is given that time slot as a TDMA slot
	 * 	if no other nodes have been assigned the slot.  If multiple nodes have 100% of having a packet and the slot has 
	 * 	not been assigned, the node with the most number of packets in its queue on avg is picked.  If multiple nodes 
	 * 	have the same avg, one of the tied nodes is picked at uniformly random.
	 * 
	 * We start at time zero and increment time to assign TDMA slots.
	 * 
	 * Also, once a TDMA slot has been assigned, it remains assigned to the node it was initially assigned to
	 * 
	 * This is a very, very naive scheduler and really only exist to show that our simulator works
	 * 		
	 * 
	 * @param workload
	 * @return
	 */
	private int[] buildSchedule(Topology topology, Workload workload, double failureChance, int searchDepth) {
		Random rand = new Random(100);
		int hyperPeriodLength = workload.getLengthOfHyperPeriod();
		int numNodes = topology.getVertices().size();
		int[] schedule;
		if( this.schedule == null ) {
			schedule = new int[hyperPeriodLength];
			Arrays.fill(schedule, -1);
		} else {
			schedule = this.schedule;
		}
		boolean scheduleChanged = true;
		while(scheduleChanged) {
			scheduleChanged = false;
			int [][][] queueSizes = new int[searchDepth][numNodes][hyperPeriodLength];
			DynamicTDMAVersionOneFactory factory = new DynamicTDMAVersionOneFactory(maxQueueSize, contentionWindow, schedule);
			for(int i = 0; i < searchDepth; i++) {
				Simulator simulator = new Simulator(topology, workload, factory, failureChance, i);
				queueSizes[i] = simulator.getQueueSizesOverHyperPeriod(hyperPeriodLength);
			}
			boolean[] nodesWithAssignments = new boolean[numNodes];
			for(int timeSlot = 0; timeSlot < hyperPeriodLength; timeSlot++) {
				if(schedule[timeSlot] < 0) {
					ArrayList<Integer> candidateNodes = new ArrayList<Integer>();
					ArrayList<Double> candidateSizes = new ArrayList<Double>();
					for(int node = 0; node < numNodes; node++) {
						double sum = 0;
						double chance = 0;
						for(int iteration = 0; iteration < searchDepth; iteration++) {
							if(queueSizes[iteration][node][timeSlot] > 0) {
								chance += 1;
							}
							sum += queueSizes[iteration][node][timeSlot];
						}
						if (chance / searchDepth == 1 && !nodesWithAssignments[node]) {
							candidateNodes.add(node);
							candidateSizes.add(sum / searchDepth);
						}
					}
					if(candidateNodes.size() == 1) {
						schedule[timeSlot] = candidateNodes.get(0);
						scheduleChanged = true;
						nodesWithAssignments[candidateNodes.get(0)] = true;
					} else if(candidateNodes.size() > 1) {
						double max = 0;
						for(int node = 0; node < candidateNodes.size(); node++) {
							if(candidateSizes.get(node) > max) {
								max = candidateSizes.get(node);
							}
						}
						int index = 0;
						int size = 0;
						for(int node = 0; node < size; node++) {
							if(candidateSizes.get(index) < max) {
								candidateSizes.remove(index);
								candidateNodes.remove(index);
							} else {
								index = index + 1;
							}
						}
						if(candidateNodes.size() == 1) {
							schedule[timeSlot] = candidateNodes.get(0);
							scheduleChanged = true;
							nodesWithAssignments[candidateNodes.get(0)] = true;
						} else {
							int selection = rand.nextInt(candidateNodes.size());
							schedule[timeSlot] = candidateNodes.get(selection);
							scheduleChanged = true;
							nodesWithAssignments[candidateNodes.get(selection)] = true;
						}
					}
				}
			}
		}
		return schedule;
		
	}
}
