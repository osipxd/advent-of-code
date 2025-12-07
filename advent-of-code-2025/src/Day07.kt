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

private fun part1(input: Diagram): Int = input.simulate().visitedSplitters
private fun part2(input: Diagram): Long = input.simulate().totalPaths

@JvmInline
private value class Diagram(private val matrix: Matrix<Char>) {

    fun simulate(): SimulationResult {
        val beams = ArrayDeque<Position>()
        val visited = mutableSetOf<Position>()
        val pathsToPosition = mutableMapOf<Position, Long>()
        var visitedSplitters = 0
        var totalPaths = 0L

        fun addBeam(position: Position, paths: Long) {
            if (visited.add(position)) beams.addLast(position)
            pathsToPosition.merge(position, paths, Long::plus)
        }

        val start = matrix.valuePositions { it == 'S' }.first()
        addBeam(start, 1)

        while (beams.isNotEmpty()) {
            val beam = beams.removeFirst()
            val paths = pathsToPosition.getValue(beam)
            val downwardPosition = beam.moveBy(Direction.DOWN, steps = 2)

            when (matrix.getOrNull(downwardPosition)) {
                '.' -> addBeam(downwardPosition, paths)
                '^' -> {
                    addBeam(downwardPosition.nextBy(Direction.LEFT), paths)
                    addBeam(downwardPosition.nextBy(Direction.RIGHT), paths)
                    visitedSplitters++
                }
                null -> totalPaths += paths
            }
        }

        return SimulationResult(visitedSplitters, totalPaths)
    }
}

private data class SimulationResult(
    val visitedSplitters: Int,
    val totalPaths: Long,
)

private fun readInput(name: String) = Diagram(readMatrix(name))