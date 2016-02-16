package workload;

import topology.Vertex;
import topology.Topology;

import java.util.List;

/**
 * Created by ochipara on 2/8/16.
 */
public abstract class Flow {
    protected final Vertex source;
    protected final Vertex destination;
    protected final Topology topology;
    protected final List<Vertex> path;
    public static int counter = 0;
    protected final int id;

    public int getPriority() {
        return priority;
    }

    protected int priority = -1;



    public Flow(Vertex source, Vertex destination) {
        this.source = source;
        this.destination = destination;
        this.topology = source.getTopology();
        this.path = topology.getPath(source, destination);
        this.id  = counter++;
    }

    public String printPath() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < path.size(); i++) {
            sb.append(path.get(i).getName());
            if (i < path.size() - 1) sb.append(" -> ");
        }
        return sb.toString();
    }

    public Vertex getSource() {
        return source;
    }

    public Vertex nextHop(Vertex vertex) {
        for (int i = 0; i < path.size(); i++) {
            Vertex v = path.get(i);
            if (v == vertex) {
                return path.get(i + 1);
            }
        }

        return null;
    }

    public int getId() {
        return id;
    }

    public Vertex getDestination() {
        return destination;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }


}
