import LensOperation.Put
import LensOperation.Remove
import java.util.*

private const val DAY = "Day15"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 1320
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput()) shouldBe 145
        measureAnswer { part2(input()) }
    }
}

private fun part1(input: List<String>): Int = input.sumOf(::hash)

private fun part2(input: List<String>): Int {
    val hashMap = LensesHashMap()

    for (operation in input.parseOperations()) {
        when (operation) {
            is Put -> hashMap.put(operation.lens)
            is Remove -> hashMap.remove(operation.label)
        }
    }
    return hashMap.focusingPower()
}

private fun hash(value: String): Int {
    var currentValue = 0
    for (char in value) {
        currentValue += char.code
        currentValue *= 17
        currentValue %= 256
    }
    return currentValue
}

private fun readInput(name: String) = readText(name).split(",")

private fun List<String>.parseOperations() = map { instruction ->
    val operationIndex = instruction.indexOfAny(charArrayOf('=', '-'))
    val label = instruction.substring(0..<operationIndex)
    when (instruction[operationIndex]) {
        '=' -> {
            val focalLength = instruction.substringAfter('=').toInt()
            Put(Lens(label, focalLength))
        }

        '-' -> Remove(label)
        else -> error("Unexpected instruction: $instruction")
    }
}

private class LensesHashMap {
    private val buckets = Array<MutableList<Lens>>(256) { LinkedList() }

    fun put(lens: Lens) {
        val bucket = bucketFor(lens.label)
        val index = bucket.indexOfFirst { it.label == lens.label }
        if (index == -1) {
            bucket += lens
        } else {
            bucket[index] = lens
        }
    }

    fun remove(label: String) {
        bucketFor(label).removeIf { it.label == label }
    }

    private fun bucketFor(label: String) = buckets[hash(label)]

    fun focusingPower(): Int = buckets.withIndex().sumOf { (i, bucket) -> bucket.focusingPower(i) }

    private fun List<Lens>.focusingPower(bucketNumber: Int): Int {
        return withIndex().sumOf { (slotNumber, lens) -> (bucketNumber + 1) * (slotNumber + 1) * lens.focalLength }
    }
}

private data class Lens(val label: String, val focalLength: Int)

private sealed interface LensOperation {
    data class Remove(val label: String) : LensOperation
    data class Put(val lens: Lens) : LensOperation
}
