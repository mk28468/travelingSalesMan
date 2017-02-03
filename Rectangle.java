package hw4;
// A Rectangle is a closed axis-aligned rectangle in the plane.
// It is defined by two opposite corners.
// For comparison, see class Interval2D defined by the book.

public class Rectangle
{
    // A constant Rectangle, containing the entire plane.
    public final static Rectangle ALL = new Rectangle(Point.MIN, Point.MAX);

    // Our fields are immutable.
    public final Point min, max;

    // Construct a rectangle with opposite corners p and q.
    // Remark: we avoid constructing new points when p.le2(q).
    public Rectangle(Point p, Point q) { min = p.min(q); max = p.max(q); }

    // Does this closed rectangle contain a point?
    public boolean contains(Point p) { return min.le2(p) && p.le2(max); }

    // Return rectangle enlarged to contain a given point.
    // Remark: we avoid construction when this.contains(p).
    public Rectangle add(Point p) {
        Point min = this.min.min(p), max = p.max(this.max);
        if (min==this.min && max==this.max) return this; // unchanged
        return new Rectangle(min, max);
    }

    // Does this rectangle intersect another?
    public boolean intersects(Rectangle that) {
        // There are four "max < min" ways to be disjoint.
        if (max.x < that.min.x || that.max.x < min.x) return false;
        if (max.y < that.min.y || that.max.y < min.y) return false;
        return true;
    }

    // Distance from this rectangle to a point.
    public double distanceTo(Point p) {
        double dx = Math.max(0, Math.max(min.x-p.x, p.x-max.x));
        double dy = Math.max(0, Math.max(min.y-p.y, p.y-max.y));
        return Math.hypot(dx, dy); // sqrt(dx*dx+dy*dy)
    }

    // The area of the rectangle.
    public double area() { return (max.x-min.x)*(max.y-min.y); }

    // Print as string, a product of two closed intervals.
    public String toString() {
        return String.format("[%f, %f] x [%f, %f]", min.x, max.x, min.y, max.y);
    }

    // Draw to StdDraw window.
    public void draw() {
        StdDraw.rectangle((max.x+min.x)/2, (max.y+min.y)/2, // center
                          (max.x-min.x)/2, (max.y-min.y)/2); // half sides
    }
}
