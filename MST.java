package hw4;
// Do not edit.
// This is KruskalMST from the book, with small modifications.
//  * removed check(), since BlueJ runs with assertions enabled.
//  * added components(), to help user check whether the graph is connected.
//  * use java.util.LinkedList instead of Queue
import java.util.LinkedList;

public class MST
{
    private double weight;  // weight of MST
    private LinkedList<Edge> mst = new LinkedList<Edge>();  // edges in MST
    private int components; // number of components (1 for tree)

    // Kruskal's algorithm
    public MST(EdgeWeightedGraph G) {
        // more efficient to build heap by passing array of edges
        MinPQ<Edge> pq = new MinPQ<Edge>();
        for (Edge e : G.edges()) {
            pq.insert(e);
        }

        // run greedy algorithm
        components = G.V();
        UF uf = new UF(components);
        while (!pq.isEmpty() && components > 1) {
            Edge e = pq.delMin();
            int v = e.either();
            int w = e.other(v);
            if (!uf.connected(v, w)) { // v-w does not create a cycle
                uf.union(v, w);  // merge v and w components
                mst.add(e);  // add edge e to mst
                weight += e.weight();
                --components;
            }
        }
    }

    // edges in minimum spanning forest as an Iterable
    public Iterable<Edge> edges() { return mst; }

    // weight of minimum spanning forest
    public double weight() { return weight; }

    // number of components (1 if a tree)
    public int components() { return components; }
}

