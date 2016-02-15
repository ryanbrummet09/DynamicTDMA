package workload;

import topology.Vertex;

/**
 * Created by ochipara on 2/12/16.
 */
public class PeriodicFlow extends Flow {
    protected final int phase;
    protected final int period;
    protected final int deadline;

    public PeriodicFlow(Vertex source, Vertex destination, int phase, int period, int deadline) {
        super(source, destination);

        this.phase = phase;
        this.period = period;
        this.deadline = deadline;
    }

    public int getPhase() {
        return phase;
    }

    public int getPeriod() {
        return period;
    }

    public int getDeadline() {
        return deadline;
    }


    public String toString() {
        return String.format("%d phi=%d period=%d deadline=%d prio=%d [%s]", id, phase, period, deadline, priority, printPath());
    }
}
