import lib.matrix.Position
import lib.matrix.get
import lib.matrix.toMatrix
import lib.matrix.valuePositions

private const val DAY = "Day21"

private typealias Keypad = Map<Char, Position>

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 126384
        measureAnswer { part1(input()) }
    }

    //"Part 2" {
    //    part2(testInput()) shouldBe 0
    //    measureAnswer { part2(input()) }
    //}
}

private fun part1(codes: List<String>): Int {
    return codes.sumOf { code ->
        val movesCount = enterCode(numericKeypad, code)
            .let { enterCode(directionalKeypad, it) }
            .let { enterCode(directionalKeypad, it) }
            .length
        movesCount * code.dropLast(1).toInt()
    }
}

private fun part2(codes: List<String>): Int = TODO()

private fun enterCode(keypad: Keypad, code: String): String = buildString {
    var position = keypad.getValue('A')

    fun moveHorizontally(diff: Int) {
        if (diff > 0) repeat(diff) { append('>') }
        else if (diff < 0) repeat(-diff) { append('<') }
    }

    fun moveVertically(diff: Int) {
        if (diff > 0) repeat(diff) { append('v') }
        else if (diff < 0) repeat(-diff) { append('^') }
    }

    for (char in code) {
        val nextPosition = keypad.getValue(char)
        if (nextPosition != position) {
            val rowDiff = nextPosition.row - position.row
            val columnDiff = nextPosition.column - position.column
            if (columnDiff < 0 && Position(position.row, nextPosition.column) in keypad.values) {
                moveHorizontally(columnDiff)
                moveVertically(rowDiff)
            } else {
                moveVertically(rowDiff)
                moveHorizontally(columnDiff)
            }
        }
        append('A')
        position = nextPosition
    }
}

private val numericKeypad = keypadMapping(
    "789",
    "456",
    "123",
    " 0A",
)

private val directionalKeypad = keypadMapping(
    " ^A",
    "<v>",
)

private fun keypadMapping(vararg lines: String): Keypad {
    val matrix = lines.toList().toMatrix()
    return matrix.valuePositions { it != ' ' }.associateBy(matrix::get)
}

private fun readInput(name: String) = readLines(name)
