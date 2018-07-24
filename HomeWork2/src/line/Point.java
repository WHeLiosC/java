package line;

/**
 * @author 李辉
 * @version 1.0
 */

public class Point {
    double x;
    double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public boolean equals​(Object obj) {
        if (obj == null)
            return false;
        if (obj instanceof Point) {
            Point p = (Point) obj;
            return p.x == x && p.y == y;
        }
        return false;
    }
}
