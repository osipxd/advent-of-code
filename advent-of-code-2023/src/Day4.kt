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

private fun part1(input: List<Card>): Int = input.sumOf { card ->
    val matches = card.numberOfMatches
    if (matches > 0) 1 shl (matches - 1) else 0
}

private fun part2(input: List<Card>): Int {
    val count = MutableList(input.size) { 1 }
    for ((i, card) in input.withIndex()) {
        val matches = card.numberOfMatches
        for (j in (i + 1)..(i + matches)) {
            count[j] += count[i]
        }
    }

    return count.sum()
}

private fun readInput(name: String) = readLines(name).map { line ->
    val (winningNumbers, myNumbers) = line.substringAfter(": ").split(" | ")
    Card(
        winningNumbers = winningNumbers.split(" ").filter { it.isNotEmpty() }.map { it.toInt() }.toSet(),
        myNumbers = myNumbers.split(" ").filter { it.isNotEmpty() }.map { it.toInt() }.toSet(),
    )
}

private data class Card(
    val winningNumbers: Set<Int>,
    val myNumbers: Set<Int>,
) {
    val numberOfMatches = winningNumbers.intersect(myNumbers).size
}
