private const val DAY = "Day03"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 357
        measureAnswer { part1(input()) }
    }

    //"Part 2" {
    //    part2(testInput()) shouldBe 0
    //    measureAnswer { part2(input()) }
    //}
}

private fun part1(input: List<BatteryBank>): Int = input.sumOf(BatteryBank::calculateJoltage)

private fun part2(input: List<BatteryBank>): Int = TODO()

@JvmInline
value class BatteryBank(val batteries: IntArray) {

    fun calculateJoltage(): Int {
        var firstBattery = batteries[0]
        var secondBattery = batteries[1]

        for ((index, battery) in batteries.withIndex().drop(2)) {
            if (battery > secondBattery) secondBattery = battery
            if (battery > firstBattery && index < batteries.lastIndex) {
                firstBattery = battery
                secondBattery = 0
            }
        }

        return firstBattery * 10 + secondBattery
    }
}

private fun readInput(name: String) = readLines(name) { line ->
    BatteryBank(line.map { it.digitToInt() }.toIntArray())
}