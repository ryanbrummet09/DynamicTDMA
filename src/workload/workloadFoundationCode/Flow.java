package workload.workloadFoundationCode;

import java.util.List;

import topology.topologyFoundationCode.Topology;
import topology.topologyFoundationCode.Vertex;

/**
 * Created by ochipara on 2/8/16.
 * Modified by ryanbrummet on 2/15/2016
 */
public abstract class Flow {
    protected final Vertex source;
    protected final Vertex destination;
    protected final Topology topology;
    protected final List<Vertex> path;
    public static int counter = 0;
    protected final int id;
    protected int priority = -1;
    private final int timeSlotsNeededToSendPacket;
    

    /**
     * returns priority of this flow
     * @return
     */
    public int getPriority() {
        return priority;
    }

   /**
    * Creates a new Flow
    * @param source
    * @param destination
    * @param timeSlotsNeededToSendPacket
    */
    public Flow(Vertex source, Vertex destination, int timeSlotsNeededToSendPacket) {
        this.source = source;
        this.destination = destination;
        this.topology = source.getTopology();
        this.path = topology.getPath(source, destination);
        this.timeSlotsNeededToSendPacket = timeSlotsNeededToSendPacket;
        this.id  = counter++;
    }

    /**
     * Prints out the path between the source to destination vertices of this flow to stdout
     * @return
     */
    public String printPath() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < path.size(); i++) {
            sb.append(path.get(i).getName());
            if (i < path.size() - 1) sb.append(" -> ");
        }
        return sb.toString();
    }

    /**
     * returns the source vertex of this flow
     * @return
     */
    public Vertex getSource() {
        return source;
    }

    /**
     * returns the next vertex to receive packets within this flow
     * @param vertex
     * @return
     */
    public Vertex nextHop(Vertex vertex) {
        for (int i = 0; i < path.size(); i++) {
            Vertex v = path.get(i);
            if (v == vertex) {
                return path.get(i + 1);
            }
        }

        return null;
    }

    /**
     * returns the id of this flow
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * returns the final destination of this flow
     * @return
     */
    public Vertex getDestination() {
        return destination;
    }

    /**
     * changes the priority of this flow
     * @param priority
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * returns the timeSlotsNeededToSendPacket
     * @return
     */
    public int getTimeSlotsNeededToSendPacket() {
    	return timeSlotsNeededToSendPacket;
    }
    
    /**
     * returns the path packets of this flow follow
     * @return
     */
    public List<Vertex> getPath() {
    	return path;
    }
    
    /**
     * Classes that extend flow must define their own toString methods
     * @return
     */
    @Override
    public abstract String toString();
}
