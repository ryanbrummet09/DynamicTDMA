package workload;

import java.util.Comparator;

/**
 * Created by ochipara on 2/12/16.
 */
public class DeadlineMonotonic implements Comparator<PeriodicFlow> {
    protected static DeadlineMonotonic dm = new DeadlineMonotonic();

    protected DeadlineMonotonic() {

    }

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
