import lib.matrix.*

private const val DAY = "Day12"

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

private fun part1(input: Matrix<Char>): Int {
    val seen = mutableSetOf<Position>()

    fun regionPrice(start: Position): Int {
        var area = 0
        var perimeter = 0
        val letter = input[start]

        val queue = ArrayDeque<Position>()
        fun addNext(position: Position) {
            if (seen.add(position)) queue += position
        }

        addNext(start)
        while (queue.isNotEmpty()) {
            val position = queue.removeFirst()
            area += 1

            Direction.orthogonal.asSequence()
                .map { position.nextBy(it) }
                .forEach { nextPosition ->
                    if (input.getOrNull(nextPosition) == letter) {
                        addNext(nextPosition)
                    } else {
                        perimeter += 1
                    }
                }
        }

        return area * perimeter
    }

    return input.positions().filter { it !in seen }.sumOf { regionPrice(it) }
}

private fun part2(input: Matrix<Char>): Int {
    val seen = mutableSetOf<Position>()

    fun regionPrice(start: Position): Int {
        var area = 0
        var sides = 0
        val letter = input[start]

        val queue = ArrayDeque<Position>()
        fun addNext(position: Position) {
            if (seen.add(position)) queue += position
        }

        fun isInRegion(position: Position): Boolean = input.getOrNull(position) == letter

        fun countCorners(position: Position): Int {
            return Direction.orthogonal.count { direction ->
                val directionInRegion = isInRegion(position.nextBy(direction))
                val nextDirectionInRegion = isInRegion(position.nextBy(direction.turn90()))

                (!directionInRegion && !nextDirectionInRegion) ||
                    (directionInRegion && nextDirectionInRegion && !isInRegion(position.nextBy(direction.turn45())))
            }
        }

        addNext(start)
        while (queue.isNotEmpty()) {
            val position = queue.removeFirst()
            area += 1
            sides += countCorners(position)

            Direction.orthogonal.asSequence()
                .map(position::nextBy)
                .filter(::isInRegion)
                .forEach(::addNext)
        }

        return area * sides
    }

    return input.positions().filter { it !in seen }.sumOf { regionPrice(it) }
}

private fun readInput(name: String) = readMatrix(name)
