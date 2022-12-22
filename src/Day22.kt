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

    private fun cubeSide(r: Int, c: Int): Int {
        return when {
            r < cubeSize -> 1
            r in cubeSize until (cubeSize * 2) -> 1 + c / cubeSize
            else -> 4 + (c - (cubeSize * 2)) / cubeSize
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
        when (direction) {
            newDirection -> {
                nextRow = nextRow.mod(cubeSize)
                nextCol = nextCol.mod(cubeSize)
            }

            DIRECTION_RIGHT -> when (newDirection) {
                DIRECTION_LEFT -> {
                    nextRow = cubeLastIndex - cubeRow
                    nextCol = cubeLastIndex
                }

                DIRECTION_DOWN -> {
                    nextRow = 0
                    nextCol = cubeLastIndex - cubeRow
                }

                DIRECTION_UP -> {
                    nextRow = cubeLastIndex
                    nextCol = cubeRow
                }

                else -> error("Unexpected move $cubeSide -> $newSide")
            }

            DIRECTION_DOWN -> when (newDirection) {
                DIRECTION_UP -> {
                    nextRow = cubeLastIndex
                    nextCol = cubeLastIndex - cubeCol
                }

                DIRECTION_RIGHT -> {
                    nextRow = cubeLastIndex - cubeCol
                    nextCol = 0
                }

                DIRECTION_LEFT -> {
                    nextRow = cubeCol
                    nextCol = cubeLastIndex
                }

                else -> error("Unexpected move $cubeSide -> $newSide")
            }

            DIRECTION_LEFT -> when (newDirection) {
                DIRECTION_DOWN -> {
                    nextRow = 0
                    nextCol = cubeRow
                }

                DIRECTION_UP -> {
                    nextRow = cubeLastIndex
                    nextCol = cubeLastIndex - cubeRow
                }

                DIRECTION_RIGHT -> {
                    nextRow = cubeLastIndex - cubeRow
                    nextCol = 0
                }

                else -> error("Unexpected move $cubeSide -> $newSide")
            }

            DIRECTION_UP -> when (newDirection) {
                DIRECTION_DOWN -> {
                    nextRow = 0
                    nextCol = cubeLastIndex - cubeCol
                }

                DIRECTION_RIGHT -> {
                    nextRow = cubeCol
                    nextCol = 0
                }

                DIRECTION_LEFT -> {
                    nextRow = cubeLastIndex - cubeCol
                    nextCol = cubeLastIndex
                }

                else -> error("Unexpected move $cubeSide -> $newSide")
            }
        }

        println(internal[row(nextRow, newSide)].length)
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

    private fun row(cubeRow: Int, side: CubeSide = cubeSide) = side.row * cubeSize + cubeRow
    private fun col(cubeCol: Int, side: CubeSide = cubeSide) = side.col * cubeSize + cubeCol

    companion object {
        const val AIR = '.'
        const val WALL = '#'
        const val VOID = ' '

        const val DIRECTION_RIGHT = 0
        const val DIRECTION_DOWN = 1
        const val DIRECTION_LEFT = 2
        const val DIRECTION_UP = 3

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
