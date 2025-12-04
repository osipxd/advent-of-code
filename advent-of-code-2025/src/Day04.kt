import lib.matrix.*

private const val DAY = "Day04"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 13
        measureAnswer { part1(input()) }
    }

    //"Part 2" {
    //    part2(testInput()) shouldBe 0
    //    measureAnswer { part2(input()) }
    //}
}

private const val PAPER_ROLL = '@'

private fun part1(input: Matrix<Char>): Int = input.valuePositions { it == PAPER_ROLL }.count { input.canBeAccessed(it) }

private fun part2(input: Matrix<Char>): Int = TODO()

private fun Matrix<Char>.canBeAccessed(position: Position): Boolean =
    position.neighbors { this.getOrNull(it) != PAPER_ROLL }.count() > 4

private fun readInput(name: String) = readMatrix(name)

// Utils

private fun Position.neighbors(condition: (Position) -> Boolean = { true }) =
    Direction.entries.asSequence().map(::nextBy).filter(condition)
