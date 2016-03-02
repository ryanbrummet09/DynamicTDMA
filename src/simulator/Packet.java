package simulator;

import java.util.HashMap;
import java.util.Map;

import workload.workloadFoundationCode.Flow;
import workload.workloadFoundationCode.PeriodicFlow;

/**
 * Created by ochipara on 2/11/16.
 * Modified by ryanbrummet on 2/15/16
 */
public class Packet {
    private Node source;
    private Node destination;
    private final Flow flow;
    private final int instance;
    private int timeSlotsNeededToSendPacket;
    protected Map<Flow, Integer> instanceCounters = new HashMap<>();
    protected int timeSinceCreation; // gives the amount of time that has passed since this packet was created
    private boolean packetHasBeenCounted;
    private int timeInCurrentQueue;
    private int packetDropped;

    /**
     * creates a new Packet for the given flow
     * @param flow
     */
    public Packet(Flow flow) {
        this.source = null;
        this.destination = null;
        this.flow = flow;
        this.timeSlotsNeededToSendPacket = flow.getTimeSlotsNeededToSendPacket();
        if (instanceCounters.containsKey(flow) == false) {
            instanceCounters.put(flow, 0);
        }

        int count = instanceCounters.get(flow);
        instance = count;
        instanceCounters.put(flow, count + 1);
        this.timeSinceCreation = 0;
        packetHasBeenCounted = false;
        timeInCurrentQueue = 0;
        packetDropped = 0;
    }

    /**
     * returns the node that is sending this packet
     * @return
     */
    public Node getSource() {
        return source;
    }

    /**
     * returns the node that this packet is being sent to
     * @return
     */
    public Node getDestination() {
        return destination;
    }

    /**
     * sets the node that this packet is being sent from
     * @param source
     */
    public void setSource(Node source) {
        this.source = source;
    }

    /**
     * returns the flow that this packet is part of
     * @return
     */
    public Flow getFlow() {
        return flow;
    }

    /**
     * sets the node that this packet is being sent to
     * @param destination
     */
    public void setDestination(Node destination) {
        this.destination = destination;
    }

    /**
     * writes this packet's information to stdout in format (%s -> %s flow %d", source, destination, flow.getId())
     */
    public String toString() {
        if (flow != null) {
            return String.format("%s -> %s flow %d", source, destination, flow.getId());
        } else {
            if (destination == null) {
                return String.format("%s -> *", source, destination);
            } else {
                return String.format("%s -> %s", source, destination);
            }
        }
    }

    /**
     * returns the instance of this packet
     * @return
     */
    public int getInstance() {
        return instance;
    }
    
    /**
     * resets the number of slots needed to finish the packet's transmission (used in cases of node failure or upon packet reception)
     */
    public void resetPacketTransmission() {
    	timeSlotsNeededToSendPacket = flow.getTimeSlotsNeededToSendPacket();
    }
    
    /**
     * decrements the number of slots needed to complete this packet transmission, returns true if packet has been completely transmitted
     * @throws IllegalStateException
     * @return
     */
    public boolean decrementSlotsNeededToCompletePacketTransmission() {
    	if(timeSlotsNeededToSendPacket > 0) {
    		timeSlotsNeededToSendPacket--;
    		if(timeSlotsNeededToSendPacket == 0) {
    			return true;
    		} else {
    			return false;
    		}
    	} else {
    		throw new IllegalStateException("You cannot decrement the number of slots needed to send a packet when the number of slots is zero");
    	}
    }
    
    /**
     * Returns the number of remaining time slots needed to complete the transmission of this packet from source to destination
     * @return
     */
    public int getSlotsNeededToCompletePacketTransmission() {
    	return timeSlotsNeededToSendPacket;
    }
    
    /**
     * increments the time counter for this packet that keeps track of how long this packet has existed
     */
    public void incrementTimeSinceCreation() {
    	timeSinceCreation++;
    }
    
    /**
     * If this packet is associated with a periodic flow, returns the ratio of packet life length to deadline.
     * 
     * A positive value less than or equal to one means that the deadline was met while a value greater than one means it was not.
     * 
     * A negative value means that the flow associated with this packet is not a PeriodicFlow.
     * @return
     */
    public double getLatencyDeadlineRatio() {
    	if(flow instanceof PeriodicFlow) {
    		return timeSinceCreation / ((PeriodicFlow) flow).getDeadline();
    	} else {
    		return -1;
    	}
    }
    
    /**
     * returns true if this packet has been previously counted by calling count packet
     * @return
     */
    public boolean getPacketHasBeenCounted() {
    	return packetHasBeenCounted;
    }
    
    /**
     * This method is called to indicate that this packet has been counted.  We use this method so that we can
     * count the number of unique packets that are created
     */
    public void countPacket() {
    	packetHasBeenCounted = true;
    }
    
    /**
     * Returns the amount of time that this packet has been in its current queue (ie the queue of the node this packet is in)
     * @return
     */
    public int getTimeInCurrentQueue() {
    	return timeInCurrentQueue;
    }
    
    /**
     * increases the time counter keeping track of how long this packet has been in its current queue (ie the queue of the node this packet is int)
     */
    public void incrementTimeInCurrentQueue() {
    	timeInCurrentQueue++;
    }
    
    /**
     * resets the time counter of this packet for how long it has been in the current queue to zero (this indicates that this packet has been transmitted)
     * @return
     */
    public void resetTimeInCurrentQueue() {
    	timeInCurrentQueue = 0;
    }
    
    /**
     * This method is called when this packet has been dropped upon reception by a receiving node (after being transmitted and removed from senders queue)
     */
    public void dropPacket(){
    	packetDropped = 1;
    }
    
    /**
     * returns true if this packet has been dropped, false otherwise
     * @return
     */
    public int getPacketDropped() {
    	return packetDropped;
    }
}
