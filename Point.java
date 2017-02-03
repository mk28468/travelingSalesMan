package hw4;
// A Point is an immutable point in the plane.
// For a much fancier class, see book/Point2D.java

public class Point
{
    // A few constant Point objects:
    public static Point ZERO = new Point(0, 0);
    public static Point MAX = new Point(Double.POSITIVE_INFINITY,
                                        Double.POSITIVE_INFINITY);
    public static Point MIN = new Point(Double.NEGATIVE_INFINITY,
                                        Double.NEGATIVE_INFINITY);

    // Immutable: the fields cannot be modified.
    public final double x, y;   // Cartesian coordinates

    // Create a Point object.
    public Point(double x, double y) { this.x = x; this.y = y; }

    // Return the Euclidean distance to another point.
    public double distanceTo(Point that) {
        return Math.hypot(this.x - that.x, this.y - that.y);
    }

    // Draw this point in StdDraw window.
    public void draw() { StdDraw.point(x, y); }

    // Draw a line to another point in StdDraw window.
    public void drawTo(Point that) {
        StdDraw.line(this.x, this.y, that.x, that.y);
    }

    // A string representing this point.
    public String toString() { return String.format("(%f, %f)", x, y); }

    // Compare to another point, lexicographically.
    // We treat x as more significant than y.
    public int compareTo(Point that) {
        int cx = Double.compare(x, that.x);
        assert -1 <= cx && cx <= 1;
        int cy = Double.compare(y, that.y);
        assert -1 <= cy && cy <= 1;
        return 3 * cx + cy;
    }
    // The nine possible values returned by compareTo (-4 to +4):
    public final int
        NW = -3+1,     N = 0+1,    NE = +3+1,
        W  = -3+0,     Z = 0+0,    E  = +3+0,
        SW = -3-1,     S = 0-1,    SE = +3-1;

    // Check whether this <= that in both coordinates.
    public boolean le2(Point that) { return x<=that.x && y<=that.y; }

    // Compute the coordinate-wise min or max of two points.
    // Remark: we avoid constructing when this.le2(that).
    Point min(Point that) {
        if (le2(that)) return this;
        return new Point(Math.min(x, that.x), Math.min(y, that.y));
    }
    Point max(Point that) {
        if (le2(that)) return that;
        return new Point(Math.max(x, that.x), Math.max(y, that.y));
    }

    // The next two methods make Point usable as a hash key.
    public int hashCode()
    {
        long v;
        v = Double.doubleToLongBits(x);
        int hx = (int)(v^(v>>>32));
        v = Double.doubleToLongBits(y);
        int hy = (int)(v^(v>>>32));
        return 37*hx + hy;
    }
    public boolean equals(Object obj) {
        if (obj==null || !(obj instanceof Point)) return false;
        Point that = (Point)obj;
        return that.x==x && that.y==y;
    }
}
