private const val DAY = "Day9"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 114
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput()) shouldBe 2
        measureAnswer { part2(input()) }
    }
}

private fun part1(input: List<List<Int>>): Int = input.sumOf { values ->
    produceDiffs(values).sumOf { it.last() }
}

private fun part2(input: List<List<Int>>): Int = input.sumOf { values ->
    var nextValue = 0
    for (value in produceDiffs(values).toList().asReversed().map { it.first() }) {
       nextValue = value - nextValue
    }
    nextValue
}

private fun produceDiffs(values: List<Int>): Sequence<List<Int>> {
    return generateSequence(values) { currentValues ->
        if (currentValues.any { it != 0 }) {
            currentValues.windowed(2) { (left, right) -> right - left }
        } else {
            null
        }
    }
}

private fun readInput(name: String) = readLines(name).map { it.splitInts(" ") }
