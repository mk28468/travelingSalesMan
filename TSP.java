package hw4;
// THIS CODE IS MY OWN WORK, IT WAS WRITTEN WITHOUT CONSULTING
// A TUTOR OR CODE WRITTEN BY OTHER STUDENTS - Junyuan Ke 2148073

// Edit this file (and GeomGraph.java) for homework.  Look for the
// specific "TODO" items below.  See Notes.txt for general
// instructions, and 2OPT.txt for a more specific discussion of 2-opt
// moves and how to find them.  There is one more "TODO" item in
// GeomGraph.java, if you have time for it.

import java.util.Iterator;



import java.util.ArrayDeque;
public class TSP
{	
	public static int[] tour;
	public static boolean[] marked;
	public static ArrayDeque<Integer> preorder = new ArrayDeque<>();
    // These methods mimic the Makefile tests (to invoke from BlueJ).
    // The bigger ones are too slow until you edit GeomGraph.java
    public static void run10()   { run("tsp10.txt",    4); }
    public static void run100()  { run("tsp100.txt",  10); }
    public static void runbier() { run("bier127.txt", 20); }
    public static void run1000() { run("tsp1000.txt", 15); }
    public static void rncirc()  { run("circuit1290.txt", 20); }
    public static void runusa()  { run("usa13509.txt", 20); }
    public static void rungerm() { run("germany15112.txt", 20); }
    public static void runmona() { run("mona-20k.txt", 20); }

    // Main entry point: run(FILENAME, D) from command line arguments.
    public static void main(String[] args)
    {
        if (args.length == 0)
            run10(); // a default test
        else if (args.length == 2)
            run(args[0], Integer.parseInt(args[1]));
        else {
            System.err.println("Expected two arguments: FILENAME D");
            System.exit(1);
        }
    }

    // These are helper methods for doing the graphics.

    // Draw a single edge, in the current color.
    // Note e does not actually have to be an edge in G,
    // we just need G to convert vertex ids to points.
    static void draw(GeomGraph G, Edge e) {
        int v = e.either(), w = e.other(v);
        Point p = G.point(v), q = G.point(w);
        p.drawTo(q);
    }

    // Draw a list of edges (in the current color).
    static void draw(GeomGraph G, Iterable<Edge> edges) {
        for (Edge e: edges) draw(G, e);
    }

    // Draw all vertices and edges of G.
    static void draw(GeomGraph G) {
        int V = G.V();
        for (int u=0; u<V; ++u) {
            Point p = G.point(u);
            p.draw();
            draw(G, G.adj(u));
        }
    }

    // Ok, here is most of the work!
    // The first few steps (finding G and the MST) are done for you.
    public static void run(String fileName, int D)
    {
        // Step 1: read the file.
        In in = new In(fileName);
        // First line is bounding-box dimensions (width and height)
        int width = in.readInt(), height = in.readInt();
        // read the sequence of points (remainin input lines)
        Bag<Point> bp = new Bag<Point>();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            bp.add(new Point(x, y));
        }
        // copy points into the points[] array, preserving
        // input order so points[0] is the first, and so on.
        int V = bp.size();
        Point[] points = new Point[V];
        {
            int v=V;
            for (Point p: bp) points[--v] = p;
        }

        // Step 2: construct and draw G.  For bigger input files,
        // constructing may be the slowest step!  Note we limit D
        // to at most V-1, since that is the maximum possible degree.
        GeomGraph G = new GeomGraph(points, Math.min(D, V-1));
        // Draw G to a StdDraw window, and save the image (as G.png).
        // We leave the StdDraw window at its default pixel size
        // (512 by 512) but rescale its axes to fit our image.
        int maxwh = Math.max(width, height);
        StdDraw.setXscale(0, maxwh);
        StdDraw.setYscale(0, maxwh);
        StdDraw.show(0);        // start deferred drawing mode
        draw(G);
        StdDraw.show(0);        // show the final image
        StdDraw.save("G.png");  // save it to a file

        // Step 3: compute MST, plot it in RED, save "MST.png",
        // and print the total weight of the MST on System.out.
        MST T = new MST(G);
        double wt = T.weight();
        Iterable<Edge> ET = T.edges(); // edge set of T
        StdDraw.clear();
        StdDraw.setPenColor(StdDraw.RED);
        draw(G, ET);
        StdDraw.show(0);
        StdDraw.save("MST.png");
        System.out.println(wt);     // first required line of output

        // Step 4: check that the MST is connected. If not, then G was
        // not connected, which could happen if D is too small.
        if (T.components() != 1) {
            System.err.println("G is not connected, giving up");
            System.exit(1);
        }

