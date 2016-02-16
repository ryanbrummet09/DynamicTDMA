package simulator;

import topology.Vertex;
import workload.Flow;
import workload.PeriodicFlow;
import workload.SaturationFlow;
import workload.Workload;

import java.util.*;

/**
 * Created by ochipara on 2/11/16.
 *
 * The simulator will be calling the node in the following manner
 *
 * (1) the contend function will be called. In this function you should implement (a) the release of packets from flows
 * and (b) determine whether a packet should be transmitted in the current slot. If a packet is to be transmitted in the
 * current slot then you should return it
 *
 * (2) the channelFeedback function will be called. This function will provide you with the state of the channel
 * (i.e., is the channel free or busy). You should use this information to appropriately implement the backoff
 *
 * (3a) the transmitResult will be called only if you have decided to transmit a packet in the contend function.
 * This function will let you know if the transmission was successful or not.
 *
 * (3b) the receive function will be called when the node receives a packet
 *
 *
 */
public abstract class Node {
    public static final int INACTIVE = 0;
    public static final int CONTENDING = 1;

    protected final Vertex vertex;
    protected final PriorityQueue<Packet> queue = new PriorityQueue<Packet>(500,PacketComparator.comparator());
    protected final Workload workload;
    protected final List<Flow> sourceFlows = new ArrayList<>();
    protected boolean isSource;
    protected final Simulator simulator;
    protected final Random random;

    protected int backoff = -1;
    protected int cw = 128;
    protected int state = INACTIVE;


    // collect statistics
    protected NodeStatistics stats = new NodeStatistics();   
    
    /**
     * creates a new node
     * @param vertex
     * @param simulator
     * @param seed
     */
    public Node(Vertex vertex, Simulator simulator, int seed) {
        this.vertex = vertex;
        this.simulator = simulator;
        this.workload = simulator.getWorkload();

        this.random = new Random(vertex.getId() * 1234 + seed);

        for (Flow flow : workload.getFlows()) {
            if (flow.getSource() == vertex) {
                sourceFlows.add(flow);
            }
        }
        isSource = sourceFlows.size() > 0;
    }

    /**
     *
     * This method is called in every time slot to
     * (1) do channel access
     * (2) release any trafic that is necessary
     *
     * @param time
     * @return a packet that you want to transmit during the current time slot
     */
    public abstract Packet contend(long time);


    /**
     * This method is called to provide channel feedback regarding the state of the channel
     *
     * @param free - indicate whether the channel is free or not
     */
    public abstract void channelFeedback(boolean free);


    /**
     * Indicates whether the transmission you decided to make in this timeslot is successful or not (a packet MUST NOT be removed from the queue unless its used all the slots in needs to complete transmission)
     * @param time
     * @param packet
     * @param success
     */
    public abstract void transmitResult(long time, Packet packet, boolean success);


    /**
     * You received a packet
     * @param time
     * @param packet
     */
    public abstract void receive(long time, Packet packet);
    
    /**
     * places a packet in this nodes queue to be sent to the nextNode as indicated by information contained in the packet
     * @param time
     * @param packet
     */
    protected void startTransmission(long time, Packet packet) {
        queue.add(packet);

        packet.setSource(this);
        Vertex nextHop = packet.getFlow().nextHop(vertex);
        Node nextNode = simulator.getNode(nextHop);
        packet.setDestination(nextNode);

        if (state == INACTIVE) {
            // start sending only if the state is inactive
            // otherwise, we are already in the middle of a transmission
            // so just put the packet in the queue
            //
            sendNext(time);
        }
    }

    /**
     * does nothing if queue is empty otherwise waits a random amount of time (based on cw) before transmitting next packet
     * @param time
     * @throws IllegalStateException
     */
    protected void sendNext(long time) {
        if (queue.size() == 0) return;
        if (state != INACTIVE) throw new IllegalStateException("The state should be inactive");

        state = CONTENDING;
        backoff = random.nextInt(cw);
    }

    /**
     * returns the NodeStatistics object associated with this node
     * @return
     */
    public NodeStatistics getStatistics() {
        return stats;
    }

    /**
     * returns the name of the vertex associated with this node
     * @return
     */
    @Override
    public String toString() {
        return vertex.toString();
    }

    /**
     * returns the state of this node (Condtending, inactive, etc)
     * @return
     */
    public int getState() {
        return state;
    }
}
