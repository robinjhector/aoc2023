package com.jonssonhector.aoc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class Day10 extends BaseProblem {

    @Override
    public Output run(String input) {

        var maze = Maze.parse(input);
        System.out.println("Start: " + maze.start);
        var atEndPos = false;
        var currentPos = maze.start;
        var lastDirection = (Direction) null;
        var part1Steps = new ArrayList<Coord>();
        while (!atEndPos) {
            var step = maze.step(currentPos, lastDirection);
            part1Steps.add(step.newPos);
            currentPos = step.newPos;
            lastDirection = step.direction;

            if (maze.start.equals(step.newPos)) {
                atEndPos = true;
            }
        }

        System.out.println("Visualized ('S' = start, '·' = non-pipe):");
        visualize(maze, part1Steps);
        System.out.println("Part 1 total steps: " + part1Steps.size());

        var cleanMaze = cleanup(maze, part1Steps);
        var adjustedMaze = expandMaze(cleanMaze);
        System.out.println("Start 2: " + adjustedMaze.start);
        currentPos = adjustedMaze.start;
        atEndPos = false;
        lastDirection = null;
        var part2Steps = new ArrayList<Coord>();
        while (!atEndPos) {
            var step = adjustedMaze.step(currentPos, lastDirection);
            part2Steps.add(step.newPos);
            currentPos = step.newPos;
            lastDirection = step.direction;

            if (adjustedMaze.start.equals(step.newPos)) {
                atEndPos = true;
            }
        }

        System.out.println("Visualized ('S' = start, '·' = non-pipe):");
        visualize(adjustedMaze, part2Steps);


        return new Output(String.valueOf(part1Steps.size() / 2), String.valueOf(0));
    }

    private void visualize(Maze maze, List<Coord> actualPipes) {
        for (int y = 0; y < maze.matrix.length; y++) {
            for (int x = 0; x < maze.matrix[0].length; x++) {
                var ncord = new Coord(x, y);
                var pipe = actualPipes.contains(ncord);
                var isStart = maze.start.equals(ncord);
                var cc = ncord.get(maze);
                var c = isStart ? 'S' : pipe ? cc : '.';
                System.out.print(c);
            }
            System.out.println();
        }
    }

    private Maze cleanup(Maze maze, ArrayList<Coord> actualPipes) {
        var newMtrx = new char[maze.matrix.length][maze.matrix[0].length];
        for (int y = 0; y < maze.matrix.length; y++) {
            for (int x = 0; x < maze.matrix[0].length; x++) {
                var ncord = new Coord(x, y);
                var actualPipe = actualPipes.contains(ncord);
                var isStart = maze.start.equals(ncord);
                var cc = ncord.get(maze);
                var c = isStart ? 'S' : actualPipe ? cc : ' ';
                newMtrx[y][x] = c;
            }
        }
        return new Maze(maze.start, newMtrx);
    }

    private int countEnclosedBlanks(Maze maze) {
        var exploredBlanks = new ArrayList<Coord>();
        var enclosedBlanks = new ArrayList<Coord>();
        for (int y = 0; y < maze.matrix.length; y++) {
            for (int x = 0; x < maze.matrix[0].length; x++) {
                var cord = new Coord(x, y);
                var c = cord.get(maze);
                if (c == ' ' && !exploredBlanks.contains(cord)) {
                    // We've found a blank! Expand it
                    var exp = expand(maze, cord);
                    exploredBlanks.addAll(exp.coords);
                    if (!exp.oob) {
                        enclosedBlanks.addAll(exp.coords);
                    }
                }
            }
        }

        return enclosedBlanks.size();
    }

    private ExpandedMaze expand(Maze maze, Coord from) {
        return new ExpandedMaze(List.of(), true);
    }

    private Maze expandMaze(Maze maze) {
        var newMaze = new char[maze.matrix.length * 3][maze.matrix[0].length * 3];
        var newStart = maze.start;
        for (int y = 0; y < maze.matrix.length; y++) {
            for (int x = 0; x < maze.matrix[0].length; x++) {
                var ny = y * 3;
                var nx = x * 3;

                var cord = new Coord(x, y);
                var expanded = switch (cord.get(maze)) {
                    case '|' -> List.of(
                        ' ', '|', ' ',
                        ' ', '|', ' ',
                        ' ', '|', ' '
                    );
                    case '-' -> List.of(
                        ' ', ' ', ' ',
                        '-', '-', '-',
                        ' ', ' ', ' '
                    );
                    case 'L' -> List.of(
                        ' ', '|', ' ',
                        ' ', 'L', '-',
                        ' ', ' ', ' '
                    );
                    case 'J' -> List.of(
                        ' ', '|', ' ',
                        '-', 'J', ' ',
                        ' ', ' ', ' '
                    );
                    case '7' -> List.of(
                        ' ', ' ', ' ',
                        '-', '7', ' ',
                        ' ', '|', ' '
                    );
                    case 'F' -> List.of(
                        ' ', ' ', ' ',
                        ' ', 'F', '-',
                        ' ', '|', ' '
                    );
                    case '.', ' ' -> List.of(
                        ' ', ' ', ' ',
                        ' ', ' ', ' ',
                        ' ', ' ', ' '
                    );
                    case 'S' -> {
                        newStart = new Coord(nx + 1, ny + 1);
                        var sExp = new ArrayList<>(List.of(
                            ' ', ' ', ' ',
                            ' ', 'S', ' ',
                            ' ', ' ', ' '
                        ));
                        // In order to expand S properly, we need to know which sides should also be expanded
                        if (y > 0) {
                            var n = maze.matrix[y - 1][x];
                            if (n == '|' || n == 'F' || n == '7') {
                                sExp.set(1, '|');
                            }
                        }
                        if (x + 1 < maze.matrix[y].length) {
                            var e = maze.matrix[y][x + 1];
                            if (e == '-' || e == '7' || e == 'J') {
                                sExp.set(5, '-');
                            }
                        }
                        if (y + 1 < maze.matrix.length) {
                            var s = maze.matrix[y + 1][x];
                            if (s == '|' || s == 'L' || s == 'J') {
                                sExp.set(7, '|');
                            }
                        }
                        if (x > 0) {
                            var w = maze.matrix[y][x - 1];
                            if (w == '-' || w == '7' || w == 'F') {
                                sExp.set(3, '-');
                            }
                        }
                        yield sExp;
                    }
                    default -> throw new IllegalArgumentException("Don't know how to expand: " + cord.get(maze));
                };

                newMaze[ny][nx] = expanded.get(0);
                newMaze[ny][nx + 1] = expanded.get(1);
                newMaze[ny][nx + 2] = expanded.get(2);
                newMaze[ny + 1][nx] = expanded.get(3);
                newMaze[ny + 1][nx + 1] = expanded.get(4);
                newMaze[ny + 1][nx + 2] = expanded.get(5);
                newMaze[ny + 2][nx] = expanded.get(6);
                newMaze[ny + 2][nx + 1] = expanded.get(7);
                newMaze[ny + 2][nx + 2] = expanded.get(8);
            }
        }

        return new Maze(newStart, newMaze);
    }

    private record Maze(Coord start, char[][] matrix) {
        private static Maze parse(String input) {
            var lines = input.lines().toList();
            var firstLine = lines.getFirst();

            var arr = new char[lines.size()][firstLine.length()];

            var start = (Coord) null;
            for (int y = 0; y < lines.size(); y++) {
                var theLine = lines.get(y);
                for (int x = 0; x < theLine.length(); x++) {
                    var c = theLine.charAt(x);
                    arr[y][x] = c;
                    if (c == 'S') {
                        start = new Coord(x, y);
                    }
                }
            }

            return new Maze(start, arr);
        }

        public Step step(Coord from, Direction lastDirection) {
            var c = matrix[from.y][from.x];
            var thr = (Supplier<Step>) () -> {
                throw new IllegalArgumentException("For " + c + ", illegal last move: " + lastDirection);
            };

            //Currently standing on Coord[x=1, y=1]:F, moving in direction NORTH
            //Moving from Coord[x=1, y=1], direction WEST towards Coord[x=0, y=1]
            //System.out.printf("Currently standing on %s:%s, moving in direction %s%n", from, c, lastDirection);
            return switch (c) {
                case 'S' -> {
                    if (from.y > 0) {
                        var n = matrix[from.y - 1][from.x];
                        if (n == '|' || n == 'F' || n == '7') {
                            // We can move north if we want
                            yield attemptMove(from, Direction.NORTH);
                        }
                    }
                    if (from.x + 1 < matrix[from.y].length) {
                        var e = matrix[from.y][from.x + 1];
                        if (e == '-' || e == '7' || e == 'J') {
                            // We can move east if we want
                            yield attemptMove(from, Direction.EAST);
                        }
                    }
                    if (from.y + 1 < matrix.length) {
                        var s = matrix[from.y + 1][from.x];
                        if (s == '|' || s == 'L' || s == 'J') {
                            // We can move south if we wish
                            yield attemptMove(from, Direction.SOUTH);
                        }
                    }
                    if (from.y > 0) {
                        var w = matrix[from.y][from.y - 1];
                        if (w == '-' || w == '7' || w == 'F') {
                            // We can move west if we wish
                            yield attemptMove(from, Direction.WEST);
                        }
                    }
                    throw new IllegalArgumentException("Idk where to go from start: " + from);
                }
                case '|' -> switch (lastDirection) {
                    // Last move linear, continue in same direction
                    case NORTH -> attemptMove(from, Direction.NORTH);
                    case SOUTH -> attemptMove(from, Direction.SOUTH);
                    default -> thr.get();
                };
                case '-' -> switch (lastDirection) {
                    // Last move linear, continue in same direction
                    case WEST -> attemptMove(from, Direction.WEST);
                    case EAST -> attemptMove(from, Direction.EAST);
                    default -> thr.get();
                };
                case 'L' -> switch (lastDirection) {
                    case WEST -> attemptMove(from, Direction.NORTH);
                    case SOUTH -> attemptMove(from, Direction.EAST);
                    default -> thr.get();
                };
                case 'J' -> switch (lastDirection) {
                    case EAST -> attemptMove(from, Direction.NORTH);
                    case SOUTH -> attemptMove(from, Direction.WEST);
                    default -> thr.get();
                };
                case '7' -> switch (lastDirection) {
                    case EAST -> attemptMove(from, Direction.SOUTH);
                    case NORTH -> attemptMove(from, Direction.WEST);
                    default -> thr.get();
                };
                case 'F' -> switch (lastDirection) {
                    case WEST -> attemptMove(from, Direction.SOUTH);
                    case NORTH -> attemptMove(from, Direction.EAST);
                    default -> thr.get();
                };
                case '.' -> throw new IllegalStateException("Currently not in a pipe: " + from);
                default -> throw new IllegalStateException("Unknown tile at " + from + ": " + c);
            };
        }

        private Step attemptMove(Coord from, Direction direction) {
            var newPos = from.move(direction);
            if (!newPos.inBounds(matrix)) {
                throw new IllegalStateException("New pos is out of bounds: " + newPos);
            }
            //System.out.printf("Moving from %s, direction %s towards %s%n", from, direction, newPos);
            return new Step(newPos, direction);
        }
    }

    private record Coord(int x, int y) {
        private boolean inBounds(char[][] matrix) {
            return x >= 0 && x < matrix[0].length && y >= 0 && y < matrix.length;
        }

        private char get(Maze maze) {
            return maze.matrix[y][x];
        }

        private Coord move(Direction direction) {
            return switch (direction) {
                case NORTH -> new Coord(x, y - 1);
                case EAST -> new Coord(x + 1, y);
                case SOUTH -> new Coord(x, y + 1);
                case WEST -> new Coord(x - 1, y);
            };
        }
    }

    private record Step(Coord newPos, Direction direction) {
    }

    private enum Direction {
        NORTH,
        EAST,
        SOUTH,
        WEST
    }

    private record ExpandedMaze(List<Coord> coords, boolean oob) {
    }
}
