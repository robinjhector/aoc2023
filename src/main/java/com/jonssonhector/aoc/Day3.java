package com.jonssonhector.aoc;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day3 extends BaseProblem {

    @Override
    public Output run(String input) {
        var matrix = linesToMatrix(input);
        var digits = new ArrayList<Integer>();

        for (int y = 0; y < matrix.length; y++) {
            for (int x = 0; x < matrix[y].length; x++) {
                char c = matrix[y][x];

                var isDigit = Character.isDigit(c);
                if (!isDigit) {
                    continue;
                }

                var digitBuilder = new StringBuilder();
                digitBuilder.append(c);

                // Look ahead and see if it's part of a bigger number
                var checkX = x + 1;
                while (checkX < matrix[y].length) {
                    if (Character.isDigit(matrix[y][checkX])) {
                        digitBuilder.append(matrix[y][checkX]);
                        checkX++;
                    } else {
                        break;
                    }
                }
                // We've collected a number!
                var digLen = digitBuilder.length();
                var digit = Integer.parseInt(digitBuilder.toString());

                for (int i = 0; i < digLen; i++) {
                    if (hasAdjSymbol(matrix, y, x + i)) {
                        digits.add(digit);
                        break;
                    }
                }

                // Jump forward
                x += digLen - 1;
                //System.out.printf("Found digit %d at %d,%d ", digit, y, x);
            }
            //System.out.println();
        }

        var part1Sum = digits.stream()
            .mapToLong(Integer::longValue)
            .sum();

        var gears = new ArrayList<Integer>();
        for (int y = 0; y < matrix.length; y++) {
            for (int x = 0; x < matrix[y].length; x++) {
                char c = matrix[y][x];

                if (c != '*') {
                    continue;
                }

                var numberAdj = getAdjNumbers(matrix, y, x);
                if (numberAdj.isPresent()) {
                    var pwr = numberAdj.get().one * numberAdj.get().two;
                    gears.add(pwr);
                }
            }
        }

        var part2Sum = gears.stream()
            .mapToLong(Integer::longValue)
            .sum();

        return new Output(String.valueOf(part1Sum), String.valueOf(part2Sum));
    }

    private Optional<TwoNumbers> getAdjNumbers(char[][] matrix, int y, int x) {
        var point = new Point(y, x);

        var p1 = (PointNumber) null;
        var p2 = (PointNumber) null;

        for (var adjPoint : point.adj()) {
            var adjPointDigit = adjPoint.compute(matrix).filter(Character::isDigit);
            if (adjPointDigit.isEmpty()) {
                continue;
            }

            var parsedNum = adjPoint.parseNum(matrix);
            if (p1 == null) {
                p1 = parsedNum;
            } else if (!parsedNum.equals(p1)) {
                p2 = parsedNum;
                return Optional.of(new TwoNumbers(p1.number(), p2.number()));
            }
        }

        return Optional.empty();
    }

    private boolean hasAdjSymbol(char[][] matrix, int y, int x) {
        var point = new Point(y, x);

        return Stream.of(point.adj())
            .map(p -> p.compute(matrix).filter(isSymbol()))
            .anyMatch(Optional::isPresent);
    }

    private char[][] linesToMatrix(String input) {
        var lines = input.lines().toList();
        var matrix = new char[lines.size()][lines.get(0).length()];

        for (int i = 0; i < lines.size(); i++) {
            var line = lines.get(i);
            for (int j = 0; j < line.length(); j++) {
                matrix[i][j] = line.charAt(j);
            }
        }

        return matrix;
    }

    private static Optional<Character> get(Supplier<Character> supp) {
        try {
            return Optional.of(supp.get());
        } catch (ArrayIndexOutOfBoundsException ex) {
            return Optional.empty();
        }
    }

    private Predicate<Character> isSymbol() {
        return c -> c != '.' && !Character.isDigit(c);
    }

    private record Point(int y, int x) {
        public Point northEast() {
            return new Point(y - 1, x + 1);
        }

        public Point north() {
            return new Point(y - 1, x);
        }

        public Point northWest() {
            return new Point(y - 1, x - 1);
        }

        public Point east() {
            return new Point(y, x + 1);
        }

        public Point west() {
            return new Point(y, x - 1);
        }

        public Point southEast() {
            return new Point(y + 1, x + 1);
        }

        public Point south() {
            return new Point(y + 1, x);
        }

        public Point southWest() {
            return new Point(y + 1, x - 1);
        }

        public Point[] adj() {
            return new Point[] {
                northWest(), north(), northEast(),
                west(), east(),
                southWest(), south(), southEast()
            };
        }

        public Optional<Character> compute(char[][] matrix) {
            return get(() -> matrix[y][x]);
        }

        public PointNumber parseNum(char[][] matrix) {
            var n = compute(matrix).filter(Character::isDigit);
            if (n.isEmpty()) {
                throw new IllegalArgumentException("Not a number");
            }

            var digitChars = new ArrayList<Character>();
            digitChars.add(n.get());
            // Walk backwards until we don't find a digit
            var checkX1 = x - 1;
            while (checkX1 >= 0) {
                var c = matrix[y][checkX1];
                if (Character.isDigit(c)) {
                    digitChars.add(0, c);
                    checkX1--;
                } else {
                    break;
                }
            }
            // Walk forward until we don't find a digit
            var checkX2 = x + 1;
            while (checkX2 < matrix[y].length) {
                var c = matrix[y][checkX2];
                if (Character.isDigit(c)) {
                    digitChars.add(c);
                    checkX2++;
                } else {
                    break;
                }
            }

            var ss = digitChars.stream()
                .map(String::valueOf)
                .collect(Collectors.joining());

            var i = Integer.parseInt(ss);
            return new PointNumber(i, y, checkX1);
        }
    }

    private record PointNumber(int number, int startY, int startX) {
        public Point getStartPoint() {
            return new Point(startY, startX);
        }
    }

    private record TwoNumbers(int one, int two) {
    }

}
