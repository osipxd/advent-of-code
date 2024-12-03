private const val DAY = "Day03"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 161
        measureAnswer { part1(input()) }
    }

    //"Part 2" {
    //    part2(testInput()) shouldBe 0
    //    measureAnswer { part2(input()) }
    //}
}

private val MUL_REGEX = Regex("""mul\((\d+),(\d+)\)""")

private fun part1(input: List<String>): Int = input.sumOf { line ->
    MUL_REGEX.findAll(line).sumOf { it.groupValues[1].toInt() * it.groupValues[2].toInt() }
}

private fun part2(input: List<String>): Int = TODO()

private fun readInput(name: String) = readLines(name)