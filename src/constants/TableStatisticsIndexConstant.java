package constants;

/**
 * gives index values for a TableStatistic object.  These values are placed here instead of table statistic so that
 * they can be easily found and looked up (ie they are in the constants package)
 * @author ryanbrummet
 *
 */
public class TableStatisticsIndexConstant {
	
	public static final int TIME = 0;  // all values are recorded at the end of a slot before the start of the next
	public static final int NODE = 1;
	public static final int NODE_BACKOFF = 2;
	public static final int NODE_CW = 3;
	public static final int NODE_QUEUE_SIZE = 4;
	public static final int NODE_TRANSMIT_ACTION = 5;
	public static final int NODE_RECEIVE_ACTION = 6;
	public static final int NODE_STATE = 7;
	public static final int PACKET_PERIOD = 8;
	public static final int PACKET_PHASE = 9;
	public static final int PACKET_DEADLINE = 10;
	public static final int PACKET_SLACK = 11;
	public static final int PACKET_TYPE_INSTANCE_NUM = 12;
	public static final int PACKET_NEXT_DEST = 13;
	public static final int PACKET_CREATION_NODE = 14;
	public static final int PACKET_FINAL_DEST_NODE = 15;
	public static final int PACKET_SLOTS_TO_SEND_TO_NEXT_DEST = 16;
	
	public static final String[] COLUMN_NAMES = {"TIME", "NODE", "NODE_BACKOFF", "NODE_CW", "NODE_QUEUE_SIZE",
			"NODE_TRANSMIT_ACTION", "NODE_RECEIVE_ACTION", "NODE_STATE", "PACKET_PERIOD", "PACKET_PHASE",
			"PACKET_DEADLINE", "PACKET_SLACK", "PACKET_TYPE_INSTANCE_NUM", "PACKET_NEXT_DEST", "PACKET_CREATION_NODE",
			"PACKET_FINAL_DEST_NODE","PACKET_SLOTS_TO_SEND_TO_NEXT_DEST"};

}
