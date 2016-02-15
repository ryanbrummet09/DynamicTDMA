package simulator;

import workload.Flow;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ochipara on 2/11/16.
 */
public class Packet {
    private Node source;
    private Node destination;
    private final Flow flow;
    private final int instance;
    protected Map<Flow, Integer> instanceCounters = new HashMap<>();

    public Packet(Flow flow) {
        this.source = null;
        this.destination = null;
        this.flow = flow;
        if (instanceCounters.containsKey(flow) == false) {
            instanceCounters.put(flow, 0);
        }

        int count = instanceCounters.get(flow);
        instance = count;
        instanceCounters.put(flow, count + 1);
    }

    public Node getSource() {
        return source;
    }

    public Node getDestination() {
        return destination;
    }

    public void setSource(Node source) {
        this.source = source;
    }

    public Flow getFlow() {
        return flow;
    }

    public void setDestination(Node destination) {
        this.destination = destination;
    }


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

    public int getInstance() {
        return instance;
    }
}
