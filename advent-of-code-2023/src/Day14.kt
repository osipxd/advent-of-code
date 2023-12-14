private const val DAY = "Day14"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 136
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput()) shouldBe 64
        measureAnswer { part2(input()) }
    }
}

private fun part1(input: List<String>): Int = input.columns().sumOf(::calculateWeight)

private fun part2(input: List<String>): Int = TODO()

private fun calculateWeight(line: String): Int {
    var freePosition = 0
    var weight = 0
    val maxWeight = line.length

    for ((i, char) in line.withIndex()) {
        when (char) {
            'O' -> {
                weight += maxWeight - freePosition
                freePosition++
            }

            '#' -> freePosition = i + 1
            else -> Unit // Ignore
        }
    }

    return weight
}

private fun readInput(name: String) = readLines(name)

private fun List<String>.columns(): List<String> = buildList {
    for (i in this@columns.first().indices) {
        val column = buildString { for (line in this@columns) append(line[i]) }
        add(column)
    }
}
