fun main() {
    val testInput = readInput("Day18_test")
    val input = readInput("Day18")

    "Part 1" {
        part1(testInput) shouldBe 64
        measureAnswer { part1(input) }
    }

    "Part 2" {
        part2(testInput) shouldBe 58
        measureAnswer { part2(input) }
    }
}

private val directions = listOf(
    listOf(0, 0, 1),
    listOf(0, 0, -1),
    listOf(0, 1, 0),
    listOf(0, -1, 0),
    listOf(1, 0, 0),
    listOf(-1, 0, 0),
)

private fun part1(input: List<Cube>): Int {
    val occupied = mutableSetOf<Cube>()

    var connectedSides = 0
    for (cube in input) {
        connectedSides += directions.count { (dx, dy, dz) ->
            val (x, y, z) = cube
            Cube(x + dx, y + dy, z + dz) in occupied
        } * 2
        occupied += cube
    }

    return input.size * 6 - connectedSides
}

private fun part2(input: List<Cube>): Int {
    val occupied = mutableSetOf<Cube>()

    var maxX = 0
    var maxY = 0
    var maxZ = 0

    for (cube in input) {
        occupied += cube

        maxX = maxOf(maxX, cube.x)
        maxY = maxOf(maxY, cube.y)
        maxZ = maxOf(maxZ, cube.z)
    }

    fun generateNeighbors(cube: Cube): Sequence<Cube> = sequence {
        val (x, y, z) = cube
        if (x >= 0) yield(Cube(x - 1, y, z))
        if (x <= maxX) yield(Cube(x + 1, y, z))
        if (y >= 0) yield(Cube(x, y - 1, z))
        if (y <= maxY) yield(Cube(x, y + 1, z))
        if (z >= 0) yield(Cube(x, y, z - 1))
        if (z <= maxZ) yield(Cube(x, y, z + 1))
    }

    var connectedSides = 0

    val airCubes = mutableSetOf(Cube(0, 0, 0))
    val queue = ArrayDeque<Cube>()
    queue.addFirst(airCubes.first())

    while (queue.isNotEmpty()) {
        val cube = queue.removeFirst()
        for (ncube in generateNeighbors(cube).filterNot { it in airCubes }) {
            if (ncube in occupied) {
                connectedSides++
            } else {
                airCubes += ncube
                queue.addLast(ncube)
            }
        }
    }

    return connectedSides
}

private fun readInput(name: String) = readLines(name).map {
    val (x, y, z) = it.splitInts()
    Cube(x, y, z)
}

private data class Cube(val x: Int, val y: Int, val z: Int)
