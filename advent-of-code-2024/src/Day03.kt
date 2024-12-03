private const val DAY = "Day03"

fun main() {
    fun testInput(id: Int) = readInput("${DAY}_test$id")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput(1)) shouldBe 161
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput(2)) shouldBe 48
        measureAnswer { part2(input()) }
    }
}

private val MUL_REGEX = Regex("""mul\((\d+),(\d+)\)""")

private fun part1(input: String): Int = MUL_REGEX.findAll(input).sumOf { it.multiplyArguments() }

private val OPERATIONS_REGEX = Regex("""do\(\)|don't\(\)|mul\((\d+),(\d+)\)""")

private fun part2(input: String): Int {
    var enabled = true
    var sum = 0

    for (match in OPERATIONS_REGEX.findAll(input)) {
        when (match.value) {
            "do()" -> enabled = true
            "don't()" -> enabled = false
            else -> if (enabled) sum += match.multiplyArguments()
        }
    }

    return sum
}

private fun MatchResult.multiplyArguments(): Int = groupValues[1].toInt() * groupValues[2].toInt()

private fun readInput(name: String) = readLines(name).joinToString("")