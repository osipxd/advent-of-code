private const val DAY = "Day09"

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
    produceDiffs(values).map { it.first() }.toList().reduceRight(Int::minus)
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

private fun readInput(name: String) = readLines(name) { it.splitInts() }
