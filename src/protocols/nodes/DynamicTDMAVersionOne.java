package protocols.nodes;

import constants.ReceptionChannelConstants;
import constants.TransmissionChannelConstants;
import simulator.Node;
import simulator.Packet;
import simulator.Simulator;
import topology.topologyFoundationCode.Vertex;
import workload.workloadFoundationCode.Flow;
import workload.workloadFoundationCode.PeriodicFlow;
import workload.workloadFoundationCode.SaturationFlow;

/**
 * 
 * @author ryanbrummet
 *
 */
public class DynamicTDMAVersionOne extends Node{
	
	private int[] schedule;
	private int currentHyperPeriodTime;
	
	public DynamicTDMAVersionOne(Vertex vertex, Simulator simulator, int maxQueueSize, int minContentionWindow, int[] schedule, int seed) {
		super(vertex, simulator, maxQueueSize, seed);
		this.cw = minContentionWindow;
		this.schedule = schedule;
		currentHyperPeriodTime = -1;
	}
	
	protected Packet abstractContend(long time) {
		
		// determine if a packet needs to be created
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
		
		// determine protocol and whether to "contend"
		currentHyperPeriodTime = (int) (time % schedule.length);
		if(schedule[currentHyperPeriodTime] < 0) {
			// run CSMA
			if (state == CONTENDING) {
	            if (queue.size() == 0) throw new IllegalStateException("You must have a packet if you're contending");

	            // check the backoff counter and transmitResult if necessary
	            if (backoff == 0) {
	                // we will transmitResult
	                Packet packet = queue.peek();
	                slotTransmissionResult = TransmissionChannelConstants.CONTENTION;
	                return packet;
	            }
	        }
			return null;
		} else if(schedule[currentHyperPeriodTime] == vertex.getId()){
			// run TDMA if this node has been assigned this slot
			Packet packet = queue.peek();
            slotTransmissionResult = TransmissionChannelConstants.CONTENTION;
            return packet;
		} else {
			// this node cannot send a packet in this slot
			// notice that we do not update any backoff counters, we only update the backoff counter
			// if the scheduled slot is CSMA
			return null;
		}
	}
	
	/**
     * Given the passed status of the channel and what portion of the workflow hyper period time is in, 
     * calling this method results in the node taking appropriate action.  The backoff is only decremented
     * if CSMA has been scheduled in the current slot.  The reason being is that it doesn't make sense to decrement
     * the backoff if the CSMA protocol isn't being used.  THEREFORE THE CONTENTION WINDOW SHOULD BE RELATIVELY SMALL
     * AT ALL TIMES
     * @param free
     */
    @Override
	public void channelFeedback(boolean free) {	
		if(schedule[currentHyperPeriodTime] < 0) {
			if ((free) && (state == CONTENDING)) {
	            if (backoff > 0) backoff--;
	        }
		}
    }
    
    /**
     * Handles the transmission of packets from this node
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
     * Handles the reception of packets at this node.  Returns the packet if this node is its final destination or it was dropped.
     * Returns null if the flow associated with the received packet is not periodic, or if this node is not the packet's final destination.
     * If packetDropped is true, stats is updated but nothing else occurs (ie we drop the packet)
     * @param time
     * @param packet
     * @param packetDropped
     */
    @Override
    public Packet abstractReceive(long time, Packet packet, boolean packetDropped) {
        assert (packet.getDestination() == this);
        
        stats.incRx();
        if(!packetDropped) {
        	slotReceptionResult = ReceptionChannelConstants.PACKET_RECEIVED;
        	if (packet.getFlow().getDestination() != vertex) {
                startTransmission(time, packet);
            } else {
                return packet;
            }
        } else {
        	slotReceptionResult = ReceptionChannelConstants.PACKET_DROPPED;
        	packet.dropPacket();
        	return packet;
        }
        
        return null;
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
