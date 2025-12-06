private const val DAY = "Day06"

fun main() {
    "Part 1" {
        fun testInput() = readHorizontalNumbers("${DAY}_test")
        fun input() = readHorizontalNumbers(DAY)

        solve(testInput()) shouldBe 4277556
        measureAnswer { solve(input()) }
    }

    "Part 2" {
        fun testInput() = readVerticalNumbers("${DAY}_test")
        fun input() = readVerticalNumbers(DAY)

        solve(testInput()) shouldBe 3263827
        measureAnswer { solve(input()) }
    }
}

private fun solve(input: List<Expression>): Long = input.sumOf(Expression::calculate)

private fun readHorizontalNumbers(name: String): List<Expression> {
    val lines = readLines(name)
    val spacesRegex = Regex("\\s+")

    val operands = lines.dropLast(1).map { it.trim().split(spacesRegex).map(String::toLong) }
    val operations = lines.last().filter { it != ' ' }

    return operations.indices.map { index ->
        Expression(
            operands = operands.map { it[index] },
            operation = parseOperation(operations[index]),
        )
    }
}

private fun readVerticalNumbers(name: String): List<Expression> {
    val lines = readLines(name)

    val operands = lines.dropLast(1)
    val operations = lines.last() + " |" // Just a trick to not handle the last expression differently

    return buildList {
        var operandIndex = 0
        var index = 1
        while (index < operations.length) {
            if (operations[index] != ' ') {
                val operands = List(index - operandIndex - 1) { i ->
                    operands.map { it[operandIndex + i] }.joinToString("").trim().toLong()
                }
                add(Expression(operands, parseOperation(operations[operandIndex])))
                operandIndex = index
            }
            index++
        }
    }
}

private fun parseOperation(operation: Char): (Long, Long) -> Long = when (operation) {
    '+' -> Long::plus
    '*' -> Long::times
    else -> error("Unknown operation $operation")
}

private class Expression(
    private val operands: List<Long>,
    private val operation: (Long, Long) -> Long,
) {
    fun calculate(): Long = operands.reduce(operation)
}
