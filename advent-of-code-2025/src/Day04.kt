import lib.matrix.*

private const val DAY = "Day04"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 13
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput()) shouldBe 43
        measureAnswer { part2(input()) }
    }
}

private const val PAPER_ROLL = '@'

private fun part1(input: Matrix<Char>): Int = input.accessibleRolls().count()
private fun part2(input: Matrix<Char>): Int = generateSequence { input.removeAccessibleRolls().takeIf { it > 0 } }.sum()

private fun Matrix<Char>.removeAccessibleRolls() = accessibleRolls().onEach { this[it] = 'x' }.count()
private fun Matrix<Char>.accessibleRolls() = valuePositions { it == PAPER_ROLL }.filter(::canBeAccessed)

private fun Matrix<Char>.canBeAccessed(position: Position) =
    position.neighbors { getOrNull(it) != PAPER_ROLL }.count() > 4

private fun readInput(name: String) = readMatrix(name)
