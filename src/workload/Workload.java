package workload;

import topology.Vertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ochipara on 2/8/16.
 */
public class Workload {
    private List<Flow> flows = new ArrayList<>();

    public Workload() {
        Flow.counter = 0;
    }
    public Flow newFlow(Vertex source, Vertex destination, int phase, int period, int deadline) {
        Flow flow = new PeriodicFlow(source, destination, phase, period, deadline);
        flows.add(flow);
        return flow;
    }

    public List<Flow> getFlows() {
        return flows;
    }

    public Flow newSaturationFlow(Vertex source, Vertex destination) {
        Flow flow = new SaturationFlow(source, destination);
        flows.add(flow);

        return flow;
    }

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

    public void print() {
        for (Flow flow : flows) {
            System.out.println(flow);
        }
    }
}
