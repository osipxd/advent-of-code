import lib.PairOf

private const val DAY = "Day24"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 2024
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        measureAnswer { part2(input()) }
    }
}

private fun part1(expressions: Map<String, LogicExpression>): Long = calculateZ(expressions)

private fun part2(expressions: Map<String, LogicExpression>): String {
    val x = expressions.readNumber('x')
    val y = expressions.readNumber('y')
    val expected = x + y

    // The values are selected manually
    val swaps = listOf<PairOf<String>>(
        // Fill with your values
    )

    val rearrangedExpressions = expressions.toMutableMap()
    for ((wire1, wire2) in swaps) {
        val expression = rearrangedExpressions.getValue(wire1)
        rearrangedExpressions[wire1] = rearrangedExpressions.getValue(wire2)
        rearrangedExpressions[wire2] = expression
    }

    checkAnswer(expected, calculateZ(rearrangedExpressions))
    return swaps.flatMap { it.toList() }.sorted().joinToString(",")
}

private fun calculateZ(inputExpressions: Map<String, LogicExpression>): Long {
    val expressions = inputExpressions.toMutableMap()
    val queue = ArrayDeque<Pair<String, LogicExpression>>()
    queue.addAll(expressions.filterKeys { it.startsWith("z") }.toList())

    while (queue.isNotEmpty()) {
        val (wire, expression) = queue.first()
        var wireValue = expression.value

        if (wireValue == null) {
            val inExpressions = expression.inWires.associateWith(expressions::getValue)
            val values = inExpressions.mapNotNull { (_, expression) -> expression.value }
            if (values.size == 2) {
                wireValue = expression.evaluate(values).also { expressions[wire] = LogicExpression.Constant(it) }
            } else {
                inExpressions.filterValues { it.value == null }
                    .forEach { (wire, expression) -> queue.addFirst(wire to expression) }
            }
        }

        if (wireValue != null) queue.removeFirst()
    }

    return expressions.readNumber('z')
}

private fun Map<String, LogicExpression>.readNumber(name: Char): Long {
    return filterKeys { it.startsWith(name) }.asSequence()
        .map { (wire, expression) -> expression.value!!.toBit(rank = wire.drop(1).toInt()) }
        .fold(0L, Long::or)
}

private sealed interface LogicExpression {
    val value: Boolean?
    val inWires: List<String>
    fun evaluate(wireValues: List<Boolean>): Boolean

    class Constant(override val value: Boolean) : LogicExpression {
        override val inWires: List<String> = emptyList()
        override fun evaluate(wireValues: List<Boolean>): Boolean = value
    }

    class Gate(
        override val inWires: List<String>,
        private val gate: (Boolean, Boolean) -> Boolean,
    ) : LogicExpression {
        override var value: Boolean? = null
            private set

        override fun evaluate(wireValues: List<Boolean>): Boolean = gate(wireValues[0], wireValues[1])
    }
}

private fun readInput(name: String): Map<String, LogicExpression> {
    val (rawInputs: List<String>, rawWires: List<String>) = readText(name).split("\n\n").map { it.lines() }

    val expressions = HashMap<String, LogicExpression>(rawInputs.size + rawWires.size)
    rawInputs.associateTo(expressions) { line ->
        val (input, value) = line.split(": ")
        input to LogicExpression.Constant(value == "1")
    }
    rawWires.associateTo(expressions) { line ->
        val (expression, outWire) = line.split(" -> ")
        val (wire1, rawGate, wire2) = expression.split(" ")
        val gate = when (rawGate) {
            "AND" -> Boolean::and
            "OR" -> Boolean::or
            "XOR" -> Boolean::xor
            else -> error("Unknown operator: $rawGate")
        }

        outWire to LogicExpression.Gate(inWires = listOf(wire1, wire2), gate = gate)
    }

    return expressions
}

// Utils

private const val MAX_LENGTH = 46

private fun checkAnswer(expected: Long, actual: Long) {
    if (expected == actual) return

    val expectedString = expected.toString(radix = 2).padStart(MAX_LENGTH, '0')
    val actualString = actual.toString(radix = 2).padStart(MAX_LENGTH, '0')
    println("$expectedString (expected)")
    println("$actualString (actual)")

    val diffEnd = (MAX_LENGTH - 1 downTo 0).first { index -> expectedString[index] != actualString[index] }
    val hint = buildString {
        for (i in 0..diffEnd) {
            append(if (expectedString[i] != actualString[i]) '^' else ' ')
        }
    }
    println(hint)

    val firstWrongRank = MAX_LENGTH - diffEnd - 1
    println("First error at rank: $firstWrongRank")
    error("Wrong answer")
}

private fun Boolean.toBit(rank: Int): Long = (if (this) 1L else 0L) shl rank
