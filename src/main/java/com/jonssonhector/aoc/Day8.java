package com.jonssonhector.aoc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class Day8 extends BaseProblem {

    /*
    RL

    AAA = (BBB, CCC)
    BBB = (DDD, EEE)
    CCC = (ZZZ, GGG)
    DDD = (DDD, DDD)
    EEE = (EEE, EEE)
    GGG = (GGG, GGG)
    ZZZ = (ZZZ, ZZZ)
     */

    @Override
    public Output run(String input) {
        var lines = input.lines()
            .filter(l -> !l.isBlank())
            .toList();

        var directions = lines.getFirst();
        var nodes = lines.stream()
            .skip(1)
            .map(l -> l.split("="))
            .collect(Collectors.toMap(
                l -> l[0].trim(),
                l -> {
                    var coords = l[1].split(",");
                    return new MapNode(coords[0].replaceAll("\\(", "").trim(), coords[1].replaceAll("\\)", "").trim());
                }
            ));

        System.out.println("Directions: " + directions);
        nodes.forEach((k, v) -> System.out.printf("Node: %s -> (%s, %s)%n", k, v.l, v.r));

        var myMap = new AMap(directions, nodes);

        // Part1
        var part1Sum = 0;
        var currentNode = myMap.nodes.get("AAA");
        var completed = currentNode == null;
        while (!completed) {
            for (var direction : myMap.directions.toCharArray()) {
                var key = currentNode.choose(direction);
                part1Sum++;
                if (key.equals("ZZZ")) {
                    completed = true;
                } else {
                    currentNode = myMap.nodes.get(key);
                }
            }
        }

        // Part 2
        // TODO: idk, come back to this
        var part2Sum = 0;
        completed = false;
        var directionIdx = 0;
        while (!completed) {
            for (char c : myMap.directions.toCharArray()) {

            }
            completed = true;
        }


        return new Output(String.valueOf(part1Sum), String.valueOf(part2Sum));
    }

    private record AMap(String directions, Map<String, MapNode> nodes) {
        public Map<String, MapNode> subMap(String keySuffix) {
            return nodes.entrySet().stream()
                .filter(e -> e.getKey().endsWith(keySuffix))
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue
                ));
        }
    }

    private record MapNode(String l, String r) {
        private String choose(char input) {
            return input == 'L' ? l : r;
        }
    }
}
