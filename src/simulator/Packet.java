package simulator;

import workload.Flow;

import java.util.HashMap;
import java.util.Map;

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
}
