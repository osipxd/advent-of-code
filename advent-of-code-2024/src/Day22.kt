private const val DAY = "Day22"

fun main() {
    fun testInput(n: Int) = readInput("${DAY}_test$n")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput(1)) shouldBe 37327623
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput(2)) shouldBe 23
        measureAnswer { part2(input()) }
    }
}

private fun part1(secretNumbers: List<Long>): Long = secretNumbers.sumOf { evolveSecret(it).last() }

private fun part2(secretNumbers: List<Long>): Int {
    val sequenceToBananas = mutableMapOf<List<Int>, Int>()

    secretNumbers.asSequence()
        .map { calculateWinsForSequences(it) }
        .forEach { sequenceToBananas.mergeWith(it) }

    return sequenceToBananas.values.max()
}

private fun calculateWinsForSequences(secretNumber: Long): Map<List<Int>, Int> {
    val sequenceToBananas = mutableMapOf<List<Int>, Int>()

    val bananas = evolveSecret(secretNumber).mapTo(ArrayList(EVOLVE_TIMES)) { (it % 10).toInt() }
    val changes = bananas.indices.map { i ->
        val prev = if (i == 0) (secretNumber % 10).toInt() else bananas[i - 1]
        val current = bananas[i]
        current - prev
    }
    changes.windowed(size = 4).withIndex().forEach { (i, window) ->
        sequenceToBananas.putIfAbsent(window, bananas[i + 3])
    }

    return sequenceToBananas
}

private fun evolveSecret(secretNumber: Long) = sequence {
    var current = secretNumber

    fun mixAndPrune(value: Long) {
        current = (current xor value) % 16777216
    }

    repeat(EVOLVE_TIMES) {
        mixAndPrune(current shl 6) // current * 64
        mixAndPrune(current shr 5) // current / 32
        mixAndPrune(current shl 11) // current * 2048
        yield(current)
    }
}

private const val EVOLVE_TIMES = 2000

private fun readInput(name: String) = readLines(name).map(String::toLong)

// Utils

private fun <K> MutableMap<K, Int>.mergeWith(other: Map<K, Int>) {
    for ((key, value) in other) {
        this[key] = this[key]?.let { it + value } ?: value
    }
}
