typealias Forest = List<List<Int>>

fun main() {
    val testInput = readInput("Day08_test")
    val input = readInput("Day08")

    "Part 1" {
        part1(testInput) shouldBe 21
        answer(part1(input))
    }

    "Part 2" {
        part2(testInput) shouldBe 8
        answer(part2(input))
    }
}

private fun part1(forest: Forest): Int {
    val n = forest.size
    val m = forest[0].size

    /** Checks the tree at position [r],[c] is visible from the given [direction]. */
    fun isVisibleFromOutside(r: Int, c: Int, direction: Direction): Boolean {
        val treeHeight = forest[r][c]
        return forest.treesByDirection(r, c, direction).all { it < treeHeight }
    }

    // All trees on edges are visible
    var count = (n + m - 2) * 2

    for (r in 1 until n - 1) {
        for (c in 1 until m - 1) {
            if (Direction.values().any { isVisibleFromOutside(r, c, it) }) count++
        }
    }

    return count
}

private fun part2(forest: Forest): Int {
    val n = forest.size
    val m = forest[0].size

    /** Counts trees visible from tree at position [r],[c] in the given [direction]. */
    fun countVisibleTrees(r: Int, c: Int, direction: Direction): Int {
        val targetTree = forest[r][c]
        var count = 0
        for (currentTree in forest.treesByDirection(r, c, direction)) {
            count++
            if (currentTree >= targetTree) return count
        }
        return count
    }

    var maxScore = 0

    // All trees on edges will have score 0, so skip it
    for (r in 1 until n - 1) {
        for (c in 1 until m - 1) {
            val score = Direction.values().map { countVisibleTrees(r, c, it) }.reduce(Int::times)
            maxScore = maxOf(maxScore, score)
        }
    }

    return maxScore
}

/** Generates sequence of trees heights for the given [direction] in order of distance from [r],[c]. */
private fun Forest.treesByDirection(r: Int, c: Int, direction: Direction): Sequence<Int> {
    return generateSequence(direction.next(r to c), direction::next)
        .takeWhile { (r, c) -> r in indices && c in first().indices }
        .map { (r, c) -> this[r][c] }
}

// dr, dc - delta row, delta column
private enum class Direction(val dr: Int = 0, val dc: Int = 0) {
    UP(dr = -1),
    LEFT(dc = -1),
    DOWN(dr = +1),
    RIGHT(dc = +1);

    /** Returns the next coordinates on this direction. */
    fun next(coordinates: Pair<Int, Int>): Pair<Int, Int> {
        val (r, c) = coordinates
        return (r + dr) to (c + dc)
    }
}

private fun readInput(name: String) = readLines(name).map { it.map(Char::digitToInt) }
