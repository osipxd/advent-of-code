import kotlin.math.max
import kotlin.math.min

private const val DAY = "Day19"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 19114
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput()) shouldBe 167409079868000
        measureAnswer { part2(input()) }
    }
}

private fun part1(input: Pair<Map<String, Workflow>, List<Part>>): Int {
    val (workflows, parts) = input

    return parts
        .filter(workflows::test)
        .sumOf(Part::rating)
}

private fun part2(input: Pair<Map<String, Workflow>, List<Part>>): Long {
    val workflows = input.first
    val possibleRanges = mutableListOf<PartRange>()
    val impossiblePartRanges = mutableListOf<PartRange>()
    val rangesOverlaps = mutableListOf<PartRange>()
    val queue = ArrayDeque<Pair<String, PartRange>>()

    fun addPossibleRange(range: PartRange) {
        val nonOverlapping = possibleRanges.fold(range) { newRange, possibleRange -> newRange - possibleRange }
        if (range != nonOverlapping) {
            println("$range -> $nonOverlapping")
        } else {
            range.printValue()
        }
        if (!nonOverlapping.isEmpty()) {
            possibleRanges += nonOverlapping
        }
    }

    queue.addFirst("in" to PartRange())
    while (queue.isNotEmpty()) {
        val (workflowId, startRange) = queue.removeFirst()
        val workflow = workflows.getValue(workflowId)

        var range = startRange
        for (rule in workflow.rules) {
            val (passedRange, restOfRange) = range.splitBy(rule)
            if (passedRange != null) {
                when (rule.result) {
                    "A" -> addPossibleRange(passedRange)
                    "R" -> impossiblePartRanges += passedRange
                    else -> queue.addLast(rule.result to passedRange)
                }
            }
            range = restOfRange ?: break
        }
    }

    val possible = possibleRanges.sumOf { it.count() }.printValue()
//    impossiblePartRanges.forEach { it.printValue() }
//    val impossible = impossiblePartRanges.sumOf { it.count() }.printValue()
//    (possible + impossible).printValue()
    PartRange().count().printValue()
    return possible
}

private fun readInput(name: String): Pair<Map<String, Workflow>, List<Part>> {
    val (rawWorkflows, rawParts) = readText(name).split("\n\n").map { it.lines() }

    fun parseRule(rule: String): Rule {
        if (':' in rule) {
            val parameter = rule.first()
            val (rawValue, result) = rule.drop(2).split(":")
            val value = rawValue.toInt()
            return if (rule[1] == '>') Rule.Grater(result, parameter, value) else Rule.Lower(result, parameter, value)
        } else {
            return Rule.Constant(rule)
        }
    }

    fun parseWorkflow(line: String): Pair<String, Workflow> {
        val label = line.substringBefore('{')
        val rules = line.substringAfter('{').dropLast(1).split(",")
        return label to Workflow(rules.map(::parseRule))
    }

    fun parsePart(line: String): Part {
        val parameters = line.trim('{', '}').split(",").associate { rawParam ->
            val (label, value) = rawParam.split("=")
            label.first() to value.toInt()
        }
        return Part(parameters)
    }

    return rawWorkflows.associate(::parseWorkflow) to rawParts.map(::parsePart)
}

private fun Map<String, Workflow>.test(part: Part): Boolean {
    var currentFlow = "in"
    while (true) {
        val workflow = getValue(currentFlow)
        when (val result = workflow.process(part)) {
            "A" -> return true
            "R" -> return false
            else -> currentFlow = result
        }
    }
}

private data class Workflow(
    val rules: List<Rule>
) {
    fun process(part: Part): String = rules.firstNotNullOf { it.process(part) }
}

private sealed interface Rule {
    val result: String
    fun process(part: Part): String?

    data class Constant(override val result: String) : Rule {
        override fun process(part: Part): String = result
    }

    data class Grater(
        override val result: String,
        val parameter: Char,
        val value: Int,
    ) : Rule {
        override fun process(part: Part): String? = result.takeIf { part.params.getValue(parameter) > value }
    }

    data class Lower(
        override val result: String,
        val parameter: Char,
        val value: Int,
    ) : Rule {
        override fun process(part: Part): String? = result.takeIf { part.params.getValue(parameter) < value }
    }
}

private data class Part(
    val params: Map<Char, Int>,
) {
    fun rating() = params.values.sum()
}

private data class PartRange(
    val params: Map<Char, IntRange> = mapOf(
        'x' to 1..4000,
        'm' to 1..4000,
        'a' to 1..4000,
        's' to 1..4000,
    )
) {
    fun isEmpty() = params.values.any { it.isEmpty() }

    fun count() = params.values.map { it.last - it.first + 1L }.reduce(Long::times)

    fun splitBy(rule: Rule): Pair<PartRange?, PartRange?> = when (rule) {
        is Rule.Constant -> this to null

        is Rule.Grater -> {
            val ranges = params.getValue(rule.parameter).splitBy(rule.value + 1)
            ranges.reversed().map { range -> withParameterRange(rule.parameter, range) }
        }

        is Rule.Lower -> {
            val ranges = params.getValue(rule.parameter).splitBy(rule.value)
            ranges.map { range -> withParameterRange(rule.parameter, range) }
        }
    }

    private fun withParameterRange(parameter: Char, range: IntRange): PartRange? {
        if (range.isEmpty()) return null
        return PartRange(params = params + (parameter to range))
    }

    operator fun minus(other: PartRange): PartRange {
        val nonOverlapping = params.mapValues { (parameter, range) -> range exclude other.params.getValue(parameter) }
        return if (nonOverlapping.isEmpty()) this else PartRange(nonOverlapping)
    }
}

private fun IntRange.splitBy(secondRangeStart: Int): Pair<IntRange, IntRange> {
    return (first..<secondRangeStart) to (secondRangeStart..last)
}

private infix fun IntRange.intersect(other: IntRange): IntRange {
    return max(this.first, other.first)..min(this.last, other.last)
}

private infix fun IntRange.exclude(other: IntRange): IntRange {
    if (other !in this) return this
    return min(this.first, other.last + 1)..max(this.last, other.first - 1)
}

private operator fun IntRange.contains(other: IntRange): Boolean = other.first in this || other.last in this

private fun <A, B> Pair<A, B>.reversed(): Pair<B, A> = second to first
private fun <T, R> Pair<T, T>.map(transform: (T) -> R): Pair<R, R> = transform(first) to transform(second)
