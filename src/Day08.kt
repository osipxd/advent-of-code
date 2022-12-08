fun main() {
    val testInput = readInput("Day08_test")
    val input = readInput("Day08")

    check(part1(testInput) == 21)
    println("Part 1: " + part1(input))

    check(part2(testInput) == 8)
    println("Part 2: " + part2(input))
}

private fun part1(input: List<String>): Int {
    val n = input.size
    val m = input[0].length
    var count = (n + m - 2) * 2

    fun visibleFrom(r: Int, c: Int, dr: Int, dc: Int): Boolean {
        val value = input[r][c].digitToInt()
        var curR = r + dr
        var curC = c + dc
        while (curR in input.indices && curC in input[0].indices) {
            if (input[curR][curC].digitToInt() >= value) return false
            curR += dr
            curC += dc
        }

        return true
    }

    val seen = mutableSetOf<Pair<Int, Int>>()
    for (r in 1 until n - 1) {
        for (c in 1 until m - 1) {
            if (visibleFrom(r, c, 0, +1) ||
                visibleFrom(r, c, +1, 0) ||
                visibleFrom(r, c, 0, -1) ||
                visibleFrom(r, c, -1, 0)
            ) {
                if (seen.add(r to c)) count += 1
            }
        }
    }

    return count
}

private fun part2(input: List<String>): Int {
    val n = input.size
    val m = input[0].length

    var maxScore = 0

    fun countTrees(r: Int, c: Int, dr: Int, dc: Int): Int {
        val value = input[r][c].digitToInt()
        var curR = r + dr
        var curC = c + dc
        var score = 0
        while (curR in input.indices && curC in input[0].indices) {
            if (input[curR][curC].digitToInt() >= value) return score + 1
            curR += dr
            curC += dc
            score += 1
        }

        return score
    }

    for (r in 1 until n - 1) {
        for (c in 1 until m - 1) {
            val score =
                countTrees(r, c, 0, +1) * countTrees(r, c, +1, 0) * countTrees(r, c, 0, -1) * countTrees(r, c, -1, 0)
            maxScore = maxOf(maxScore, score)
        }
    }

    return maxScore
}

private fun readInput(name: String) = readLines(name)
