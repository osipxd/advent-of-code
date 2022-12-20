fun main() {
    val testInput = readInput("Day20_test")
    val input = readInput("Day20")

    "Part 1" {
        part1(testInput) shouldBe 3
        measureAnswer { part1(input) }
    }

    "Part 2" {
        part2(testInput) shouldBe 1623178306
        measureAnswer { part2(input) }
    }
}

private fun part1(input: List<Long>): Long = mix(input, times = 1)
private fun part2(input: List<Long>): Long = mix(input, times = 10, decryptionKey = 811589153)

private fun mix(rawInput: List<Long>, times: Int, decryptionKey: Long = 1): Long {
    val original = rawInput.mapIndexed { i, value -> i to value * decryptionKey }
    val mixed = original.toMutableList()

    repeat(times) {
        for (value in original) mixed.move(value)
    }

    val zeroIndex = mixed.indexOfFirst { it.second == 0L }
    fun groveCoordinateValue(shift: Int) = mixed[(zeroIndex + shift) % mixed.size].second

    return groveCoordinateValue(1000) + groveCoordinateValue(2000) + groveCoordinateValue(3000)
}

private fun MutableList<Pair<Int, Long>>.move(value: Pair<Int, Long>) {
    val index = indexOf(value)
    removeAt(index)
    add((index + value.second).mod(size), value)
}

private fun readInput(name: String) = readLines(name).map { it.toLong() }
