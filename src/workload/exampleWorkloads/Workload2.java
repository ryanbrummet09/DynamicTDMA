package workload.exampleWorkloads;

import java.util.List;
import java.util.Random;

import topology.topologyFoundationCode.Topology;
import topology.topologyFoundationCode.Vertex;
import workload.workloadFoundationCode.Workload;

public class Workload2 extends AbstractExampleWorkload{
	
	private int[] flowClasses;
	private boolean randomFlowPhase;
	private int randomSeed;
	
	/**
	 * Use this constructor and getNewInstanceOfThisWorkload() to create a workload of type Workload2
	 * @param topology
	 * @param numOfSlotsNeededToSendPacket
	 * @param flowClasses
	 * @param randomFlowPhase
	 * @param printWorkload
	 * @param randomSeed
	 */
	public Workload2(Topology topology, int numOfSlotsNeededToSendPacket, int[] flowClasses, boolean randomFlowPhase, boolean printWorkload, int randomSeed) {
		super(topology, numOfSlotsNeededToSendPacket, printWorkload);
		this.flowClasses = flowClasses;
		this.randomFlowPhase = randomFlowPhase;
		this.randomSeed = randomSeed;
	}
	
	/**
	 * Creates a new Workload2 workload for the topology associated with this object
	 */
	public Workload getNewInstanceOfThisWorkload() {
		
		Workload workload = new Workload(numOfSlotsNeededToSendPacket);
        Random rand = new Random(randomSeed);
		
        List<Vertex> vertices = topology.getVerticesWithOneNeighbor();
        Vertex gateway = topology.getGateway();
    	for(Vertex vertex : vertices) {
    		if(vertex != gateway) {
    			if(flowClasses.length == 1) {
        			if(randomFlowPhase) {
        				workload.newPeriodicFlow(vertex, gateway, rand.nextInt(flowClasses[0]), flowClasses[0], flowClasses[0]);
        			} else {
        				workload.newPeriodicFlow(vertex, gateway, 0, flowClasses[0], flowClasses[0]);
        			}
        		} else {
        			int pickedFlowClass = rand.nextInt(flowClasses.length);
        			if(randomFlowPhase) {
        				workload.newPeriodicFlow(vertex, gateway, rand.nextInt(flowClasses[pickedFlowClass]), flowClasses[pickedFlowClass], flowClasses[pickedFlowClass]);
        			} else {
        				workload.newPeriodicFlow(vertex, gateway, 0, flowClasses[pickedFlowClass], flowClasses[pickedFlowClass]);
        			}
        		}
    		}
    	}
    	workload.assignPriorities();
    	
    	return workload;
	}

}
