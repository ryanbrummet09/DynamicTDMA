package constants;

/**
 * constants for channel transmission
 * @author ryanbrummet
 *
 */
public class TransmissionChannelConstants {
	
	public static final int IDLE = 0;  // no traffic on channel or nothing sent
	public static final int CONTENTION = 1;  // more than one node is attempting to transmit on channel (this node participated)
	public static final int TRANSMISSION = 2; // one node sent a packet (or portion of one) over the channel (this node) NOTICE THAT THIS DOES NOT MEAN THE PACKET TRANSMISSION COMPLETELED, IT MAY TAKE MORE SLOTS TO FINISH TRANSMISSION
	public static final int PACKET_DROPPED = 3; // packet dropped (packet sent by node was dropped by receiver)
	public static final int FAILED = 4;  // transmission failed due to random chance (ie transmission occurred but did not reach destination)

}
