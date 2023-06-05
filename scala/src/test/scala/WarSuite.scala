import Cards.{ *, given }
import OrphanInstances.given
import War.*
import cats.data.NonEmptyList
import cats.implicits.*
import munit.ScalaCheckSuite
import org.scalacheck.Gen
import org.scalacheck.Prop.*

class WarSuite extends ScalaCheckSuite:
  given playersGen: Gen[NonEmptyList[Int]] = Gen.choose(1, deck.length).map:
    playerCount =>
      NonEmptyList.fromListUnsafe((1 to playerCount).toList)

  property(
    "Cards are distributed between players such that no player has more than 1 card over any other"
  ) {
    forAll(Gen.const(deck), playersGen) {
      (cards: List[Card], players: NonEmptyList[Int]) =>
        (cards.length == 52 && players.length > 0) ==> {
          val cardCounts = distributeCards(cards)(players)
            .map(_.cards.length)
            .toList
            .toSet

          1 <= cardCounts.size || cardCounts.size <= 2
        }
    }
  }

  property("All cards are distributed among the players") {
    forAll(Gen.const(deck), playersGen) {
      (cards: List[Card], players: NonEmptyList[Int]) =>
        (cards.length == 52 && players.length > 0) ==> {
          val totalCardCount = distributeCards(cards)(players)
            .map(_.cards.length)
            .foldLeft(0)(_ + _)

          totalCardCount == deck.length
        }
    }
  }
