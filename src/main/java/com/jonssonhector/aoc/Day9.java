package com.jonssonhector.aoc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day9 extends BaseProblem {

    /*
    0 3 6 9 12 15
    1 3 6 10 15 21
    10 13 16 21 30 45
     */
    @Override
    public Output run(String input) {
        var inputLines = input.lines()
            .map(l -> List.of(l.split(" ")))
            .map(l -> l.stream().mapToInt(Integer::parseInt).toArray())
            .toList();

        var part1Sum = 0;
        for (var inputLine : inputLines) {
            var nextVal = calcNextVal(inputLine);
            part1Sum += nextVal;
        }

        // Part2
        var part2Sum = 0;
        for (var inputLine : inputLines) {
            var prevVal = calcPrevVal(inputLine);
            part2Sum += prevVal;
        }

        return new Output(String.valueOf(part1Sum), String.valueOf(part2Sum));
    }

    private int calcNextVal(int[] inputLine) {
        var lineLength = inputLine.length;
        var expanded = expand(new ArrayList<>(), inputLine, lineLength, true);
        expanded.forEach(e -> System.out.println(Arrays.toString(e)));

        var interp = 0;
        for (int i = expanded.size() - 1; i > 0; i--) {
            var row = expanded.get(i);
            var prevRow = expanded.get(i - 1);
            interp = prevRow[prevRow.length - 1] + row[row.length - 1];

            var interpRow = new int[prevRow.length + 1];
            System.arraycopy(prevRow, 0, interpRow, 0, prevRow.length);
            interpRow[interpRow.length - 1] = interp;
            expanded.set(i - 1, interpRow);
        }

        return interp;
    }

    private int calcPrevVal(int[] inputLine) {
        var lineLength = inputLine.length;
        var expanded = expand(new ArrayList<>(), inputLine, lineLength, false);
        expanded.forEach(e -> System.out.println(Arrays.toString(e)));

        var interp = 0;
        for (int i = expanded.size() - 1; i > 0; i--) {
            var row = expanded.get(i);
            var prevRow = expanded.get(i - 1);
            interp = prevRow[0] - row[0];

            var interpRow = new int[prevRow.length + 1];
            System.arraycopy(prevRow, 0, interpRow, 1, prevRow.length);
            interpRow[0] = interp;
            expanded.set(i - 1, interpRow);
        }

        return interp;
    }

    private List<int[]> expand(List<int[]> result, int[] inputLine, int origLineLength, boolean padStart) {
        if (Arrays.stream(inputLine).allMatch(i -> i == 0)) {
            return result;
        }

        if (padStart) {
            // Expand inputLine to be same length as origLineLength
            // By padding the start of the array with zeroes
            var expanded = new int[origLineLength];
            var diff = origLineLength - inputLine.length;
            if (origLineLength - diff >= 0) {
                System.arraycopy(inputLine, 0, expanded, diff, origLineLength - diff);
            }
            result.add(expanded);
        } else {
            result.add(inputLine);
        }

        var nextLine = new int[inputLine.length - 1];
        for (int i = 0; i < inputLine.length - 1; i++) {
            var numb = inputLine[i];
            var nextNumb = inputLine[i + 1];
            nextLine[i] = nextNumb - numb;
        }

        return expand(result, nextLine, origLineLength, padStart);
    }
}
