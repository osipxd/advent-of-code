import java.util.*

fun main() {
    val testInput = readInput("Day13_test")
    val input = readInput("Day13")

    "Part 1" {
        part1(testInput) shouldBe 13
        answer(part1(input))
    }

    "Part 2" {
        part2(testInput) shouldBe 140
        answer(part2(input))
    }
}

private fun part1(input: List<ListNode>): Int {
    var i = 0
    var sum = 0

    input.chunked(2) { (left, right) ->
        i++

        if (left <= right) {
            sum += i
        }
    }

    return sum
}

private fun part2(input: List<ListNode>): Int {
    fun divider(value: Int) = ListNode.Multiple(listOf(ListNode.Multiple(listOf(ListNode.Single(value)))))

    val dividerA = divider(2)
    val dividerB = divider(6)

    val sorted = (input + listOf(dividerA, dividerB)).sorted()

    return (sorted.indexOf(dividerA) + 1) * (sorted.indexOf(dividerB) + 1)
}

private fun readInput(name: String) = readLines(name).filter { it.isNotEmpty() }.mapNotNull { line ->
    val value = StringBuilder()
    val stack = Stack<ListNode?>()

    for (char in line) {
        when {
            char == '[' -> stack.push(null)

            char.isDigit() -> value.append(char)

            else -> {
                if (value.isNotEmpty()) {
                    stack.push(ListNode.Single(value.toString().toInt()))
                    value.clear()
                }

                if (char == ']') {
                    val list = mutableListOf<ListNode>()
                    var last = stack.pop()
                    while (last != null) {
                        list += last
                        last = stack.pop()
                    }

                    stack.push(ListNode.Multiple(list.reversed()))
                }
            }
        }
    }

    stack.single()
}

private sealed interface ListNode : Comparable<ListNode> {

    class Multiple(val values: List<ListNode>) : ListNode {
        override fun toString(): String = values.toString()
    }

    class Single(val value: Int) : ListNode {
        override fun toString(): String = value.toString()
    }

    override fun compareTo(other: ListNode): Int {
        if (this is Single && other is Single) {
            return this.value compareTo other.value
        }

        val left = if (this is Multiple) this else Multiple(listOf(this))
        val right = if (other is Multiple) other else Multiple(listOf(other))

        for (i in 0 until minOf(left.values.size, right.values.size)) {
            val result = left.values[i] compareTo right.values[i]
            if (result != 0) return result
        }

        return left.values.size compareTo right.values.size
    }
}
