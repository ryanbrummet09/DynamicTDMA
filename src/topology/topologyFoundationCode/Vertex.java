package topology.topologyFoundationCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ochipara on 2/8/16.
 * Comments added by ryanbrummet on 2/15/16
 */
public class Vertex {
    protected final String name;
    protected final List<Edge> outgoing = new ArrayList<>();
    protected final Topology topology;
    private final int id;

    /**
     * Creates a new Vertex
     * @param topology
     * @param name
     * @param id
     */
    public Vertex(Topology topology, String name, int id) {
        this.name = name;
        this.topology = topology;
        this.id = id;
    }
    
    /**
     * returns the name of this vertex
     * @return
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * returns the name of this vertex, same behavior as toString()
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * adds an outgoing edge from this vertex to the destination vertex
     * @param destination
     */
    public void addOutgoing(Edge destination) {
        outgoing.add(destination);
    }

    /**
     * returns a list of all outgoing edges from this vertex
     * @return
     */
    public List<Edge> getOutgoing() {
        return outgoing;
    }

    /**
     * returns the id of this vertex
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * returns the topology that this vertex is contained in
     * @return
     */
    public Topology getTopology() {
        return topology;
    }
}
