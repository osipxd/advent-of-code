fun main() {
    val (testBlizzards, testSize) = readInput("Day24_test")
    val (inputBlizzards, inputSize) = readInput("Day24")

    "Part 1" {
        part1(testBlizzards, testSize) shouldBe 18
        measureAnswer { part1(inputBlizzards, inputSize) }
    }

    "Part 2" {
        part2(testBlizzards, testSize) shouldBe 54
        measureAnswer { part2(inputBlizzards, inputSize) }
    }
}

private fun part1(blizzards: Map<Pair<Int, Int>, Blizzard>, size: Pair<Int, Int>): Int {
    val (height, width) = size
    val valley = BlizzardBasin(blizzards, height, width)
    return valley.findShortestPathTo(targetR = height, targetC = width - 1)
}

private fun part2(blizzards: Map<Pair<Int, Int>, Blizzard>, size: Pair<Int, Int>): Int {
    val (height, width) = size
    val valley = BlizzardBasin(blizzards, height, width)
    return valley.findShortestPathTo(targetR = height, targetC = width - 1) +
        valley.findShortestPathTo(targetR = -1, targetC = 0) +
        valley.findShortestPathTo(targetR = height, targetC = width - 1)
}

private class BlizzardBasin(
    blizzards: Map<Pair<Int, Int>, Blizzard>,
    private val height: Int,
    private val width: Int
) {

    private var blizzardsPositions = blizzards.mapValues { (_, blizzard) -> listOf(blizzard) }

    private var positionR = -1
    private var positionC = 0

    fun findShortestPathTo(targetR: Int, targetC: Int): Int {
        val queue = ArrayDeque<Step>()
        queue.addLast(Step(r = positionR, c = positionC, turn = 0))

        val seen = mutableSetOf<Step>()
        fun addNextStep(step: Step) {
            if (step in seen) return
            queue.addLast(step)
            seen += step
        }

        var currentTurn = 0
        while (queue.isNotEmpty()) {
            val (r, c, turn) = queue.removeFirst()

            // Should we move blizzards?
            if (currentTurn != turn) {
                currentTurn = turn
                moveBlizzards()
            }

            // We can not share the same position with blizzards
            if (r to c in blizzardsPositions) continue

            // If current position is the target, return it
            if (r == targetR && c == targetC) {
                positionR = r
                positionC = c
                return turn
            }

            // Try to wait
            addNextStep(Step(r, c, turn + 1))

            // Or go to one of neighbors
            for ((nr, nc) in neighborsOf(r, c)) {
                addNextStep(Step(nr, nc, turn + 1))
            }
        }

        error("No way!")
    }

    private fun moveBlizzards() {
        blizzardsPositions = buildMap {
            for ((position, blizzards) in blizzardsPositions) {
                val (r, c) = position
                for (blizzard in blizzards) {
                    val newPosition = blizzard.nextPosition(r, c)
                    put(newPosition, getOrDefault(newPosition, emptyList()) + blizzard)
                }
            }
        }
    }

    fun Blizzard.nextPosition(r: Int, c: Int): Pair<Int, Int> {
        return (r + dr).mod(height) to (c + dc).mod(width)
    }

    private fun neighborsOf(r: Int, c: Int): Sequence<Pair<Int, Int>> {
        if (r == -1 && c == 0) return sequenceOf(0 to 0)
        if (r == height && c == width - 1) return sequenceOf(height - 1 to width - 1)

        return sequence {
            if (r > 0) yield(r - 1 to c)
            if (r < height - 1) yield(r + 1 to c)
            if (c > 0) yield(r to c - 1)
            if (c < width - 1) yield(r to c + 1)

            if (r == 0 && c == 0) yield(-1 to 0)
            if (r == height - 1 && c == width - 1) yield(height to width - 1)
        }
    }

    private data class Step(val r: Int, val c: Int, val turn: Int)
}

private fun readInput(name: String): Pair<Map<Pair<Int, Int>, Blizzard>, Pair<Int, Int>> {
    val lines = readLines(name)
    val blizzards = mutableMapOf<Pair<Int, Int>, Blizzard>()

    for (r in 1 until lines.lastIndex) {
        for (c in 1 until lines[r].lastIndex) {
            val blizzard = Blizzard.of(lines[r][c])
            if (blizzard != null) blizzards[r - 1 to c - 1] = blizzard
        }
    }

    return blizzards to (lines.size - 2 to lines[0].length - 2)
}

private enum class Blizzard(val symbol: Char, val dr: Int, val dc: Int) {
    UP(symbol = '^', dr = -1, dc = 0),
    DOWN(symbol = 'v', dr = +1, dc = 0),
    LEFT(symbol = '<', dr = 0, dc = -1),
    RIGHT(symbol = '>', dr = 0, dc = +1);

    companion object {
        fun of(symbol: Char): Blizzard? = values().find { it.symbol == symbol }
    }
}
