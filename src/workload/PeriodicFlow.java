package workload;

import topology.Vertex;

/**
 * Created by ochipara on 2/12/16.
 * Modified by ryanbrummet on 2/15/16
 */
public class PeriodicFlow extends Flow {
    protected final int phase;
    protected final int period;
    protected final int deadline;

    /**
     * Creates a new periodic flow
     * @param source
     * @param destination
     * @param phase
     * @param period
     * @param deadline
     * @param timeSlotsNeededToSendPacket
     */
    public PeriodicFlow(Vertex source, Vertex destination, int phase, int period, int deadline, int timeSlotsNeededToSendPacket) {
        super(source, destination, timeSlotsNeededToSendPacket);

        this.phase = phase;
        this.period = period;
        this.deadline = deadline;
    }

    /**
     * returns the phase of this periodic flow
     * @return
     */
    public int getPhase() {
        return phase;
    }

    /**
     * returns the period of this periodic flow
     * @return
     */
    public int getPeriod() {
        return period;
    }

    /**
     * returns the deadline of this periodic flow
     * @return
     */
    public int getDeadline() {
        return deadline;
    }

    /**
     * prints information about this flow to stdout in the form ("%d phi=%d period=%d deadline=%d prio=%d [%s]", id, phase, period, deadline, priority, printPath())
     * @return
     */
    @Override
    public String toString() {
        return String.format("%d phi=%d period=%d deadline=%d prio=%d [%s]", id, phase, period, deadline, priority, printPath());
    }
}
