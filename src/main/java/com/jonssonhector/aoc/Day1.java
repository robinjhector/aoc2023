package com.jonssonhector.aoc;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class Day1 extends BaseProblem {

    @Override
    public Output run(String input) {
        var stream = input.lines().toList();
        var pat = Pattern.compile("\\d");
        var day1Sum = 0L;

        for (String line : stream) {
            var numbers = pat.matcher(line).results().map(MatchResult::group).toList();

            if (numbers.isEmpty()) {
                continue;
            }

            var combined = getCombined(numbers);

            day1Sum += Long.parseLong(combined);
        }

        // Part 2
        var day2Sum = 0L;
        for (String line : stream) {
            var lineDigits = new ArrayList<String>();
            for (int i = 0; i < line.toCharArray().length; i++) {
                var dig = parseDigitWord(line, i);
                if (dig != null) {
                    lineDigits.add(dig);
                }
            }
            if (lineDigits.isEmpty()) {
                continue;
            }

            var combined = getCombined(lineDigits);
            day2Sum += Long.parseLong(combined);
        }

        return new Output(
            String.valueOf(day1Sum),
            String.valueOf(day2Sum)
        );
    }

    private String parseDigitWord(String line, int i) {
        var chr = line.toCharArray();

        while (i < chr.length) {
            var c = chr[i];
            if (Character.isDigit(c)) {
                return String.valueOf(c);
            } else {
                var sb = line.substring(i);
                if (sb.startsWith("one")) {
                    return "1";
                } else if (sb.startsWith("two")) {
                    return "2";
                } else if (sb.startsWith("three")) {
                    return "3";
                } else if (sb.startsWith("four")) {
                    return "4";
                } else if (sb.startsWith("five")) {
                    return "5";
                } else if (sb.startsWith("six")) {
                    return "6";
                } else if (sb.startsWith("seven")) {
                    return "7";
                } else if (sb.startsWith("eight")) {
                    return "8";
                } else if (sb.startsWith("nine")) {
                    return "9";
                }
            }
            i++;
        }

        return null;
    }

    private static String getCombined(List<String> numbers) {
        return numbers.size() == 1 ?
            numbers.getFirst() + numbers.getFirst() :
            numbers.getFirst() + numbers.getLast();
    }

}
