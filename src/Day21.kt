fun main() {
    val testInput = readInput("Day21_test")
    val input = readInput("Day21")

    "Part 1" {
        part1(testInput) shouldBe 152
        measureAnswer { part1(input) }
    }

    "Part 2" {
        part2(testInput) shouldBe 301
        measureAnswer { part2(input) }
    }
}

private fun part1(input: Map<String, YellingMonkey>): Long {
    input.calculateMonkeyAnswer("root")
    return input.getValue("root").answer!!
}

private fun part2(input: Map<String, YellingMonkey>): Long {
    TODO()
}

private fun Map<String, YellingMonkey>.calculateMonkeyAnswer(monkey: String) {
    val stack = ArrayDeque<YellingMonkey>()
    stack.addFirst(getValue(monkey))

    while (stack.isNotEmpty()) {
        val current = stack.first()

        if (current.answer != null) {
            stack.removeFirst()
        } else {
            val (a, b) = current.waitFor.map { getValue(it) }

            val answerA = a.answer
            val answerB = b.answer
            if (answerA != null && answerB != null) {
                current.answer = current.operation?.invoke(answerA, answerB)
            } else {
                if (answerA == null) stack.addFirst(a)
                if (answerB == null) stack.addFirst(b)
            }
        }
    }
}

private fun readInput(name: String) = readLines(name).map { line ->
    val (monkeyName, rawOperation) = line.split(": ")
    val operationParts = rawOperation.split(" ")
    val operation: ((Long, Long) -> Long)? = if (operationParts.size == 3) {
        when (operationParts[1]) {
            "+" -> Long::plus
            "*" -> Long::times
            "/" -> Long::div
            "-" -> Long::minus
            else -> error("Unknown operation ${operationParts[1]}")
        }
    } else null

    YellingMonkey(
        name = monkeyName,
        waitFor = if (operationParts.size == 3) listOf(operationParts[0], operationParts[2]) else emptyList(),
        symbol = operationParts.getOrNull(1).orEmpty(),
        operation = operation,
    ).apply { answer = rawOperation.toLongOrNull() }
}.associateBy { it.name }

private data class YellingMonkey(
    val name: String,
    val waitFor: List<String>,
    val symbol: String,
    val operation: ((Long, Long) -> Long)?,
) {
    var answer: Long? = null
}
