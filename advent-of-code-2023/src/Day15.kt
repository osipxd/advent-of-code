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

private fun hash(value: String): Int = value.fold(initial = 0) { currentValue, char ->
    (currentValue + char.code) * 17 % 256
}

private fun readInput(name: String) = readText(name).split(",")

private fun List<String>.parseOperations() = map { instruction ->
    if ('=' in instruction) {
        val (label, focalLength) = instruction.split("=")
        Put(Lens(label, focalLength.toInt()))
    } else {
        Remove(label = instruction.dropLast(1))
    }
}

private class LensesHashMap {
    private val boxes = List<MutableList<Lens>>(256) { LinkedList() }

    fun put(lens: Lens) {
        val box = findBox(lens.label)
        val index = box.indexOfFirst { it.label == lens.label }
        if (index == -1) {
            box += lens
        } else {
            box[index] = lens
        }
    }

    fun remove(label: String) {
        findBox(label).removeIf { it.label == label }
    }

    private fun findBox(label: String) = boxes[hash(label)]

    fun focusingPower(): Int = boxes.withIndex().sumOf { (i, box) -> box.focusingPower(i) }

    private fun List<Lens>.focusingPower(boxNumber: Int): Int {
        return withIndex().sumOf { (slotNumber, lens) -> (boxNumber + 1) * (slotNumber + 1) * lens.focalLength }
    }
}

private data class Lens(val label: String, val focalLength: Int)

private sealed interface LensOperation {
    data class Remove(val label: String) : LensOperation
    data class Put(val lens: Lens) : LensOperation
}
