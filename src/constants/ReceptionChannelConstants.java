package constants;

/**
 * Constants for packet reception
 * @author ryanbrummet
 *
 */
public class ReceptionChannelConstants {
	public static final int IDLE = 0;  // indicates no packet was sent or collision
	public static final int PACKET_DROPPED = 1;  // receiving node dropped packet
	public static final int PACKET_RECEIVED = 2; // receiving node properly received packet
}
