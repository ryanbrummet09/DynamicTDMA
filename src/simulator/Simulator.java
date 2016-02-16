package simulator;

import topology.Topology;
import topology.Vertex;
import workload.Workload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ochipara on 2/8/16.
 * Modified by ryanbrummet on 2/15/16
 */
public class Simulator {
    private final Topology topology;
    private final HashMap<Vertex, Node> vertex2nodes = new HashMap<>();
    private final List<Node> nodes = new ArrayList<>();
    private final Workload workload;
    private final NodeFactory nodeFactory;
    
    /**
     * creates a new simulator 
     * @param topology
     * @param workload
     * @param nodeFactory
     * @param seed
     */
    public Simulator(Topology topology, Workload workload, NodeFactory nodeFactory, int seed) {
        this.topology = topology;
        this.workload = workload;
        this.nodeFactory = nodeFactory;

        List<Vertex> vertices = topology.getVertices();
        for (Vertex vertex : vertices) {

            Node node = nodeFactory.newNode(vertex, this, seed);
            vertex2nodes.put(vertex, node);
            nodes.add(node);
        }
    }

    /**
     * Run the simulator for a given number of slots
     * @param numSlots
     * @return
     */
    public RunStatistics run(int numSlots) {
        List<Packet> inflight = new ArrayList<>();

        RunStatistics runStats = new RunStatistics(numSlots);

        for (long time = 0; time < numSlots; time++) {
            // check if the node has a packet to transmitResult
            for (Node node : nodes) {
                Packet packet = node.contend(time);
                if (packet != null) {
                	inflight.add(packet);
                }
            }

            // notify nodes of the outcome
            int contenders = 0;
            for (Node node : nodes) {
                // let the nodes know if the channel is free or busy
                node.channelFeedback(inflight.size() == 0);
                if (node.getState() == Node.CONTENDING) contenders++;
            }
            runStats.setContenders((int) time, contenders);


            boolean success = inflight.size() == 1;
            while(inflight.size() > 0) {
                Packet packet = inflight.remove(0);
                packet.getSource().transmitResult(time, packet, success);
                if (success && packet.getSlotsNeededToCompletePacketTransmission() == 0) {
                	packet.resetPacketTransmission();
                	packet.getDestination().receive(time, packet);
                }
            }
        }
        return runStats;
    }

    /**
     * returns the node associated with the given vertex
     * @param dst
     * @return
     */
    public Node getNode(Vertex dst) {
        return vertex2nodes.get(dst);
    }

    /**
     * returns the topology passed to this simulator
     * @return
     */
    public Topology getTopology() {
        return topology;
    }

    /**
     * returns the node with the given id
     * @param id
     * @return
     */
    public Node getNodeById(int id) {
        Vertex vertex = topology.getVertexById(id);
        return vertex2nodes.get(vertex);
    }

    /**
     * returns the workload passed to this simulator
     * @return
     */
    public Workload getWorkload() {
        return workload;
    }
}
