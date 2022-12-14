fun main() {
    val testInput = readInput("Day14_test")
    val input = readInput("Day14")

    "Part 1" {
        part1(testInput) shouldBe 24
        answer(part1(input))
    }

    "Part 2" {
        part2(testInput) shouldBe 93
        answer(part2(input))
    }
}

private const val SHIFT = 0
private const val SIZE = 1000

private fun part1(input: List<List<List<Int>>>): Int {
    val map = Array(SIZE) { Array(SIZE) { '.' } }

    var lastRow = 0
    for (points in input) {
        points
            .onEach { lastRow = maxOf(lastRow, it[1]) }
            .windowed(2) { (from, to) ->
                for (c in (from[0]..to[0]).normalize()) {
                    for (r in (from[1]..to[1]).normalize()) {
                        map[r][c - SHIFT] = '#'
                    }
                }
            }
    }

    var fallen = 0
    val (fallR, fallC) = 0 to (500 - SHIFT)

    while (true) {
        fallen++
        var col = fallC
        var row = fallR
        while (row < lastRow) {
            when {
                map[row + 1][col] == '.' -> {
                    row++
                }

                map[row + 1][col - 1] == '.' -> {
                    row++
                    col--
                }

                map[row + 1][col + 1] == '.' -> {
                    row++
                    col++
                }

                else -> {
                    map[row][col] = 'o'
                    break
                }
            }
        }

        if (row >= lastRow) break
    }

//    println(map.joinToString("\n") { it.joinToString("") })
    return fallen - 1
}

private fun part2(input: List<List<List<Int>>>): Int {
    val map = Array(SIZE) { Array(SIZE) { '.' } }

    var lastRow = 0
    for (points in input) {
        points
            .onEach { lastRow = maxOf(lastRow, it[1]) }
            .windowed(2) { (from, to) ->
                for (c in (from[0]..to[0]).normalize()) {
                    for (r in (from[1]..to[1]).normalize()) {
                        map[r][c - SHIFT] = '#'
                    }
                }
            }
    }

    for (c in 0 until SIZE) map[lastRow + 2][c] = '#'
    lastRow += 2

    var fallen = 0
    val (fallR, fallC) = 0 to (500 - SHIFT)

    while (true) {
        fallen++
        if (map[fallR][fallC] != '.') break

        var col = fallC
        var row = fallR
        while (row < lastRow) {
            when {
                map[row + 1][col] == '.' -> {
                    row++
                }

                map[row + 1][col - 1] == '.' -> {
                    row++
                    col--
                }

                map[row + 1][col + 1] == '.' -> {
                    row++
                    col++
                }

                else -> {
                    map[row][col] = 'o'
                    break
                }
            }
        }

        if (row >= lastRow) break
    }

//    println(map.joinToString("\n") { it.joinToString("") })
    return fallen - 1
}

private fun IntRange.normalize() = if (start > endInclusive) endInclusive..start else this

private fun readInput(name: String) = readLines(name).map { line ->
    line.split(" -> ").map { it.split(",").map(String::toInt) }
}
