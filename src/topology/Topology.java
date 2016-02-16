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
 * Modified by ryanbrummet 2/15/16
 */
public class Topology {
    protected ShortestPaths shortestPaths = null;
    protected List<Vertex> vertices = new ArrayList<>();
    protected List<Edge> edges = new ArrayList<>();
    protected HashMap<String, Vertex> name2node = new HashMap<>();
    protected HashMap<Integer, Vertex> id2node = new HashMap<>();
    protected HashMap<Vertex, Edge> src2link = new HashMap<>();
    protected int vertexCounter = 0;

    /**
     * creates a new vertex in this topology
     * @param name
     * @throws IllegalArgumentException
     * @return
     */
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

    /**
     * Creates a clique topology with the given number of vertices
     * @param numVertices
     */
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

    /**
     * returns a list of outgoing edges for the given vertex in this topology
     * @param vertex
     * @return
     */
    public List<Edge> getOutgoing(Vertex vertex) {
        return vertex.getOutgoing();
    }

    /**
     * connects the two passed vertices to each other within this topology
     * @param a
     * @param b
     * @throws IllegalStateException
     */
    public void biconnect(Vertex a, Vertex b) {
    	if(vertices.contains(a) && vertices.contains(b)) {
    		connect(a, b);
            connect(b, a);
    	} else {
    		throw new IllegalStateException("You cannot biconnect two vertices using this topology if they are not in the topology");
    	}
        
    }
    
    /**
     * connects the source vertex with the destination vertex ONE WAY, does not connect destination to source
     * @param source
     * @param destination
     * @throws IllegalStateException
     */
    public void connect(Vertex source, Vertex destination) {
        if (source == null) throw new IllegalStateException("Source cannot be null");
        if (!vertices.contains(source)) throw new IllegalStateException("Source must be within the given topology");
        if (destination == null) throw new IllegalStateException("Destination cannot be null");
        if (!vertices.contains(destination)) throw new IllegalStateException("Destination must be within the given topology");

        Edge edge = new Edge(source, destination);
        edges.add(edge);

        src2link.put(edge.getSource(), edge);
        source.addOutgoing(edge);
    }

    /**
     * Creates and prints to stdout a graphical representation of this topology
     * @return
     */
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

    /**
     * Creates and saves a graphical representation of this topology
     * @param s
     * @throws IOException
     */
    public void graphViz(String s) throws IOException {
        Path f = Paths.get(s);
        Files.write(f, graphViz().getBytes());
    }

    /**
     * returns a list of the vertices in this topology
     * @return
     */
    public List<Vertex> getVertices() {
        return vertices;
    }

    /**
     * returns a list of the edges in this topology
     * @return
     */
    public List<Edge> getEdges() {
        return edges;
    }

    /**
     * returns the vertex with given id in this topology 
     * @param id
     * @return
     */
    public Vertex getVertexById(int id) {
        return id2node.get(id);
    }

    /**
     * returns the shortest path as a list of vertices from the vertex source to the vertex destination
     * @param source
     * @param destination
     * @return
     */
    public List<Vertex> getPath(Vertex source, Vertex destination) {
        if (shortestPaths == null) {
            shortestPaths = new ShortestPaths(this);
        }
        return shortestPaths.getPath(source, destination);
    }
}
