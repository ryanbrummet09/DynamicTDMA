package simulator;

/**
 * Created by ochipara on 2/12/16.
 * Modified by ryanbrummet on 2/15/16
 */
public class NodeStatistics {
    private long tx = 0;
    private long rx = 0;

    /**
     * returns the number of received packets 
     * @return
     */
    public long getRx() {
        return rx;
    }
    
    /**
     * returns the number of transmitted packets 
     * @return
     */
    public long getTx() {
    	return tx;
    }
    
    /**
     * increments the number of received packets by one 
     */
    public void incRx() {
    	rx++;
    }
    
    /**
     * increments the number of transmitted packets by one 
     */
    public void incTx() {
    	tx++;
    }
}
