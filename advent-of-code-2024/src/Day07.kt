private const val DAY = "Day07"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 3749
        measureAnswer { part1(input()) }
    }

    //"Part 2" {
    //    part2(testInput()) shouldBe 0
    //    measureAnswer { part2(input()) }
    //}
}

private fun part1(input: List<Equation>): Long =
    input.filter { isPossibleEquation(it.numbers, it.testValue) }
        .sumOf { it.testValue }

private fun isPossibleEquation(numbers: List<Long>, testValue: Long): Boolean {
    fun check(index: Int, value: Long, nextIndex: Int = index + 1): Boolean = when {
        index == numbers.lastIndex -> value == testValue
        value > testValue -> false

        else -> check(nextIndex, value + numbers[nextIndex]) ||
                check(nextIndex, value * numbers[nextIndex])
    }

    return check(index = 0, value = numbers.first())
}

private fun part2(input: List<Equation>): Long = TODO()

private fun readInput(name: String) = readLines(name) { line ->
    Equation(
        numbers = line.substringAfter(": ").splitLongs(),
        testValue = line.substringBefore(": ").toLong(),
    )
}

private data class Equation(
    val numbers: List<Long>,
    val testValue: Long,
)