import CubeSide.*

fun main() {
    val (testMap, testPath) = readInput("Day22_test")
    val (map, path) = readInput("Day22")

//    "Part 1" {
//        part1(testMap, testPath) shouldBe 6032
//        measureAnswer { part1(map, path) }
//    }

    "Part 2" {
//        part2(testMap, testPath) shouldBe 5031
        measureAnswer { part2(map, path) }
    }
}

private fun part1(map: Map, path: List<Step>): Int {
    map.reset()
    path.forEach { step ->
        println(step)
        map.applyStep(step)
    }

    return map.password()
}

private fun part2(map: Map, path: List<Step>): Int {
    map.reset()
    path.forEach { step ->
        println(step)
        map.applyStep(step)
    }

    return map.password()
}

private fun readInput(name: String): Pair<Map, List<Step>> {
    val (rawMap, rawPath) = readText(name).split("\n\n")

    val path = buildList {
        val buffer = StringBuilder()
        fun flush() {
            if (buffer.isNotEmpty()) {
                add(Step.Forward(buffer.toString().toInt()))
                buffer.clear()
            }
        }

        for (char in rawPath) {
            if (char.isDigit()) {
                buffer.append(char)
            } else {
                flush()
                add(Step.Rotate(char))
            }
        }
        flush()
    }

    return Map(rawMap.lines()) to path
}

