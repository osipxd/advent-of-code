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

private fun part1(input: List<Monkey>): Int {
    val inspectionsCounts = IntArray(input.size) { 0 }

    repeat(20) {
        for ((i, monkey) in input.withIndex()) {
            inspectionsCounts[i] += monkey.items.size
            for (item in monkey.takeItems()) {
                val new = monkey.operation(item.toLong()) / 3
                val target = if (new % monkey.testValue == 0L) monkey.ifTrue else monkey.ifFalse
                input[target].addItem(new.toInt())
            }
        }
    }

    return inspectionsCounts.sortedDescending().take(2).reduce(Int::times)
}

private fun part2(input: List<Monkey>): Long {
    val inspectionsCounts = LongArray(input.size) { 0 }
    val mod = input.map { it.testValue.toLong() }.reduce(Long::times)

    repeat(10000) { round ->
        for ((i, monkey) in input.withIndex()) {
            inspectionsCounts[i] += monkey.items.size.toLong()
            for (item in monkey.takeItems()) {
                val new = (monkey.operation(item.toLong()) % mod).toInt()
                val target = if (new % monkey.testValue == 0) monkey.ifTrue else monkey.ifFalse
                input[target].addItem(new)
            }
        }
        if (round + 1 in listOf(1, 20, 1000, 2000)) {
            println(inspectionsCounts.contentToString())
        }
    }

    return inspectionsCounts.sortedDescending().take(2).reduce(Long::times)
}

private fun readInput(name: String) = readText(name).splitToSequence("\n\n").map { rawMonkey ->
    val (items, operation, test, ifTrue, ifFalse) = rawMonkey.split("\n").drop(1)

    Monkey(
        startingItems = items.substringAfter(": ").split(", ").map { it.toInt() },
        operation = operationOf(operation),
        testValue = test.substringAfter("divisible by ").toInt(),
        ifFalse = ifFalse.substringAfter("throw to monkey ").toInt(),
        ifTrue = ifTrue.substringAfter("throw to monkey ").toInt(),
    )
}.toList()

private fun operationOf(operation: String): (Long) -> Long {
    val parts = operation.substringAfter(" = ").split(" ")
    val (takeOld, b) = if (parts[2] == "old") true to 0L else false to parts[2].toLong()
    return if (parts[1] == "+") {
        { old: Long -> old + if (takeOld) old else b }
    } else {
        { old: Long -> old * if (takeOld) old else b }
    }
}

private class Monkey(
    startingItems: List<Int>,
    val operation: (Long) -> Long,
    val testValue: Int,
    val ifFalse: Int,
    val ifTrue: Int
) {
    val items: MutableList<Int> = startingItems.toMutableList()

    fun takeItems() = items.toList().also { items.clear() }

    fun addItem(item: Int) {
        items += item
    }
}
