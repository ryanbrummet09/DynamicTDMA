package protocols;

import simulator.Node;
import simulator.Packet;
import simulator.Simulator;
import topology.topologyFoundationCode.Vertex;
import workload.workloadFoundationCode.Flow;
import workload.workloadFoundationCode.PeriodicFlow;
import workload.workloadFoundationCode.SaturationFlow;

/**
 * Created by ochipara on 2/13/16.
 * Modified by ryanbrummet on 2/15/16
 */
public class SimpleCSMA extends Node {
	
	/**
	 * Constructor for SimpleCSMA node
	 * @param vertex
	 * @param simulator
	 * @param maxQueueSize
	 * @param contentionWindow
	 * @param seed
	 */
    public SimpleCSMA(Vertex vertex, Simulator simulator, int maxQueueSize, int contentionWindow, int seed) {
        super(vertex, simulator, maxQueueSize, seed);
        cw = contentionWindow;
    }

    /**
     * Instructs node to contend for the channel
     * @param time
     * @return
     */
    @Override
    protected Packet abstractContend(long time) {
        if (isSource) {
            // i am a source, so I need to check if I need to release a packet
            for (Flow flow : sourceFlows) {
                if (flow instanceof PeriodicFlow) {
                    PeriodicFlow periodicFlow = (PeriodicFlow) flow;
                    int phase = periodicFlow.getPhase();
                    int period = periodicFlow.getPeriod();

                    if (time % period == phase) {
                        // release the packet now
                        Packet packet = new Packet(periodicFlow);
                        startTransmission(time, packet);
                    }
                } else if (flow instanceof SaturationFlow) {
                    SaturationFlow saturationFlow = (SaturationFlow) flow;
                    if (queue.size() == 0) {
                        Packet packet = new Packet(saturationFlow);
                        startTransmission(time, packet);
                    }
                }
            }
        }

        if (state == CONTENDING) {
            if (queue.size() == 0) throw new IllegalStateException("You must have a packet if you're contending");

            // check the backoff counter and transmitResult if necessary
            if (backoff == 0) {
                // we will transmitResult
                Packet packet = queue.peek();
                return packet;
            }
        }

        return null;
    }

    /**
     * Given the passed status of the channel, calling this method results in the node taking appropriate action
     * @param free
     */
    @Override
    public void channelFeedback(boolean free) {
        if ((free) && (state == CONTENDING)) {
            if (backoff > 0) backoff--;
        }
    }

    /**
     * Handles the transmission of packets from this node (constant contention window)
     * @param time
     * @param packet
     * @param success
     */
    public void transmitResult(long time, Packet packet, boolean success) {
        assert (state == CONTENDING);
        assert (queue.peek() == packet);

        if (success) {
        	
        	if(packet.decrementSlotsNeededToCompletePacketTransmission()) {
        		// remove the packet from the queue
                queue.remove();

                stats.incTx();
                
                state = INACTIVE;
                sendNext(time);
        	}
        } else {
        	state = INACTIVE;
            sendNext(time);
        }
    }

    /**
     * Handles the reception of packets at this node.  Returns the latency deadline ratio of the packet if this node is its final destination or
     * -1 if the flow associated with the received packet is not periodic, if this node is not the packet's final destination, or if the packet was dropped.
     * If packetDropped is true, stats is updated but nothing else occurs (ie we drop the packet)
     * @param time
     * @param packet
     * @param packetDropped
     */
    @Override
    public double receive(long time, Packet packet, boolean packetDropped) {
        assert (packet.getDestination() == this);

        stats.incRx();
        if(!packetDropped) {
        	if (packet.getFlow().getDestination() != vertex) {
                startTransmission(time, packet);
            } else {
                return packet.getLatencyDeadlineRatio();
            }
        }
        
        return -1;
    }
    
    /**
     * does nothing if queue is empty otherwise waits a random amount of time (based on cw) before transmitting next packet
     * @param time
     * @throws IllegalStateException
     */
    @Override
    protected void sendNext(long time) {
        if (queue.size() == 0) return;
        if (state != INACTIVE) throw new IllegalStateException("The state should be inactive");

        state = CONTENDING;
        backoff = random.nextInt(cw);
    }
}
