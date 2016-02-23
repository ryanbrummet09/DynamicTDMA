package topology;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ochipara on 2/8/16.
 * Comments added by ryanbrummet on 2/15/16
 */
public class ShortestPaths {
    private Topology topology;
    private HashMap<String, List<Vertex>> paths = new HashMap<>();


    public ShortestPaths(Topology topology) {
        this.topology = topology;
    }


    /**
     * finds the shortest paths between the given vertex and all other vertices in the graph and saves the result to a hashmap
     * @param src
     */
    public void buildPaths(Vertex src) {
        List<Vertex> vertices = topology.getVertices();
        List<Edge> edges = topology.getEdges();
        int N = vertices.size();

        int d[] = new int[N];
        int predecessor[] = new int[N];
        for (int i = 0; i < N; i++) {
            d[i] = -1;
            predecessor[i] = -1;
        }
        d[src.getId()] = 0;


        for (int i = 0; i < N; i++) {
            boolean change = false;
            for (Edge edge : edges) {
                int srcId = edge.getSource().getId();
                int dstId = edge.getDestination().getId();

                //System.out.println(String.format("PRE: %s->%s d[%s]=%d, d[%s]=%d", edge.getSource(), edge.getDestination(), edge.getSource(), d[srcId], edge.getDestination(), d[dstId]));
                if ((d[srcId] + 1 < d[dstId]) || (d[dstId] == -1)) {
                    if (d[srcId] >= 0) {
                        d[dstId] = d[srcId] + 1;
                        predecessor[dstId] = srcId;
                        change = true;
                        //System.out.println(String.format("POST: %s->%s d[%s]=%d, d[%s]=%d", edge.getSource(), edge.getDestination(), edge.getSource(), d[srcId], edge.getDestination(), d[dstId]));
                    }
                }
            }

            // early termination
            if (change == false) break;
        }


        for (Vertex dst : vertices) {
            if (dst != src) {
                String key = String.format("%s->%s", src.getName(), dst.getName());
                List<Vertex> path = new LinkedList<>();

                int prevId = dst.getId();
                do {
                    Vertex prevVertex = topology.getVertexById(prevId);
                    path.add(prevVertex);
                    prevId = predecessor[prevId];
                } while(prevId != src.getId());
                path.add(src);
                Collections.reverse(path);

                paths.put(key, path);
            }
        }
    }


    /**
     * returns the shortest path between the src and dst nodes as a list of vertices
     * @param src
     * @param dst
     * @return
     */
    public List<Vertex> getPath(Vertex src, Vertex dst) {
        String key = String.format("%s->%s", src.getName(), dst.getName());
        if (paths.containsKey(key)) {
            return paths.get(key);
        }

        buildPaths(src);
        return paths.get(key);
    }
}
