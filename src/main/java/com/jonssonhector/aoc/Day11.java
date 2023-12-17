package com.jonssonhector.aoc;

import java.util.ArrayList;
import java.util.List;

public class Day11 extends BaseProblem {

    @Override
    public Output run(String input) {

        var starMap = StarMap.parse(input);

        var starLocations = new ArrayList<Point>();

        boolean[] emptyRows = new boolean[starMap.matrix.length];
        boolean[] emptyColumns = new boolean[starMap.matrix[0].length];
        for (int y = 0; y < starMap.matrix.length; y++) {
            emptyRows[y] = isRowEmpty(starMap, y);
        }
        for (int i = 0; i < starMap.matrix[0].length; i++) {
            emptyColumns[i] = isColumnEmpty(starMap, i);
        }

        for (int y = 0; y < starMap.matrix.length; y++) {
            for (int x = 0; x < starMap.matrix[y].length; x++) {
                if (starMap.matrix[y][x] == '#') {
                    starLocations.add(new Point(x, y));
                }
            }
        }

        var part1Sum = sumOfShortestPath(expandUniverse(starLocations, emptyRows, emptyColumns, 1));

        System.out.println("10x: " + sumOfShortestPath(expandUniverse(starLocations, emptyRows, emptyColumns, 9)));
        System.out.println("100x: " + sumOfShortestPath(expandUniverse(starLocations, emptyRows, emptyColumns, 99)));

        var part2Sum = sumOfShortestPath(expandUniverse(starLocations, emptyRows, emptyColumns, 999_999));
        return new Output(String.valueOf(part1Sum), String.valueOf(part2Sum));
    }

    private static ArrayList<Point> expandUniverse(
        List<Point> starLocations,
        boolean[] emptyRows,
        boolean[] emptyColumns,
        int factor
    ) {
        var adjustedStarLocations = new ArrayList<Point>();
        for (var starLocation : starLocations) {
            var x = starLocation.x();
            var y = starLocation.y();

            var rowOffset = 0;
            for (int i = 0; i < y; i++) {
                // Do we have a blank row "above us"? If so we should move our star by 1
                if (emptyRows[i]) {
                    rowOffset += factor;
                }
            }

            var colOffset = 0;
            for (int i = 0; i < x; i++) {
                // Do we have a blank column "to the left of us"? If so we should move our star by 1
                if (emptyColumns[i]) {
                    colOffset += factor;
                }
            }

            var newStarLocation = new Point(x + colOffset, y + rowOffset);
            adjustedStarLocations.add(newStarLocation);
        }
        return adjustedStarLocations;
    }

    private static long sumOfShortestPath(List<Point> stars) {
        var distance = 0L;
        for (int i = 0; i < stars.size(); i++) {
            var star = stars.get(i);
            for (int j = i + 1; j < stars.size(); j++) {
                var otherStar = stars.get(j);
                distance += star.distance(otherStar);
            }
        }
        return distance;
    }

    private static boolean isRowEmpty(StarMap map, int y) {
        for (char c : map.matrix[y]) {
            if (c == '#') {
                return false;
            }
        }
        return true;
    }

    private static boolean isColumnEmpty(StarMap map, int x) {
        for (char[] chars : map.matrix) {
            if (chars[x] == '#') {
                return false;
            }
        }
        return true;
    }

    private record StarMap(char[][] matrix) {
        public static StarMap parse(String input) {
            var lines = input.lines().toList();
            var matrix = new char[lines.size()][lines.getFirst().length()];

            for (int y = 0; y < lines.size(); y++) {
                for (int x = 0; x < lines.get(y).toCharArray().length; x++) {
                    matrix[y][x] = lines.get(y).charAt(x);
                }
            }
            return new StarMap(matrix);
        }
    }
}
