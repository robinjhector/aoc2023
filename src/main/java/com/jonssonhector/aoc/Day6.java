package com.jonssonhector.aoc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day6 extends BaseProblem {

    @Override
    public Output run(String input) {
        var races = parseRaces(input);

        var part1Sum = 1;
        for (var race : races) {
            var waysToWin = getWaysToWin(race);

            //System.out.println("WAYS TO WIN: " + waysToWin);
            part1Sum *= waysToWin;
        }

        // Part2
        var p2TimeStr = new StringBuilder();
        var p2DistStr = new StringBuilder();
        for (var race : races) {
            p2TimeStr.append(race.duration);
            p2DistStr.append(race.recordDistance);
        }

        var part2SingleRace = new Race(
            Long.parseLong(p2TimeStr.toString()),
            Long.parseLong(p2DistStr.toString())
        );

        //System.out.println("Part 2 race: " + part2SingleRace);

        var part2Sum = getWaysToWin(part2SingleRace);

        return new Output(String.valueOf(part1Sum), String.valueOf(part2Sum));
    }

    private static int getWaysToWin(Race race) {
        long timeLeft = race.duration;
        var waysToWin = 0;

        while (timeLeft > 0) {
            // Each iteration is 1ms
            var hold = timeLeft - 1;
            var raceTime = race.duration - hold;
            var dist = hold * raceTime;

            if (dist > race.recordDistance) {
                waysToWin++;
            }

            //System.out.printf("Race hold %dms, travelTime=%dms, distance=%dmm%n", hold, raceTime, dist);
            timeLeft--;
        }
        return waysToWin;
    }

    private List<Race> parseRaces(String input) {
        var lines = input.lines().toList();
        var times = Arrays.stream(lines.get(0).split(" +"))
            .filter(r -> r.matches("\\d+"))
            .map(Integer::parseInt)
            .toList();
        var distances = Arrays.stream(lines.get(1).split(" +"))
            .filter(r -> r.matches("\\d+"))
            .map(Integer::parseInt)
            .toList();

        var result = new ArrayList<Race>();
        for (int i = 0; i < times.size(); i++) {
            result.add(new Race(times.get(i), distances.get(i)));
        }
        return result;
    }

    private record Race(long duration, long recordDistance) {

    }

}
