private const val DAY = "Day5"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 35
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput()) shouldBe 46
        measureAnswer { part2(input()) }
    }
}

// Runtime: 10ms
private fun part1(almanac: Almanac): Long {
    return almanac.seeds.minOf { seed -> almanac.findLocation(seed) }
}

// Runtime: 4m 35s
private fun part2(almanac: Almanac): Long {
    return almanac.seeds.chunked(2).minOf { (rangeStart, rangeSize) ->
        (rangeStart until (rangeStart + rangeSize))
            .also { println(it) }
            .minOf { seed -> almanac.findLocation(seed) }
    }
}

private fun readInput(name: String): Almanac {
    val input = ArrayDeque(readLines(name))
    fun readMappings(): List<Mapping> {
        if (input.first().isEmpty()) input.removeFirst()
        input.removeFirst() // Drop description
        return buildList {
            while (input.isNotEmpty() && input.first().isNotEmpty()) {
                val (destinationStart, sourceStart, rangeSize) = input.removeFirst().split(" ").map(String::toLong)
                add(
                    Mapping(
                        range = sourceStart until (sourceStart + rangeSize),
                        offset = destinationStart - sourceStart,
                    )
                )
            }
        }
    }

    return Almanac(
        seeds = input.removeFirst().substringAfter(": ").split(" ").map(String::toLong),
        seedToSoil = readMappings(),
        soilToFertilizer = readMappings(),
        fertilizerToWater = readMappings(),
        waterToLight = readMappings(),
        lightToTemperature = readMappings(),
        temperatureToHumidity = readMappings(),
        humidityToLocation = readMappings(),
    )
}

private data class Almanac(
    val seeds: List<Long>,
    val seedToSoil: List<Mapping>,
    val soilToFertilizer: List<Mapping>,
    val fertilizerToWater: List<Mapping>,
    val waterToLight: List<Mapping>,
    val lightToTemperature: List<Mapping>,
    val temperatureToHumidity: List<Mapping>,
    val humidityToLocation: List<Mapping>,
) {
    fun findLocation(seed: Long): Long {
        return seed
            .let(seedToSoil::map)
            .let(soilToFertilizer::map)
            .let(fertilizerToWater::map)
            .let(waterToLight::map)
            .let(lightToTemperature::map)
            .let(temperatureToHumidity::map)
            .let(humidityToLocation::map)
    }
}

private fun List<Mapping>.map(value: Long): Long {
    val mapping = find { value in it } ?: Mapping.OneToOne
    return mapping.map(value)
}

private class Mapping(val range: LongRange, val offset: Long) {

    operator fun contains(value: Long) = value in range

    fun map(value: Long) = value + offset

    companion object {
        val OneToOne = Mapping(0..Long.MAX_VALUE, 0)
    }
}
