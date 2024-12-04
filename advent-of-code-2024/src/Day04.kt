import lib.matrix.*
import lib.matrix.Direction.Companion.nextInDirection

private const val DAY = "Day04"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 18
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput()) shouldBe 9
        measureAnswer { part2(input()) }
    }
}

private fun part1(input: Matrix<Char>): Int =
    input.positions
        .filter { input[it] == 'X' }
        .sumOf { position -> Direction.entries.count { isXmas(input, position, it) } }

private fun isXmas(matrix: Matrix<Char>, position: Position, direction: Direction): Boolean {
    return matrix.readWord(position, direction, length = 4) == "XMAS"
}

private fun part2(input: Matrix<Char>): Int =
    input.positions.count { position -> input[position] == 'A' && isX_MAS(input, position) }

private fun isX_MAS(matrix: Matrix<Char>, position: Position): Boolean {
    val word1 = matrix.readWord(
        start = position.nextInDirection(Direction.UP_LEFT),
        direction = Direction.DOWN_RIGHT,
        length = 3,
    )
    val word2 = matrix.readWord(
        start = position.nextInDirection(Direction.DOWN_LEFT),
        direction = Direction.UP_RIGHT,
        length = 3,
    )

    return word1.isMas() && word2.isMas()
}

private fun String.isMas() = this == "MAS" || this == "SAM"

private fun readInput(name: String) = readMatrix(name)

// Utils

private val Matrix<*>.positions: Sequence<Position>
    get() = sequence {
        for (row in rowIndices) {
            for (column in columnIndices) {
                yield(Position(row, column))
            }
        }
    }

private fun Matrix<Char>.readWord(start: Position, direction: Direction, length: Int): String {
    return walk(start, direction).take(length).joinToString("")
}

private fun <T> Matrix<T>.walk(start: Position, direction: Direction): Sequence<T> {
    val matrix = this
    return sequence {
        var position = start
        while (position in matrix) {
            yield(matrix[position])
            position = position.nextInDirection(direction)
        }
    }
}
