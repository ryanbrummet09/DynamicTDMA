package experimentation;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import protocols.ExponentialBackoffCSMAFactory;
import protocols.SimpleCSMAFactory;
import simulator.Simulator;
import simulator.statistics.TableStatistics;
import topology.exampleTopologies.AbstractExampleTopology;
import topology.exampleTopologies.RealTopology1;
import topology.topologyFoundationCode.Topology;
import workload.exampleWorkloads.AbstractExampleWorkload;
import workload.exampleWorkloads.Workload1;
import workload.workloadFoundationCode.Workload;

public class ParallelRunExperiment {
	
    
    public static void main(String args[]) {
    	int numOfSlotsNeededToSendPacket = 1;
    	final int totalAmountOfTimeToRunSim = 5000;
    	int numOfTimesToRepeatSim = 1000;
    	final int protocol = 1;  // 1 for simple CSMA, 2 for exponential CSMA,
    	final int minContentionWindow = 16;  // (16,32,64) via "Performance Analysis of the IEEE 802.11 Distributed Coordination Function" J-SAC 2000
    	final int maxContentionWindow = 1024;  // (1024) via same as minContentionWindow
    	boolean randomFlowPhase = true;
    	int[] flowClasses = {200,250,300}; // only has an effect on periodic flows (ie workflow classes 1 and 2)
    	int randomSeed = 1;  // this affects only the generation of flows, the simulator has a different seed associated with the random values it generates
    	final double failureChance = 0;  // value on the interval [0,100].  This gives the percent chance of a transmission failing
    	final int maxNodeQueueSize = 10;  // gives the maximum number of packets for a node to have in its queue before it starts dropping packets
    	
    	AbstractExampleTopology topologyFactory = new RealTopology1(true);
    	final Topology topology = topologyFactory.getNewInstanceOfThisTopology();
    	AbstractExampleWorkload workloadFactory = new Workload1(topology, numOfSlotsNeededToSendPacket, flowClasses, randomFlowPhase, true, randomSeed);
    	final Workload workload = workloadFactory.getNewInstanceOfThisWorkload();
    	final String experimentFileName = "SimpleCSMA_CW16_FLOW200S250S300_RandPHASE1_Queue10_RUN"; //"CSMASimulation";
    	
    	
    	int threads = Runtime.getRuntime().availableProcessors();
        ExecutorService service = Executors.newFixedThreadPool(threads);
        
        final int[] simIter = new int[numOfTimesToRepeatSim];
        for(int i = 0; i < numOfTimesToRepeatSim; i++){
        	simIter[i] = i;
        }
        
        for (final int simNum : simIter) {
            Callable<Object> callable = new Callable<Object>() {
				public Object call() throws Exception {
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
                	
                    return null;
                }
            };
            service.submit(callable);
        }
        service.shutdown();
    }
    
}
