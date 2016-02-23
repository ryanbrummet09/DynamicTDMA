package workload;

import topology.Vertex;

/**
 * Created by ochipara on 2/12/16.
 */
public class SaturationFlow extends Flow {

	/**
	 * creates a new Saturation flow
	 * @param source
	 * @param destination
	 * @param timeSlotsNeededToSendPacket
	 */
    public SaturationFlow(Vertex source, Vertex destination, int timeSlotsNeededToSendPacket) {
        super(source, destination,timeSlotsNeededToSendPacket);
    }
    
    /**
     * prints information about this flow to stdout in the form ("%d [%s]", id, printPath())
     * @return
     */
    @Override
    public String toString() {
        return String.format("SaturationFlow: %d [%s]", id, printPath());
    }
}
