fun main() {
    val testInput = readInput("Day23_test")
    val input = readInput("Day23")

    "Part 1" {
        part1(testInput) shouldBe 110
        measureAnswer { part1(input) }
    }

    "Part 2" {
        part2(testInput) shouldBe 20
        measureAnswer { part2(input) }
    }
}

private fun part1(input: List<String>): Int {
    val map = GroundMap(input)

    repeat(10) { map.tick() }
    return map.countEmptyCells()
}

private fun part2(input: List<String>): Int {
    val map = GroundMap(input)

    var steps = 1
    while (map.tick()) steps++
    return steps
}

private class GroundMap(input: List<String>) {

    val elves = mutableSetOf<Pair<Int, Int>>()
    private var firstDirection = 0 // NORTH

    private val batch = mutableMapOf<Pair<Int, Int>, List<Pair<Int, Int>>>()

    init {
        for (r in input.indices) {
            for (c in input[0].indices) {
                if (input[r][c] == '#') elves.add(r to c)
            }
        }
    }

    fun tick(): Boolean {
        // 1. Propose directions
        for (elf in elves) {
            // Skip elves without neighbors
            if (neighbors(elf).none { it in elves }) continue

            val dest = directions(elf)
                .find { cells -> cells.none { it in elves } }
                ?.first()
            if (dest != null) batch[dest] = batch.getOrDefault(dest, emptyList()) + elf
        }

        // 2. Move
        var moved = false
        for ((dest, candidates) in batch) {
            if (candidates.size == 1) {
                elves.remove(candidates.single())
                elves.add(dest)
                moved = true
            }
        }

        // 3. Change direction, clear batch
        firstDirection = (firstDirection + 1) % 4
        batch.clear()

        return moved
    }

    private fun neighbors(position: Pair<Int, Int>): Sequence<Pair<Int, Int>> {
        val (r, c) = position
        return sequenceOf(-1 to 0, -1 to 1, 0 to 1, 1 to 1, 1 to 0, 1 to -1, 0 to -1, -1 to -1)
            .map { (dr, dc) -> (r + dr) to (c + dc) }
    }

    private fun directions(position: Pair<Int, Int>): Sequence<List<Pair<Int, Int>>> {
        val directions = listOf(
            listOf(-1 to 0, -1 to -1, -1 to +1), // NORTH
            listOf(+1 to 0, +1 to -1, +1 to +1), // SOUTH
            listOf(0 to -1, -1 to -1, +1 to -1), // WEST
            listOf(0 to +1, -1 to +1, +1 to +1), // EAST
        )

        return sequence {
            val (r, c) = position
            for (i in 0..3) {
                yield(directions[(firstDirection + i) % 4].map { (dr, dc) -> (r + dr) to (c + dc) })
            }
        }
    }

    fun countEmptyCells(): Int {
        val (minR, maxR, minC, maxC) = getRectangle()
        return (maxR - minR + 1) * (maxC - minC + 1) - elves.size
    }

    private fun debugPrint() {
        println("Direction: $firstDirection")
        val (minR, maxR, minC, maxC) = getRectangle()
        for (r in minR..maxR) {
            for (c in minC..maxC) {
                print(if (r to c in elves) "# " else ". ")
            }
            println()
        }
    }

    private fun getRectangle(): List<Int> {
        val minR = elves.minOf { it.first }
        val maxR = elves.maxOf { it.first }
        val minC = elves.minOf { it.second }
        val maxC = elves.maxOf { it.second }
        return listOf(minR, maxR, minC, maxC)
    }
}

private fun readInput(name: String) = readLines(name)
