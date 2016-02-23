import java.io.IOException;
import java.util.List;
import java.util.Random;

import csma.ExponentialBackoffCSMAFactory;
import csma.SimpleCSMAFactory;
import simulator.RunStatistics;
import simulator.Simulator;
import topology.Topology;
import topology.Vertex;
import workload.Workload;
import simulator.Node;

/**
 * 
 * @author ryanbrummet
 * Created on 2/16/16
 *
 */
public class RealTopologyTest {
	
	/**
	 * This Realistic topology came from "Real-Time Communication in Low-Power Mobile Wireless Networks", CCNC 2016
	 * 
	 * All edges are bi-directional and A is the gateway.
	 * 
	 * W,V,U,T,S <-> R
	 * Q,P,R <-> O
	 * N,O <-> M
	 * E <-> D
	 * L <-> K
	 * I,J <-> H
	 * G,H <-> F
	 * M,C,B,D,K,F <-> A
	 * 
	 * @param args
	 */
	public static void main(String args[]) throws IOException {
		
		/*
		 * If you add an additional workflow class be sure to add a description within the below comment block
		 * 
		 * Possible workflows: (1) every node has one periodic flow originating from it, 
		 * (2) only leaf nodes have originating periodic flows, (3) each node has one saturation flow originating
		 * from it
		 * 
		 * The gateway, a, never has a flow originating from it and every flow has a set as its final destination
		 * 
		 * Nodes that have a flow originating from it are referred from this point on as candidate nodes
		 * 
		 * Each flow is assumed to be periodic.  The phase of each periodic flow is considered either to be zero or random
		 * 
		 * This test allows the user to specify a set of flow classes (as an array).  If more than one flow class
		 * is given, candidate nodes will be randomly assigned a flow class.  If one flow class is given, each candidate
		 * node will be assigned that flow class.  For each flow class it is assumed that the period and deadlines are the same
		 * 
		 * If experimentFileName is null, no results will be saved.  Otherwise results will be saved according to
		 * the format [experimentFileName] + [SimulationRun#].
		 * 
		 */
		int numOfSlotsNeededToSendPacket = 1;
		int totalAmountOfTimeToRunSim = 10000;
		int numOfTimesToRepeatSim = 50;
		int protocol = 2;  // 1 for simple CSMA, 2 for exponential CSMA,
		int minContentionWindow = 16;  // (16,32,64) via "Performance Analysis of the IEEE 802.11 Distributed Coordination Function" J-SAC 2000
		int maxContentionWindow = 1024;  // (1024) via same as minContentionWindow
		boolean randomFlowPhase = false;
		int[] flowClasses = {300}; // only has an effect on periodic flows (ie workflow classes 1 and 2)
		int workFlowClass = 1; //options include 1,2,3
		int randomSeed = 1;  // this affects only the generation of flows, the simulator has a different seed associated with the random values it generates
		double failureChance = 0;  // value on the interval [0,100].  This gives the percent chance of a transmission failing
		int maxNodeQueueSize = 10;  // gives the maximum number of packets for a node to have in its queue before it starts dropping packets
		String experimentFileName = "contenders16exp1024Flow100Rand0WorkFlow1PacketSlotSize1_INST";
		
		
		String gateWayNode = "A";
		
		Topology topology = new Topology();
		Vertex a = topology.newVertex("A");
		Vertex b = topology.newVertex("B");
		Vertex c = topology.newVertex("C");
		Vertex d = topology.newVertex("D");
		Vertex e = topology.newVertex("E");
		Vertex f = topology.newVertex("F");
		Vertex g = topology.newVertex("G");
		Vertex h = topology.newVertex("H");
		Vertex i = topology.newVertex("I");
		Vertex j = topology.newVertex("J");
		Vertex k = topology.newVertex("K");
		Vertex l = topology.newVertex("L");
		Vertex m = topology.newVertex("M");
		Vertex n = topology.newVertex("N");
		Vertex o = topology.newVertex("O");
		Vertex p = topology.newVertex("P");
		Vertex q = topology.newVertex("Q");
		Vertex r = topology.newVertex("R");
		Vertex s = topology.newVertex("S");
		Vertex t = topology.newVertex("T");
		Vertex u = topology.newVertex("U");
		Vertex v = topology.newVertex("V");
		Vertex w = topology.newVertex("W");
		
		topology.biconnect(w, r);
		topology.biconnect(v, r);
		topology.biconnect(u, r);
		topology.biconnect(t, r);
		topology.biconnect(s, r);
		topology.biconnect(r, o);
		topology.biconnect(q, o);
		topology.biconnect(p, o);
		topology.biconnect(o, m);
		topology.biconnect(n, m);
		topology.biconnect(e, d);
		topology.biconnect(l, k);
		topology.biconnect(i, h);
		topology.biconnect(j, h);
		topology.biconnect(g, f);
		topology.biconnect(h, f);
		topology.biconnect(m, a);
		topology.biconnect(c, a);
		topology.biconnect(b, a);
		topology.biconnect(d, a);
		topology.biconnect(f, a);
		topology.biconnect(k, a);
		
		System.out.println(topology.graphViz());
        topology.graphViz("topology.dot");
        
        
        Workload workload = new Workload(numOfSlotsNeededToSendPacket);
        Random rand = new Random(randomSeed);
        if(workFlowClass == 1) {
        	List<Vertex> vertices = topology.getVertices();
        	for(Vertex vertex : vertices) {
        		if(vertex != a) {
        			if(flowClasses.length == 1) {
            			if(randomFlowPhase) {
            				workload.newPeriodicFlow(vertex, a, rand.nextInt(flowClasses[0]), flowClasses[0], flowClasses[0]);
            			} else {
            				workload.newPeriodicFlow(vertex, a, 0, flowClasses[0], flowClasses[0]);
            			}
            		} else {
            			int pickedFlowClass = rand.nextInt(flowClasses.length);
            			if(randomFlowPhase) {
            				workload.newPeriodicFlow(vertex, a, rand.nextInt(flowClasses[pickedFlowClass]), flowClasses[pickedFlowClass], flowClasses[pickedFlowClass]);
            			} else {
            				workload.newPeriodicFlow(vertex, a, 0, flowClasses[pickedFlowClass], flowClasses[pickedFlowClass]);
            			}
            		}
        		}
        	}
        	workload.assignPriorities();
        } else if(workFlowClass == 2) {
        	List<Vertex> vertices = topology.getVerticesWithOneNeighbor();
        	for(Vertex vertex : vertices) {
        		if(vertex != a) {
        			if(flowClasses.length == 1) {
            			if(randomFlowPhase) {
            				workload.newPeriodicFlow(vertex, a, rand.nextInt(flowClasses[0]), flowClasses[0], flowClasses[0]);
            			} else {
            				workload.newPeriodicFlow(vertex, a, 0, flowClasses[0], flowClasses[0]);
            			}
            		} else {
            			int pickedFlowClass = rand.nextInt(flowClasses.length);
            			if(randomFlowPhase) {
            				workload.newPeriodicFlow(vertex, a, rand.nextInt(flowClasses[pickedFlowClass]), flowClasses[pickedFlowClass], flowClasses[pickedFlowClass]);
            			} else {
            				workload.newPeriodicFlow(vertex, a, 0, flowClasses[pickedFlowClass], flowClasses[pickedFlowClass]);
            			}
            		}
        		}
        	}
        	workload.assignPriorities();
        } else if(workFlowClass == 3) {
        	List<Vertex> vertices = topology.getVertices();
        	for(Vertex vertex : vertices) {
        		if(vertex != a) {
        			workload.newSaturationFlow(vertex, a);
        		}
        	}
        } else {
        	throw new IllegalStateException("You have specified an invalid workflow class."
        			+ "  Either define a workflow class with the id you have provided or pick a defined workflow class");
        }
        workload.print();
        System.out.println("\n");
        
        
        double[] simChannelIdle = new double[numOfTimesToRepeatSim];
        double[] simChannelContention = new double[numOfTimesToRepeatSim];
        double[] simChannelRealUsage = new double[numOfTimesToRepeatSim];
        double[] simPacketsCreated = new double[numOfTimesToRepeatSim];
        double[] simPacketsDropped = new double[numOfTimesToRepeatSim];
        double[] simPercentPacketsDropped = new double[numOfTimesToRepeatSim];
        double[] simChannelFailure = new double[numOfTimesToRepeatSim];
        long[] simTotalPacketsReachingGateway = new long[numOfTimesToRepeatSim];
        for(int simNum = 0; simNum < numOfTimesToRepeatSim; simNum++) {
        	
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
        	
        	RunStatistics stats = simulator.run(totalAmountOfTimeToRunSim);
    		if(experimentFileName != null) {
    			stats.saveContenders(experimentFileName + Integer.toString(simNum));
    		}
    		
    		List<Node> nodes = simulator.getNodes();
    		int numPacketsReceievedAtGateway = 0;
    		for(Node node : nodes) {
    			if(topology.getVertexByName(gateWayNode) == node.getVertex()) {
    				numPacketsReceievedAtGateway += node.getStatistics().getRx();
    				break;
    			}
    		}
        	
        	simChannelIdle[simNum] = stats.getChannelIdlePercent();
        	simChannelContention[simNum] = stats.getChannelContentionPercent();
        	simChannelRealUsage[simNum] = stats.getChannelRealUsagePercent();
        	simPacketsCreated[simNum] = stats.getNumPacketsCreated();
        	simPacketsDropped[simNum] = stats.getNumPacketsDropped();
        	simPercentPacketsDropped[simNum] = stats.getPercentOfPacketsDropped();
        	simChannelFailure[simNum] = stats.getChannelRandomFailurePercent();
        	simTotalPacketsReachingGateway[simNum] = numPacketsReceievedAtGateway;
        }
        
        double avgPercentIdle = 0;
        double avgPercentContention = 0;
        double avgPercentRealUsage = 0;
        double avgPacketsCreated = 0;
        double avgPacketsDropped = 0;
        double avgPercentPacketsDropped = 0;
        double avgChannelFailure = 0;
        double avgPacketsReceivedAtGateway = 0;
        for(int temp = 0; temp < numOfTimesToRepeatSim; temp++) {
        	avgPercentIdle += simChannelIdle[temp];
        	avgPercentContention += simChannelContention[temp];
        	avgPercentRealUsage += simChannelRealUsage[temp];
        	avgPacketsCreated += simPacketsCreated[temp];
        	avgPacketsDropped += simPacketsDropped[temp];
        	avgPercentPacketsDropped += simPercentPacketsDropped[temp];
        	avgChannelFailure += simChannelFailure[temp];
        	avgPacketsReceivedAtGateway += simTotalPacketsReachingGateway[temp];
        }
        avgPercentIdle = (avgPercentIdle / numOfTimesToRepeatSim) * 100;
        avgPercentContention = (avgPercentContention / numOfTimesToRepeatSim) * 100;
        avgPercentRealUsage = (avgPercentRealUsage / numOfTimesToRepeatSim) * 100;
        avgPacketsCreated = (avgPacketsCreated / numOfTimesToRepeatSim);
        avgPacketsDropped = (avgPacketsDropped / numOfTimesToRepeatSim);
        avgPercentPacketsDropped = (avgPercentPacketsDropped / numOfTimesToRepeatSim) * 100;
        avgChannelFailure = (avgChannelFailure / numOfTimesToRepeatSim) * 100;
        avgPacketsReceivedAtGateway = (avgPacketsReceivedAtGateway / numOfTimesToRepeatSim);
        
        System.out.println("Avg Channel Idle: " + Double.toString(avgPercentIdle) + "%");
        System.out.println("Avg Channel Contention: " + Double.toString(avgPercentContention) + "%");
        System.out.println("Avg Channel Real Usage: " + Double.toString(avgPercentRealUsage) + "%");
        System.out.println("Avg Number Uniquely Generated Packets: " + Double.toString(avgPacketsCreated));
        System.out.println("Avg Number of Packets Dropped: " + Double.toString(avgPacketsDropped));
        System.out.println("Avg Percentage of Packets Dropped: " + Double.toString(avgPercentPacketsDropped) + "%");
        System.out.println("Avg Channel Failure: " + Double.toString(avgChannelFailure) + "%");
        System.out.println("Avg Total Num of Packets Received At Gateway: " + Double.toString(avgPacketsReceivedAtGateway));
	}

}
