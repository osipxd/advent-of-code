private const val DAY = "Day7"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        part1(testInput()) shouldBe 6440
        measureAnswer { part1(input()) }
    }

    "Part 2" {
        part2(testInput()) shouldBe 5905
        measureAnswer { part2(input()) }
    }
}

private fun part1(input: List<Hand>): Int = input
    .sortedWith(Hand.comparator(rankByCard()) { it.power() })
    .withIndex()
    .sumOf { (index, hand) -> hand.bid * (index + 1) }

private fun part2(input: List<Hand>): Int = input
    .sortedWith(Hand.comparator(rankByCard(withJoker = true)) { it.powerWithJoker() })
    .withIndex()
    .sumOf { (index, hand) -> hand.bid * (index + 1) }

private fun readInput(name: String) = readLines(name)
    .map { line ->
        val (cards, rawBid) = line.split(" ")
        Hand(cards, rawBid.toInt())
    }

private data class Hand(val cards: String, val bid: Int) {

    fun power(): Int {
        val hist = mutableMapOf<Char, Int>()
        for (card in cards) hist[card] = hist.getOrDefault(card, 0) + 1

        return when {
            hist.size == 1 -> FIVE_OF_KIND
            hist.size == 2 && hist.values.any { it == 4 } -> FOUR_OF_KIND
            hist.size == 2 -> FULL_HOUSE
            hist.size == 3 && hist.values.any { it == 3 } -> THREE_OF_KIND
            hist.size == 3 -> TWO_PAIR
            hist.size == 4 -> ONE_PAIR
            else -> HIGH_CARD
        }
    }

    fun powerWithJoker(): Int {
        val hist = mutableMapOf<Char, Int>()
        for (card in cards) hist[card] = hist.getOrDefault(card, 0) + 1
        val jokers = hist.remove('J') ?: 0

        return when (jokers) {
            0 -> power()
            1 -> when {
                hist.size == 1 -> FIVE_OF_KIND // *AAA -> AAAA
                hist.size == 2 && hist.values.any { it == 3 } -> FOUR_OF_KIND // *AAAK -> AAAAK
                hist.size == 2 -> FULL_HOUSE // *AAKK -> AAAKK
                hist.size == 3 -> THREE_OF_KIND // *AAKQ -> AAAKQ
                else -> ONE_PAIR // *AKQT -> AAKQT
            }

            2 -> when (hist.size) {
                1 -> FIVE_OF_KIND // **AAA -> AAAAA
                2 -> FOUR_OF_KIND // **AAK -> AAAAK
                else -> THREE_OF_KIND // **AKQ -> AAAKQ
            }
            
            3 -> when (hist.size) {
                1 -> FIVE_OF_KIND // ***AA -> AAAAA
                else -> FOUR_OF_KIND // ***AK -> AAAAK
            }
            
            else -> FIVE_OF_KIND // ***** -> AAAAAA, ****A -> AAAAA
        }
    }

    companion object {
        fun comparator(rankByCard: Map<Char, Int>, calculatePower: (Hand) -> Int) = compareBy(
            calculatePower,
            { rankByCard.getValue(it.cards[0]) },
            { rankByCard.getValue(it.cards[1]) },
            { rankByCard.getValue(it.cards[2]) },
            { rankByCard.getValue(it.cards[3]) },
            { rankByCard.getValue(it.cards[4]) },
        )
    }
}

private fun rankByCard(withJoker: Boolean = false) = mapOf(
    '2' to 0,
    '3' to 1,
    '4' to 2,
    '5' to 3,
    '6' to 4,
    '7' to 5,
    '8' to 6,
    '9' to 7,
    'T' to 8,
    'J' to if (withJoker) -1 else 9,
    'Q' to 10,
    'K' to 11,
    'A' to 12,
)

const val FIVE_OF_KIND = 6
const val FOUR_OF_KIND = 5
const val FULL_HOUSE = 4
const val THREE_OF_KIND = 3
const val TWO_PAIR = 2
const val ONE_PAIR = 1
const val HIGH_CARD = 0
