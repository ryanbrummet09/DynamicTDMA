package workload;

import java.util.Comparator;

/**
 * Created by ochipara on 2/12/16.
 * Comments added by ryanbrummet on 2/15/2016
 */
public class DeadlineMonotonic implements Comparator<PeriodicFlow> {
    protected static DeadlineMonotonic dm = new DeadlineMonotonic();

    protected DeadlineMonotonic() {

    }

    /**
     *  compares and gives periodic flows with shorter deadlines higher priority
     *  @param flow1
     *  @param flow2
     *  @return
     */
    @Override
    public int compare(PeriodicFlow flow1, PeriodicFlow flow2) {
        int deadline1 = flow1.getDeadline();
        int deadline2 = flow2.getDeadline();
        return Integer.compare(deadline1, deadline2);
    }

    public static DeadlineMonotonic comparator() {
        return dm;
    }
}
