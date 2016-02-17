package csma;

import simulator.Node;
import simulator.Packet;
import simulator.Simulator;
import topology.Vertex;
import workload.Flow;
import workload.PeriodicFlow;
import workload.SaturationFlow;

/**
 * 
 * @author ryanbrummet
 * created on 2/16/16
 */
public class ExponentialBackoffCSMA extends Node{
	
	private int minContentionWindow;
	private int maxContentionWindow;

	/**
	 * Constructor for ExponentialBackoffCSMA node
	 * @param vertex
	 * @param simulator
	 * @param minContentionWindow
	 * @param maxContentionWindow
	 * @param seed
	 */
	public ExponentialBackoffCSMA(Vertex vertex, Simulator simulator, int minContentionWindow, int maxContentionWindow, int seed) {
        super(vertex, simulator,seed);
        this.minContentionWindow = minContentionWindow;
        this.maxContentionWindow = maxContentionWindow;
    }
	
	/**
	 * Instructs node to contend for the channel
	 * @param time
	 * @return
	 */
	@Override
	public Packet contend(long time) {
        if (isSource) {
            // i am a source, so I need to check if I need to release a packet
            for (Flow flow : sourceFlows) {
                if (flow instanceof PeriodicFlow) {
                    PeriodicFlow periodicFlow = (PeriodicFlow) flow;
                    int phase = periodicFlow.getPhase();
                    int period = periodicFlow.getPeriod();

                    if (time % period == phase) {
                        // release the packet now
                        Packet packet = new Packet(flow);
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
	 * Handles the transmission of packets from this node (exponentially increasing contention window size)
	 * @param time
	 * @param packet
	 * @param success
	 */
	@Override
	public void transmitResult(long time, Packet packet, boolean success) {
        assert (state == CONTENDING);
        assert (queue.peek() == packet);

        if (success) {
        	if(packet.decrementSlotsNeededToCompletePacketTransmission()) {
        		// remove the packet from the queue
                queue.remove();
                stats.incTx();             
                cw = minContentionWindow;
   
                state = INACTIVE;
                sendNext(time);
        	}
        } else {
        	if (cw * 2 > maxContentionWindow) {
        		cw = maxContentionWindow;
        	} else {
        		cw = cw * 2;
        	}
        	
        	state = INACTIVE;
            sendNext(time);
        }
    }
	
	/**
     * Handles the reception of packets at this node
     * @param time
     * @param packet
     */
	@Override
	public void receive(long time, Packet packet) {
        assert (packet.getDestination() == this);

        stats.incRx();
        if (packet.getFlow().getDestination() != vertex) {
            startTransmission(time, packet);
        } else {
            // i am the destination, so I do not have to do anything
        }
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