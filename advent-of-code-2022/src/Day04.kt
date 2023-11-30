fun main() {
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 2)

    val input = readInput("Day04")
    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}

private fun part1(input: Sequence<List<IntRange>>): Int = input.count { (a, b) -> b in a || a in b }
private fun part2(input: Sequence<List<IntRange>>): Int = input.count { (a, b) -> a.first in b || b.first in a }

// Parse each line to lists of ranges:
//   "1-3,4-6" -> [1..3, 4..6]
private fun readInput(name: String) = readLines(name).asSequence().map { line ->
    line.split(",").map { range ->
        val (start, end) = range.split("-").map(String::toInt)
        start..end
    }
}

// Checks if [this] range contains both the start and the end of the [other] range
// It is an operator, so we can call it using "in":
//   a.contains(b) == b in a
private operator fun IntRange.contains(other: IntRange): Boolean = this.first <= other.first && this.last >= other.last

