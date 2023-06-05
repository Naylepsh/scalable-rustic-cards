import Cards.{ *, given }
import OrphanInstances.given
import War.*
import cats.data.NonEmptyList
import cats.implicits.*
import munit.ScalaCheckSuite
import org.scalacheck.Gen
import org.scalacheck.Prop.*

class WarSuite extends ScalaCheckSuite:
  property(
    "Cards are distributed between players such that no player has more than 1 card over any other"
  ) {
    forAll(Gen.const(deck), Gen.choose(2, 10)) {
      (cards: List[Card], playerCount: Int) =>
        (cards.length == 52 && playerCount > 0) ==> {
          val playerIds = NonEmptyList.fromListUnsafe((1 to playerCount).toList)
          val cardCounts = distributeCards(cards)(playerIds)
            .map(_.cards.length)
            .toList
            .toSet

          1 <= cardCounts.size || cardCounts.size <= 2
        }
    }
  }

  property("All cards are distributed among the players") {
    forAll(Gen.const(deck), Gen.choose(4, 12)) {
      (cards: List[Card], playerCount: Int) =>
        (cards.length == 52 && playerCount > 0) ==> {
          val playerIds = NonEmptyList.fromListUnsafe((1 to playerCount).toList)
          val totalCardCount = distributeCards(cards)(playerIds)
            .map(_.cards.length)
            .foldLeft(0)(_ + _)

          totalCardCount == deck.length
        }
    }
  }
