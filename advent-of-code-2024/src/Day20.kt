import lib.matrix.*

private const val DAY = "Day20"

private typealias Racetrack = Matrix<Char>
private typealias FairPath = LinkedHashMap<Position, Int>

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 44
        measureAnswer { part1(input(), minimalCheatBonus = 100) }
    }

    //"Part 2" {
    //    part2(testInput()) shouldBe 0
    //    measureAnswer { part2(input()) }
    //}
}

private fun part1(map: Racetrack, minimalCheatBonus: Int = 0): Int {
    val path = runFairly(map)
    return path.keys.sumOf { position ->
        map.calculateCheatBonuses(path, position).count { it >= minimalCheatBonus }
    }
}

private fun part2(map: Racetrack): Int = TODO()

private fun runFairly(map: Racetrack): FairPath {
    val start = map.valuePositions { it == 'S' }.first()
    val path = linkedMapOf<Position, Int>()

    fun addNext(position: Position, steps: Int) {
        path[position] = steps
    }

    addNext(start, steps = 0)
    while (map[path.lastEntry().key] != 'E') {
        val (position, steps) = path.lastEntry()
        val nextPosition = position.neighbors { it !in path && map[it] != '#' }.single()
        addNext(nextPosition, steps = steps + 1)
    }

    return path
}

private fun Racetrack.calculateCheatBonuses(path: FairPath, position: Position, steps: Int = 2): Sequence<Int> {
    return position.neighbors { get(it) == '#' }
        .map { wallPosition ->
            val vector = (position to wallPosition)
            position + vector * steps
        }
        .filter { it in path }
        .map { positionAfterCheat -> path.getValue(positionAfterCheat) - path.getValue(position) - steps }
}

private fun readInput(name: String): Racetrack = readMatrix(name)

// Utils

private fun Position.neighbors(condition: (Position) -> Boolean) =
    Direction.orthogonal.asSequence().map(::nextBy).filter(condition)

private infix fun Position.to(other: Position): MatrixVector =
    MatrixVector(other.row - this.row, other.column - this.column)
