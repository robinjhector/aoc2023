package com.jonssonhector.aoc;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Day2 extends BaseProblem {

    @Override
    public Output run(String input) {
        var games = input.lines()
            .map(this::parseLine)
            .toList();

        // Part 1 requirements
        var maxRed = 12;
        var maxGreen = 13;
        var maxBlue = 14;

        var part1Sum = games.stream()
            .filter(g -> g.rounds.stream()
                .allMatch(gr -> gr.red <= maxRed && gr.green <= maxGreen && gr.blue <= maxBlue))
            .mapToInt(g -> g.num)
            .sum();

        var part2Sum = games.stream()
            .mapToLong(g -> {
                var maxR = g.maxRed();
                var maxG = g.maxGreen();
                var maxB = g.maxBlue();

                return (long) maxR * maxG * maxB;
            })
            .sum();

        return new Output(String.valueOf(part1Sum), String.valueOf(part2Sum));
    }

    private Game parseLine(String line) {
        var prefix = line.split(":");
        var game = prefix[0].substring(5);
        var gameNum = Integer.parseInt(game);

        var rounds = Arrays.stream(prefix[1].split(";"))
            .map(String::trim)
            .map(this::parseGameRounds)
            .toList();

        //System.out.printf("RAW: %s  --  PARSED game: %d, rounds: %s%n", line, gameNum, rounds);
        return new Game(gameNum, rounds);
    }

    private static final Pattern ROUND_PAT = Pattern.compile("(\\d+) (red|green|blue)");
    private GameRound parseGameRounds(String string) {
        var mc = ROUND_PAT.matcher(string);

        int r = 0, g = 0, b = 0;
        while (mc.find()) {
            var count = mc.group(1);
            var colour = mc.group(2);

            switch (colour) {
                case "red" -> r += Integer.parseInt(count);
                case "green" -> g += Integer.parseInt(count);
                case "blue" -> b += Integer.parseInt(count);
            }
        }

        //System.out.println("PARSED GAME ROUND: " + string + " -> r:" + r + ", g:" + g + ", b:" + b);
        return new GameRound(r, g, b);
    }

    private record Game(int num, List<GameRound> rounds) {
        public int maxRed() {
            return rounds.stream()
                .mapToInt(GameRound::red)
                .max()
                .orElse(1);
        }

        public int maxGreen() {
            return rounds.stream()
                .mapToInt(GameRound::green)
                .max()
                .orElse(1);
        }

        public int maxBlue() {
            return rounds.stream()
                .mapToInt(GameRound::blue)
                .max()
                .orElse(1);
        }
    }

    private record GameRound(int red, int green, int blue) {
    }
}
