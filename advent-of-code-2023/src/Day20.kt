import CommunicationModule.*

private typealias Modules = Map<String, CommunicationModule>

private const val DAY = "Day20"

fun main() {
    fun testInput(index: Int) = readInput("${DAY}_test$index")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput(0)) shouldBe 32000000
        part1(testInput(1)) shouldBe 11687500
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        measureAnswer { part2(input()) }
    }
}

private fun part1(input: Modules): Int {
    var lowCount = 0
    var highCount = 0
    repeat(times = 1000) {
        val (low, high) = input.pushButton()
        lowCount += low
        highCount += high
    }

    return lowCount * highCount
}

private fun part2(input: Modules): Int {
    TODO()
}

private fun Modules.pushButton(): Pair<Int, Int> {
    val queue = ArrayDeque<Impulse>()
    var lowCount = 0
    var highCount = 0

    fun addImpulses(impulses: List<Impulse>) {
        for (impulse in impulses) {
            queue.addLast(impulse)
            if (impulse.value == SIGNAL_LOW) {
                lowCount++
            } else {
                highCount++
            }
        }
    }

    addImpulses(Button.processImpulse(Impulse.Dummy))
    while (queue.isNotEmpty()) {
        val impulse = queue.removeFirst()
        val destination = get(impulse.destination) ?: continue
        addImpulses(destination.processImpulse(impulse))
    }

    return lowCount to highCount
}

private fun readInput(name: String): Modules {
    val modules = readLines(name) { line ->
        val (descriptor, rawDestinations) = line.split(" -> ")
        val destinations = rawDestinations.split(", ")
        when {
            descriptor == CommunicationModule.BROADCASTER -> Broadcast(destinations)
            descriptor.startsWith("%") -> FlipFlop(descriptor.drop(1), destinations)
            descriptor.startsWith("&") -> Conjunction(descriptor.drop(1), destinations)
            else -> error("Unexpected descriptor: $descriptor")
        }
    }.associateBy { it.label }

    val conjunctionModules = modules.filterValues { it is Conjunction }.keys
    modules.forEach { (label, module) ->
        module.destinations.forEach { destination ->
            if (destination in conjunctionModules) {
                (modules.getValue(destination) as Conjunction).registerInput(label)
            }
        }
    }

    return modules
}

private sealed interface CommunicationModule {
    val label: String
    val destinations: List<String>

    fun processImpulse(impulse: Impulse): List<Impulse>

    data object Button : CommunicationModule {
        override val label: String = "button"
        override val destinations: List<String> = listOf(BROADCASTER)
        override fun processImpulse(impulse: Impulse): List<Impulse> = impulse(SIGNAL_LOW)
    }

    data class Broadcast(override val destinations: List<String>) : CommunicationModule {
        override val label: String = BROADCASTER
        override fun processImpulse(impulse: Impulse): List<Impulse> = impulse(impulse.value)
    }

    data class FlipFlop(
        override val label: String,
        override val destinations: List<String>
    ) : CommunicationModule {
        private var enabled: Boolean = false

        override fun processImpulse(impulse: Impulse): List<Impulse> {
            return if (impulse.value == SIGNAL_LOW) {
                enabled = !enabled
                impulse(if (enabled) SIGNAL_HIGH else SIGNAL_LOW)
            } else {
                emptyList()
            }
        }
    }

    data class Conjunction(
        override val label: String,
        override val destinations: List<String>
    ) : CommunicationModule {
        private val memory = mutableMapOf<String, Int>()

        fun registerInput(label: String) {
            memory[label] = SIGNAL_LOW
        }

        override fun processImpulse(impulse: Impulse): List<Impulse> {
            memory[impulse.source] = impulse.value
            val resultSignal = if (memory.values.all { it == SIGNAL_HIGH }) SIGNAL_LOW else SIGNAL_HIGH
            return impulse(resultSignal)
        }
    }

    companion object {
        const val BROADCASTER = "broadcaster"

        private fun CommunicationModule.impulse(value: Int) = destinations.map { Impulse(label, it, value) }
    }
}

private data class Impulse(
    val source: String,
    val destination: String,
    val value: Int,
) {
    companion object {
        val Dummy = Impulse(source = "", destination = "", value = SIGNAL_NONE)
    }
}

private const val SIGNAL_LOW = -1
private const val SIGNAL_NONE = 0
private const val SIGNAL_HIGH = +1
