private const val DAY = "Day12"

private typealias Record = Pair<String, List<Int>>

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 21
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput()) shouldBe 525152
        measureAnswer { part2(input()) }
    }
}

private fun part1(input: List<Record>) = input.sumOf { (pattern, numbers) ->
    countArrangements(pattern, numbers)
}

private fun part2(input: List<Record>) = input.sumOf { (pattern, numbers) ->
    val multiPattern = listOf(pattern).repeat(5).joinToString("?")
    val multiNumbers = numbers.repeat(5)
    countArrangements(multiPattern, multiNumbers)
}

private val memory = mutableMapOf<Pair<String, List<Int>>, Long>()

private fun countArrangements(pattern: String, numbers: List<Int>): Long {
    when {
        !pattern.canFitNumbers(numbers) -> return 0
        pattern.isEmpty() -> return 1
    }

    fun countDotWays() = countArrangements(pattern.drop(1), numbers)
    fun countHashWays(): Long = memory.getOrPut(pattern to numbers) {
        val nextNumber = numbers.firstOrNull()
        if (nextNumber != null && pattern.canFitNumber(nextNumber)) {
            countArrangements(pattern.drop(nextNumber + 1), numbers.drop(1))
        } else {
            0
        }
    }

    return when (pattern.first()) {
        '.' -> countDotWays()
        '#' -> countHashWays()
        '?' -> countDotWays() + countHashWays()
        else -> error("Unexpected pattern: $pattern")
    }
}

private fun String.canFitNumbers(numbers: List<Int>): Boolean = length >= numbers.sum() + numbers.size - 1

private fun String.canFitNumber(number: Int): Boolean {
    return length >= number &&
        take(number).none { it == '.' } &&
        getOrNull(number) != '#'
}

private fun readInput(name: String) = readLines(name).map { line ->
    val (pattern, rawNumbers) = line.split(" ")
    pattern to rawNumbers.splitInts()
}

private fun <T> List<T>.repeat(times: Int): List<T> = List(size * times) { get(it % size) }
