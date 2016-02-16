package csma;

import simulator.Node;
import simulator.Packet;
import simulator.Simulator;
import topology.Vertex;
import workload.Flow;
import workload.PeriodicFlow;
import workload.SaturationFlow;
import workload.Workload;

/**
 * Created by ochipara on 2/13/16.
 */
public class SimpleCSMA extends Node {
    public SimpleCSMA(Vertex vertex, Simulator simulator) {
        super(vertex, simulator);
    }

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

    @Override
    public void channelFeedback(boolean free) {
        if ((free) && (state == CONTENDING)) {
            if (backoff > 0) backoff--;
        }
    }

    @Override
    public void transmitResult(long time, Packet packet, boolean success) {
        assert (state == CONTENDING);

        if (success) {
            logger.debug(String.format("%d %s transmission %s successful", time, vertex, packet));
            // remove the packet from the queue
            Packet p = queue.remove();
            assert (p == packet);

            stats.tx += 1;
        } else {
            logger.debug(String.format("%d %s transmission %s unsuccessful", time, vertex, packet));
        }

        state = INACTIVE;
        sendNext(time);
    }

    @Override
    public void receive(long time, Packet packet) {
        assert (packet.getDestination() == this);

        logger.debug(String.format("%d %s received packet %s", time, vertex, packet));
        stats.rx++;
        if (packet.getFlow().getDestination() != vertex) {
            startTransmission(time, packet);
        } else {
            // i am the destination, so I do not have to do anything
        }
    }


}
