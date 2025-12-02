import kotlin.math.abs

private const val DAY = "Day01"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 3
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput()) shouldBe 6
        measureAnswer { part2(input()) }
    }
}

private const val MAX_POSITION = 99

private fun part1(input: List<Int>): Int {
    var password = 0
    input.fold(50) { acc, rotation ->
        val newPosition = (acc + rotation).mod(MAX_POSITION + 1)
        if (newPosition == 0) password++
        newPosition
    }

    return password
}

private fun part2(input: List<Int>): Int {
    var password = 0
    input.fold(50) { position, rotation ->
        password += abs(rotation) / (MAX_POSITION + 1)
        val newPosition = (position + rotation).mod(MAX_POSITION + 1)
        if (newPosition == 0 ||
            rotation > 0 && newPosition < position ||
            position != 0 && rotation < 0 && newPosition > position) password++
        newPosition
    }

    return password
}

private fun readInput(name: String) = readLines(name).map { line ->
    val sign = if (line.first() == 'R') +1 else -1
    sign * line.drop(1).toInt()
}