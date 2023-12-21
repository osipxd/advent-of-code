import lib.matrix.Matrix
import lib.matrix.Position
import lib.matrix.contains
import lib.matrix.get

private const val DAY = "Day21"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput(), steps = 6) shouldBe 16
        measureAnswer { part1(input(), steps = 64) }
    }

//    "Part 2" {
//        part2(testInput()) shouldBe 0
//        measureAnswer { part2(input()) }
//    }
}

private fun part1(input: Matrix<Char>, steps: Int): Int {
    val start = Position(input.rowCount / 2, input.columnCount / 2)

    var pots = setOf(start)
    repeat(steps) {
        pots = pots.flatMap { it.neighbors() }.filter { it in input && input[it] != '#' }.toSet()
    }

    return pots.size
}

private fun readInput(name: String) = readMatrix(name)
