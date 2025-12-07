import lib.matrix.*

private const val DAY = "Day07"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 21
        measureAnswer { part1(input()) }
    }

    //"Part 2" {
    //    part2(testInput()) shouldBe 0
    //    measureAnswer { part2(input()) }
    //}
}

private fun part1(input: Matrix<Char>): Int {
    val beams = ArrayDeque<Position>()
    val visited = mutableSetOf<Position>()
    var visitedSplitters = 0

    fun visitSplitter(position: Position) {
        if (!visited.add(position)) return

        val leftBeam = position.nextBy(Direction.LEFT)
        val rightBeam = position.nextBy(Direction.RIGHT)
        if (visited.add(leftBeam)) beams.addLast(leftBeam)
        if (visited.add(rightBeam)) beams.addLast(rightBeam)
        visitedSplitters += 1
    }

    beams.addLast(input.valuePositions { it == 'S' }.first())
    while (beams.isNotEmpty()) {
        val position = beams.removeFirst()
        val nextPosition = position.moveBy(Direction.DOWN, steps = 2)
        when (input.getOrNull(nextPosition)) {
            '.' -> {
                visited.add(nextPosition)
                beams.addLast(nextPosition)
            }
            '^' -> visitSplitter(nextPosition)
        }
    }

    return visitedSplitters
}

private fun part2(input: Matrix<Char>): Int = TODO()

private fun readInput(name: String) = readMatrix(name)