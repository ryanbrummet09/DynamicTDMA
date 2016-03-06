package simulator;

import java.util.Comparator;

/**
 * Created by ochipara on 2/12/16.
 * Comments added by ryanbrummet on 2/15/16
 */
public class PacketComparator implements Comparator<Packet> {
    protected static PacketComparator comparator = new PacketComparator();

    /**
     * compares/orders packets by priority
     */
    @Override
    public int compare(Packet packet1, Packet packet2) {
        int flow1 = packet1.getFlow().getPriority();
        int flow2 = packet2.getFlow().getPriority();

        if (flow1 != flow2) {
            return Integer.compare(flow1, flow2);
        } else {
            int c1 = packet1.getInstance();
            int c2 = packet2.getInstance();

            return  Integer.compare(c1, c2);
        }
    }

    /**
     * returns the static PacketComparator object
     * @return
     */
    public static PacketComparator comparator() {
        return comparator;
    }
}
