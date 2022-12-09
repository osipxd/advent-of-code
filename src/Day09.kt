fun main() {
    val testInput = readInput("Day09_test")
    val testInput2 = readInput("Day09_test2")
    val input = readInput("Day09")

    "Part 1" {
        part1(testInput) shouldBe 13
        answer(part1(input))
    }

    "Part 2" {
        part2(testInput2) shouldBe 36
        answer(part2(input))
    }
}

private fun part1(input: List<Pair<String, Int>>): Int {
    var headPosition = 0 to 0
    var tailPosition = 0 to 0

    val visited = mutableSetOf(tailPosition)
    for ((command, value) in input) {
        var (hx, hy) = headPosition
        var (tx, ty) = tailPosition

        repeat(value) {
            when (command) {
                "R" -> {
                    hx++
                    val diff = hx - tx
                    if (diff > 1) {
                        tx++
                        if (hy != ty) ty = hy
                    }
                }

                "L" -> {
                    hx--
                    val diff = tx - hx
                    if (diff > 1) {
                        tx--
                        if (hy != ty) ty = hy
                    }
                }

                "D" -> {
                    hy--
                    val diff = ty - hy
                    if (diff > 1) {
                        ty--
                        if (tx != hx) tx = hx
                    }
                }

                "U" -> {
                    hy++
                    val diff = hy - ty
                    if (diff > 1) {
                        ty++
                        if (tx != hx) tx = hx
                    }
                }
            }
            visited.add(tx to ty)
        }

        headPosition = (hx to hy)
        tailPosition = (tx to ty)
    }

    return visited.size
}

private fun part2(input: List<Pair<String, Int>>): Int {
    val positions = Array(10) { 0 to 0 }

    val visited = mutableSetOf(positions.last())

    for ((command, value) in input) {
        println("== $command $value ==")

        repeat(value) {
            var (hx, hy) = positions.first()
            when (command) {
                "R" -> hx++
                "L" -> hx--
                "U" -> hy++
                "D" -> hy--
            }
            positions[0] = hx to hy

            for (i in 1..positions.lastIndex) {
                val (hx, hy) = positions[i - 1]
                var (tx, ty) = positions[i]

                val dx = hx - tx
                val dy = hy - ty

                when {
                    dx > 1 -> {
                        tx++
                        if (dy > 0) ty++ else if (dy < 0) ty--
                    }

                    dx < -1 -> {
                        tx--
                        if (dy > 0) ty++ else if (dy < 0) ty--
                    }

                    dy < -1 -> {
                        ty--
                        if (dx > 0) tx++ else if (dx < 0) tx--
                    }

                    dy > 1 -> {
                        ty++
                        if (dx > 0) tx++ else if (dx < 0) tx--
                    }
                }

                positions[i] = tx to ty
            }

            visited.add(positions.last())
        }
        //dump(positions)
    }

    return visited.size
}

private fun dump(positions: Array<Pair<Int, Int>>) {
    val map = Array(30) { CharArray(30) { '.' } }
    map[5][11] = 's'
    for (i in positions.indices.reversed()) {
        val (x, y) = positions[i]
        map[y + 5][x + 11] = if (i == 0) 'H' else i.digitToChar()
    }

    println(map.reversed().joinToString("\n") { it.joinToString(" ") })
    println()
}

private fun readInput(name: String) = readLines(name).map {
    val (command, number) = it.split(" ")
    command to number.toInt()
}
