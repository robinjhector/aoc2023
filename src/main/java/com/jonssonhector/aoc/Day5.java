package com.jonssonhector.aoc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class Day5 extends BaseProblem {

    @Override
    public Output run(String input) {
        var lines = input.lines().toList();
        var part1Input = parse(lines, false);

        long part1Lowest = Long.MAX_VALUE;
        for (var seed : part1Input.seeds) {
            // Single number only in p1
            var dest = part1Input.followP1(seed.startInclusive);
            if (dest < part1Lowest) {
                part1Lowest = dest;
            }
        }

        // Part 2
        // TODO: This is so badly implemented
        // TODO: Come back and see if we can make it some other way
        var part2Input = parse(lines, true);
        var futures = new ArrayList<CompletableFuture<Long>>();
        try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
            for (var seedRange : part2Input.seeds) {
                System.out.println("Starting checking seed range: " + seedRange);
                var future = CompletableFuture.supplyAsync(() -> {
                    long lowest = Long.MAX_VALUE;
                    for (var seed : seedRange.iterator()) {
                        var dest = part2Input.followP1(seed);
                        if (dest < lowest) {
                            lowest = dest;
                        }
                    }
                    System.out.println("Completed checking seed range: " + seedRange);
                    return lowest;
                }, exec);

                futures.add(future);
            }
        }

        var s = futures.stream()
            .map(CompletableFuture::join)
            .mapToLong(l -> l)
            .min();

        return new Output(String.valueOf(part1Lowest), String.valueOf(s.getAsLong()));
    }

    private record Input(
        List<Range> seeds,
        MapRange seedToSoil,
        MapRange soilToFertilizer,
        MapRange fertilizerToWater,
        MapRange waterToLight,
        MapRange lightToTemperature,
        MapRange temperatureToHumidity,
        MapRange humidityToLocation
    ) {
        public long followP1(long seed) {
            var soil = seedToSoil.get(seed);
            var fertilizer = soilToFertilizer.get(soil);
            var water = fertilizerToWater.get(fertilizer);
            var light = waterToLight.get(water);
            var temperature = lightToTemperature.get(light);
            var humidity = temperatureToHumidity.get(temperature);
            var location = humidityToLocation.get(humidity);
            /*
            System.out.printf("Following seed (p1): %d = ", seed);
            System.out.printf("Soil: %d  ", soil);
            System.out.printf("Fertilizer: %d  ", fertilizer);
            System.out.printf("Water: %d  ", water);
            System.out.printf("Light: %d  ", light);
            System.out.printf("Temperature: %d  ", temperature);
            System.out.printf("Humidity: %d  ", humidity);
            System.out.printf("LOCATION: %d%n", location);
             */

            return location;
        }

        public long followP2(Range seedRange) {
            var soil = seedToSoil.get(seedRange);
            var fertilizer = soilToFertilizer.get(soil);
            var water = fertilizerToWater.get(fertilizer);
            var light = waterToLight.get(water);
            var temperature = lightToTemperature.get(light);
            var humidity = temperatureToHumidity.get(temperature);
            var location = humidityToLocation.get(humidity);

            System.out.printf("Following seed range (p2): %s = ", seedRange);
            System.out.printf("Soil: %s  ", soil);
            System.out.printf("Fertilizer: %s  ", fertilizer);
            System.out.printf("Water: %s  ", water);
            System.out.printf("Light: %s  ", light);
            System.out.printf("Temperature: %s  ", temperature);
            System.out.printf("Humidity: %s  ", humidity);
            System.out.printf("LOCATION: %s%n", location);

            return location.endExclusive;
        }
    }

    private record MapRange(List<RangePair> rangePairs) {
        public long get(long sourceInput) {
            var rangePair = rangePairs.stream()
                .filter(rp -> rp.source.contains(sourceInput))
                .findFirst();

            if (rangePair.isEmpty()) {
                return sourceInput;
            }

            var offset = rangePair.get().source.calcOffset(sourceInput);
            return rangePair.get().destination.plus(offset);
        }

        public Range get(Range sourceInput) {
            rangePairs.stream()
                .filter(rp -> rp.source.intersects(sourceInput))
                .forEach(rp -> System.out.printf("INTERSECTS: %s%n", rp));
            return null;
        }
    }

    private record RangePair(Range source, Range destination) {
    }

    private record Range(long startInclusive, long endExclusive) {

        public static Range single(long number) {
            return new Range(number, number + 1);
        }

        public boolean contains(long digit) {
            var contains = digit >= startInclusive && digit < endExclusive;
            //System.out.printf("%s contains %d ? %s%n", this, digit, contains);
            return contains;
        }

        public boolean intersects(Range check) {
            return check.startInclusive < startInclusive && check.endExclusive > endExclusive;
        }

        public long plus(long offset) {
            return startInclusive + offset;
        }

        public long calcOffset(long input) {
            var offset =  input - startInclusive;
            //System.out.printf("%s offset of %d : %d%n", this, input, offset);
            return offset;
        }

//        public void forEach(LongConsumer consumer) {
//            LongStream.range(startInclusive, endExclusive).forEach(consumer);
//        }

        @Override
        public String toString() {
            return "[%d, %d)".formatted(startInclusive, endExclusive);
        }

        public Iterable<Long> iterator() {
            return () -> new Iterator<>() {
                private long current = startInclusive;

                @Override
                public boolean hasNext() {
                    return current < endExclusive;
                }

                @Override
                public Long next() {
                    return current++;
                }
            };
        }
    }

    private static Input parse(List<String> lines, boolean p2) {
        var seeds = new ArrayList<Range>();
        if (!p2) {
            var seedsRaw = lines.get(0);
            seedsRaw = seedsRaw.replaceAll("[^\\d\\s]", "");
            Arrays.stream(seedsRaw.split("\\s"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .map(Range::single)
                .forEach(seeds::add);
        } else {
            var seedsRaw = lines.get(0);
            var pat = Pattern.compile("(\\d+) (\\d+)");
            var mas = pat.matcher(seedsRaw);

            while (mas.find()) {
                var rangeStart = Long.parseLong(mas.group(1));
                var rangeLen = Long.parseLong(mas.group(2));
                var rangeEnd = rangeStart + rangeLen;
                seeds.add(new Range(rangeStart, rangeEnd));
            }
        }

        // FORMAT: destination range start, source range start, range length
        var seedToSoil = new ArrayList<RangePair>();
        var soilToFertilizer = new ArrayList<RangePair>();
        var fertilizerToWater = new ArrayList<RangePair>();
        var waterToLight = new ArrayList<RangePair>();
        var lightToTemperature = new ArrayList<RangePair>();
        var temperatureToHumidity = new ArrayList<RangePair>();
        var humidityToLocation = new ArrayList<RangePair>();

        var lineCount = 1;
        var activeList = (List<RangePair>) null;
        while (lineCount < lines.size()) {
            var line = lines.get(lineCount);
            if (line.isBlank()) {
                lineCount++;
                continue;
            }

            if (line.equals("seed-to-soil map:")) {
                activeList = seedToSoil;
            } else if (line.equals("soil-to-fertilizer map:")) {
                activeList = soilToFertilizer;
            } else if (line.equals("fertilizer-to-water map:")) {
                activeList = fertilizerToWater;
            } else if (line.equals("water-to-light map:")) {
                activeList = waterToLight;
            } else if (line.equals("light-to-temperature map:")) {
                activeList = lightToTemperature;
            } else if (line.equals("temperature-to-humidity map:")) {
                activeList = temperatureToHumidity;
            } else if (line.equals("humidity-to-location map:")) {
                activeList = humidityToLocation;
            } else if (line.matches("^\\d+\\s\\d+\\s\\d+$")) {
                // FORMAT: destination range start, source range start, range length
                var split = line.split("\\s");
                var rangeLen = Long.parseLong(split[2]);
                var destination = new Range(Long.parseLong(split[0]), Long.parseLong(split[0]) + rangeLen);
                var source = new Range(Long.parseLong(split[1]), Long.parseLong(split[1]) + rangeLen);
                //System.out.printf("PARSED: \"%s\" INTO src=%s, dst=%s%n", line, source, destination);
                activeList.add(new RangePair(source, destination));
            } else {
                System.out.println("Unknown line: " + line);
            }

            lineCount++;
        }

        System.out.printf("""
            seeds:                  %s
            seedToSoil:             %s
            soilToFertilizer        %s
            fertilizerToWater       %s
            waterToLight            %s
            lightToTemperature      %s
            temperatureToHumidity   %s
            humidityToLocation      %s
            
            """,
            seeds,
            seedToSoil,
            soilToFertilizer,
            fertilizerToWater,
            waterToLight,
            lightToTemperature,
            temperatureToHumidity,
            humidityToLocation
        );

        return new Input(
            seeds,
            new MapRange(seedToSoil),
            new MapRange(soilToFertilizer),
            new MapRange(fertilizerToWater),
            new MapRange(waterToLight),
            new MapRange(lightToTemperature),
            new MapRange(temperatureToHumidity),
            new MapRange(humidityToLocation)
        );
    }
}
