import lib.matrix.Matrix
import lib.matrix.Position
import lib.matrix.get
import lib.matrix.readMatrix

private const val DAY = "Day04"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 18
        measureAnswer { part1(input()) }
    }

    //"Part 2" {
    //    part2(testInput()) shouldBe 0
    //    measureAnswer { part2(input()) }
    //}
}

private fun part1(input: Matrix<Char>): Int {
    return input.positions.sumOf { position ->
        if (input[position] != 'X') return@sumOf 0
        directions.count { isXmas(input, position, it) }
    }
}

private const val XMAS = "XMAS"

private fun isXmas(input: Matrix<Char>, position: Position, direction: Pair<Int, Int>): Boolean {
    var (row, column) = position
    val (dr, dc) = direction
    var index = 0

    while (index < XMAS.lastIndex) {
        row += dr
        column += dc
        index++
        if (row !in input.rowIndices || column !in input.columnIndices || input[row, column] != XMAS[index]) return false
    }

    return true
}

private fun part2(input: Matrix<Char>): Int = TODO()

private val Matrix<*>.positions: Sequence<Position>
    get() = sequence {
        for (row in rowIndices) {
            for (column in columnIndices) {
                yield(Position(row, column))
            }
        }
    }

private val directions: Sequence<Pair<Int, Int>>
    get() = sequenceOf(
        -1 to -1,
        -1 to  0,
        -1 to +1,
         0 to -1,
         0 to +1,
        +1 to -1,
        +1 to  0,
        +1 to +1,
    )

private fun readInput(name: String) = readMatrix(name)
