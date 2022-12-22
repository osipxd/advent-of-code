fun main() {
    val (testMap, testPath) = readInput("Day22_test")
    val (map, path) = readInput("Day22")

    "Part 1" {
        part1(testMap, testPath) shouldBe 6032
        measureAnswer { part1(map, path) }
    }

    //"Part 2" {
    //    part2(testInput) shouldBe 5031
    //    measureAnswer { part2(input) }
    //}
}

private fun part1(map: Map, path: List<Step>): Int {
    map.reset()
    path.forEach { step ->
        println(step)
        map.applyStep(step)
    }

    return map.password()
}

private fun part2(input: List<String>): Int {
    TODO()
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
    val height = internal.size
    val width = internal[0].length

    var row = 0
    var col = 0
    var direction = 0

    init { reset() }

    fun reset() {
        row = 0
        col = internal[0].indexOfFirst { it == AIR }
        direction = 0
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
        println("$distance from $row, $col")
        for (i in 1..distance) {
            val (r, c) = nextInDirection()
            if (internal[r][c] == AIR) {
                row = r
                col = c
            } else {
                break
            }
        }
    }

    private fun nextInDirection(): Pair<Int, Int> {
        val (dr, dc) = directions[direction]

        var nextRow = row
        var nextCol = col

        if (dc != 0) {
            do {
                nextCol = (nextCol + dc).mod(internal[row].length)
            } while (internal[row][nextCol] == VOID)
        } else {
            do {
                nextRow = (nextRow + dr).mod(internal.size)
            } while (col > internal[nextRow].lastIndex || internal[nextRow][col] == VOID)
        }

        return nextRow to nextCol
    }

    companion object {
        const val AIR = '.'
        const val WALL = '#'
        const val VOID = ' '

        val directions = listOf(
            0 to +1,
            +1 to 0,
            0 to -1,
            -1 to 0,
        )
    }
}

private sealed interface Step {
    data class Forward(val distance: Int) : Step
    data class Rotate(val direction: Char) : Step {
        val directionChange get() = if (direction == 'R') +1 else -1
    }
}
