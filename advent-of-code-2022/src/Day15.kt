import kotlin.math.abs

fun main() {
    val testInput = readInput("Day15_test")
    val input = readInput("Day15")

    "Part 1" {
        part1(testInput, targetY = 10) shouldBe 26
        answer(part1(input, targetY = 2_000_000))
    }

    "Part 2" {
        part2(testInput, max = 20) shouldBe 56000011
        answer(part2(input, max = 4_000_000))
    }
}

private fun part1(input: List<List<Int>>, targetY: Int): Int {
    val notABeacon = mutableListOf<IntRange>()
    val beacons = mutableSetOf<Int>()

    for ((sensorX, sensorY, beaconX, beaconY) in input) {
        val beaconDistance = abs(beaconX - sensorX) + abs((beaconY - sensorY))
        val targetDistance = abs(sensorY - targetY)
        if (beaconY == targetY) beacons += beaconX
        if (targetDistance < beaconDistance) {
            val diff = beaconDistance - targetDistance
            notABeacon += (sensorX - diff)..(sensorX + diff)
        }
    }

    return notABeacon.flatten().distinct().filterNot { it in beacons }.count()
}

private const val TUNING_X = 4_000_000L

private fun part2(input: List<List<Int>>, max: Int): Long {
    fun checkSolution(x: Int, y: Int): Boolean {
        if (x !in 0..max || y !in 0..max) return false

        return input.all { (sensorX, sensorY, beaconX, beaconY) ->
            val beaconDistance = abs(beaconX - sensorX) + abs(beaconY - sensorY)
            val pointDistance = abs(x - sensorX) + abs(y - sensorY)
            pointDistance > beaconDistance
        }
    }

    var counter = 0
    for ((sensorX, sensorY, beaconX, beaconY) in input) {
        println(counter++)
        val targetDistance = abs(beaconX - sensorX) + abs(beaconY - sensorY) + 1

        for (yDiff in 0..targetDistance) {
            val xDiff = targetDistance - yDiff
            if (checkSolution(sensorX + xDiff, sensorY + yDiff)) return (sensorX + xDiff) * TUNING_X + (sensorY + yDiff)
            if (checkSolution(sensorX - xDiff, sensorY + yDiff)) return (sensorX - xDiff) * TUNING_X + (sensorY + yDiff)
            if (checkSolution(sensorX + xDiff, sensorY - yDiff)) return (sensorX + xDiff) * TUNING_X + (sensorY - yDiff)
            if (checkSolution(sensorX - xDiff, sensorY - yDiff)) return (sensorX - xDiff) * TUNING_X + (sensorY - yDiff)
        }
    }

    error("There no answer?")
}

private fun readInput(name: String) = readLines(name).map { line ->
    line.replace(Regex("[^\\d-]+"), " ").trim().splitInts(" ")
}
