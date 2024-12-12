import lib.matrix.*

private const val DAY = "Day12"

private typealias GardenMap = Matrix<Char>

fun main() {
    fun testInput(n: Int) = readInput("${DAY}_test$n")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput(0)) shouldBe 160
        part1(testInput(1)) shouldBe 140
        part1(testInput(2)) shouldBe 772
        part1(testInput(3)) shouldBe 1930
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput(1)) shouldBe 80
        part2(testInput(2)) shouldBe 436
        part2(testInput(4)) shouldBe 236
        part2(testInput(5)) shouldBe 368
        part2(testInput(3)) shouldBe 1206
        measureAnswer { part2(input()) }
    }
}

private fun part1(map: GardenMap): Int = calculateRegionsPrice(map, RegionContext::countSides)
private fun part2(map: GardenMap): Int = calculateRegionsPrice(map, RegionContext::countCorners)

private fun RegionContext.countSides(position: Position): Int =
    Direction.orthogonal.count { !isInRegion(position.nextBy(it)) }

/**
 * Counts corners in a region by checking each orthogonal direction:
 * - Outer corners: Where two adjacent sides are outside the region
 * - Inner corners: Where two adjacent sides are inside but diagonal is outside
 */
private fun RegionContext.countCorners(position: Position): Int = Direction.diagonal.count { diagonalDirection ->
    val leftPathInRegion = isInRegion(position.nextBy(diagonalDirection.turn45(clockwise = false)))
    val rightPathInRegion = isInRegion(position.nextBy(diagonalDirection.turn45(clockwise = true)))

    // Is it an outer corner?
    (!leftPathInRegion && !rightPathInRegion) ||
        // Or, maybe an inner corner?
        (leftPathInRegion && rightPathInRegion && !isInRegion(position.nextBy(diagonalDirection)))
}

private fun calculateRegionsPrice(map: GardenMap, countFenceSegments: RegionContext.(Position) -> Int): Int {
    val seen = mutableSetOf<Position>()

    fun regionPrice(start: Position): Int {
        var regionArea = 0
        var fenceSegments = 0
        val context = RegionContext(map, regionLabel = map[start])

        val queue = ArrayDeque<Position>()
        fun addNext(position: Position) {
            if (seen.add(position)) queue += position
        }

        addNext(start)
        while (queue.isNotEmpty()) {
            val position = queue.removeFirst()
            regionArea += 1
            fenceSegments += context.countFenceSegments(position)

            Direction.orthogonal.asSequence()
                .map(position::nextBy)
                .filter(context::isInRegion)
                .forEach(::addNext)
        }

        return regionArea * fenceSegments
    }

    return map.positions().filter { it !in seen }.sumOf { regionPrice(it) }
}

private class RegionContext(val map: GardenMap, private val regionLabel: Char) {
    fun isInRegion(position: Position): Boolean = map.getOrNull(position) == regionLabel
}

private fun readInput(name: String) = readMatrix(name)
