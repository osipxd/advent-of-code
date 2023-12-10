private typealias EngineSchematic = List<CharArray>

private const val DAY = "Day03"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 4361
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput()) shouldBe 467835
        measureAnswer { part2(input()) }
    }
}

private fun part1(input: EngineSchematic): Int {
    return input.sumIf(predicate = { !it.isDigit() && it != '.' }) { row, col ->
        input.findNeighborNumbers(row, col).sum()
    }
}

private fun part2(input: EngineSchematic): Int {
    return input.sumIf(predicate = { it == '*' }) { row, col ->
        val numbers = input.findNeighborNumbers(row, col)
        if (numbers.size == 2) numbers[0] * numbers[1] else 0
    }
}

private fun EngineSchematic.sumIf(
    predicate: (char: Char) -> Boolean,
    selector: (row: Int, col: Int) -> Int,
): Int {
    var sum = 0
    for (row in indices) {
        for (col in this[row].indices) {
            if (predicate(this[row][col])) sum += selector(row, col)
        }
    }

    return sum
}

private fun EngineSchematic.findNeighborNumbers(row: Int, col: Int): List<Int> {
    return neighborsOf(row, col)
        .filter { (r, c) -> r in indices && c in first().indices }
        .map { (r, c) -> this[r].extractNumberAt(c) }
        .filter { it != 0 }
        .toList()
}

private fun neighborsOf(r: Int, c: Int): Sequence<Pair<Int, Int>> = sequenceOf(
    r - 1 to c - 1, // top left
    r - 1 to c,     // top
    r - 1 to c + 1, // top right
    r to c - 1,     // left
    r to c + 1,     // right
    r + 1 to c - 1, // bottom left
    r + 1 to c,     // bottom
    r + 1 to c + 1, // bottom right
)

private fun CharArray.extractNumberAt(index: Int): Int {
    if (!this[index].isDigit()) return 0

    var numberStart = 0
    for (i in index downTo 0) {
        if (this[i].isDigit()) numberStart = i else break
    }
    var numberEnd = 0
    for (i in index..lastIndex) {
        if (this[i].isDigit()) numberEnd = i else break
    }

    val number = String(sliceArray(numberStart..numberEnd)).toInt()
    for (i in numberStart..numberEnd) this[i] = '.'

    return number
}

private fun readInput(name: String) = readLines(name).map { it.toCharArray() }
