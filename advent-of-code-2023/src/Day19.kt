private const val DAY = "Day19"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 19114
        measureAnswer { part1(input()) }
    }

    //"Part 2" {
    //    part2(testInput()) shouldBe 0
    //    measureAnswer { part2(input()) }
    //}
}

private fun part1(input: Pair<Map<String, Workflow>, List<Part>>): Int {
    val (workflows, parts) = input

    return parts
        .filter(workflows::test)
        .sumOf(Part::rating)
}

private fun part2(input: Pair<Map<String, Workflow>, List<Part>>): Int = TODO()

private fun readInput(name: String): Pair<Map<String, Workflow>, List<Part>> {
    val (rawWorkflows, rawParts) = readText(name).split("\n\n").map { it.lines() }

    fun parseRule(rule: String): (Part) -> String? {
        if (':' in rule) {
            val parameter = rule.first()
            val expected = if (rule[1] == '>') 1 else -1
            val (rawValue, result) = rule.drop(2).split(":")
            val value = rawValue.toInt()
            return { part -> result.takeIf { part.params.getValue(parameter) compareTo value == expected } }
        } else {
            return { _ -> rule }
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
        when (val result = workflow.send(part)) {
            "A" -> return true
            "R" -> return false
            else -> currentFlow = result
        }
    }
}

private class Workflow(
    val rules: List<(Part) -> String?>
) {
    fun send(part: Part): String = rules.firstNotNullOf { rule -> rule(part) }
}

private data class Part(
    val params: Map<Char, Int>
) {
    fun rating() = params.values.sum()
}
