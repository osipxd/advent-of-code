private const val DAY = "Day17"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe "4,6,3,5,6,3,5,2,1,0"
        measureAnswer { part1(input()) }
    }

    //"Part 2" {
    //    part2(testInput()) shouldBe 0
    //    measureAnswer { part2(input()) }
    //}
}

private fun part1(computer: Computer): String {
    computer.runProgram()
    return computer.out.joinToString(",")
}

private fun part2(computer: Computer): Int = TODO()

private class Computer(val registers: IntArray, val program: List<Int>) {
    var instructionPointer = 0
    val out = mutableListOf<Int>()

    fun comboOperand(operand: Int): Int = when (operand) {
        in 0..3 -> operand
        in 4..6 -> registers[operand - 4]
        else -> error("Invalid combo operand: $operand")
    }

    fun runProgram() {
        while (instructionPointer in program.indices) {
            val pointerBefore = instructionPointer
            val instruction = Instruction.byOpcode(program[instructionPointer])
            with(instruction) { execute(program[instructionPointer + 1]) }
            if (pointerBefore == instructionPointer) instructionPointer += 2
        }
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
            /* bxl */ Instruction { operand -> registers[B] = registers[B] xor operand },
            /* bst */ Instruction { operand -> registers[B] = comboOperand(operand) % 8 },
            /* jnz */ Instruction { operand -> if (registers[A] != 0) instructionPointer = operand },
            /* bxc */ Instruction { registers[B] = registers[B] xor registers[C] },
            /* out */ Instruction { operand -> out.add(comboOperand(operand) % 8) },
            /* bdv */ xdv(B),
            /* cdv */ xdv(C),
        )

        private fun xdv(register: Int) = Instruction { operand ->
            val denominator = powOf2(comboOperand(operand))
            registers[register] = registers[A] / denominator
        }

        fun byOpcode(opcode: Int) = instructionsByOpcode[opcode]
    }
}

private fun readInput(name: String): Computer {
    val (rawRegisters, rawProgram) = readText(name).split("\n\n")
    return Computer(
        registers = rawRegisters.lines().map { it.substringAfter(": ").toInt() }.toIntArray(),
        program = rawProgram.substringAfter(": ").splitInts()
    )
}

// Utils

private fun powOf2(power: Int): Int = 1.shl(power)
