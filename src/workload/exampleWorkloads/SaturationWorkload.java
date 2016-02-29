package workload.exampleWorkloads;

import java.util.List;

import topology.topologyFoundationCode.Topology;
import topology.topologyFoundationCode.Vertex;
import workload.workloadFoundationCode.Workload;

/**
 * 
 * @author ryanbrummet
 *
 */
public class SaturationWorkload extends AbstractExampleWorkload{
	
	/**
	 * Use this constructor and the method getNewInstanceOfThisWorkload() to create a saturation workload for the 
	 * given topology where each packet takes numOfSlotsNeededToSendPacket to send a packet.  If printWorkload is true
	 * the workload is printed to stdout.
	 * @param topology
	 * @param numOfSlotsNeededToSendPacket
	 * @param printWorkload
	 */
	public SaturationWorkload(Topology topology, int numOfSlotsNeededToSendPacket, boolean printWorkload) {
		super(topology, numOfSlotsNeededToSendPacket, printWorkload);
	}
	
	/**
	 * Creates a new saturation workload for the topology associated with this object
	 */
	public Workload getNewInstanceOfThisWorkload() {
		Workload workload = new Workload(numOfSlotsNeededToSendPacket);
		
		List<Vertex> vertices = topology.getVertices();
		Vertex gateway = topology.getGateway();
    	for(Vertex vertex : vertices) {
    		if(vertex != gateway) {
    			workload.newSaturationFlow(vertex, gateway);
    		}
    	}
		
    	if(printWorkload) {
    		workload.print();
    	}
    	
    	return workload;
	}

}
