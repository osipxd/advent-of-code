fun main() {
    val day = "1"
    val input = readInput("Day$day")

    "Part 1" {
        val testInput = readInput("Day${day}_test1")
        part1(testInput) shouldBe 142
        measureAnswer { part1(input) }
    }

    "Part 2" {
        val testInput = readInput("Day${day}_test2")
        part2(testInput) shouldBe 281
        measureAnswer { part2(input) }
    }
}

private fun part1(input: List<String>): Int = input.sumOf(::extractSimpleCalibrationValue)

private fun extractSimpleCalibrationValue(line: String): Int {
    val firstDigit = line.first { it.isDigit() }
    val secondDigit = line.last { it.isDigit() }
    return "$firstDigit$secondDigit".toInt()
}

private fun part2(input: List<String>): Int = input.sumOf(::extractRealCalibrationValue)

private const val DIGITS_REGEX = "one|two|three|four|five|six|seven|eight|nine" 
private val realDigitRegex = Regex("""\d|$DIGITS_REGEX""")
private val realDigitRegexReversed = Regex("""\d|${DIGITS_REGEX.reversed()}""")

private fun extractRealCalibrationValue(line: String): Int {
    val firstDigit = realDigitRegex.find(line)!!.value.digitToInt() * 10
    val secondDigit = realDigitRegexReversed.find(line.reversed())!!.value.reversed().digitToInt()
    return firstDigit + secondDigit
}

private fun String.digitToInt(): Int = when(this) {
    "one" -> 1
    "two" -> 2
    "three" -> 3
    "four" -> 4
    "five" -> 5
    "six" -> 6
    "seven" -> 7
    "eight" -> 8
    "nine" -> 9
    else -> single().digitToInt()
}

private fun readInput(name: String) = readLines(name)
