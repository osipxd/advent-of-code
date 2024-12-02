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
        assertEquals(expected, this)
    }

    fun answer(value: Any?, expected: Any? = null) {
        println("Answer: $value")
        if (expected != null) assertEquals(expected, value)
    }

    /**
     * It is not a benchmark, so it can be only used to compare algorithms taking
     * long execution time (several seconds or longer).
     */
    fun measureAnswer(expected: Any? = null, calculate: () -> Any?) {
        val value: Any?
        val time = measureTime { value = calculate() }
        println("Answer: $value (done in $time)")
        if (expected != null) assertEquals(expected, value)
    }

    private fun assertEquals(expected: Any?, actual: Any?) {
        check(actual == expected) { "Expected '$expected', but was '$actual'" }
    }
}