private class Map(
    val internal: List<String>
) {

    private val cubeSideNeighbors = mapOf(
        (TOP to DIRECTION_RIGHT) to (RIGHT to DIRECTION_RIGHT),
        (TOP to DIRECTION_DOWN) to (FRONT to DIRECTION_DOWN),
        (TOP to DIRECTION_LEFT) to (LEFT to DIRECTION_RIGHT),
        (TOP to DIRECTION_UP) to (BACK to DIRECTION_RIGHT),

        (RIGHT to DIRECTION_RIGHT) to (BOTTOM to DIRECTION_LEFT),
        (RIGHT to DIRECTION_DOWN) to (FRONT to DIRECTION_LEFT),
        (RIGHT to DIRECTION_LEFT) to (TOP to DIRECTION_LEFT),
        (RIGHT to DIRECTION_UP) to (BACK to DIRECTION_UP),

        (FRONT to DIRECTION_RIGHT) to (RIGHT to DIRECTION_UP),
        (FRONT to DIRECTION_DOWN) to (BOTTOM to DIRECTION_DOWN),
        (FRONT to DIRECTION_LEFT) to (LEFT to DIRECTION_DOWN),
        (FRONT to DIRECTION_UP) to (TOP to DIRECTION_UP),

        (LEFT to DIRECTION_RIGHT) to (BOTTOM to DIRECTION_RIGHT),
        (LEFT to DIRECTION_DOWN) to (BACK to DIRECTION_DOWN),
        (LEFT to DIRECTION_LEFT) to (TOP to DIRECTION_RIGHT),
        (LEFT to DIRECTION_UP) to (FRONT to DIRECTION_RIGHT),

        (BOTTOM to DIRECTION_RIGHT) to (RIGHT to DIRECTION_LEFT),
        (BOTTOM to DIRECTION_DOWN) to (BACK to DIRECTION_LEFT),
        (BOTTOM to DIRECTION_LEFT) to (LEFT to DIRECTION_LEFT),
        (BOTTOM to DIRECTION_UP) to (FRONT to DIRECTION_UP),

        (BACK to DIRECTION_RIGHT) to (BOTTOM to DIRECTION_UP),
        (BACK to DIRECTION_DOWN) to (RIGHT to DIRECTION_DOWN),
        (BACK to DIRECTION_LEFT) to (TOP to DIRECTION_DOWN),
        (BACK to DIRECTION_UP) to (LEFT to DIRECTION_UP),
    )

    val cubeSize = internal.size / 4
    val cubeLastIndex = cubeSize - 1
    val cubeIndices = 0..cubeLastIndex

    val row get() = row(cubeRow)
    val col get() = col(cubeCol)

    var cubeRow = 0
    var cubeCol = 0
    var cubeSide = TOP
    var direction = DIRECTION_RIGHT

    init {
        reset()
    }

    fun reset() {
        cubeRow = 0
        cubeCol = 0
        cubeSide = TOP
        direction = DIRECTION_RIGHT
    }

    fun password() = 1000 * (row + 1) + 4 * (col + 1) + direction

    fun applyStep(step: Step) {
        when (step) {
            is Step.Rotate -> rotate(step.directionChange)
            is Step.Forward -> goForward(step.distance)
        }
    }

    private fun rotate(directionChange: Int) {
        direction = (direction + directionChange).mod(directions.size)
    }

    private fun goForward(distance: Int) {
        println("from ${row + 1}, ${col + 1} forward $distance in $direction")
        for (i in 1..distance) {
            if (!stepInDirection()) break
        }
    }

    private fun stepInDirection(): Boolean {
        val (dr, dc) = directions[direction]

        var nextRow = cubeRow + dr
        var nextCol = cubeCol + dc
        if (nextRow in cubeIndices && nextCol in cubeIndices) {
            println("$cubeSide $nextRow,$nextCol")
            println("$cubeSide ${row(nextRow)},${col(nextCol)} (${internal.size}x${internal[row(nextRow)].length})")
            return if (internal[row(nextRow)][col(nextCol)] == AIR) {
                cubeRow = nextRow
                cubeCol = nextCol
                true
            } else {
                false
            }
        }

        val (newSide, newDirection) = cubeSideNeighbors.getValue(cubeSide to direction)
        val wrapped = wrappedCoordinates(cubeRow, cubeCol, newDirection)
        nextRow = wrapped.first
        nextCol = wrapped.second

        return if (internal[row(nextRow, newSide)][col(nextCol, newSide)] == AIR) {
            println("$cubeRow,$cubeCol ($cubeSide) -> $nextRow,$nextCol ($newSide)")

            cubeRow = nextRow
            cubeCol = nextCol
            cubeSide = newSide
            direction = newDirection
            true
        } else {
            false
        }
    }

    private fun wrappedCoordinates(r: Int, c: Int, newDirection: Int): Pair<Int, Int> {
        var wrappedR = 0
        var wrappedC = 0

        // 1. First coordinate. Simply set first row/col for the new direction
        when (newDirection) {
            DIRECTION_RIGHT -> wrappedC = 0
            DIRECTION_DOWN -> wrappedR = 0
            DIRECTION_LEFT -> wrappedC = cubeLastIndex
            DIRECTION_UP -> wrappedR = cubeLastIndex
        }

        // 2. Second coordinate. Tricky logic a bit
        val orientation = direction.orientation
        val newOrientation = newDirection.orientation

        when {
            // Direction not changed, so just use the same row/col depending on orientation
            direction == newDirection -> when (orientation) {
                ORIENTATION_HORIZONTAL -> wrappedR = r
                ORIENTATION_VERTICAL -> wrappedC = c
            }

            // Direction changed, but orientation the same. Let's invert row/col
            orientation == newOrientation -> when (orientation) {
                ORIENTATION_HORIZONTAL -> wrappedR = inverted(r)
                ORIENTATION_VERTICAL -> wrappedC = inverted(c)
            }

            // Orientation changed. Let's use row for col or col for row depending on the orientation
            else -> {
                // Direction pairs when we should NOT invert coordinate are:
                //    DOWN (1) <-> LEFT (2), sum = 3
                //   RIGHT (0) <-> UP   (3), sum = 3
                val invert = direction + newDirection != 3
                // Whether should we set row or column? Depends on the orientation
                when (newOrientation) {
                    ORIENTATION_VERTICAL -> wrappedC = (if (invert) inverted(r) else r)
                    ORIENTATION_HORIZONTAL -> wrappedR = (if (invert) inverted(c) else c)
                }
            }
        }

        return wrappedR to wrappedC
    }

    /** Returns inverted coordinate, starting from the end of the cube. */
    private fun inverted(coordinate: Int) = cubeLastIndex - coordinate

    private fun row(cubeRow: Int, side: CubeSide = cubeSide) = side.row * cubeSize + cubeRow
    private fun col(cubeCol: Int, side: CubeSide = cubeSide) = side.col * cubeSize + cubeCol

    companion object {
        const val AIR = '.'

        const val DIRECTION_RIGHT = 0
        const val DIRECTION_DOWN = 1
        const val DIRECTION_LEFT = 2
        const val DIRECTION_UP = 3

        const val ORIENTATION_HORIZONTAL = 0
        const val ORIENTATION_VERTICAL = 1

        // RIGHT (0), LEFT (2) -> HORIZONTAL (0)
        // DOWN (1), UP (3) -> VERTICAL (3)
        val Int.orientation: Int get() = this % 2

        val directions = listOf(
            0 to +1,
            +1 to 0,
            0 to -1,
            -1 to 0,
        )
    }
}

private enum class CubeSide(val row: Int, val col: Int) {
    TOP(row = 0, col = 1),
    RIGHT(row = 0, col = 2),
    FRONT(row = 1, col = 1),
    LEFT(row = 2, col = 0),
    BOTTOM(row = 2, col = 1),
    BACK(row = 3, col = 0),
}

private sealed interface Step {
    data class Forward(val distance: Int) : Step
    data class Rotate(val direction: Char) : Step {
        val directionChange get() = if (direction == 'R') +1 else -1
    }
}
