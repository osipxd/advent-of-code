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
    val (listA, listB) = input.map { it.sorted() }
    return listA.indices.sumOf { abs(listA[it] - listB[it]) }
}

private fun part2(input: Pair<List<Int>, List<Int>>): Int {
    val (listA, listB) = input
    val listBCounts = listB.groupingBy { it }.eachCount()
    return listA.sumOf { a -> a * listBCounts.getOrDefault(a, 0) }
}

private fun readInput(name: String): Pair<List<Int>, List<Int>> {
    val listA = mutableListOf<Int>()
    val listB = mutableListOf<Int>()
    readLines(name) {
        val (a, b) = it.splitInts("   ")
        listA.add(a)
        listB.add(b)
    }

    return listA to listB
}