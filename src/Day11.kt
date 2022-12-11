fun main() {
    "Part 1" {
        val testInput = readInput("Day11_test")
        val input = readInput("Day11")
        part1(testInput) shouldBe 10605
        answer(part1(input))
    }

    "Part 2" {
        val testInput = readInput("Day11_test")
        val input = readInput("Day11")
        part2(testInput) shouldBe 2713310158
        answer(part2(input))
    }
}

private fun part1(input: List<Monkey>): Long = solve(input, rounds = 20, keepCalm = true)
private fun part2(input: List<Monkey>): Long = solve(input, rounds = 10_000, keepCalm = false)

private fun solve(monkeys: List<Monkey>, rounds: Int, keepCalm: Boolean): Long {
    val inspectionsCounts = IntArray(monkeys.size) { 0 }

    // All `divideBy` values are prime numbers, so we can multiply them all together
    // and take remainder from division by resulting value.
    // We should do it after each operation to not overflow Int.
    val mod = monkeys.map { it.divideBy.toLong() }.reduce(Long::times)

    repeat(rounds) {
        for ((i, monkey) in monkeys.withIndex()) {
            inspectionsCounts[i] += monkey.items.size

            for (item in monkey.takeItems()) {
                val calmLevel = if (keepCalm) 3 else 1
                val new = (monkey.operation(item) / calmLevel % mod).toInt()
                val target = if (new % monkey.divideBy == 0) monkey.ifTrue else monkey.ifFalse
                monkeys[target].items.add(new)
            }
        }
    }

    return inspectionsCounts.sortedDescending().take(2).map(Int::toLong).reduce(Long::times)
}

private fun readInput(name: String) = readText(name).splitToSequence("\n\n").map { rawMonkey ->
    val (items, operation, test, ifTrue, ifFalse) = rawMonkey.lines().drop(1)

    Monkey(
        startingItems = items.substringAfter(": ").split(", ").map(String::toInt),
        operation = parseOperation(operation),
        divideBy = test.substringAfter("divisible by ").toInt(),
        ifFalse = ifFalse.substringAfter("throw to monkey ").toInt(),
        ifTrue = ifTrue.substringAfter("throw to monkey ").toInt(),
    )
}.toList()

private fun parseOperation(input: String): (Int) -> Long {
    val parts = input.substringAfter(" = ").split(" ")
    val operation: (Long, Long) -> Long = if (parts[1] == "+") Long::plus else Long::times
    val b = parts[2].toLongOrNull()
    return { old -> operation(old.toLong(), b ?: old.toLong()) }
}

private class Monkey(
    startingItems: List<Int>,
    val operation: (Int) -> Long,
    val divideBy: Int,
    val ifFalse: Int,
    val ifTrue: Int
) {
    val items: MutableList<Int> = startingItems.toMutableList()

    fun takeItems() = items.toList().also { items.clear() }
}
