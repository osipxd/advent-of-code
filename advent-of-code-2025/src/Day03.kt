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
        var totalJoltage = 0L

        var index = 0
        var pickedBatteryIndex = 0
        var leftToPick = count

        while (index <= batteries.lastIndex) {
            if (batteries[index] > batteries[pickedBatteryIndex]) pickedBatteryIndex = index

            val spaceLeft = batteries.lastIndex - index + 1
            if (spaceLeft > leftToPick) {
                // Search further for the max battery
                index++
            } else {
                // Add the picked battery
                totalJoltage = totalJoltage * 10 + batteries[pickedBatteryIndex]
                leftToPick--

                // Reset index to the next after the picked battery
                index = pickedBatteryIndex + 1
                pickedBatteryIndex = index
            }
        }

        return totalJoltage
    }
}

private fun readInput(name: String) = readLines(name) { line -> BatteryBank(line.map(Char::digitToInt)) }