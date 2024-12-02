import kotlin.math.abs

private const val DAY = "Day01"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 11
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput()) shouldBe 31
        measureAnswer { part2(input()) }
    }
}

private fun part1(input: Pair<List<Int>, List<Int>>): Int {
    val (lefts, rights) = input.map { it.sorted() }
    return lefts.indices.sumOf { abs(lefts[it] - rights[it]) }
}

private fun part2(input: Pair<List<Int>, List<Int>>): Int {
    val (lefts, rights) = input
    val rightsHistogram = rights.groupingBy { it }.eachCount()
    return lefts.sumOf { left -> left * rightsHistogram.getOrDefault(left, 0) }
}

private fun readInput(name: String): Pair<List<Int>, List<Int>> =
    readLines(name) { it.splitInts("   ").takePair() }.unzip()
