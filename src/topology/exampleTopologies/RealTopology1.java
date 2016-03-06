package topology.exampleTopologies;

import java.io.IOException;

import topology.topologyFoundationCode.Topology;
import topology.topologyFoundationCode.Vertex;

/**
 * 
 * @author ryanbrummet
 * 
 * 
 * This Realistic topology came from "Real-Time Communication in Low-Power Mobile Wireless Networks", CCNC 2016
 * 
 * All edges are bi-directional and A is the gateway.
 *  
 * W,V,U,T,S <-> R
 * Q,P,R <-> O
 * N,O <-> M
 * E <-> D
 * L <-> K
 * I,J <-> H
 * G,H <-> F
 * M,C,B,D,K,F <-> A
 * 
 */
public class RealTopology1 extends AbstractExampleTopology{
	
	/**
	 * Use this this constructor and the method getNewInstanceOfThisTopology to construct the predefined topology called
	 * RealTopology1 which will be printed to stdout dependent on the value of printTopologyToStdOut
	 * @param printTopologyToStdOut
	 */
	public RealTopology1(boolean printTopologyToStdOut) {
		super(printTopologyToStdOut);
	}
	
	/**
	 * Use this this constructor and the method getNewInstanceOfThisTopology to construct the predefined topology called
	 * RealTopology1 which will be printed to stdout dependent on the value of printTopologyToStdOut and saved in a file
	 * with the given name
	 * @param printTopologyToStdOut
	 * @param saveFileName
	 */
	public RealTopology1(boolean printTopologyToStdOut, String saveFileName) {
		super(printTopologyToStdOut, saveFileName);
	}
	
	public Topology getNewInstanceOfThisTopology() {
		
		Topology topology = new Topology();
		Vertex a = topology.newVertex("A",true);
		Vertex b = topology.newVertex("B",false);
		Vertex c = topology.newVertex("C",false);
		Vertex d = topology.newVertex("D",false);
		Vertex e = topology.newVertex("E",false);
		Vertex f = topology.newVertex("F",false);
		Vertex g = topology.newVertex("G",false);
		Vertex h = topology.newVertex("H",false);
		Vertex i = topology.newVertex("I",false);
		Vertex j = topology.newVertex("J",false);
		Vertex k = topology.newVertex("K",false);
		Vertex l = topology.newVertex("L",false);
		Vertex m = topology.newVertex("M",false);
		Vertex n = topology.newVertex("N",false);
		Vertex o = topology.newVertex("O",false);
		Vertex p = topology.newVertex("P",false);
		Vertex q = topology.newVertex("Q",false);
		Vertex r = topology.newVertex("R",false);
		Vertex s = topology.newVertex("S",false);
		Vertex t = topology.newVertex("T",false);
		Vertex u = topology.newVertex("U",false);
		Vertex v = topology.newVertex("V",false);
		Vertex w = topology.newVertex("W",false);
		
		topology.biconnect(w, r);
		topology.biconnect(v, r);
		topology.biconnect(u, r);
		topology.biconnect(t, r);
		topology.biconnect(s, r);
		topology.biconnect(r, o);
		topology.biconnect(q, o);
		topology.biconnect(p, o);
		topology.biconnect(o, m);
		topology.biconnect(n, m);
		topology.biconnect(e, d);
		topology.biconnect(l, k);
		topology.biconnect(i, h);
		topology.biconnect(j, h);
		topology.biconnect(g, f);
		topology.biconnect(h, f);
		topology.biconnect(m, a);
		topology.biconnect(c, a);
		topology.biconnect(b, a);
		topology.biconnect(d, a);
		topology.biconnect(f, a);
		topology.biconnect(k, a);
		
		if(printTopologyToStdOut){
			System.out.println(topology.graphViz());
		}
		if(saveFileName != null) {
			try {
				topology.graphViz(saveFileName);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		return topology;
	}
	
}
