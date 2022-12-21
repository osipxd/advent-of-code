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
    return input.requireAnswer("root").toLong()
}

private fun part2(input: Map<String, YellingMonkey>): Long {
    val rootMonkey = input.getValue("root")
    val me = input.getValue("humn")

    // Calculate answers for left and right side
    input.calculateMonkeyAnswer("root")
    val (leftBefore, rightBefore) = rootMonkey.waitFor.map(input::requireAnswer)
    input.resetAnswers()

    // Increase my answer by 1 to see how thi will affect resulting answer
    val originalAnswer = me.answer!!
    me.answer = originalAnswer + 1

    // Calculate answers for left and right side again to figure out
    // what part has changed and how it is changed
    input.calculateMonkeyAnswer("root")
    val (leftAfter, rightAfter) = rootMonkey.waitFor.map(input::requireAnswer)

    // Calculate value shift - divide original gap by te diff after we changed answer by one
    val shift = if (rightAfter == rightBefore) {
        (leftBefore - rightBefore) / (leftBefore - leftAfter)
    } else {
        (rightBefore - leftBefore) / (rightBefore - rightAfter)
    }

    return (originalAnswer + shift).toLong()
}

private fun Map<String, YellingMonkey>.requireAnswer(name: String) = checkNotNull(getValue(name).answer)

private fun Map<String, YellingMonkey>.resetAnswers() {
    values.asSequence()
        .filter { it.waitFor.isNotEmpty() }
        .forEach { it.answer = null }
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
    val operation: ((Double, Double) -> Double)? = if (operationParts.size == 3) {
        when (operationParts[1]) {
            "+" -> Double::plus
            "*" -> Double::times
            "/" -> Double::div
            "-" -> Double::minus
            else -> error("Unknown operation ${operationParts[1]}")
        }
    } else null

    YellingMonkey(
        name = monkeyName,
        waitFor = if (operationParts.size == 3) listOf(operationParts[0], operationParts[2]) else emptyList(),
        operation = operation,
    ).apply { answer = rawOperation.toDoubleOrNull() }
}.associateBy { it.name }

private data class YellingMonkey(
    val name: String,
    val waitFor: List<String>,
    val operation: ((Double, Double) -> Double)?,
) {
    var answer: Double? = null
}
