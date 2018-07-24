package line;

/**
 * @author 李辉
 * @version 1.0
 */

public class Line {
    Point a;
    Point b;

    public Line(Point a, Point b) {
        this.a = a;
        this.b = b;
    }

    public void isLine() throws PointSameException {
        if (a.equals​(b))
            throw new PointSameException("Points are same!");
        System.out.println("This is a line!");
    }

    public static void main(String[] args) throws PointSameException {
        Point p1 = new Point(2, 3);
        Point p2 = new Point(3, 5);
        Point p3 = new Point(2, 3);

        Line l1 = new Line(p1, p2);
        Line l2 = new Line(p1, p3);

        l1.isLine();
        l2.isLine();
    }
}
