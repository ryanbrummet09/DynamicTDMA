package simulator;

import java.util.*;

import constants.ReceptionChannelConstants;
import constants.TransmissionChannelConstants;
import simulator.statistics.NodeStatistics;
import topology.topologyFoundationCode.Vertex;
import workload.workloadFoundationCode.Flow;
import workload.workloadFoundationCode.Workload;

/**
 * Created by ochipara on 2/11/16.
 * Modified by ryanbrummet on 2/16/16
 *
 * The simulator will be calling the node in the following manner
 *
 * (1) the contend function will be called. In this function you should implement (a) the release of packets from flows
 * and (b) determine whether a packet should be transmitted in the current slot. If a packet is to be transmitted in the
 * current slot then you should return it
 *
 * (2) the channelFeedback function will be called. This function will provide you with the state of the channel
 * (i.e., is the channel free or busy). You should use this information to appropriately implement the backoff
 *
 * (3a) the transmitResult will be called only if you have decided to transmit a packet in the contend function.
 * This function will let you know if the transmission was successful or not.
 *
 * (3b) the receive function will be called when the node receives a packet
 *
 *
 */
public abstract class Node {
    public static final int INACTIVE = 0;
    public static final int CONTENDING = 1;

    protected final Vertex vertex;
    protected final PriorityQueue<Packet> queue = new PriorityQueue<Packet>(50,PacketComparator.comparator());
    protected final Workload workload;
    protected final List<Flow> sourceFlows = new ArrayList<>();
    protected boolean isSource;
    protected final Simulator simulator;
    protected final Random random;

    protected int backoff = -1;
    protected int cw;
    protected int state = INACTIVE;
    
    private int maxQueueSize;
    
    public int slotTransmissionResult;
    public int slotReceptionResult;


    // collect statistics
    protected NodeStatistics stats = new NodeStatistics();   
    
    /**
     * creates a new node
     * @param vertex
     * @param oldSimulator
     * @param maxQueueSize
     * @param seed
     */
    public Node(Vertex vertex, Simulator simulator, int maxQueueSize, int seed) {
        this.vertex = vertex;
        this.simulator = simulator;
        this.workload = simulator.getWorkload();

        this.random = new Random(vertex.getId() * 1234 + seed);

        for (Flow flow : workload.getFlows()) {
            if (flow.getSource() == vertex) {
                sourceFlows.add(flow);
            }
        }
        isSource = sourceFlows.size() > 0;
        this.maxQueueSize = maxQueueSize;
    }

    /**
     *
     * This method is called in every time slot to
     * (1) do channel access
     * (2) release any traffic that is necessary
     * 
     * THIS MUST CALL THE METHOD updatePacketTimeCounters()
     *
     * @param time
     * @return a packet that you want to transmit during the current time slot
     */
    protected abstract Packet abstractContend(long time);
    
    /**
     * This method is added only to make sure that each time abstractContend is called that each packet in this nodes queue has its timers incremented
     * @param time
     * @return
     */
    public Packet contend(long time) {
    	slotTransmissionResult = TransmissionChannelConstants.IDLE;
    	slotReceptionResult = ReceptionChannelConstants.IDLE;
    	Packet packet = abstractContend(time);
    	
    	for(Packet p : queue) {
        	p.incrementTimeSinceCreation();
        	p.incrementTimeInCurrentQueue();
        }
    	return packet;
    }


    /**
     * This method is called to provide channel feedback regarding the state of the channel
     *
     * @param free - indicate whether the channel is free or not
     */
    public abstract void channelFeedback(boolean free);


    /**
     * Indicates whether the transmission you decided to make in this timeslot is successful or not (a packet MUST NOT be removed from the queue unless its used all the slots in needs to complete transmission)
     * @param time
     * @param packet
     * @param success
     */
    public abstract void transmitResult(long time, Packet packet, boolean success);
    
    /**
     * You received a packet. Returns ((mean latency) / (deadline)) of a packet if the destination was the gateway, null otherwise
     * @param time
     * @param packet
     * @param packetDropped
     * @return
     */
    public abstract Packet abstractReceive(long time, Packet packet, boolean packetDropped);

    /**
     * This method is added only to make sure that each time abstractReceived is called the received packet's time in queue  is reset to zero
     * @param time
     * @return
     */
    public Packet receive(long time, Packet packet, boolean packetDropped) {
    	Packet receivedPacket = abstractReceive(time, packet, packetDropped);
    	if(receivedPacket != null) {
    		receivedPacket.resetTimeInCurrentQueue();
    		return receivedPacket;
    	} else {
    		return null;
    	}
    }
    
    /**
     * places a packet in this nodes queue to be sent to the nextNode as indicated by information contained in the packet.
     * packets are only placed in the queue if there is room, otherwise they are dropped
     * @param time
     * @param packet
     */
    protected void startTransmission(long time, Packet packet) {
    	
    	if(queue.size() < maxQueueSize) {
    		queue.add(packet);
            /*
            if(queue.size() > maxQueueSize) {
            	throw new IllegalStateException("The number of packets in node " + vertex.toString() + " exceeds its maximum allowed.");
            }*/

            packet.setSource(this);
            Vertex nextHop = packet.getFlow().nextHop(vertex);
            Node nextNode = simulator.getNode(nextHop);
            packet.setDestination(nextNode);

            if (state == INACTIVE) {
                // start sending only if the state is inactive
                // otherwise, we are already in the middle of a transmission
                // so just put the packet in the queue
                //
                sendNext(time);
            }
    	}
    }

    /**
     * specifies behavior of node when told to send next packet in queue: must check that the queue is non empty, in correct state WHEN CALLED, assign contention state, and assign backoff value
     * @param time
     * @throws IllegalStateException
     */
    protected abstract void sendNext(long time);

    /**
     * returns the NodeStatistics object associated with this node
     * @return
     */
    public NodeStatistics getStatistics() {
        return stats;
    }

    /**
     * returns the name of the vertex associated with this node
     * @return
     */
    @Override
    public String toString() {
        return vertex.toString();
    }

    /**
     * returns the state of this node (Contending, inactive, etc)
     * @return
     */
    public int getState() {
        return state;
    }
    
    /**
     * returns the number of packets currently in this nodes queue
     * @return
     */
    public int getNumPacketsInQueue() {
    	return queue.size();
    }
    
    /**
     * returns the maximum number of packets allowed in this nodes queue at a time
     * @return
     */
    public int getMaxQueueSize() {
    	return maxQueueSize;
    }
    
    /**
     * returns the vertex associated with this node
     * @return
     */
    public Vertex getVertex() {
    	return vertex;
    }
    
    
}