        // Step 5 (TODO):
        //
        // Compute a TSP (a cyclic tour of the points), by doing a DFS on the
        // MST, starting at 0.  The tour should visit the vertices in preorder,
        // that is the order they are first discovered by DFS (so 0 is first).
        // Since the tour may "shortcut" across non-tree edges from one vertex
        // to the next, the tour may use edges that are not part of G or T.
        // That is OK, since we are allowed to use any possible geometric edge
        // the edge weight is always just the point-to-point distance.
        // (Note the Point class has a method to compute distances.)
        // The tour should be cyclic, meaning that we count (and draw) an edge
        // from its last vertex back to its first.
        //
        // I recommend that you represent the tour as simply an int array, say
        // "int[] tour".  So tour[0] is the id of the first vertex (0) in the
        // tour, tour[1] is the next vertex id, on up to tour[V-1].
        //
        // You should print out two lines to System.out:
        //
        //   first line: the sequence of vertex ids (array contents starts with 0),
        //  second line: the total weight of the tour (counting the back edge)
        //
        // For examples, see http://www.mathcs.emory.edu/~cs323000/share/hw4-outputs/
        tour = new int[G.V()];
        marked = new boolean[G.V()];
        int count = G.V() ;
        EdgeWeightedGraph ewg = new EdgeWeightedGraph(G.V());
        for (Edge e: ET) 
        	ewg.addEdge(e);
        marked[0] = true;
        preorder.push(0);
        dfs(ewg,0);
        while(count != 0){
        	int temp = preorder.removeFirst();
        	tour[count-1] = temp;
        	count --;
        }
        for(int i = 0; i < tour.length; i ++){
        	System.out.print(tour[i] + " ");
        }
        /*
        ArrayDeque<Integer> path = new ArrayDeque<Integer>();
        ArrayDeque<Integer> result = new ArrayDeque<Integer>();
        
        
        path.push(0);
        result.push(0);
	    	while(!path.isEmpty()){
	    		int check = path.pop();
	    		marked[check] = true;
	    		for(Edge e : ewg.adj(check)){
	    			int temp = e.other(check);
	    			if(!marked[temp]){
	    				result.addLast(temp);
	    				path.addLast(temp);		
	    			}
	    		}
	    		
	    	}
	    	while(!result.isEmpty()){
	    		int ans = result.removeLast();
	    		tour[count] = ans;
	    		count--;
	    	}
	    */
	   System.out.println("");//start another line
	    
	    //calculating the weight of the tour
	    EdgeWeightedGraph shortestpath = new EdgeWeightedGraph(tour.length);
	    double pathweight = 0;
	    for(int j = 0; j < tour.length-1; j++){
	    	Point first = G.point(tour[j]);
	    	Point next = G.point(tour[j+1]);
	    	double weight = first.distanceTo(next);
	    	pathweight += weight;
	    	Edge e = new Edge(tour[j], tour[j+1], weight);
	    	shortestpath.addEdge(e);
	    }
	    Point last = G.point(tour[tour.length-1]);
	    Point origin = G.point(tour[0]);
	    double lastweight = last.distanceTo(origin);
	    pathweight += lastweight;
	    Edge e = new Edge(tour[tour.length-1], tour[0], lastweight);
	    shortestpath.addEdge(e);
        
        
       

        // Step 6 (TODO):
        //
        // Clear the graphics image (as we did above), and set the plotting color
        // to StdDraw.GREEN.  Draw the edges of the tour, and save the resulting
        // image to "TSP1.png".
	    StdDraw.clear();
        StdDraw.setPenColor(StdDraw.GREEN);
        draw(G , shortestpath.edges());
        StdDraw.show(0);
        StdDraw.save("TSP1.png");
        System.out.println(pathweight);     

        // Step 7 (TODO):
        //
        // Improve your TSP by doing "2-opt" moves, until you cannot find a
        // 2-opt move that improves it at all (or, by more than a tiny amount,
        // like say 1e-6).  See 2OPT.txt for more details on this process.
        //
        // Note that to keep this from being too time-consuming, we only consider
        // 2-opt moves that result from adding an edge of G (so, we do not consider
        // all O(V^2) possible edges).   Since we do not consider *all* possible
        // edges, it is possible that one or two crossings may survive; that is
        // probably OK as long as they are rare.
        //
        // Finally print out the two lines again (tour sequence and weight)
        // like we did for the first tour.
        
