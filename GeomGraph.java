package hw4;

import java.util.Iterator;



// THIS CODE IS MY OWN WORK, IT WAS WRITTEN WITHOUT CONSULTING
// A TUTOR OR CODE WRITTEN BY OTHER STUDENTS - Junyuan Ke 2148073

// TODO: make the constructor faster, by using a QuadTree to find the
// nearest neighbors of each point.  Basically you need to construct a
// QuadTree to model the array of Point objects, and then repeatedly
// use its pairsNear(p) (or valuesNear(p)) method to find the points
// which are closest to a given point p.
//
// Note the QuadTree is something like a symbol table.  The Point
// objects are its keys, and the QuadTree maps those keys to values.
// For our use, I recommend the "value" associated with a Point should
// be an Integer: the corresponding vertex id of that Point.
//
// Finally, you can get an unbalanced QuadTree if you insert the items
// in a bad order.  One easy fix to this is to insert the points in a
// random order, then the expected depth is O(log N).  You can use the
// StdRandom.shuffle() methods to randomly permute an array.

// A GeomGraph is an EdgeWeightedGraph, with vertices correspond to
// points in the plane, Point objects.  Furthermore, we add edges from
// each vertex to its D nearest neighbors, where the edge weight
// equals the Euclidean distance between the two points.  (For large N
// we pick some D like 20, so the graph remains sparse.)  Our
// constructor may create duplicate edges, since the same edge may be
// added twice, once from each end.  We don't mind that.

public class GeomGraph extends EdgeWeightedGraph
{
    // Our array of Point objects, given in the constructor.
    final private Point[] points;

    // Add edges from v to its D closest neighbors.  As given it runs
    // in O(V lg V) time, but a QuadTree could make it faster.
    private void addEdges(int v, int D)
    {
        Point p = points[v];
        int V = points.length;        
        // Construct ALL possible edges from v.
        Edge[] all = new Edge[V];
        for (int w=0; w<V; ++w)
            all[w] = new Edge(v, w, p.distanceTo(points[w]));
        // Sort the Edge objects by weight.
        java.util.Arrays.sort(all);
        // Add the first D edges (skipping the self-loop, all[0]).
        for (int i=1; i<=D; ++i)
            addEdge(all[i]);
       
       
        
    }

    // Given vertex id v, return its corresponding Point object.
    public Point point(int v) { return points[v]; }

    // Return degree of vertex v (length of its adjacency list)
    // Note that this counts parallel edges.
    public int degree(int v) { return ((Bag<Edge>)adj(v)).size(); }

    // Constructor: we save the points array, and connect each vertex
    // to its D closest neighbors.  O(V^2) time.  An edge may be added
    // twice (once from each end), we do not worry about that here.
    public GeomGraph(Point[] points, int D)
    {
    	
        super(points.length);
        this.points = points;
        /*
        int V = V();
        for (int v=0; v<V; ++v)
            addEdges(v, D);
        */
        //construct the quad tree and insert points randomly into the tree
    	 QuadTree<Integer> QT = new QuadTree<Integer>();
    	 int[] id = new int[points.length];
    	 for(int i = 0; i < id.length; i++)
    		 id[i] = i;
    	 StdRandom.shuffle(id, 0, id.length-1);	 
    	 for(int i = 0; i < points.length; i ++){
    		 Point p = points[id[i]];
    		 QT.insert(p,id[i]); 
    		 
    	 }
    	 for(int k = 0; k < points.length; k++){
    		 Point p = points[k];
    		 Iterator<Integer> vals = QT.valuesNear(p);
    		 vals.next();
    		 for(int j = 0; j < D; j++){
    			 int v = vals.next();
    			 Point q = points[v];
    			 Edge e = new Edge(k, v, p.distanceTo(q));
    			 addEdge(e);
    			 
    		 }
    	 }
    	 
    	 
    	 
    	 
    }
}

