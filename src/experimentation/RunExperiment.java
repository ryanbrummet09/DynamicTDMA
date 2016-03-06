package experimentation;
import java.io.IOException;

import protocols.factories.ExponentialBackoffCSMAFactory;
import protocols.factories.SimpleCSMAFactory;
import simulator.Simulator;
import simulator.statistics.TableStatistics;
import topology.exampleTopologies.AbstractExampleTopology;
import topology.exampleTopologies.RealTopology1;
import topology.topologyFoundationCode.Topology;
import workload.exampleWorkloads.AbstractExampleWorkload;
import workload.exampleWorkloads.Workload1;
import workload.workloadFoundationCode.Workload;

/**
 * 
 * @author ryanbrummet
 *
 */
public class RunExperiment {

	public static void main(String[] args) throws IOException {
		
		int numOfSlotsNeededToSendPacket = 1;
		int totalAmountOfTimeToRunSim = 10000;
		int numOfTimesToRepeatSim = 50;
		int protocol = 2;  // 1 for simple CSMA, 2 for exponential CSMA,
		int minContentionWindow = 16;  // (16,32,64) via "Performance Analysis of the IEEE 802.11 Distributed Coordination Function" J-SAC 2000
		int maxContentionWindow = 1024;  // (1024) via same as minContentionWindow
		boolean randomFlowPhase = false;
		int[] flowClasses = {200,250,300}; // only has an effect on periodic flows (ie workflow classes 1 and 2)
		int randomSeed = 1;  // this affects only the generation of flows, the simulator has a different seed associated with the random values it generates
		double failureChance = 0;  // value on the interval [0,100].  This gives the percent chance of a transmission failing
		int maxNodeQueueSize = 10;  // gives the maximum number of packets for a node to have in its queue before it starts dropping packets
		
		AbstractExampleTopology topologyFactory = new RealTopology1(true);
		Topology topology = topologyFactory.getNewInstanceOfThisTopology();
		AbstractExampleWorkload workloadFactory = new Workload1(topology, numOfSlotsNeededToSendPacket, flowClasses, randomFlowPhase, true, randomSeed);
		Workload workload = workloadFactory.getNewInstanceOfThisWorkload();
		String experimentFileName = "Temp"; //"CSMASimulation";
		
		
		//AggregateRunStatistics aggregateStats = new AggregateRunStatistics(numOfTimesToRepeatSim);
		for(int simNum = 0; simNum < numOfTimesToRepeatSim; simNum++) {
			System.out.println(Integer.toString(simNum));
        	Simulator simulator;
        	
        	// simple CSMA
        	if(protocol == 1) {
        		simulator = new Simulator(topology, workload, new SimpleCSMAFactory(maxNodeQueueSize, minContentionWindow), failureChance, simNum);
        	// exponential CSMA
        	} else if(protocol == 2){
        		simulator = new Simulator(topology, workload, new ExponentialBackoffCSMAFactory(maxNodeQueueSize, minContentionWindow,maxContentionWindow), failureChance, simNum);
        	} else {
        		throw new IllegalStateException("The protocol that you specified does not exist."
        				+ "  Either define a protocol with the id you have provided or pick a defined protocol.");
        	}
        	
        	TableStatistics stats = simulator.runTableStats(totalAmountOfTimeToRunSim);
        	
        	if(experimentFileName != null) {
    			stats.saveResultsToFile(experimentFileName + Integer.toString(simNum));
    		}
        	//aggregateStats.addRunStatistics(simNum, stats);
        	
		}

	}

}
