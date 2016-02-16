package simulator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by ochipara on 2/12/16.
 * Comments added by ryanbrummet on 2/15/16
 */
public class RunStatistics {
    protected final int contenders[];

    /**
     * creates a new RunStatistics object
     * @param numSlots
     */
    public RunStatistics(int numSlots) {
        contenders = new int[numSlots];
    }

    /**
     * saves the number of contenders for the given timeslot
     * @param slot
     * @param count
     */
    public void setContenders(int slot, int count) {
        contenders[slot] = count;
    }

    /**
     * saves the the number of contenders for each time slot from a full simulation run to file
     * @param fn
     * @throws IOException
     */
    public void saveContenders(String fn) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(fn));
        for (int slot = 0; slot < contenders.length; slot++) {
            bw.write(contenders[slot] + " ");
        }

        bw.close();
    }
}
