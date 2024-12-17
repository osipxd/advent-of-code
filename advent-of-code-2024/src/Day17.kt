private const val DAY = "Day17"

fun main() {
    fun testInput(n: Int) = readInput("${DAY}_test$n")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput(1)) shouldBe "4,6,3,5,6,3,5,2,1,0"
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput(2)) shouldBe 117440
        measureAnswer { part2(input()) }
    }
}

private fun part1(computer: Computer): String = computer.runProgram().joinToString(",")

private fun part2(computer: Computer): Long {
    val states = ArrayDeque<Pair<Long, Int>>()
    fun addStateRange(range: LongRange, index: Int) = states.addAll(range.map { it to index })

    addStateRange(1..7L, index = computer.program.lastIndex)
    while (states.isNotEmpty()) {
        val (registerA, index) = states.removeFirst()
        val result = computer.runProgram(registerA, ignoreLoop = true).single()
        if (result == computer.program[index]) {
            if (index == 0) return registerA
            else addStateRange((registerA * 8)..<((registerA + 1) * 8), index - 1)
        }
    }

    error("It is not possible for the given program")
}

private class Computer(val registers: LongArray, val program: List<Int>) {
    var instructionPointer = 0
    val out = mutableListOf<Int>()

    fun comboOperand(operand: Int): Long = when (operand) {
        in 0..3 -> operand.toLong()
        in 4..6 -> registers[operand - 4]
        else -> error("Invalid combo operand: $operand")
    }

    fun runProgram(registerA: Long = registers[A], ignoreLoop: Boolean = false): List<Int> {
        reset()
        registers[A] = registerA
        while (instructionPointer in program.indices) {
            val pointerBefore = instructionPointer
            val instruction = Instruction.byOpcode(program[instructionPointer])
            with(instruction) { execute(program[instructionPointer + 1]) }
            if (pointerBefore == instructionPointer) instructionPointer += 2
            else if (ignoreLoop) break
        }
        return out.toList()
    }

    private fun reset() {
        instructionPointer = 0
        out.clear()
        registers.fill(0)
    }
}

private const val A = 0
private const val B = 1
private const val C = 2

private fun interface Instruction {
    fun Computer.execute(operand: Int)

    companion object {
        private val instructionsByOpcode = listOf(
            /* adv */ xdv(A),
            /* bxl */ Instruction { operand -> registers[B] = registers[B] xor operand.toLong() },
            /* bst */ Instruction { operand -> registers[B] = comboOperand(operand) % 8 },
            /* jnz */ Instruction { operand -> if (registers[A] != 0L) instructionPointer = operand },
            /* bxc */ Instruction { registers[B] = registers[B] xor registers[C] },
            /* out */ Instruction { operand -> out.add((comboOperand(operand) % 8).toInt()) },
            /* bdv */ xdv(B),
            /* cdv */ xdv(C),
        )

        private fun xdv(register: Int) = Instruction { operand ->
            val denominator = powOf2(comboOperand(operand).toInt())
            registers[register] = registers[A] / denominator
        }

        fun byOpcode(opcode: Int) = instructionsByOpcode[opcode]
    }
}

private fun readInput(name: String): Computer {
    val (rawRegisters, rawProgram) = readText(name).split("\n\n")
    return Computer(
        registers = rawRegisters.lines().map { it.substringAfter(": ").toLong() }.toLongArray(),
        program = rawProgram.substringAfter(": ").splitInts()
    )
}

// Utils

private fun powOf2(power: Int): Int = 1.shl(power)
