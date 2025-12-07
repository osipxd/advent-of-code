import lib.matrix.*

private const val DAY = "Day07"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 21
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput()) shouldBe 40
        measureAnswer { part2(input()) }
    }
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

private fun part2(input: Matrix<Char>): Long {
    val beams = ArrayDeque<Position>()
    val visited = mutableSetOf<Position>()
    val pathsToPosition = mutableMapOf<Position, Long>()
    var totalPaths = 0L

    fun visitSplitter(position: Position, paths: Long) {
        if (!visited.add(position)) return

        val leftBeam = position.nextBy(Direction.LEFT)
        if (visited.add(leftBeam)) beams.addLast(leftBeam)
        pathsToPosition[leftBeam] = pathsToPosition.getOrDefault(leftBeam, 0) + paths

        val rightBeam = position.nextBy(Direction.RIGHT)
        if (visited.add(rightBeam)) beams.addLast(rightBeam)
        pathsToPosition[rightBeam] = pathsToPosition.getOrDefault(rightBeam, 0) + paths
    }

    val start = input.valuePositions { it == 'S' }.first()
    beams.addLast(start)
    pathsToPosition[start] = 1
    while (beams.isNotEmpty()) {
        val position = beams.removeFirst()
        val paths = pathsToPosition.getValue(position)
        val nextPosition = position.moveBy(Direction.DOWN, steps = 2)
        when (input.getOrNull(nextPosition)) {
            '.' -> {
                if (visited.add(nextPosition)) beams.addLast(nextPosition)
                pathsToPosition[nextPosition] = pathsToPosition.getOrDefault(nextPosition, 0) + paths
            }

            '^' -> visitSplitter(nextPosition, paths)
            null -> totalPaths += paths
        }
    }

    return totalPaths
}

private fun readInput(name: String) = readMatrix(name)