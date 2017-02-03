package hw4;
// You do not need to edit this file for homework.

// A QuadTree is a data structure for mapping 2d points to values.
// The key type is a Point (see Point.java), the Value type is a
// parameter.  In our intended usage (in GeomGraph.java), Value is
// Integer, a vertex id.

// It is something like a naive BST, but with up to four children
// (instead of two) per node.  There is no effort to balance this
// tree, so the user should do something like random insertion order
// (if possible) to keep the expected depth O(log N).

// Given a query point (not necessarily one of the key points),
// the pairsNear( we can
// list points from the QuadTree in order of increasing distance from
// that query point, fairly efficiently. This is loosely based on
// http://algs4.cs.princeton.edu/92search/
// We have added Node.bbox and DistIter.

import java.util.Iterator;

public class QuadTree<Value>
{
    // Data:
    private Node root = null;

    // A Pair is an immutable (key, value) pair.
    // QuadTree.Pair is visible in our iterator, so this is public.
    public class Pair {
        public final Point key;
        public final Value value;
        Pair(Point k, Value v) { key=k; value=v; }
        // The next two methods are overridden in Node subclass, below.
        double distanceTo(Point p) { return key.distanceTo(p); }
        boolean isLeaf() { return true; }
    }

    // A Node in our tree is a Pair with up to 4 children, and a bbox.
    // This subclass is private, not visible outside QuadTree.
    private class Node extends Pair
    {
        // The two fields are null while this Node is still a leaf.
        Node[] quad = null;     // subtree array, one per quadrant
        Rectangle bbox = null;  // bounding box of points in subtree
        // Construct as a leaf (no children yet):
        Node(Point p, Value v) { super(p, v); }
        // An int from 0 to 3: the quadrant of p, relative to our key.
        int quadrant(Point p) { return (key.x<p.x ? 2:0) + (key.y<p.y ? 1:0); }
        final static int SE=0, NE=1, SW=2, NW=3; // the quadrants ids
        // We override both of these methods from Pair:
        @Override double distanceTo(Point p) {
            return bbox==null ? key.distanceTo(p) : bbox.distanceTo(p);
        }
        @Override boolean isLeaf() { return quad==null; }
    }

    // Suppressing the generic type warning for this array creation.
    @SuppressWarnings("unchecked")
    private Node[] makeQuad() { return (Node[])(new QuadTree.Node[4]); }

    // Insert point p with associated value v.
    public void insert(Point p, Value v) { root = insert(root, p, v); }
    private Node insert(Node h, Point p, Value v)
    {
        if (h == null) return new Node(p, v); // new leaf
        int q = h.quadrant(p);                // which way does it go?
        if (h.isLeaf()) {
            // Node h stops being a leaf.
            h.bbox = new Rectangle(h.key, p);
            h.quad = makeQuad();
            h.quad[q] = new Node(p,v); // a new leaf under h
        } else {
            h.bbox = h.bbox.add(p); // possibly enlarge the bbox
            h.quad[q] = insert(h.quad[q], p, v);
        }
        return h;
    }

    // The method pairsNear(q) returns a DistIter.  That is, an
    // iterator which returns Pairs (points) from the QuadTree, in
    // order of increasing distance from the query point q.
    public Iterator<Pair> pairsNear(Point q) { return new DistIter(q); }

    private class DistIter implements Iterator<Pair>
    {
        MinPQ<Pair> pq;
        DistIter(Point query) {
            pq = new MinPQ<Pair>(30, new DistComp(query));
            add(root);
        }
        void add(Pair pair) { if (pair!=null) pq.insert(pair); }
        public boolean hasNext() { return !pq.isEmpty(); }
        public Pair next() {
            while (true) {
                Pair pair = pq.delMin();
                if (pair.isLeaf()) return pair; // single point, easy!
                // Otherwise, it is a Node, so reinsert its children
                // (and its "Pair" but with no children).
                Node n = (Node)pair;
                pq.insert(new Pair(n.key, n.value)); // without children
                for (Node q: n.quad) add(q);         // each child separately
            }
        }
        public void remove() { throw new UnsupportedOperationException(); }
    }

    // valuesNear(p) is an iterator just like pairsNear(p), but it
    // returns just the values, rather than the pairs containing them.
    public Iterator<Value> valuesNear(Point p) {
        return new PairsToValues(pairsNear(p));
    }
    private class PairsToValues implements Iterator<Value> {
        Iterator<Pair> ip;
        PairsToValues(Iterator<Pair> i) { ip = i; }
        public boolean hasNext() { return ip.hasNext(); }
        public Value next() { return ip.next().value; }
        public void remove() { ip.remove(); }
    }

    // A Comparator, sorting Pair objects according to distance from p.
    private class DistComp implements java.util.Comparator<Pair>
    {
        final Point p;          // the query point
        DistComp(Point p) { this.p = p; }
        public int compare(Pair a, Pair b) { // a or b may be Node
            return Double.compare(a.distanceTo(p), b.distanceTo(p));
        }
    }
}
