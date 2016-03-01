package simulator.statistics;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import constants.TransmissionChannelConstants;

/**
 * Created by ochipara on 2/12/16.
 * Comments added by ryanbrummet on 2/15/16
 */
public class RunStatistics {
    protected final int contenders[];
    private final int[] successfulTransmissions;
    private int numPacketsCreated;
    private final int numOfSlotsUsed;

    /**
     * creates a new RunStatistics object
     * @param numSlots
     */
    public RunStatistics(int numSlots) {
        contenders = new int[numSlots];
        this.successfulTransmissions = new int[numSlots];
        this.numPacketsCreated = 0;
        this.numOfSlotsUsed = numSlots;
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
     * for the given timeslot, sets whether a transmission occurred or is ongoing (int is used instead of boolean to allow 
     * for different kinds of success: 0 is idle, 1 is contention, 2 is transmission, 3 is packet dropped, 
     * 4 is failed due to random chance)
     * @param slot
     * @param success
     */
    public void setsuccessfulTransmission(int slot, int success) {
    	successfulTransmissions[slot] = success;
    }
    
    /**
     * returns an array giving the number of contenders in each slot of the simulation associated with this object
     * @return
     */
    public int[] getContenders() {
    	return contenders;
    }
    
    /**
     * returns an array giving the transmission results of every slot of the simulation associated with this object
     * @return
     */
    public int[] getSuccessfulTransmissions() {
    	return successfulTransmissions;
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
    public void saveSuccessfulTransmissions(String fn) throws IOException {
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
    		if(successfulTransmissions[i] == TransmissionChannelConstants.IDLE) {
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
    		if(successfulTransmissions[i] == TransmissionChannelConstants.CONTENTION) {
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
    	for(int i = 0; i < successfulTransmissions.length; i++) {
    		if(successfulTransmissions[i] == TransmissionChannelConstants.TRANSMISSION) {
    			percent++;
    		}
    	}
    	return percent / successfulTransmissions.length;
    }
    
    /**
     * used to update the number of unique packets created during a simulation
     * @param numNewPackets
     */
    public void updateNumPacketsCreated(int numNewPackets) {
    	numPacketsCreated += numNewPackets;
    }
    
    /**
     * returns the number of packets that were dropped during the simulation
     * @return
     */
    public int getNumPacketsDropped() {
    	int num = 0;
    	for(int i = 0; i < successfulTransmissions.length; i++) {
    		if(successfulTransmissions[i] == TransmissionChannelConstants.PACKET_DROPPED) {
    			num++;
    		}
    	}
    	return num;
    }
    
    /**
     * returns the percentage of packets that were created that were dropped
     * @return
     */
    public double getPercentOfPacketsDropped() {
    	double temp = getNumPacketsDropped();
    	return temp / numPacketsCreated;
    }
    
    /**
     * returns the number of packets that were created during the simulation
     * @return
     */
    public int getNumPacketsCreated() {
    	return numPacketsCreated;
    }
    
    /**
     * returns the percentage of the time during which the channel experienced random failures
     * @return
     */
    public double getChannelRandomFailurePercent() {
    	double percent = 0;
    	for(int i = 0; i < successfulTransmissions.length; i++) {
    		if(successfulTransmissions[i] == TransmissionChannelConstants.FAILED) {
    			percent++;
    		}
    	}
    	return percent / successfulTransmissions.length;
    }
    
    /**
     * returns the number of slots used to run the simulation associated with this RunStatistics object
     * @return
     */
    public int numSlotsUsedInSimulation(){
    	return numOfSlotsUsed;
    }
}
