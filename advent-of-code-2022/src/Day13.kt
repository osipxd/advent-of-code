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

private fun part1(packets: List<Packet>): Int {
    var i = 0
    var sum = 0

    packets.chunked(2) { (left, right) ->
        i++
        if (left <= right) sum += i
    }

    return sum
}

private fun part2(packets: List<Packet>): Int {
    // Shorthand to create divider item in form [[$value]]
    fun divider(value: Int) = Packet.Complex(Packet.Complex(Packet.Simple(value)))

    val dividerA = divider(2)
    val dividerB = divider(6)

    return (packets.count { it < dividerA } + 1) * (packets.count { it < dividerB } + 2)
}

private fun readInput(name: String) = readLines(name).filter(String::isNotEmpty).map(::eval)

// Sad joke. We have no eval, so let's parse lists!
private fun eval(line: String): Packet {
    val number = StringBuilder()
    val stack = ArrayDeque<Packet?>()

    fun pushNumber() {
        if (number.isNotEmpty()) {
            stack.addFirst(Packet.Simple(number.toString().toInt()))
            number.clear()
        }
    }

    fun pushComplexPacket() {
        val nested = ArrayDeque<Packet>()
        var current = stack.removeFirst()

        // Remove elements until list start
        while (current != null) {
            nested.addFirst(current)
            current = stack.removeFirst()
        }

        // Push back nested list
        stack.addFirst(Packet.Complex(nested))
    }

    for (char in line) {
        when (char) {
            // We will use `null` as a marker of list start
            '[' -> stack.addFirst(null)
            ',' -> pushNumber()
            ']' -> {
                pushNumber()
                pushComplexPacket()
            }

            else -> number.append(char)
        }
    }

    return checkNotNull(stack.single())
}

private sealed interface Packet : Comparable<Packet> {

    class Simple(val value: Int) : Packet {
        override fun toString(): String = value.toString()

        override fun compareTo(other: Packet): Int = when (other) {
            is Simple -> value compareTo other.value
            is Complex -> Complex(this) compareTo other
        }
    }

    class Complex(val values: List<Packet>) : Packet {
        constructor(value: Packet) : this(listOf(value))

        override fun toString(): String = values.toString()

        override fun compareTo(other: Packet): Int {
            val left = this
            val right = if (other is Complex) other else Complex(other)

            for (i in 0 until minOf(left.values.size, right.values.size)) {
                val result = left.values[i] compareTo right.values[i]
                if (result != 0) return result
            }

            return left.values.size compareTo right.values.size
        }
    }
}
