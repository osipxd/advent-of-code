private const val DAY = "Day06"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun testInput2() = readInput2("${DAY}_test")
    fun input() = readInput(DAY)
    fun input2() = readInput2(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 288
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput2()) shouldBe 71503
        measureAnswer { part2(input2()) }
    }
}

private fun part1(input: Pair<List<Int>, List<Int>>): Int {
    val (times, distances) = input
    return times.indices
        .map { i ->
            val time = times[i]
            (1 until time).count { timeToCharge ->
                timeToCharge * (time - timeToCharge) > distances[i]
            }
        }
        .reduce(Int::times)
}

private fun part2(input: Pair<Long, Long>): Int {
    val (time, distance) = input
    return (1 until time).count { timeToCharge ->
        timeToCharge * (time - timeToCharge) > distance
    }
}

private val spacesRegex = Regex("\\s+")

private fun readInput(name: String): Pair<List<Int>, List<Int>> {
    val (time, distance) = readLines(name).map { line ->
        line.substringAfter(":").trim()
            .split(spacesRegex)
            .map(String::toInt)
    }

    return time to distance
}

private fun readInput2(name: String): Pair<Long, Long> {
    val (time, distance) = readLines(name).map { line ->
        line.substringAfter(":").trim()
            .replace(" ", "")
            .toLong()
    }

    return time to distance
}
