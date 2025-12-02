
import lib.PairOf

private const val DAY = "Day25"

private typealias Schematic = List<Int>

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 3
        measureAnswer { part1(input()) }
    }
}

private fun part1(input: PairOf<List<Schematic>>): Int {
    val (keys, locks) = input
    return locks.sumOf { lock -> keys.count { key -> key fits lock } }
}

private infix fun Schematic.fits(other: Schematic): Boolean = zip(other).all { (a, b) -> a + b <= MAX_HEIGHT }

private const val MAX_HEIGHT = 5

private fun readInput(name: String) = readText(name).split("\n\n")
    .map { it.lines() }
    .partition { it.first().startsWith("#") }
    .map { it.map(List<String>::toHeights) }

private fun List<String>.toHeights(): List<Int> =
    columns().map { column -> column.count { it == '#' } - 1 }

// Utils

private fun List<String>.columns(): List<String> = buildList {
    for (i in this@columns.first().indices) {
        val column = buildString { for (line in this@columns) append(line[i]) }
        add(column)
    }
}
