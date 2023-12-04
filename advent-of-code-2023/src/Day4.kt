private const val DAY = "Day4"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 13
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput()) shouldBe 30
        measureAnswer { part2(input()) }
    }
}

private fun part1(input: List<Int>): Int =
    input.sumOf { matches -> if (matches > 0) powerOfTwo(matches - 1) else 0 }

private fun powerOfTwo(power: Int) = 1 shl power

private fun part2(input: List<Int>): Int {
    val cardsCount = MutableList(input.size) { 1 }
    for ((i, matches) in input.withIndex()) {
        for (diff in 1..matches) {
            cardsCount[i + diff] += cardsCount[i]
        }
    }

    return cardsCount.sum()
}

private val spacesRegex = Regex("\\s+")

private fun readInput(name: String) = readLines(name).map { line ->
    val (winningNumbers, myNumbers) = line.substringAfter(":")
        .split("|")
        .map { numbers -> numbers.trim().split(spacesRegex).toSet() }

    (winningNumbers intersect myNumbers).size
}
