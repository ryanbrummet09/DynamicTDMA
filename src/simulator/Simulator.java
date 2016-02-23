package simulator;

import topology.Topology;
import topology.Vertex;
import workload.Workload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by ochipara on 2/8/16.
 * Modified by ryanbrummet on 2/15/16
 */
public class Simulator {
    private final Topology topology;
    private final HashMap<Vertex, Node> vertex2nodes = new HashMap<>();
    private final List<Node> nodes = new ArrayList<>();
    private final Workload workload;
    @SuppressWarnings("unused")
	private final NodeFactory nodeFactory;
    private final double failureChance;
    private final Random rand;
    
    /**
     * creates a new simulator 
     * @param topology
     * @param workload
     * @param nodeFactory
     * @param failureChance
     * @param seed
     */
    public Simulator(Topology topology, Workload workload, NodeFactory nodeFactory, double failureChance, int seed) {
        this.topology = topology;
        this.workload = workload;
        this.nodeFactory = nodeFactory;
        if(failureChance < 0 || failureChance > 100) {
        	throw new IllegalArgumentException("The Failure chance of a packet must be between 0 and 100%");
        } else {
        	this.failureChance = failureChance;
        }
        rand = new Random(seed);
        
        List<Vertex> vertices = topology.getVertices();
        for (Vertex vertex : vertices) {

            Node node = nodeFactory.newNode(vertex, this, seed);
            vertex2nodes.put(vertex, node);
            nodes.add(node);
        }
    }

    /**
     * Run the simulator for a given number of slots
     * @param numSlots
     * @return
     */
    public RunStatistics run(int numSlots) {
        List<Packet> inflight = new ArrayList<>();

        RunStatistics runStats = new RunStatistics(numSlots);

        for (long time = 0; time < numSlots; time++) {
            // check if the node has a packet to transmitResult
            for (Node node : nodes) {
                Packet packet = node.contend(time);
                if (packet != null) {
                	inflight.add(packet);
                	if(!packet.getPacketHasBeenCounted()) {
                		packet.countPacket();
                		runStats.updateNumPacketsCreated(1);
                	}
                }
            }

            // notify nodes of the outcome
            int contenders = 0;
            for (Node node : nodes) {
                // let the nodes know if the channel is free or busy
                node.channelFeedback(inflight.size() == 0);
                if (node.getState() == Node.CONTENDING) contenders++;
            }
            runStats.setContenders((int) time, contenders);

            boolean success;
            boolean packetDrop = false;
            if(inflight.size() == 1) {
            	if(inflight.get(0).getDestination().getNumPacketsInQueue() < inflight.get(0).getDestination().getMaxQueueSize()) {
            		if(rand.nextDouble() * 100 < failureChance) {
        				success = false; // packet transmission failed due to random failure chance (outside interference, etc)
        			} else {
        				success = true; // packet successfully transmitted
        			}
            	} else {
            		success = true;  // dropped packet
            		packetDrop = true;
            	}
            } else { 
            	success = false; // collision or idle
            }
            if(inflight.size() == 0) {
            	// channel idle
            	runStats.setsuccessfulTransmission((int) time, 0);
            } else if(inflight.size() == 1) {
            	if(packetDrop) {
            		// packet dropped
            		runStats.setsuccessfulTransmission((int) time, 3);
            	} else {
            		if(success) {
            			// packet transmitted
                    	runStats.setsuccessfulTransmission((int) time, 2);
            		} else {
            			// transmission failed due to random chance (treated as collision such that no ack was received)
                    	runStats.setsuccessfulTransmission((int) time, 4);
            		}
            	}
            } else {
            	// contention
            	runStats.setsuccessfulTransmission((int) time, 1);
            }
            while(inflight.size() > 0) {
                Packet packet = inflight.remove(0);
                packet.getSource().transmitResult(time, packet, success);
                if (success && packet.getSlotsNeededToCompletePacketTransmission() == 0) {
                	packet.resetPacketTransmission();
                	packet.getDestination().receive(time, packet, packetDrop);
                }
            }
        }
        return runStats;
    }

    /**
     * returns the node associated with the given vertex
     * @param dst
     * @return
     */
    public Node getNode(Vertex dst) {
        return vertex2nodes.get(dst);
    }

    /**
     * returns the topology passed to this simulator
     * @return
     */
    public Topology getTopology() {
        return topology;
    }

    /**
     * returns the node with the given id
     * @param id
     * @return
     */
    public Node getNodeById(int id) {
        Vertex vertex = topology.getVertexById(id);
        return vertex2nodes.get(vertex);
    }

    /**
     * returns the workload passed to this simulator
     * @return
     */
    public Workload getWorkload() {
        return workload;
    }
    
    /**
     * Returns a list of all nodes within the topology associated with this simulator
     * @return
     */
    public List<Node> getNodes() {
    	return nodes;
    }
}
