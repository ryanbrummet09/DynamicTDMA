package topology.topologyFoundationCode;

/**
 * Created by ochipara on 2/8/16.
 * Comments added by ryanbrummet on 2/15/16
 */
public class Edge {
    private final Vertex source;
    private final Vertex destination;

    /**
     * Creates a new edge
     * @param source
     * @param destination
     */
    public Edge(Vertex source, Vertex destination) {
        this.source = source;
        this.destination = destination;
    }

    /**
     * returns the source of this edge
     * @return
     */
    public Vertex getSource() {
        return source;
    }

    /**
     * returns the destination of this edge
     * @return
     */
    public Vertex getDestination() {
        return destination;
    }
}
