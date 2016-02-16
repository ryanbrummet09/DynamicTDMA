package topology;

/**
 * Created by ochipara on 2/8/16.
 */
public class Edge {
    private final Vertex source;
    private final Vertex destination;

    public Edge(Vertex source, Vertex destination) {
        this.source = source;
        this.destination = destination;
    }

    public Vertex getSource() {
        return source;
    }

    public Vertex getDestination() {
        return destination;
    }
}
