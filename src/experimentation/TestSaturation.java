package experimentation;
import simulator.Node;
import simulator.Simulator;
import simulator.statistics.RunStatistics;
import topology.topologyFoundationCode.Topology;
import topology.topologyFoundationCode.Vertex;
import workload.workloadFoundationCode.Workload;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import protocols.factories.SimpleCSMAFactory;

/**
 * Created by ochipara on 2/12/16.
 */
public class TestSaturation {
    static class SaturationResult {
        protected long rx;
        protected long contenders;

        public SaturationResult(int contenders, long rx) {
            this.contenders = contenders;
            this.rx = rx;
        }

        public String toString() {
            return String.format("%d %d", contenders, rx);
        }
    }

    public static final void main(String[] args) throws IOException {
        //int contenders[] = new int[] {2, 4, 8, 16, 32, 64, 128, 256, 1024};
        int contenders[] = new int[] {256};

        List<SaturationResult> results = new LinkedList<>();
        for (int N : contenders) {
            // generate the topology
            Topology topology = new Topology();
            topology.generateSingleHop(N);
            topology.graphViz("topology.dot");


            // setup the workload
            Workload workload = new Workload();
            Vertex dest = topology.getVertexById(0);
            for (int i = 1; i < N; i++) {
                workload.newSaturationFlow(topology.getVertexById(i), dest);
            }

            Simulator simulator = new Simulator(topology, workload, new SimpleCSMAFactory(10,128),0, 1);
            RunStatistics stats = simulator.run(10000);
            stats.saveContenders("contenders.txt");

            Node dst = simulator.getNodeById(0);
            long rx = dst.getStatistics().getRx();
            results.add(new SaturationResult(N, rx));


            System.out.println(String.format("%d %d", N, rx));
        }

        System.out.println("\n\n");
        for (SaturationResult result : results) {
            System.out.println(result);
        }
    }
}
