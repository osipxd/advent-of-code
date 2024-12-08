import lib.matrix.*
import lib.pairCombinations

private const val DAY = "Day08"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 14
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput()) shouldBe 34
        measureAnswer { part2(input()) }
    }
}

private fun part1(input: AntennasMap): Int = countAntinodes(input)
private fun part2(input: AntennasMap): Int = countAntinodes(input, resonantHarmonics = true)

private fun countAntinodes(input: AntennasMap, resonantHarmonics: Boolean = false): Int {
    fun propagateSignal(pos1: Position, pos2: Position): Sequence<Position> {
        val dr = pos1.row - pos2.row
        val dc = pos1.column - pos2.column

        return when (resonantHarmonics) {
            false -> sequenceOf(pos1.offsetBy(dr, dc), pos2.offsetBy(-dr, -dc)).filter { it in input.bounds }
            true -> pos1.walk(dr, dc, input.bounds) + pos2.walk(-dr, -dc, input.bounds)
        }
    }

    return input.antennas.values.asSequence()
        .flatMap { it.pairCombinations() }
        .flatMap { (pos1, pos2) -> propagateSignal(pos1, pos2) }
        .distinct()
        .count()
}

private fun readInput(name: String): AntennasMap {
    val rawMap = readMatrix(name)
    val antennas = buildMap<Char, MutableList<Position>> {
        rawMap.positions()
            .filter { rawMap[it] != '.' }
            .forEach { position -> getOrPut(rawMap[position], ::mutableListOf).add(position) }
    }

    return AntennasMap(rawMap.bounds, antennas)
}

private data class AntennasMap(
    val bounds: Bounds,
    val antennas: Map<Char, List<Position>>
)

// Utils

private fun Position.walk(rowStep: Int, columnStep: Int, bounds: Bounds): Sequence<Position> = sequence {
    var position = this@walk
    while (position in bounds) {
        yield(position)
        position = position.offsetBy(rowStep, columnStep)
    }
}
