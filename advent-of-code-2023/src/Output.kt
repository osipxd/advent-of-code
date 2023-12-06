import kotlin.time.measureTime

/**
 * Usage:
 * ```
 * "Part 1" {
 *    // Use TaskPartScope DSL here
 * }
 * ```
 */
inline operator fun String.invoke(body: TaskPartScope.() -> Unit) {
    println("=== $this ===")
    runCatching { TaskPartScope().apply(body) }
        .onFailure(Throwable::printStackTrace)
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

    /**
     * It is not a benchmark, so it can be only used to compare algorithms taking
     * long execution time (several seconds or longer).
     */
    fun measureAnswer(calculate: () -> Any?) {
        val value: Any?
        val time = measureTime { value = calculate() }
        println("Answer: $value (done in $time)")
    }
}
