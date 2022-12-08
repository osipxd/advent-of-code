
/**
 * Usage:
 * ```
 * "Part 1" {
 *    // Use PartScope DSL here
 * }
 * ```
 */
inline operator fun String.invoke(body: PartScope.() -> Unit) {
    println("=== $this ===")
    PartScope().apply(body)
    println()
}

class PartScope {
    infix fun <T> T.shouldBe(expected: T) {
        println("Test: $this")
        check(this == expected)
    }

    fun answer(value: Any?) {
        println("Answer: $value")
    }
}
