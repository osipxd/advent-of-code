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

    //"Part 2" {
    //    part2(testInput()) shouldBe 0
    //    measureAnswer { part2(input()) }
    //}
}

private fun part1(input: AntennasMap): Int {
    return input.antennas.values.asSequence()
        .flatMap { it.pairCombinations() }
        .flatMap { (pos1, pos2) ->
            val dr = pos1.row - pos2.row
            val dc = pos1.column - pos2.column

            sequenceOf(
                pos1.offsetBy(row = dr, column = dc),
                pos2.offsetBy(row = -dr, column = -dc),
            )
        }
        .filter { it in input.bounds }
        .distinct()
        .count()
}

private fun part2(input: AntennasMap): Int = TODO()

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
