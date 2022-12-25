import kotlin.math.pow

fun main() {
    val testInput = readLines("Day25_test")
    val input = readLines("Day25")

    "Part 1" {
        part1(testInput) shouldBe "2=-1=0"
        measureAnswer { part1(input) }
    }
}

private fun part1(input: List<String>): String = input.sumOf { it.snafuToDecimal() }.decimalToSnafu()

private fun String.snafuToDecimal(): Long {
    return reversed()
        .withIndex()
        .sumOf { (i, char) -> char.snafuDigitToDecimal() * (5L pow i) }
}

private fun Char.snafuDigitToDecimal() = when (this) {
    '=' -> -2
    '-' -> -1
    else -> digitToInt()
}

private infix fun Long.pow(other: Int): Long = this.toDouble().pow(other).toLong()

private val snafuDigits = listOf('=', '-', '0', '1', '2')

private fun Long.decimalToSnafu(): String {
    var decimal = this
    val result = StringBuilder()

    while (decimal > 0) {
        val rem = (decimal + 2) % 5;
        decimal = (decimal + 2) / 5;
        result.append(snafuDigits[rem.toInt()])
    }

    return result.reversed().toString()
}
