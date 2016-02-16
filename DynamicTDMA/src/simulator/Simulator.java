package simulator;

import topology.Topology;
import topology.Vertex;
import workload.Workload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by ochipara on 2/8/16.
 */
public class Simulator {
    private final Topology topology;
    private final HashMap<Vertex, Node> vertex2nodes = new HashMap<>();
    private final List<Node> nodes = new ArrayList<>();
    private final Workload workload;

    private static final Logger logger = LogManager.getLogger("sim");
    private final NodeFactory nodeFactory;


    public Simulator(Topology topology, Workload workload, NodeFactory nodeFactory) {
        this.topology = topology;
        this.workload = workload;
        this.nodeFactory = nodeFactory;

        List<Vertex> vertices = topology.getVertices();
        for (Vertex vertex : vertices) {

            Node node = nodeFactory.newNode(vertex, this);
            vertex2nodes.put(vertex, node);
            nodes.add(node);
        }
    }

    /**
     * Run the simulator for a given number of slots
     *
     * @param numSlots
     */
    public RunStatistics run(int numSlots) {
        logger.info("Simulating " + numSlots);
        List<Packet> inflight = new ArrayList<>();

        RunStatistics runStats = new RunStatistics(numSlots);

        for (long time = 0; time < numSlots; time++) {
            // check if the node has a packet to transmitResult
            for (Node node : nodes) {
                Packet packet = node.contend(time);
                if (packet != null) inflight.add(packet);
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
                if (success) packet.getDestination().receive(time, packet);
            }
        }
        return runStats;
    }

    public Node getNode(Vertex dst) {
        return vertex2nodes.get(dst);
    }

    public Topology getTopology() {
        return topology;
    }

    public Node getNodeById(int id) {
        Vertex vertex = topology.getVertexById(id);
        return vertex2nodes.get(vertex);
    }

    public Workload getWorkload() {
        return workload;
    }
}
