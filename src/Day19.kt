fun main() {
    val testInput = readInput("Day19_test")
    val input = readInput("Day19")

    "Part 1" {
        part1(testInput) shouldBe 33
        measureAnswer { part1(input) }
    }

    "Part 2" {
        part2(testInput) shouldBe (56 * 62)
        measureAnswer { part2(input) }
    }
}

private fun readInput(name: String) = readLines(name).map(FactoryBlueprint.Companion::fromString)

private fun part1(input: List<FactoryBlueprint>): Int =
    input.withIndex().sumOf { (i, bp) -> maxGeodes(bp, time = 24) * (i + 1) }

private fun part2(input: List<FactoryBlueprint>): Int =
    input.take(3).map { bp -> maxGeodes(bp, time = 32) }.reduce(Int::times)

private fun maxGeodes(blueprint: FactoryBlueprint, time: Int): Int {
    val maxBotsInTime = Array(time + 2) { 0 }

    val queue = ArrayDeque<State>()

    val seenStates = mutableSetOf<State>()
    fun addNextState(state: State) {
        if (state in seenStates) return

        queue.addLast(state)
        seenStates += state
    }

    addNextState(State(time))
    var maxGeodes = 0

    while (queue.isNotEmpty()) {
        val state = queue.removeFirst()

        if (state.timeLeft == 0) {
            maxGeodes = maxOf(maxGeodes, state.geodes)
            continue
        }

        if (state.geodeBots < maxBotsInTime[state.timeLeft + 1]) continue
        maxBotsInTime[state.timeLeft] = maxOf(state.geodeBots, maxBotsInTime[state.timeLeft])

        addNextState(state.tick())

        if (state.shouldBuildGeodeBot(blueprint)) {
            addNextState(
                state.tick(
                    spendOre = blueprint.geodeBotCostOre,
                    spendObsidian = blueprint.geodeBotCostObsidian,
                    newGeodeBots = +1,
                )
            )
        }

        if (state.shouldBuildObsidianBot(blueprint)) {
            addNextState(
                state.tick(
                    spendOre = blueprint.obsidianBotCostOre,
                    spendClay = blueprint.obsidianBotCostClay,
                    newObsidianBots = +1,
                )
            )
        }

        if (state.shouldBuildClayBot(blueprint)) {
            addNextState(
                state.tick(
                    spendOre = blueprint.clayBotCost,
                    newClayBots = +1,
                )
            )
        }

        if (state.shouldBuildOreBot(blueprint)) {
            addNextState(
                state.tick(
                    spendOre = blueprint.oreBotCost,
                    newOreBots = +1,
                )
            )
        }
    }

    return maxGeodes
}

private data class State(
    val timeLeft: Int,

    val ore: Int = 0,
    val clay: Int = 0,
    val obsidian: Int = 0,
    val geodes: Int = 0,

    val oreBots: Int = 1,
    val clayBots: Int = 0,
    val obsidianBots: Int = 0,
    val geodeBots: Int = 0,
) {
    fun shouldBuildOreBot(bp: FactoryBlueprint) = oreBots < bp.maxOreCost && bp.canBuildOreBot(ore)
    fun shouldBuildClayBot(bp: FactoryBlueprint) = clayBots < bp.obsidianBotCostClay && bp.canBuildClayBot(ore)
    fun shouldBuildObsidianBot(bp: FactoryBlueprint) = obsidianBots < bp.geodeBotCostObsidian && bp.canBuildObsidianBot(ore, clay)
    fun shouldBuildGeodeBot(bp: FactoryBlueprint) = bp.canBuildGeodeBot(ore, obsidian)

    fun tick(
        spendOre: Int = 0,
        spendClay: Int = 0,
        spendObsidian: Int = 0,
        newOreBots: Int = 0,
        newClayBots: Int = 0,
        newObsidianBots: Int = 0,
        newGeodeBots: Int = 0,
    ): State = State(
        timeLeft = timeLeft - 1,
        ore = ore + oreBots - spendOre,
        clay = clay + clayBots - spendClay,
        obsidian = obsidian + obsidianBots - spendObsidian,
        geodes = geodes + geodeBots,
        oreBots = oreBots + newOreBots,
        clayBots = clayBots + newClayBots,
        obsidianBots = obsidianBots + newObsidianBots,
        geodeBots = geodeBots + newGeodeBots,
    )
}

private class FactoryBlueprint(
    val oreBotCost: Int,
    val clayBotCost: Int,
    val obsidianBotCostOre: Int,
    val obsidianBotCostClay: Int,
    val geodeBotCostOre: Int,
    val geodeBotCostObsidian: Int,
) {
    val maxOreCost = maxOf(clayBotCost, obsidianBotCostOre, geodeBotCostOre)

    fun canBuildOreBot(ore: Int) = ore >= oreBotCost
    fun canBuildClayBot(ore: Int) = ore >= clayBotCost
    fun canBuildObsidianBot(ore: Int, clay: Int) = ore >= obsidianBotCostOre && clay >= obsidianBotCostClay
    fun canBuildGeodeBot(ore: Int, obsidian: Int) = ore >= geodeBotCostOre && obsidian >= geodeBotCostObsidian

    companion object {
        private val pattern = Regex(
            "Blueprint \\d+: Each ore robot costs (\\d+) ore. " +
                "Each clay robot costs (\\d+) ore. " +
                "Each obsidian robot costs (\\d+) ore and (\\d+) clay. " +
                "Each geode robot costs (\\d+) ore and (\\d+) obsidian."
        )

        fun fromString(line: String): FactoryBlueprint {
            val (oreCost, clayCost, obsidianCostOre, obsidianCostClay, geodeCostOre, geodeCostObsidian) =
                pattern.matchEntire(line)!!.destructured
            return FactoryBlueprint(
                oreBotCost = oreCost.toInt(),
                clayBotCost = clayCost.toInt(),
                obsidianBotCostOre = obsidianCostOre.toInt(),
                obsidianBotCostClay = obsidianCostClay.toInt(),
                geodeBotCostOre = geodeCostOre.toInt(),
                geodeBotCostObsidian = geodeCostObsidian.toInt(),
            )
        }
    }
}
