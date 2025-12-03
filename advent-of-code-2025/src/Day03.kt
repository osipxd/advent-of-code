private const val DAY = "Day03"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 357
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput()) shouldBe 3121910778619
        measureAnswer { part2(input()) }
    }
}

private fun part1(input: List<BatteryBank>): Long = input.sumOf { it.calculateJoltage(count = 2) }
private fun part2(input: List<BatteryBank>): Long = input.sumOf { it.calculateJoltage(count = 12) }

@JvmInline
value class BatteryBank(val batteries: List<Int>) {

    fun calculateJoltage(count: Int): Long {
        val pickedBatteries = IntArray(count) { it }
        val lastPickedIndex = pickedBatteries.lastIndex

        fun pickBatteries(replaceIndex: Int, batteryIndex: Int) {
            for (i in 0..lastPickedIndex - replaceIndex) pickedBatteries[replaceIndex + i] = batteryIndex + i
        }

        for ((index, battery) in batteries.withIndex().drop(1)) {
            val spaceLeft = batteries.lastIndex - index
            for (replaceIndex in (lastPickedIndex - spaceLeft).coerceAtLeast(0)..index.coerceAtMost(lastPickedIndex)) {
                if (index > pickedBatteries[replaceIndex] && battery > batteries[pickedBatteries[replaceIndex]]) {
                    pickBatteries(replaceIndex, index)
                    break
                }
            }
        }

        return pickedBatteries.fold(0L) { acc, pickedBattery -> acc * 10 + batteries[pickedBattery] }
    }
}

private fun readInput(name: String) = readLines(name) { line -> BatteryBank(line.map(Char::digitToInt)) }