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
    return almanac.seeds.minOf { seed ->
        var result = seed
        for (mappings in almanac.mappingsLayers) result = mappings.map(result)
        result
    }
}

private fun List<Mapping>.map(value: Long) = find { value in it }?.map(value) ?: value

// Runtime: 4m 35s
private fun part2(almanac: Almanac): Long {
    return almanac.seeds.chunked(2).minOf { (rangeStart, rangeSize) ->
        (rangeStart rangeOfSize rangeSize).minOf(almanac::findLocation)
    }
}

private fun readInput(name: String): Almanac {
    val almanacInput = readText(name).split("\n\n")

    fun parseMapping(line: String): Mapping {
        val (destinationStart, sourceStart, rangeSize) = line.splitLongs()
        return Mapping(
            range = sourceStart rangeOfSize rangeSize,
            offset = destinationStart - sourceStart,
        )
    }

    return Almanac(
        seeds = almanacInput.first().substringAfter(": ").splitLongs(),
        mappingsLayers = almanacInput.drop(1).map { mappingInput ->
            mappingInput.lineSequence()
                .drop(1) // Skip mappings layer description
                .map(::parseMapping)
                .toList()
        }
    )
}

private data class Almanac(
    val seeds: List<Long>,
    val mappingsLayers: List<List<Mapping>>
)

private class Mapping(val range: LongRange, val offset: Long) {

    operator fun contains(value: Long) = value in range

    fun map(value: Long) = value + offset
}

// region Utils
private fun String.splitLongs() = split(" ").map(String::toLong)

private infix fun Long.rangeOfSize(size: Long) = this..<(this + size)
// endregion
