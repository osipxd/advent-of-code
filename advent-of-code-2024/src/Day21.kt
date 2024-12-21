import lib.matrix.Position
import lib.matrix.get
import lib.matrix.positions
import lib.matrix.toMatrix

private const val DAY = "Day21"

private typealias Keypad = Map<Char, Position>

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 126384
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput()) shouldBe 154115708116294
        measureAnswer { part2(input()) }
    }
}

private fun part1(codes: List<String>): Long = solve(codes, directionalKeypads = 2)
private fun part2(codes: List<String>): Long = solve(codes, directionalKeypads = 25)

private fun solve(codes: List<String>, directionalKeypads: Int): Long =
    codes.sumOf { code -> codeComplexity(code, directionalKeypads) }

private fun codeComplexity(code: String, directionalKeypads: Int): Long {
    val memory = mutableMapOf<Pair<String, Int>, Long>()
    fun fragmentComplexity(fragment: String, keypadsLeft: Int): Long = memory.getOrPut(fragment to keypadsLeft) {
        if (keypadsLeft == 0) return fragment.length.toLong()

        val moves = enterCode(directionalKeypad, fragment)
        codeFragments(moves).sumOf { fragmentComplexity(it, keypadsLeft - 1) }
    }

    val moves = enterCode(numericKeypad, code)
    val buttonTaps = codeFragments(moves).sumOf { fragmentComplexity(it, directionalKeypads) }
    return buttonTaps * code.dropLast(1).toInt()
}

private fun codeFragments(code: String) = sequence {
    var start = -1
    for (i in code.indices) {
        if (code[i] == 'A') {
            yield(code.substring(start + 1..i))
            start = i
        }
    }
}

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
            val canStartHorizontally = Position(position.row, nextPosition.column) != keypad[' ']
            val canStartVertically = Position(nextPosition.row, position.column) != keypad[' ']

            if (!canStartVertically || canStartHorizontally && columnDiff < 0) {
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
    return matrix.positions().associateBy(matrix::get)
}

private fun readInput(name: String) = readLines(name)
