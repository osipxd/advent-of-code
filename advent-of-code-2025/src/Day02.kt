private const val DAY = "Day02"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 1227775554
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput()) shouldBe 4174379265
        measureAnswer { part2(input()) }
    }
}

private fun part1(input: List<LongRange>): Long = input.sumOf { it.invalidIds(repetitions = 2).sum() }

private fun part2(input: List<LongRange>): Long = input.sumOf { range ->
    val lengths = range.first.length..range.last.length
    val possibleRepetitions = (2..lengths.last).asSequence()
        .filter { repetitions -> lengths.any { it % repetitions == 0 } }
    possibleRepetitions.flatMap { range.invalidIds(it) }.distinct().sum()
}

private fun LongRange.invalidIds(repetitions: Int): Sequence<Long> {
    check(repetitions >= 2) { "Invalid repetitions: $repetitions. Should be >= 2" }

    val firstPartStart = first.take { length -> length / repetitions }
    val firstPartEnd = last.take { length -> (length + repetitions - 1) / repetitions }

    return (firstPartStart..firstPartEnd).asSequence()
        .map { "$it".repeat(repetitions).toLong() }
        .filter { it in this }
}

private val Long.length get() = toString().length

// Example: 1234.take(2) -> 12
private inline fun Long.take(count: (length: Int) -> Int): Long {
    val stringValue = toString()
    return stringValue.take(count(stringValue.length)).ifEmpty { "0" }.toLong()
}

private fun readInput(name: String) = readText(name).split(",").map {
    val (start, end) = it.splitLongs("-")
    start..end
}
