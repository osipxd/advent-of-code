private const val DAY = "Day07"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 3749
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput()) shouldBe 11387
        measureAnswer { part2(input()) }
    }
}

private fun part1(input: List<Equation>): Long = sumValidEquations(input, listOf(Long::plus, Long::times))
private fun part2(input: List<Equation>): Long = sumValidEquations(input, listOf(Long::plus, Long::times, Long::concat))

private fun sumValidEquations(input: List<Equation>, operators: List<Operator>): Long =
    input.filter { testEquation(it.numbers, it.testValue, operators) }
        .sumOf { it.testValue }

private fun testEquation(numbers: List<Long>, testValue: Long, operators: List<Operator>): Boolean {
    fun test(index: Int, value: Long): Boolean = when {
        index == numbers.lastIndex -> value == testValue
        value > testValue -> false
        else -> operators.any { operator -> test(index + 1, operator(value, numbers[index + 1])) }
    }

    return test(index = 0, value = numbers[0])
}

private infix fun Long.concat(other: Long): Long = "$this$other".toLong()

private fun readInput(name: String) = readLines(name) { line ->
    Equation(
        numbers = line.substringAfter(": ").splitLongs(),
        testValue = line.substringBefore(": ").toLong(),
    )
}

private typealias Operator = (Long, Long) -> Long
private data class Equation(val numbers: List<Long>, val testValue: Long)
