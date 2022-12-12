fun main() {
    "Part 1" {
        val testInput = readInput("Day12_test")
        val input = readInput("Day12")
        part1(testInput) shouldBe 31
        answer(part1(input))
    }

    "Part 2" {
        val testInput = readInput("Day12_test")
        val input = readInput("Day12")
        part2(testInput) shouldBe 29
        answer(part2(input))
    }
}

private fun part1(field: Array<CharArray>): Int = climbDown(field)
private fun part2(field: Array<CharArray>): Int = climbDown(field, shouldStop = { it == 'a' })

// Time - O(N*M), Space - O(N*M)
//   where, N - number of rows, M - number of columns
private fun climbDown(field: Array<CharArray>, shouldStop: (Char) -> Boolean = { false }): Int {
    // Remember start and end position and then replace it with 'a' and 'z'
    val (startR, startC) = field.findPositionOf('S')
    field[startR][startC] = 'a'

    val (endR, endC) = field.findPositionOf('E')
    field[endR][endC] = 'z'

    // Record shortest distance to each
    val distance = mutableMapOf((endR to endC) to 0)

    // Here we will place blocks which we climbed down to.
    // Yes, we climb down instead of climb up - it is more suitable for Part 2 solution
    val queue = ArrayDeque<Pair<Int, Int>>()
    queue += endR to endC

    // And then, for each block we found, search the next block possible to step down
    while (queue.isNotEmpty()) {
        val (r, c) = queue.removeFirst()

        // We found the shortest path to the start point. Stop
        if (r == startR && c == startC) break

        // Check if we should stop earlier
        val char = field[r][c]
        if (shouldStop(char)) return distance.getValue(r to c)

        // For each neighbor
        for (neighbor in field.neighbors(r, c)) {
            // Check if we can climb down to the neighbor block
            //  or, if this neighbor was not already checked before
            val (neighborR, neighborC) = neighbor
            if (char > field[neighborR][neighborC] + 1 || neighbor in distance) continue

            // Record distance to this neighbor
            distance[neighbor] = distance.getValue(r to c) + 1
            // and add it to the queue to check its neighbors
            queue.addLast(neighbor)
        }
    }

    return distance.getValue(startR to startC)
}

// Search the given char position for O(N*M) time
private fun Array<CharArray>.findPositionOf(char: Char): Pair<Int, Int> {
    for (r in indices) {
        for (c in first().indices) {
            if (this[r][c] == char) return r to c
        }
    }

    error("Given char not found!")
}

/** Creates sequence of neighbors around the given [r], [c] */
private fun Array<CharArray>.neighbors(r: Int, c: Int) = sequence {
    if (r > 0) yield(r - 1 to c)
    if (r < lastIndex) yield(r + 1 to c)
    if (c > 0) yield(r to c - 1)
    if (c < first().lastIndex) yield(r to c + 1)
}

private fun readInput(name: String) = readLines(name).map { it.toCharArray() }.toTypedArray()
