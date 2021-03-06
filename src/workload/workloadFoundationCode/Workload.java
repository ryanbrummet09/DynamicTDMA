package workload.workloadFoundationCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import topology.topologyFoundationCode.Vertex;

/**
 * Created by ochipara on 2/8/16.
 * Modified by ryanbrummet on 2/15/2016
 */
public class Workload {
    private List<Flow> flows = new ArrayList<>();
    private int timeSlotsNeededToSendPacket;

    /**
     * auto assigns packet transmission time to 1 time unit
     */
    public Workload() {
        Flow.counter = 0;
        this.timeSlotsNeededToSendPacket = 1;
    }
    
    /**
     * assigns packet transmission time to the input param timeSlotsNeededToSendPacket
     * @param timeSlotsNeededToSendPacket
     */
    public Workload(int timeSlotsNeededToSendPacket) {
    	Flow.counter = 0;
    	this.timeSlotsNeededToSendPacket = timeSlotsNeededToSendPacket;
    }
    
    /**
     * calling this method creates a periodic flow where packets are sent/received by the default timeSlotsNeededToSendPacket
     * @param source
     * @param destination
     * @param phase
     * @param period
     * @param deadline
     * @return
     */
    public Flow newPeriodicFlow(Vertex source, Vertex destination, int phase, int period, int deadline) {
        Flow flow = new PeriodicFlow(source, destination, phase, period, deadline, timeSlotsNeededToSendPacket);
        flows.add(flow);
        return flow;
    }
    
    /**
     * calling this method creates a periodic flow where packets are sent/received by the passed packetSlots (ie timeSlotsNeededToSendPacket)
     * @param source
     * @param destination
     * @param phase
     * @param period
     * @param deadline
     * @param packetSlots
     * @return
     */
    public Flow newPeriodicFlow(Vertex source, Vertex destination, int phase, int period, int deadline, int packetSlots) {
    	Flow flow = new PeriodicFlow(source, destination, phase, period, deadline, packetSlots);
    	flows.add(flow);
    	return flow;
    }

    /**
     * Returns the list of flows that are associated with this workflow
     * @return
     */
    public List<Flow> getFlows() {
        return flows;
    }

    /**
     * calling this method creates a saturation flow where packets are sent/received by the default timeSlotsNeededToSendPacket
     * @param source
     * @param destination
     * @return
     */
    public Flow newSaturationFlow(Vertex source, Vertex destination) {
        Flow flow = new SaturationFlow(source, destination, timeSlotsNeededToSendPacket);
        flows.add(flow);

        return flow;
    }
    
    /**
     * calling this method creates a saturation flow where packets are sent/received by the passed packetSlots (ie timeSlotsNeededToSendPacket) 
     * @param source
     * @param destination
     * @param packetSlots
     * @return
     */
    public Flow newSaturationFlow(Vertex source, Vertex destination, int packetSlots) {
    	Flow flow = new SaturationFlow(source, destination, packetSlots);
    	flows.add(flow);
    	
    	return flow;
    }
    
    /**
     * Assigns a priority to each flow associated with this workflow by monotonically comparing flow deadlines (shorter deadline => higher priority)
     * @throws UnsupportedOperationException
     */
    public void assignPriorities() {
        List<PeriodicFlow> periodicFlows = new LinkedList<>();
        for (Flow flow: flows) {
            if (flow instanceof PeriodicFlow) {
                periodicFlows.add((PeriodicFlow) flow);
            } else {
                throw new UnsupportedOperationException("All flows should be periodic");
            }
        }

        Collections.sort(periodicFlows, DeadlineMonotonic.comparator());
        int priority = 0;
        for (PeriodicFlow flow : periodicFlows) {
            flow.setPriority(priority++);
        }
    }

    /**
     * Prints each flow to stdout associated with this workflow
     */
    public void print() {
        for (Flow flow : flows) {
            System.out.println(flow);
        }
    }
    
    /**
     * calculates and returns the length of the hyper period of this workload where only the periodic
     * flows of this workload are used to determine the hyper period
     * @return
     */
    public int getLengthOfHyperPeriod() {
    	ArrayList<Integer> deadlines = new ArrayList<Integer>();
    	for(int i = 0; i < flows.size(); i++) {
    		if (flows.get(i) instanceof PeriodicFlow) {
    			deadlines.add(((PeriodicFlow) flows.get(i)).deadline);
    		}
    	}
    	int hyperPeriodLength = 0;
    	for(int i = 0; i < flows.size() - 1; i++) {
    		if(i == 0) {
    			hyperPeriodLength = lcm(deadlines.get(i),deadlines.get(i + 1));
    		} else {
    			hyperPeriodLength = lcm(hyperPeriodLength, deadlines.get(i + 1));
    		}
    	}
    	return hyperPeriodLength;
    }
    
    /**
     * finds the greatest common divisor of a and b
     * @param a
     * @param b
     * @return
     */
    private static int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
    
    /**
     * finds the least common multiple of a and b
     * @param a
     * @param b
     * @return
     */
    private static int lcm(int a, int b) {
    	if (a < 0) {
    		a = -a;
    	}
    	if (b < 0) {
    		b = -b;
    	}
    	return (a * b) / gcd(a,b);
    }
}
