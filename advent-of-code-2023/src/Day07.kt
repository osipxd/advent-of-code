private const val DAY = "Day07"

fun main() {
    fun testInput() = readInput("${DAY}_test")
    fun input() = readInput(DAY)

    "Part 1" {
        solve(testInput()) shouldBe 6440
        measureAnswer { solve(input()) }
    }

    "Part 2" {
        solve(testInput(), withJoker = true) shouldBe 5905
        measureAnswer { solve(input(), withJoker = true) }
    }
}

private fun solve(input: List<Hand>, withJoker: Boolean = false): Int = input
    .sortedWith(Hand.comparator(withJoker))
    .withIndex()
    .sumOf { (index, hand) -> hand.bid * (index + 1) }

private fun readInput(name: String) = readLines(name).map { line ->
    val (cards, rawBid) = line.split(" ")
    Hand(cards, rawBid.toInt())
}

private data class Hand(val cards: String, val bid: Int) {

    fun power(withJoker: Boolean): Int {
        val cardsCount = mutableMapOf<Char, Int>()
        cards.groupingBy { it }.eachCountTo(cardsCount)

        val jokers = if (withJoker) cardsCount.remove('J') ?: 0 else 0
        val maxCardsCount = cardsCount.values.maxOrNull() ?: 0

        return when (maxCardsCount + jokers) {
            5 -> FIVE_OF_KIND
            4 -> FOUR_OF_KIND
            3 -> if (cardsCount.size == 2) FULL_HOUSE else THREE_OF_KIND
            2 -> if (cardsCount.size == 3) TWO_PAIR else ONE_PAIR
            else -> HIGH_CARD
        }
    }

    companion object {
        fun comparator(withJoker: Boolean): Comparator<Hand> {
            return compareBy<Hand> { it.power(withJoker) }
                .thenComparing { left, right ->
                    left.cards.indices.asSequence()
                        .map { i -> compareCards(left.cards[i], right.cards[i], withJoker) }
                        .first { it != 0 }
                }
        }

        private fun compareCards(cardA: Char, cardB: Char, withJoker: Boolean): Int {
            return rankOfCard(cardA, withJoker) compareTo rankOfCard(cardB, withJoker)
        }

        private fun rankOfCard(card: Char, withJoker: Boolean): Int = when (card) {
            in '2'..'9' -> card.digitToInt()
            'T' -> 10
            'J' -> if (withJoker) 1 else 11
            'Q' -> 12
            'K' -> 13
            'A' -> 14
            else -> error("Unexpected card: $card")
        }
    }
}

const val FIVE_OF_KIND = 6
const val FOUR_OF_KIND = 5
const val FULL_HOUSE = 4
const val THREE_OF_KIND = 3
const val TWO_PAIR = 2
const val ONE_PAIR = 1
const val HIGH_CARD = 0
