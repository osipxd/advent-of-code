fun main() {
    val testInput = readInput("Day17_test")
    val input = readInput("Day17")

    "Part 1" {
        part1(testInput) shouldBe 3068
        measureAnswer { part1(input, print = true) }
    }

    //"Part 2" {
    //    part2(testInput) shouldBe 0
    //    measureAnswer { part2(input) }
    //}
}

private const val WIDTH = 7

private fun part1(input: String, print: Boolean = false): Int {
    val chamber = TetrisChamber(input)

    repeat(2022) {
        chamber.fallNext()
    }

    if (print) chamber.print()
    return chamber.height
}

private class TetrisChamber(private val commands: String) {

    var height = 0

    private val occupied = mutableMapOf<Pair<Int, Int>, String>()

    private var nextCommand = 0
    private fun nextCommand(): Char {
        return commands[nextCommand].also { nextCommand = (nextCommand + 1) % commands.length }
    }

    fun fallNext() {
        val rock = nextRock()

        var positionX = START_X
        var positionY = height + START_Y

        fun fitsAt(x: Int = positionX, y: Int = positionY): Boolean {
            return (0 until rock.height).all { r -> rock[r].none {  c -> y + r to x + c in occupied } }
        }

        fun moveLeft() {
            if (positionX == 0) return
            if (fitsAt(x = positionX - 1)) positionX--
        }

        fun moveRight() {
            if (positionX == WIDTH - rock.width) return
            if (fitsAt(x = positionX + 1)) positionX++
        }

        fun moveDown(): Boolean {
            if (positionY == 0) return false
            return if (fitsAt(y = positionY - 1)) {
                positionY--
                true
            } else {
                false
            }
        }

        do {
            when (nextCommand()) {
                '<' -> moveLeft()
                '>' -> moveRight()
            }
        } while (moveDown())
        height = maxOf(height, positionY + rock.height)

        for (r in 0 until rock.height) {
            for (c in rock[r]) {
                occupied[positionY + r to positionX + c] = rock.symbol
            }
        }
    }

    private var nextRock = 0

    private fun nextRock(): Rock = Rock.rocks[nextRock].also {
        nextRock = (nextRock + 1) % Rock.rocks.size
    }

    fun print() {
        val chamber = Array(height) { Array(WIDTH) { AIR } }
        for ((point, symbol) in occupied) chamber[point.first][point.second] = symbol
        println(chamber.reversed().joinToString("\n") { it.joinToString("") })
    }

    companion object {
        private const val AIR = "⬜️️"

        private const val START_X = 2
        private const val START_Y = 3
    }
}

private class Rock(val symbol: String, vararg val lines: IntRange) {
    val height = lines.size
    val width = lines.maxOf { it.last } + 1

    operator fun get(i: Int) = lines[i]

    companion object {
        val rocks = listOf(
            // --
            Rock("🟥", 0..3),
            // +
            Rock("🟩", 1..1, 0..2, 1..1),
            // L
            Rock("🟨", 0..2, 2..2, 2..2),
            // |
            Rock("🟦", 0..0, 0..0, 0..0, 0..0),
            // ◼
            Rock("🟫", 0..1, 0..1),
        )
    }
}

private fun part2(input: String): Int = TODO()

private fun readInput(name: String) = readText(name)
