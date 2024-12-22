private const val DAY = "Day22"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 37327623
        measureAnswer { part1(input()) }
    }

    //"Part 2" {
    //    part2(testInput()) shouldBe 0
    //    measureAnswer { part2(input()) }
    //}
}

private fun part1(secretNumbers: List<Long>): Long = secretNumbers.sumOf { evolveSecret(it, times = 2000) }

private fun part2(secretNumbers: List<Long>): Long = TODO()

private fun evolveSecret(secretNumber: Long, times: Int): Long {
    var current = secretNumber

    fun mixAndPrune(value: Long): Long = (current xor value) % 16777216

    repeat(times) {
        current = mixAndPrune(current shl 6)
        current = mixAndPrune(current shr 5)
        current = mixAndPrune(current shl 11)
    }

    return current
}

private fun readInput(name: String) = readLines(name).map(String::toLong)