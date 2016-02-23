import csma.SimpleCSMAFactory;
import simulator.RunStatistics;
import simulator.Simulator;
import topology.Vertex;
import topology.Topology;
import workload.Workload;

import java.io.IOException;

/**
 * Created by ochipara on 2/8/16.
 */
public class PolicyGenerator {
    /**
     * A -> B -> C -> D
     *           ^
     *           |
     *           E
     *           ^
     *           |
     *           F
     * @param args
     */
    public static final void main(String[] args) throws IOException {
        Topology topology = new Topology();
        Vertex a = topology.newVertex("A");
        Vertex b = topology.newVertex("B");
        Vertex c = topology.newVertex("C");
        Vertex d = topology.newVertex("D");
        Vertex e = topology.newVertex("E");
        Vertex f = topology.newVertex("F");
        Vertex g = topology.newVertex("G");

        topology.biconnect(a, b);
        topology.biconnect(b, c);
        topology.biconnect(c, d);
        topology.biconnect(e, c);
        topology.biconnect(e, f);
        topology.biconnect(e, g);

        System.out.println(topology.graphViz());
        topology.graphViz("topology.dot");

        Workload workload = new Workload(10);
        int baseRate = 300;
        workload.newPeriodicFlow(d, a, 0, baseRate, baseRate);
        workload.newPeriodicFlow(f, a, 0, baseRate * 2, baseRate * 2);
        workload.newPeriodicFlow(g, a, 0, baseRate * 2, baseRate * 2);
        workload.assignPriorities();
        workload.print();

        System.out.println("\nCompleted");


        Simulator sim = new Simulator(topology, workload, new SimpleCSMAFactory(10,128),0, 1);
        RunStatistics stats = sim.run(200000);
        stats.saveContenders("contenders.txt");

    }
}
