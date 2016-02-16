package topology;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ochipara on 2/8/16.
 */
public class Topology {
    protected ShortestPaths shortestPaths = null;
    protected List<Vertex> vertices = new ArrayList<>();
    protected List<Edge> edges = new ArrayList<>();
    protected HashMap<String, Vertex> name2node = new HashMap<>();
    protected HashMap<Integer, Vertex> id2node = new HashMap<>();
    protected HashMap<Vertex, Edge> src2link = new HashMap<>();
    protected int vertexCounter = 0;

    public Vertex newVertex(String name) {
        if (name2node.keySet().contains(name)) {
            throw new IllegalArgumentException("Duplicate node name");
        }
        Vertex vertex = new Vertex(this, name, vertexCounter++);

        vertices.add(vertex);

        id2node.put(vertex.getId(), vertex);
        name2node.put(vertex.getName(), vertex);

        return vertex;
    }

    public void generateSingleHop(int numVertices) {
        for (int i = 0; i < numVertices; i++) {
            newVertex(Integer.toString(i));
        }

        for (int i = 0; i < numVertices; i++) {
            Vertex ni = id2node.get(i);
            for (int j = 0; j < numVertices; j++) {
                if (i != j) {
                    Vertex nj = id2node.get(j);

                    connect(ni, nj);
                }
            }
        }
    }

    public List<Edge> getOutgoing(Vertex vertex) {
        return vertex.getOutgoing();
    }

    public void biconnect(Vertex a, Vertex b) {
        connect(a, b);
        connect(b, a);
    }

    public void connect(Vertex source, Vertex destination) {
        if (source == null) throw new IllegalStateException("Source cannot be null");
        if (destination == null) throw new IllegalStateException("Destination cannot be null");

        Edge edge = new Edge(source, destination);
        edges.add(edge);

        src2link.put(edge.getSource(), edge);
        source.addOutgoing(edge);
    }


    public String graphViz() {
        StringBuffer sb = new StringBuffer();

        sb.append("digraph topology {\n");
        sb.append("\tconcentrate=true\n");
        for (Edge edge : edges) {
            sb.append(String.format("\t%s->%s;\n", edge.getSource(), edge.getDestination()));
        }
        sb.append("}\n");

        return sb.toString();
    }

    public void graphViz(String s) throws IOException {
        Path f = Paths.get(s);
        Files.write(f, graphViz().getBytes());
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public Vertex getVertexById(int id) {
        return id2node.get(id);
    }

    public List<Vertex> getPath(Vertex source, Vertex destination) {
        if (shortestPaths == null) {
            shortestPaths = new ShortestPaths(this);
        }
        return shortestPaths.getPath(source, destination);
    }


}
