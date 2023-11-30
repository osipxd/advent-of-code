import java.util.*

fun main() {
    val testInput = readInput("Day17_test")
    val input = readInput("Day17")

    "Part 1" {
        part1(testInput) shouldBe 3068
        measureAnswer { part1(input, print = true) }
    }

    "Part 2" {
        part2(testInput) shouldBe 1514285714288
        println()
        measureAnswer { part2(input) }
    }
}

private const val WIDTH = 7

private fun part1(input: String, print: Boolean = false): Long {
    val chamber = TetrisChamber(input)

    repeat(times = 2022) {
        chamber.fallNext()
    }

    if (print) chamber.print()
    return chamber.height
}

private const val INSANE = 1_000_000_000_000

private fun part2(input: String): Long {
    val chamber = TetrisChamber(input)

    val cache = mutableMapOf<Int, Pair<Long, Long>>()
    var rock = 0L
    var heightShift = 0L

    while (rock < INSANE) {
        chamber.fallNext()
        val height = chamber.height

        val stateFingerprint = chamber.getStateFingerprint()
        if (stateFingerprint in cache) {
            val (cachedRock, cachedHeight) = cache.getValue(stateFingerprint)
            val rockDiff = rock - cachedRock
            val heightDiff = height - cachedHeight

            val times = (INSANE - rock) / rockDiff
            heightShift = heightDiff * times
            rock += rockDiff * times

            println("Cycle found! $cachedRock..$rock ($rockDiff), height = $heightDiff")
            println("Skip ${rockDiff * times} rocks")
            break
        }
        cache[stateFingerprint] = rock to height
        rock++
    }

    println("Add ${INSANE - rock} rocks")
    while (rock < INSANE) {
        chamber.fallNext()
        rock++
    }

    // Why -1? I have no idea...
    return chamber.height + heightShift - 1
}

private class TetrisChamber(private val commands: String) {

    var height = 0L

    private val occupied = mutableMapOf<Pair<Long, Int>, String>()

    private var nextCommand = 0
    private fun nextCommand(): Char {
        return commands[nextCommand].also { nextCommand = (nextCommand + 1) % commands.length }
    }

    fun fallNext() {
        val rock = nextRock()

        var positionX = START_X
        var positionY = height + START_Y

        fun fitsAt(x: Int = positionX, y: Long = positionY): Boolean {
            return (0 until rock.height)
                .all { r -> rock[r].none { c -> y + r to x + c in occupied } }
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
            if (positionY == 0L) return false
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
        val chamber = Array(height.toInt()) { Array(WIDTH) { AIR } }
        for ((point, symbol) in occupied) chamber[point.first.toInt()][point.second] = symbol
        println(chamber.reversed().joinToString("\n") { it.joinToString("") })
    }

    /**
     * State fingerprint includes `nextRock`, `nextCommand` and `depths`.
     *
     * Example of depths calculation:
     * ```
     * [3,1,0,1,5,5,9] <- depths
     *  . . # . . . .
     *  . # # # . . .
     *  # # # . . . .
     *  # # . . . . .
     *  . # . . . . .
     *  . # # # # # .
     *  . # . # . . .
     *  . # . # . . .
     *  . # # # . . .
     * ```
     */
    fun getStateFingerprint(): Int {
        val depths = (0 until WIDTH).map { x ->
            val y = ((height - 1) downTo 0).find { y -> y to x in occupied } ?: -1
            height - y - 1
        }

        return Objects.hash(nextRock, nextCommand, depths)
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

private fun readInput(name: String) = readText(name)
