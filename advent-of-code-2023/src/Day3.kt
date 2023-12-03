private const val DAY = "Day3"

fun main() {
    var testInput = readInput("${DAY}_test")
    var input = readInput(DAY)

    "Part 1" {
        part1(testInput) shouldBe 4361
        measureAnswer { part1(input) }
    }

    testInput = readInput("${DAY}_test")
    input = readInput(DAY)
    "Part 2" {
        part2(testInput) shouldBe 467835
        measureAnswer { part2(input) }
    }
}

private fun part1(input: List<CharArray>): Int {
    var sum = 0
    for (y in input.indices) {
        for (x in input.first().indices) {
            val char = input[y][x]
            if (!char.isDigit() && char != '.') {
                sum += input.findSumOfNeighbors(x, y)
            }
        }
    }

    return sum
}

private fun part2(input: List<CharArray>): Int {
    var sum = 0
    for (y in input.indices) {
        for (x in input.first().indices) {
            if (input[y][x] == '*') {
                val clone = input.map { it.clone() }
                sum += clone.findGearRatio(x, y)
            }
        }
    }

    return sum
}

private fun List<CharArray>.findGearRatio(x: Int, y: Int): Int {
    val numbers = ArrayDeque<Int>()
    fun findNumber(x: Int, y: Int) {
        val number = this.findNumber(x, y)
        println(number)
        if (number > 0) numbers.add(number)
    }
    findNumber(x + 1, y - 1)
    findNumber(x, y - 1)
    findNumber(x - 1, y - 1)
    findNumber(x - 1, y)
    findNumber(x + 1, y)
    findNumber(x + 1, y + 1)
    findNumber(x, y + 1)
    findNumber(x - 1, y + 1)

    println(numbers)
    return if (numbers.size == 2) numbers[0] * numbers[1] else 0
}

private fun List<CharArray>.findSumOfNeighbors(x: Int, y: Int): Int {
    val maxY = lastIndex
    val maxX = first().lastIndex

    var sum = 0
    if (y > 0) {
        if (x < maxY) sum += findNumber(x + 1, y - 1)
        sum += findNumber(x, y - 1)
        if (x > 0) sum += findNumber(x - 1, y - 1)
    }
    if (x > 0) sum += findNumber(x - 1, y)
    if (x < maxX) sum += findNumber(x + 1, y)
    if (y < maxY) {
        if (x < maxY) sum += findNumber(x + 1, y + 1)
        sum += findNumber(x, y + 1)
        if (x > 0) sum += findNumber(x - 1, y + 1)
    }

    return sum
}

private fun List<CharArray>.findNumber(x: Int, y: Int): Int {
    if (x !in first().indices || y !in indices) return 0

    val line = this[y]
    if (!line[x].isDigit()) return 0

    var firstDigitX = x
    for (i in x downTo 0) {
        if (line[i].isDigit()) firstDigitX = i else break
    }
    var lastDigitX = x
    for (i in x..line.lastIndex) {
        if (line[i].isDigit()) lastDigitX = i else break
    }

    val number = String(line.slice(firstDigitX..lastDigitX).toCharArray()).toInt()
    for (i in firstDigitX..lastDigitX) line[i] = '.'

    return number
}

private fun readInput(name: String) = readLines(name).map { it.toCharArray() }
