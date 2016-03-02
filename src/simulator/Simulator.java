package simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import constants.TableStatisticsIndexConstant;
import constants.TransmissionChannelConstants;
import simulator.statistics.RunStatistics;
import simulator.statistics.TableStatistics;
import topology.topologyFoundationCode.Topology;
import topology.topologyFoundationCode.Vertex;
import workload.workloadFoundationCode.PeriodicFlow;
import workload.workloadFoundationCode.Workload;

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
            	runStats.setsuccessfulTransmission((int) time, TransmissionChannelConstants.IDLE);
            } else if(inflight.size() == 1) {
            	if(packetDrop) {
            		// packet dropped
            		runStats.setsuccessfulTransmission((int) time, TransmissionChannelConstants.PACKET_DROPPED);
            	} else {
            		if(success) {
            			// packet transmitted
                    	runStats.setsuccessfulTransmission((int) time, TransmissionChannelConstants.TRANSMISSION);
            		} else {
            			// transmission failed due to random chance (treated as collision such that no ack was received)
                    	runStats.setsuccessfulTransmission((int) time, TransmissionChannelConstants.FAILED);
            		}
            	}
            } else {
            	// contention
            	runStats.setsuccessfulTransmission((int) time, TransmissionChannelConstants.CONTENTION);
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
     * Run the simulator for a given number of slots
     * @param numSlots
     * @return
     */
    public TableStatistics runTableStats(int numSlots) {
        List<Packet> inflight = new ArrayList<>();

        TableStatistics stats = new TableStatistics();
        
        for (long time = 0; time < numSlots; time++) {
            // check if the node has a packet to transmitResult
            for (Node node : nodes) {
                Packet packet = node.contend(time);
                if (packet != null) {
                	inflight.add(packet);
                	if(!packet.getPacketHasBeenCounted()) {
                		packet.countPacket();
                	}
                }
            }

            // notify nodes of the outcome
            for (Node node : nodes) {
                // let the nodes know if the channel is free or busy
                node.channelFeedback(inflight.size() == 0);
            }
           

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
            } else if(inflight.size() == 1) {
            	if(packetDrop) {
            		// packet dropped
            		inflight.get(0).getSource().slotTransmissionResult = TransmissionChannelConstants.PACKET_DROPPED;
            	} else {
            		if(success) {
            			// packet transmitted
                    	inflight.get(0).getSource().slotTransmissionResult = TransmissionChannelConstants.TRANSMISSION;
            		} else {
            			// transmission failed due to random chance (treated as collision such that no ack was received)
                    	inflight.get(0).getSource().slotTransmissionResult = TransmissionChannelConstants.FAILED;
            		}
            	}
            } else {
            	// contention
            }
            
            List<Packet> packetsThatReceachedGatewayOrWereDropped = new ArrayList<Packet>();
            while(inflight.size() > 0) {
            	
                Packet packet = inflight.remove(0);
                packet.getSource().transmitResult(time, packet, success);
                if (success && packet.getSlotsNeededToCompletePacketTransmission() == 0) {
                	packet.resetPacketTransmission();
                	Packet temp = packet.getDestination().receive(time, packet, packetDrop);
                	if(temp != null) {
                		packetsThatReceachedGatewayOrWereDropped.add(temp);
                	}
                }
            }
            
            updateTableStats(stats,packetsThatReceachedGatewayOrWereDropped,(int) time);
        }
        return stats;
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
    
    /**
     * updates the passed TableStatistics object 
     * @param stats
     * @param time
     */
    private void updateTableStats(TableStatistics stats, List<Packet> packetsThatReceachedGatewayOrWereDropped, int time) {

    	for(Node node : nodes) {
    		PriorityQueue<Packet> queue = node.queue;
    		if(queue.size() == 0) {
    			for(int i = 0 ; i < TableStatisticsIndexConstant.COLUMN_NAMES.length; i++) {
    				switch (i) {
    				case TableStatisticsIndexConstant.TIME :
    					stats.assign(i, time);
    					break;
    				case TableStatisticsIndexConstant.NODE :
    					stats.assign(i, node.getVertex().getId());
    					break;
    				case TableStatisticsIndexConstant.NODE_BACKOFF :
    					stats.assign(i, node.backoff);
    					break;
    				case TableStatisticsIndexConstant.NODE_CW :
    					stats.assign(i, node.cw);
    					break;
    				case TableStatisticsIndexConstant.NODE_QUEUE_SIZE :
    					stats.assign(i, node.queue.size());
    					break;
    				case TableStatisticsIndexConstant.NODE_TRANSMIT_ACTION :
    					stats.assign(i, node.slotTransmissionResult);
    					break;
    				case TableStatisticsIndexConstant.NODE_RECEIVE_ACTION :
    					stats.assign(i, node.slotReceptionResult);
    					break;
    				case TableStatisticsIndexConstant.NODE_STATE :
    					stats.assign(i, node.state);
    					break;
    				case TableStatisticsIndexConstant.PACKET_PERIOD :
    					stats.assign(i, -1); 
    					break;
    				case TableStatisticsIndexConstant.PACKET_PHASE :
    					stats.assign(i, -1);
    					break;
    				case TableStatisticsIndexConstant.PACKET_DEADLINE :
    					stats.assign(i, -1);
    					break;
    				case TableStatisticsIndexConstant.PACKET_SLACK :
    					stats.assign(i, -1);
    					break;
    				case TableStatisticsIndexConstant.PACKET_TYPE_INSTANCE_NUM :
    					stats.assign(i, -1);
    					break;
    				case TableStatisticsIndexConstant.PACKET_NEXT_DEST :
    					stats.assign(i, -1);
    					break;
    				case TableStatisticsIndexConstant.PACKET_CREATION_NODE :
    					stats.assign(i, -1);
    					break;
    				case TableStatisticsIndexConstant.PACKET_FINAL_DEST_NODE :
    					stats.assign(i, -1);
    					break;
    				case TableStatisticsIndexConstant.PACKET_SLOTS_TO_SEND_TO_NEXT_DEST :
    					stats.assign(i, -1);
    					break;
    				case TableStatisticsIndexConstant.PACKET_TIME_IN_CURRENT_QUEUE :
    					stats.assign(i, -1);
    					break;
    				case TableStatisticsIndexConstant.PACKET_DROPPED :
    					stats.assign(i, -1);
    					break;
    				case TableStatisticsIndexConstant.PACKET_POSITION_IN_QUEUE :
    					stats.assign(i, -1);
    					break;
    				}
    			}
    			stats.next();
    		} else {
    			int index = 0;
    			for(Packet packet : queue) {
        			for(int i = 0 ; i < TableStatisticsIndexConstant.COLUMN_NAMES.length; i++) {
        				switch (i) {
        				case TableStatisticsIndexConstant.TIME :
        					stats.assign(i, time);
        					break;
        				case TableStatisticsIndexConstant.NODE :
        					stats.assign(i, node.getVertex().getId());
        					break;
        				case TableStatisticsIndexConstant.NODE_BACKOFF :
        					stats.assign(i, node.backoff);
        					break;
        				case TableStatisticsIndexConstant.NODE_CW :
        					stats.assign(i, node.cw);
        					break;
        				case TableStatisticsIndexConstant.NODE_QUEUE_SIZE :
        					stats.assign(i, node.queue.size());
        					break;
        				case TableStatisticsIndexConstant.NODE_TRANSMIT_ACTION :
        					stats.assign(i, node.slotTransmissionResult);
        					break;
        				case TableStatisticsIndexConstant.NODE_RECEIVE_ACTION :
        					stats.assign(i, node.slotReceptionResult);
        					break;
        				case TableStatisticsIndexConstant.NODE_STATE :
        					stats.assign(i, node.state);
        					break;
        				case TableStatisticsIndexConstant.PACKET_PERIOD :
        					stats.assign(i, ((PeriodicFlow) packet.getFlow()).getPeriod()); 
        					break;
        				case TableStatisticsIndexConstant.PACKET_PHASE :
        					stats.assign(i, ((PeriodicFlow) packet.getFlow()).getPhase());
        					break;
        				case TableStatisticsIndexConstant.PACKET_DEADLINE :
        					stats.assign(i, ((PeriodicFlow) packet.getFlow()).getDeadline());
        					break;
        				case TableStatisticsIndexConstant.PACKET_SLACK :
        					stats.assign(i, ((PeriodicFlow) packet.getFlow()).getDeadline() - packet.timeSinceCreation);
        					break;
        				case TableStatisticsIndexConstant.PACKET_TYPE_INSTANCE_NUM :
        					stats.assign(i, packet.getInstance());
        					break;
        				case TableStatisticsIndexConstant.PACKET_NEXT_DEST :
        					stats.assign(i, packet.getDestination().getVertex().getId());
        					break;
        				case TableStatisticsIndexConstant.PACKET_CREATION_NODE :
        					stats.assign(i, packet.getFlow().getSource().getId());
        					break;
        				case TableStatisticsIndexConstant.PACKET_FINAL_DEST_NODE :
        					stats.assign(i, packet.getFlow().getDestination().getId());
        					break;
        				case TableStatisticsIndexConstant.PACKET_SLOTS_TO_SEND_TO_NEXT_DEST :
        					stats.assign(i, packet.getSlotsNeededToCompletePacketTransmission());
        					break;
        				case TableStatisticsIndexConstant.PACKET_TIME_IN_CURRENT_QUEUE :
        					stats.assign(i, packet.getTimeInCurrentQueue());
        					break;
        				case TableStatisticsIndexConstant.PACKET_DROPPED :
        					stats.assign(i, packet.getPacketDropped());
        					break;
        				case TableStatisticsIndexConstant.PACKET_POSITION_IN_QUEUE :
        					stats.assign(i, index);
        					break;
        				}
        			}
        			index = index + 1;
        			stats.next();
        		}
    		}
    	}
    	for(Packet packet : packetsThatReceachedGatewayOrWereDropped) {
    		for(int i = 0 ; i < TableStatisticsIndexConstant.COLUMN_NAMES.length; i++) {
				switch (i) {
				case TableStatisticsIndexConstant.TIME :
					stats.assign(i, time);
					break;
				case TableStatisticsIndexConstant.NODE :
					stats.assign(i, -1);
					break;
				case TableStatisticsIndexConstant.NODE_BACKOFF :
					stats.assign(i, -1);
					break;
				case TableStatisticsIndexConstant.NODE_CW :
					stats.assign(i, -1);
					break;
				case TableStatisticsIndexConstant.NODE_QUEUE_SIZE :
					stats.assign(i, -1);
					break;
				case TableStatisticsIndexConstant.NODE_TRANSMIT_ACTION :
					stats.assign(i, -1);
					break;
				case TableStatisticsIndexConstant.NODE_RECEIVE_ACTION :
					stats.assign(i, -1);
					break;
				case TableStatisticsIndexConstant.NODE_STATE :
					stats.assign(i, -1);
					break;
				case TableStatisticsIndexConstant.PACKET_PERIOD :
					stats.assign(i, ((PeriodicFlow) packet.getFlow()).getPeriod()); 
					break;
				case TableStatisticsIndexConstant.PACKET_PHASE :
					stats.assign(i, ((PeriodicFlow) packet.getFlow()).getPhase());
					break;
				case TableStatisticsIndexConstant.PACKET_DEADLINE :
					stats.assign(i, ((PeriodicFlow) packet.getFlow()).getDeadline());
					break;
				case TableStatisticsIndexConstant.PACKET_SLACK :
					stats.assign(i, ((PeriodicFlow) packet.getFlow()).getDeadline() - packet.timeSinceCreation);
					break;
				case TableStatisticsIndexConstant.PACKET_TYPE_INSTANCE_NUM :
					stats.assign(i, packet.getInstance());
					break;
				case TableStatisticsIndexConstant.PACKET_NEXT_DEST :
					stats.assign(i, packet.getDestination().getVertex().getId());
					break;
				case TableStatisticsIndexConstant.PACKET_CREATION_NODE :
					stats.assign(i, packet.getFlow().getSource().getId());
					break;
				case TableStatisticsIndexConstant.PACKET_FINAL_DEST_NODE :
					stats.assign(i, packet.getFlow().getDestination().getId());
					break;
				case TableStatisticsIndexConstant.PACKET_SLOTS_TO_SEND_TO_NEXT_DEST :
					stats.assign(i, packet.getSlotsNeededToCompletePacketTransmission());
					break;
				case TableStatisticsIndexConstant.PACKET_TIME_IN_CURRENT_QUEUE :
					stats.assign(i, packet.getTimeInCurrentQueue());
					break;
				case TableStatisticsIndexConstant.PACKET_DROPPED :
					stats.assign(i, packet.getPacketDropped());
					break;
				case TableStatisticsIndexConstant.PACKET_POSITION_IN_QUEUE :
					stats.assign(i, -1);
					break;
				}
			}
			stats.next();
    	}
    }
}
