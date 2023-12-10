import kotlin.math.max
import kotlin.math.min

private const val DAY = "Day05"

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

private fun part1(almanac: Almanac): Long {
    return almanac.seeds.minOf { seed ->
        var result = seed
        for (mappings in almanac.mappingsLayers) result = mappings.transform(result)
        result
    }
}

private fun List<Mapping>.transform(value: Long) = find { value in it }?.transform(value) ?: value

private fun part2(almanac: Almanac): Long {
    // Initially fill with ranges from input
    // Pairs: <layer of mapping> to <range>
    val ranges = ArrayDeque(
        almanac.seeds.chunked(2)
            .map { (start, size) -> 0 to (start rangeOfSize size) }
    )

    val lastLayer = almanac.mappingsLayers.lastIndex
    var minLocation = Long.MAX_VALUE

    while (ranges.isNotEmpty()) {
        val (layer, range) = ranges.removeFirst()
        val mapping = almanac.mappingsLayers[layer].find { range in it }
        val rangeForNextLayer = if (mapping != null) {
            val (before, intersection, after) = range intersect mapping.range
            if (before != null) ranges.addFirst(layer to before)
            if (after != null) ranges.addFirst(layer to after)
            mapping.transform(intersection)
        } else {
            range
        }

        if (layer == lastLayer) {
            minLocation = min(minLocation, rangeForNextLayer.first)
        } else {
            ranges.addLast(layer + 1 to rangeForNextLayer)
        }
    }

    return minLocation
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
    operator fun contains(values: LongRange) = values.first in range || values.last in range

    fun transform(value: Long) = value + offset
    fun transform(values: LongRange) = transform(values.first)..transform(values.last)
}

// region Utils
private fun String.splitLongs() = split(" ").map(String::toLong)

private infix fun Long.rangeOfSize(size: Long) = this..<(this + size)

// Returns: (before?, intersection, after?)
private infix fun LongRange.intersect(other: LongRange) = Triple(
    (first..<max(first, other.first)).takeUnless { it.isEmpty() }, // Before
    max(first, other.first)..min(last, other.last), // Intersection
    (min(last, other.last) + 1..last).takeUnless { it.isEmpty() }, // After
)
// endregion
