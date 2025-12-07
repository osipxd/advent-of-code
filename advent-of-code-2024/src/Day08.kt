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
        val vector = MatrixVector.between(pos1, pos2)
        return when (resonantHarmonics) {
            true -> pos1.walk(-vector).inBounds(input.bounds) + pos2.walk(vector).inBounds(input.bounds)
            false -> sequenceOf(pos1 - vector, pos2 + vector).filter { it in input.bounds }
        }
    }

    return input.antennaGroups.asSequence()
        .flatMap { it.pairCombinations() }
        .flatMap { (pos1, pos2) -> propagateSignal(pos1, pos2) }
        .distinct()
        .count()
}

private fun readInput(name: String): AntennasMap {
    val rawMap = readMatrix(name)
    val antennaGroups = buildMap {
        rawMap.valuePositions { it != '.' }
            .forEach { position -> getOrPut(rawMap[position], ::mutableListOf).add(position) }
    }

    return AntennasMap(rawMap.bounds, antennaGroups.values)
}

private data class AntennasMap(
    val bounds: Bounds,
    val antennaGroups: Collection<List<Position>>
)
