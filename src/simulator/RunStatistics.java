package simulator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by ochipara on 2/12/16.
 */
public class RunStatistics {
    protected final int contenders[];

    public RunStatistics(int numSlots) {
        contenders = new int[numSlots];
    }

    public void setContenders(int slot, int count) {
        contenders[slot] = count;
    }

    public void saveContenders(String fn) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(fn));
        for (int slot = 0; slot < contenders.length; slot++) {
            bw.write(contenders[slot] + " ");
        }

        bw.close();
    }
}
