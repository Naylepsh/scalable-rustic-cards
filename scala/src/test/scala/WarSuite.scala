import Cards.{ *, given }
import OrphanInstances.given
import War.*
import cats.data.NonEmptyList
import cats.implicits.*
import munit.ScalaCheckSuite
import org.scalacheck.Gen
import org.scalacheck.Prop.*

class WarSuite extends ScalaCheckSuite:
  val deckGen = Gen.oneOf(1 to 1).map(_ => deck.shuffled)

  property(
    "Cards are distributed between players such that no player has more than 1 card over any other"
  ) {
    forAll(deckGen, Gen.choose(2, 10)) { (deck: List[Card], playerCount: Int) =>
      val playerIds = NonEmptyList.fromListUnsafe((1 to playerCount).toList)
      val cardCounts =
        distributeCards(deck)(playerIds)
          .map(_.cards.length)
          .toList
          .toSet

      assert(1 <= cardCounts.size || cardCounts.size <= 2)
    }
  }