       boolean again = true;
       while (true) {
          again = false;
          for(Edge testEdge : G.edges()){
        	  int v = testEdge.either();
        	  int w = testEdge.other(v);
        //find the indices i and j, positions in the tour, so tour[i]=v and tour[j]=w
        	  int i = 0, j = 0;
        	  for(int k = 0; k < tour.length; k++){
        		  if(tour[k] == v)
        			  i = k;
        		  else if(tour[k] == w)
        			  j = k;
        	  }
        	//make sure that i is always smaller than j, they swap v and w
        	  if(j < i){
        		  int temp = j;
        		  j = i;
        		  i = temp;
        	//swap v and w after this
        		  int swap = w;
        		  w = v;
        		  v = swap;
        	  }

        	  
        //Let vp =tour[i+1] and wp =tour[j+1], If v'==w, or w'==v, then we can give up now
        	  int vp = tour[(i+1) % V];
        	  int wp = tour[(j+1) % V];
        	  if (vp == w || wp == v)     		  
        		  continue;
        	  else{
        		  //construct edge v to v', and w to w'
        		  	double weightvTovp = G.point(v).distanceTo(G.point(vp));
        		  	double weightwTowp = G.point(w).distanceTo(G.point(wp));
        		  	double weightSum1 = weightvTovp + weightwTowp;
        		  
        		  //define a second edge e'=(v',w')
        		  	Point first = G.point(vp);
      	    		Point next = G.point(wp);
      	    		double weight = first.distanceTo(next);
      	    		Edge testEdgeP = new Edge(vp, wp, weight);
      	    		double weightSum2 = testEdge.weight() + testEdgeP.weight();
      	    		//weightSum2 = weight of e and e'
      	    		//compare the weight of two combinations, if improve is positive, then there is room for improvement
      	    		double netChange = weightSum2 - weightSum1;
        		  
      	    		if (netChange < -0.000001) {
      	    			// do the 2-opt move for e;  You can reverse the vertex ids from tour[i+1] to tour[j] 
      	    			ArrayDeque<Integer> help = new ArrayDeque<>();
      	    			for(int k = i+1; k < j + 1; k++)
      	    				help.push(tour[k]);
      	    			  			
      	    			//reversing ids from tour[i+1] to tour[j];
      	    			for(int m = i+1; m < j+1; m++)
      	    				tour[m] = help.pop();
      	    			again = true;
      	    			for(int m = 0; m < tour.length; m++)
      	    	          	System.out.print(tour[m] + " ");
      	    	          System.out.println("");
      	    			/*for(int n = 0; n < tour.length; n++)
      	    	        	System.out.print(tour[n] + " ");
      	    				System.out.println("");
      	    			*/
      	    	   }
        	  }
           }
          
       }
          
 /*

        // Step 8 (TODO):
        //
        // Clear the graphics image again, set the color to StdDraw.BLUE, and
        // draw the tour one more time.  Same the image as "TSP2.png".
        EdgeWeightedGraph optshortestpath = new EdgeWeightedGraph(tour.length);
	    double optpathweight = 0;
	    for(int j = 0; j < tour.length-1; j++){
	    	Point first = G.point(tour[j]);
	    	Point next = G.point(tour[j+1]);
	    	double weight = first.distanceTo(next);
	    	optpathweight += weight;
	    	Edge opte = new Edge(tour[j], tour[j+1], weight);
	    	optshortestpath.addEdge(opte);
	    }
	    Point optlast = G.point(tour[tour.length-1]);
	    Point optorigin = G.point(tour[0]);
	    double optlastweight = optlast.distanceTo(optorigin);
	    optpathweight += optlastweight;
	    Edge opte = new Edge(tour[tour.length-1], tour[0], optlastweight);
	    optshortestpath.addEdge(opte);
	    
	    
	    StdDraw.clear();
        StdDraw.setPenColor(StdDraw.BLUE);
        draw(G , optshortestpath.edges());
        StdDraw.show(0);
        StdDraw.save("TSP2.png");
        System.out.println("");
        System.out.println(optpathweight); 

        // Step 9 (done):
        //
        // Exit, you have two choices here.
        //
        // If you use "System.exit(0)", then the graphic window closes immediately,
        // and the program ends without any input from the user (you).
        // Otherwise, the program waits and will not end until you close the window.
        // For turnin purposes, System.exit(0) is more convenient,
        // but for testing purposes it is nicer to see that final image.
        //
        // Your choice:
        // System.exit(0);
        System.err.println("Close the window to exit...");
        
    */
    }
    
    
	private static void While(boolean b) {
		// TODO Auto-generated method stub
		
	}
	
	private static void dfs(EdgeWeightedGraph G, int v) {
        marked[v] = true;
        for (Edge e : G.adj(v)) {
        	int w = e.other(v);
            if (!marked[w]) {
                preorder.push(w);
                //System.out.println(w);
                dfs(G, w);
            }
        }
    }
    
}
