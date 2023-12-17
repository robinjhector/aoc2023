package com.jonssonhector.aoc;

public record Point(int x, int y) implements Comparable<Point> {

    public Point dx(int dx) {
        return new Point(x + dx, y);
    }

    public Point dy(int dy) {
        return new Point(x, y + dy);
    }

    public int distance(Point o) {
        return Math.abs(x - o.x()) + Math.abs(y - o.y());
    }

    public Point[] adj3x3() {
        return new Point[] {
            new Point(x - 1, y - 1), new Point(x, y - 1), new Point(x + 1, y - 1),
            new Point(x - 1, y), new Point(x, y), new Point(x + 1, y),
            new Point(x - 1, y + 1), new Point(x, y + 1), new Point(x + 1, y + 1)
        };
    }

    @Override
    public int compareTo(Point o) {
        return distance(o);
    }
}
