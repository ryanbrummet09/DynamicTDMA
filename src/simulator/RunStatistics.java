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
    private final int[] successfulTransmissions;

    /**
     * creates a new RunStatistics object
     * @param numSlots
     */
    public RunStatistics(int numSlots) {
        contenders = new int[numSlots];
        this.successfulTransmissions = new int[numSlots];
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
     * for the given timeslot, sets whether a transmission occurred or is ongoing (int is used instead of boolean to allow for different kinds of success: 0 is idle, 1 is contention, 2 is transmission,)
     * @param slot
     * @param success
     */
    public void setsuccessfulTransmission(int slot, int success) {
    	successfulTransmissions[slot] = success;
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
    
    /**
     * saves success/failure information for each time slot from a full simulation run to file
     * @param fn
     * @throws IOException
     */
    public void saveSuccesses(String fn) throws IOException {
    	BufferedWriter bw = new BufferedWriter(new FileWriter(fn));
    	for (int slot = 0; slot < successfulTransmissions.length; slot++) {
    		bw.write(successfulTransmissions[slot] + " ");
    	}
    	bw.close();
    }
    
    /**
     * returns the percentage of time during which there is no contention for the channel and no packets are being transmitted
     * @return
     */
    public double getChannelIdlePercent() {
    	double percent = 0;
    	for(int i = 0; i < successfulTransmissions.length; i++ ) {
    		if(successfulTransmissions[i] == 0) {
    			percent++;
    		}
    	}
    	return percent / successfulTransmissions.length;
    }
    
    /**
     * returns the percentage of time during which there is contention on the channel
     * @return
     */
    public double getChannelContentionPercent() {
    	double percent = 0;
    	for(int i = 0; i < successfulTransmissions.length; i++ ) {
    		if(successfulTransmissions[i] == 1) {
    			percent++;
    		}
    	}
    	return percent / successfulTransmissions.length;
    }
    
    /**
     * returns the percentage of time during which the channel is used to actually send packets
     * @return
     */
    public double getChannelRealUsagePercent() {
    	double percent = 0;
    	for(int i = 0; i < successfulTransmissions.length; i++ ) {
    		if(successfulTransmissions[i] == 2) {
    			percent++;
    		}
    	}
    	return percent / successfulTransmissions.length;
    }
}
