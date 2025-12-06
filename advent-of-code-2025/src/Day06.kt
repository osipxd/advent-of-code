private const val DAY = "Day06"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 4277556
        measureAnswer { part1(input()) }
    }

    //"Part 2" {
    //    part2(testInput()) shouldBe 0
    //    measureAnswer { part2(input()) }
    //}
}

private fun part1(input: List<Expression>): Long = input.sumOf(Expression::calculate)

private fun part2(input: List<Expression>): Int = TODO()

private fun readInput(name: String): List<Expression> {
    val lines = readLines(name)
    val spacesRegex = Regex("\\s+")

    val operands = lines.dropLast(1).map { it.trim().split(spacesRegex).map(String::toLong) }
    val operations = lines.last().filter { it != ' ' }

    return operations.indices.map { index ->
        Expression(
            operands = operands.map { it[index] },
            operation = when(operations[index]) {
                '+' -> Long::plus
                '*' -> Long::times
                else -> error("Unknown operation ${operations[index]}")
            },
        )
    }
}

private class Expression(
    private val operands: List<Long>,
    private val operation: (Long, Long) -> Long,
) {
    fun calculate(): Long = operands.reduce(operation)
}
