import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

/**
 * Usage:
 * ```
 * "Part 1" {
 *    // Use PartScope DSL here
 * }
 * ```
 */
inline operator fun String.invoke(body: TaskPartScope.() -> Unit) {
    println("=== $this ===")
    TaskPartScope().apply(body)
    println()
}

class TaskPartScope {
    infix fun <T> T.shouldBe(expected: T) {
        println("Test: $this")
        check(this == expected)
    }

    fun answer(value: Any?) {
        println("Answer: $value")
    }

    @OptIn(ExperimentalTime::class)
    fun measureAnswer(calculate: () -> Any?) {
        val value: Any?
        val time = measureTime { value = calculate() }
        println("Answer: $value (done in $time)")
    }
}
