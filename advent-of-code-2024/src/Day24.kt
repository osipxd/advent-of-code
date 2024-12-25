private const val DAY = "Day24"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 2024
        measureAnswer { part1(input()) }
    }

    //"Part 2" {
    //    part2(testInput()) shouldBe 0
    //    measureAnswer { part2(input()) }
    //}
}

private fun part1(expressions: Map<String, LogicExpression>): Long {
    val queue = ArrayDeque<Pair<String, LogicExpression>>()
    queue.addAll(expressions.filterKeys { it.startsWith("z") }.toList())

    var result = 0L
    while (queue.isNotEmpty()) {
        val (wire, expression) = queue.first()
        var wireValue = expression.value

        if (wireValue == null) {
            val inExpressions = expression.inWires.associateWith(expressions::getValue)
            val values = inExpressions.mapNotNull { (_, expression) -> expression.value }
            if (values.size == 2) {
                wireValue = expression.evaluate(values)
            } else {
                inExpressions.filterValues { it.value == null }
                    .forEach { (wire, expression) -> queue.addFirst(wire to expression) }
            }
        }

        if (wireValue != null) {
            queue.removeFirst()
            if (wire.startsWith("z")) {
                result = result or wireValue.toBit(wire.drop(1).toInt())
            }
        }
    }

    return result
}

private fun part2(expressions: Map<String, LogicExpression>): Long = TODO()

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

        override fun evaluate(wireValues: List<Boolean>): Boolean {
            return value ?: gate(wireValues[0], wireValues[1]).also { value = it }
        }
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

private fun Boolean.toBit(rank: Int): Long = (if (this) 1L else 0L) shl rank
