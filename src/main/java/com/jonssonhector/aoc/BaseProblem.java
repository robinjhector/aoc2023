package com.jonssonhector.aoc;

public abstract class BaseProblem {

    public abstract Output run(String input);

    public record Output(String part1, String part2) {
    }
}
