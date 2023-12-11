package com.jonssonhector.aoc;

import java.nio.charset.StandardCharsets;

public class Runner {

    public static void main(String[] args) throws Exception {
        if (args.length < 1 || args.length > 2) {
            throw new IllegalArgumentException("Expected 1, or 2 arguments, got %d".formatted(args.length));
        }

        var day = Integer.parseInt(args[0]);
        var input = args.length == 2 ? args[1] : null;
        var fromCli = input != null;

        if (input == null) {
            var file = "/day%d.txt".formatted(day);
            try (var resource = Runner.class.getResourceAsStream(file)) {
                if (resource == null) {
                    throw new IllegalArgumentException("Could not find resource %s".formatted(file));
                }
                input = new String(resource.readAllBytes(), StandardCharsets.UTF_8);
            }
        }

        System.out.printf("""
            ----------------------
            |  Running (Day %d)  |
            ----------------------
            | Data: %s
            ----------------------
            """, day, fromCli ? "From CLI" : "From file");

        var s = System.currentTimeMillis();
        var output = switch (day) {
            case 1 -> new Day1().run(input);
            case 2 -> new Day2().run(input);
            case 3 -> new Day3().run(input);
            case 4 -> new Day4().run(input);
            case 5 -> new Day5().run(input);
            case 6 -> new Day6().run(input);
            case 7 -> new Day7().run(input);

            default -> throw new IllegalArgumentException("Not implemented yet");
        };
        var time = System.currentTimeMillis() - s;

        System.out.printf("""
            ---------------------
            |  Output (Day %d)  |
            ---------------------
            Part1: %s
            Part2: %s
            Time taken: %dms
            ---------------------
            """,
            day,
            output.part1(),
            output.part2(),
            time
        );
    }
}
