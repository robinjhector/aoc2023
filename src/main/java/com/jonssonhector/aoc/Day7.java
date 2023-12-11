package com.jonssonhector.aoc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day7 extends BaseProblem {
    /*
    32T3K 765
    T55J5 684
    KK677 28
    KTJJT 220
    QQQJA 483
     */

    //private static boolean WITH_JOKERS = false;

    @Override
    public Output run(String input) {
        var hands = input.lines()
            .map(l -> Hand.parse(l, false))
            .toList();

        //hands.forEach(h -> System.out.println("Hand: " + h));

        var sortedHands = hands.stream().sorted(strongest(false)).toList();

        var part1Sum = 0L;
        for (int i = 0; i < sortedHands.size(); i++) {
            var hand = sortedHands.get(i);
            part1Sum += ((long) (i + 1) * hand.bid);
        }
        sortedHands.forEach(h -> System.out.println("Sorted hand1: " + h));
        System.out.println();

        // Part2
        var hands2 = input.lines()
            .map(l -> Hand.parse(l, true))
            .toList();

        var sortedHands2 = hands2.stream().sorted(strongest(true)).toList();

        sortedHands2.forEach(h -> System.out.println("Sorted hand2: " + h));
        System.out.println();

        var part2Sum = 0L;
        for (int i = 0; i < sortedHands2.size(); i++) {
            var hand = sortedHands2.get(i);
            part2Sum += ((long) (i + 1) * hand.bid);
        }

        return new Output(String.valueOf(part1Sum), String.valueOf(part2Sum));
    }

    private static Comparator<Hand> strongest(boolean withJokers) {
        return Comparator
            .comparing(Hand::typeOfHand)
            .thenComparing((o1, o2) -> {
                for (int i = 0; i < o1.cards.size(); i++) {
                    if (withJokers) {
                        var c1 = o1.raw.get(i);
                        var c2 = o2.raw.get(i);
                        var c1Str = c1 == Card.JACK ? -1 : c1.ordinal();
                        var c2Str = c2 == Card.JACK ? -1 : c2.ordinal();

                        var res = Integer.compare(c2Str, c1Str);
                        if (res != 0) {
                            return res;
                        }
                    } else {
                        var c1 = o1.cards.get(i);
                        var c2 = o2.cards.get(i);

                        var res = Integer.compare(c2.ordinal(), c1.ordinal());
                        if (res != 0) {
                            return res;
                        }
                    }
                }

                return 0;
            })
            .reversed();
    }

    private record Hand(List<Card> cards, List<Card> raw, int bid) {
        private static Hand parse(String line, boolean withJokers) {
            var spl = line.split(" ");
            var cardStr = spl[0];
            var bid = Integer.parseInt(spl[1]);
            var cardList = cardStr.chars()
                .mapToObj(i -> (char) i)
                .map(Card::fromChar)
                .collect(Collectors.toList());

            // Handling jokers
            if (withJokers && cardList.contains(Card.JACK)) {
                return handleJokers(cardList, bid);
            } else {
                return new Hand(cardList, cardList, bid);
            }
        }

        private static Hand handleJokers(List<Card> cards, int bid) {
            // Generate all possible combinations by replacing jokers with all other card values.
            var allPossibleHands = generateAllJokerReplacements(cards, cards, bid, 0);
            return Collections.max(allPossibleHands, strongest(true));
        }

        private static List<Hand> generateAllJokerReplacements(List<Card> cards, List<Card> raw, int bid, int index) {
            if (index >= cards.size()) { // Base case: If index is beyond hand size, return the current hand without changes.
                return List.of(new Hand(List.copyOf(cards), raw, bid));
            }

            var hands = new ArrayList<Hand>();
            // Handle the case when the current card is a joker.
            if (cards.get(index) == Card.JACK) {
                // Recurse for every card value the joker can take on, except itself (JACK).
                for (var card : Card.values()) {
                    if (card != Card.JACK) {
                        var newCards = new ArrayList<>(cards);
                        newCards.set(index, card); // Replace the joker with the current card value.
                        hands.addAll(generateAllJokerReplacements(newCards, raw, bid, index + 1));
                    }
                }
            } else {
                // If the current card is not a joker, just proceed to the next index.
                hands.addAll(generateAllJokerReplacements(cards, raw, bid, index + 1));
            }

            return hands;
        }

        public Type typeOfHand() {
            if (cards.stream().distinct().count() == 1) {
                return Type.FIVE_OF_A_KIND;
            }

            var grouped = cards.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            var valueGroups = grouped.values();
            if (valueGroups.stream().anyMatch(s -> s == 5)) {
                return Type.FIVE_OF_A_KIND;
            }
            if (valueGroups.stream().anyMatch(s -> s == 4)) {
                return Type.FOUR_OF_A_KIND;
            }
            if (valueGroups.stream().filter(s -> s == 3).count() == 1 && valueGroups.stream().filter(s -> s == 2).count() == 1) {
                return Type.FULL_HOUSE;
            }
            if (valueGroups.stream().anyMatch(s -> s == 3)) {
                return Type.THREE_OF_A_KIND;
            }
            if (valueGroups.stream().filter(s -> s == 2).count() == 2) {
                return Type.TWO_PAIR;
            }
            if (valueGroups.stream().anyMatch(s -> s == 2)) {
                return Type.ONE_PAIR;
            }
            if (valueGroups.stream().allMatch(s -> s == 1)) {
                return Type.HIGH_CARD;
            }
            throw new IllegalArgumentException("Unknown type: " + cards + " - " + valueGroups);
        }

        @Override
        public String toString() {
            var sb = new StringJoiner(", ", "Hand{", "}");
            sb.add(cards.toString());
            sb.add("type: " + typeOfHand());
            if (!cards.equals(raw)) {
                sb.add("raw: " + raw);
            }
            sb.add("bid: " + bid);
            return sb.toString();
        }

        enum Type {
            FIVE_OF_A_KIND,
            FOUR_OF_A_KIND,
            FULL_HOUSE,
            THREE_OF_A_KIND,
            TWO_PAIR,
            ONE_PAIR,
            HIGH_CARD
        }
    }

    private enum Card {
        TWO,
        THREE,
        FOUR,
        FIVE,
        SIX,
        SEVEN,
        EIGHT,
        NINE,
        TEN,
        JACK,
        QUEEN,
        KING,
        ACE;

        public static Card fromChar(Character c) {
            if (Character.isDigit(c)) {
                var integer = Character.getNumericValue(c);
                if (integer >= 2 && integer < 10) {
                    return Card.values()[integer - 2];
                }
                throw new IllegalArgumentException("Not in range: " + integer);
            }

            return switch (c) {
                case 'T' -> TEN;
                case 'J' -> JACK;
                case 'Q' -> QUEEN;
                case 'K' -> KING;
                case 'A' -> ACE;
                default -> throw new IllegalArgumentException("Unknown char: " + c);
            };
        }
    }
}
