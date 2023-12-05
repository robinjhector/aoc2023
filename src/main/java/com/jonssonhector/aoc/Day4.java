package com.jonssonhector.aoc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Day4 extends BaseProblem {

    @Override
    public Output run(String input) {
        var lines = input.lines().toList();

        var cards = lines.stream()
            .map(Card::parseRow)
            .collect(Collectors.toCollection(ArrayList::new));

        var part1Sum = cards.stream()
            .reduce(0, (accum, card) -> {
                //System.out.println("Calculating card: " + card);
                var hits = (int) card.winningNumbers.stream()
                    .filter(card.cardNumbers()::contains)
                    .count();

                var points = 0;
                while (hits > 0) {
                    points = points == 0 ? 1 : points * 2;
                    hits--;
                }

                //System.out.println("Hits: " + hits + " - Points: " + points);

                return accum + points;
            }, (a,b) -> a);

        // Part 2
        var result = new HashMap<Integer, List<Card>>();
        for (var card : cards) {
            result.put(card.number, new ArrayList<>());
            result.get(card.number).add(card);
        }

        for (var cardNum : result.keySet()) {
            var cards2 = result.get(cardNum);

            for (var card : cards2) {
                var hits = (int) card.winningNumbers.stream()
                    .filter(card.cardNumbers()::contains)
                    .count();

                if (hits > 0) {
                    var toAdd = cards.subList(cardNum, cardNum + hits);
                    //System.out.printf("Card %d has %d hits adding: %s at idx %d%n", card.number, hits, toAdd, cardNum);
                    for (Card cardToAdd : toAdd) {
                        result.get(cardToAdd.number).add(cardToAdd);
                    }
                }
            }
        }

        var part2Sum = result.values().stream()
            .mapToLong(List::size)
            .sum();

//        cards.stream()
//            .collect(Collectors.groupingBy(
//                Card::number,
//                Collectors.counting()
//            ))
//            .entrySet()
//            .stream()
//            .sorted(Map.Entry.comparingByKey())
//            .forEach(e -> System.out.println("Card: " + e.getKey() + " count: " + e.getValue()));


        return new Output(String.valueOf(part1Sum), String.valueOf(part2Sum));
    }

    private record Card(int number, List<Integer> winningNumbers, List<Integer> cardNumbers) {

        public static Card parseRow(String string) {
            var split1 = string.split(":");
            var cardNum = Integer.parseInt(split1[0].replaceAll("[^\\d.]", ""));

            var numbs = split1[1].split("\\|");
            var winners = Arrays.stream(numbs[0].split(" "))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .toList();

            var cardNumbs = Arrays.stream(numbs[1].split(" "))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .toList();

            return new Card(cardNum, winners, cardNumbs);
        }

        @Override
        public String toString() {
            return "Card{" +
                "number=" + number +
                '}';
        }
    }
}
