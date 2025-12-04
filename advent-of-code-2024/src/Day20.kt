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

    "Part 2" {
        part2(testInput()) shouldBe 3081
        measureAnswer { part2(input(), minimalCheatBonus = 100) }
    }
}

private fun part1(map: Racetrack, minimalCheatBonus: Int = 0): Int = solve(map, minimalCheatBonus, maxCheatSteps = 2)
private fun part2(map: Racetrack, minimalCheatBonus: Int = 0): Int = solve(map, minimalCheatBonus, maxCheatSteps = 20)

private fun solve(map: Racetrack, minimalCheatBonus: Int, maxCheatSteps: Int): Int {
    val path = runFairly(map)
    return path.keys.sumOf { position ->
        map.findCheats(path, position, maxCheatSteps).count { it >= minimalCheatBonus }
    }
}

private fun runFairly(map: Racetrack): FairPath {
    val start = map.valuePositions { it == 'S' }.first()
    val path = linkedMapOf<Position, Int>()

    fun addNext(position: Position, steps: Int) {
        path[position] = steps
    }

    addNext(start, steps = 0)
    while (map[path.lastEntry().key] != 'E') {
        val (position, steps) = path.lastEntry()
        val nextPosition = position.orthogonalNeighbors { it !in path && map[it] != '#' }.single()
        addNext(nextPosition, steps = steps + 1)
    }

    return path
}

private fun Racetrack.findCheats(path: FairPath, startPosition: Position, maxSteps: Int): Sequence<Int> = sequence {
    val seen = mutableSetOf<Position>()
    val queue = ArrayDeque<Pair<Position, Int>>()

    fun addNext(position: Position, steps: Int) {
        if (position in bounds && seen.add(position)) queue.addLast(position to steps)
    }

    addNext(startPosition, steps = 0)
    while (queue.isNotEmpty()) {
        val (position, steps) = queue.removeFirst()
        if (position in path) {
            val cheatBonus = path.getValue(position) - path.getValue(startPosition) - steps
            if (cheatBonus > 0) yield(cheatBonus)
        }

        if (steps < maxSteps) {
            position.orthogonalNeighbors().forEach { addNext(it, steps = steps + 1) }
        }
    }
}

private fun readInput(name: String): Racetrack = readMatrix(name)

// Utils

private fun Position.orthogonalNeighbors(condition: (Position) -> Boolean = { true }) =
    neighbors(Direction.orthogonal, condition)
