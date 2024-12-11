private const val DAY = "Day11"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 55312
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput()) shouldBe 65601038650482
        measureAnswer { part2(input()) }
    }
}

private fun part1(input: List<Long>): Long = countStonesAfterBlinks(input, totalBlinks = 25)
private fun part2(input: List<Long>): Long = countStonesAfterBlinks(input, totalBlinks = 75)

private fun countStonesAfterBlinks(stones: List<Long>, totalBlinks: Int): Long {
    val memory = mutableMapOf<Pair<Long, Int>, Long>()

    fun count(stone: Long, blinksLeft: Int): Long = memory.getOrPut(stone to blinksLeft) {
        if (blinksLeft == 0) return 1

        when {
            stone == 0L -> count(1, blinksLeft - 1)
            stone.countDigits() % 2 == 0 -> {
                val (left, right) = stone.splitDigits()
                count(left, blinksLeft - 1) + count(right, blinksLeft - 1)
            }

            else -> count(stone * 2024, blinksLeft - 1)
        }
    }

    return stones.sumOf { count(it, totalBlinks) }
}

private fun readInput(name: String) = readText(name).splitLongs()

// Utils

private fun Long.countDigits() = toString().length

private fun Long.splitDigits(): Pair<Long, Long> {
    val value = toString()
    val half = value.length / 2
    return value.take(half).toLong() to value.takeLast(half).toLong()
}
