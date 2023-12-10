private const val DAY = "Day01"

fun main() {
    val input = readInput(DAY)
    val testInput1 = readInput("${DAY}_test1")
    val testInput2 = readInput("${DAY}_test2")

    "Part 1" {
        part1(testInput1) shouldBe 142
        measureAnswer { part1(input) }
    }

    "Part 2 (Regex)" {
        part2Regex(testInput2) shouldBe 281
        measureAnswer { part2Regex(input) }
    }

    "Part 2 (findAnyOf)" {
        part2FindAnyOf(testInput2) shouldBe 281
        measureAnswer { part2FindAnyOf(input) }
    }
}

private fun part1(input: List<String>): Int = input.sumOf(::extractSimpleCalibrationValue)

private fun extractSimpleCalibrationValue(line: String): Int {
    val firstDigit = line.first { it.isDigit() }
    val secondDigit = line.last { it.isDigit() }
    return "$firstDigit$secondDigit".toInt()
}

//////////////////////////////////

private fun part2Regex(input: List<String>): Int = input.sumOf(::extractCalibrationValueWithRegex)

private const val DIGITS_REGEX = "one|two|three|four|five|six|seven|eight|nine"
private val realDigitRegex = Regex("""\d|$DIGITS_REGEX""")
private val realDigitRegexReversed = Regex("""\d|${DIGITS_REGEX.reversed()}""")

private fun extractCalibrationValueWithRegex(line: String): Int {
    val firstDigit = realDigitRegex.requireValue(line).digitToInt()
    val secondDigit = realDigitRegexReversed.requireValue(line.reversed()).reversed().digitToInt()
    return firstDigit * 10 + secondDigit
}

private fun Regex.requireValue(input: String) = requireNotNull(find(input)).value

//////////////////////////////////

private fun part2FindAnyOf(input: List<String>): Int = input.sumOf(::extractCalibrationValueWithFindAnyOf)

private val digits = setOf(
    "1", "2", "3", "4", "5", "6", "7", "8", "9",
    "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
)

private fun extractCalibrationValueWithFindAnyOf(line: String): Int {
    val firstDigit = line.findAnyOf(digits).requireDigit()
    val secondDigit = line.findLastAnyOf(digits).requireDigit()
    return firstDigit * 10 + secondDigit
}

private fun Pair<Int, String>?.requireDigit(): Int {
    checkNotNull(this)
    return second.digitToInt()
}

//////////////////////////////////

private fun String.digitToInt(): Int = when (this) {
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
