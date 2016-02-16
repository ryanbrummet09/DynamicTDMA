package topology;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ochipara on 2/8/16.
 */
public class Vertex {
    protected final String name;
    protected final List<Edge> outgoing = new ArrayList<>();
    protected final Topology topology;
    private final int id;

    public Vertex(Topology topology, String name, int id) {
        this.name = name;
        this.topology = topology;
        this.id = id;
    }
    
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void addOutgoing(Edge destination) {
        outgoing.add(destination);
    }

    public List<Edge> getOutgoing() {
        return outgoing;
    }

    public int getId() {
        return id;
    }

    public Topology getTopology() {
        return topology;
    }
}
