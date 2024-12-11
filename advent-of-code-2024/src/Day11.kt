private const val DAY = "Day11"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 55312
        measureAnswer { part1(input()) }
    }

    //"Part 2" {
    //    part2(testInput()) shouldBe 0
    //    measureAnswer { part2(input()) }
    //}
}

private fun part1(input: List<Long>): Int = countStones(input, blinks = 25)

private fun part2(input: List<Long>): Int = TODO()

private fun countStones(stones: List<Long>, blinks: Int): Int {
    val memory = mutableMapOf<Long, IntArray>()
    fun memoized(stone: Long, blinksLeft: Int, block: () -> Int): Int {
        val blinkValues = memory.getOrPut(stone) { IntArray(blinks + 1) { -1 } }
        if (blinkValues[blinksLeft] == -1) blinkValues[blinksLeft] = block()
        return blinkValues[blinksLeft]
    }

    fun count(stone: Long, blinksLeft: Int): Int = memoized(stone, blinksLeft) {
        if (blinksLeft == 0) return@memoized 1

        when {
            stone == 0L -> count(1, blinksLeft - 1)
            stone.toString().length % 2 == 0 -> {
                val stoneValue = stone.toString()
                val left = stoneValue.take(stoneValue.length / 2).toLong()
                val right = stoneValue.takeLast(stoneValue.length / 2).toLong()
                count(left, blinksLeft - 1) + count(right, blinksLeft - 1)
            }

            else -> count(stone * 2024, blinksLeft - 1)
        }
    }

    return stones.sumOf { count(it, blinks) }
}

private fun readInput(name: String) = readText(name).splitLongs()
