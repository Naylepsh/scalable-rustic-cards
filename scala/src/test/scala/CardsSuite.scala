import munit.ScalaCheckSuite
import org.scalacheck.Prop._
import OrphanInstances.given
import Cards.{*, given}
import org.scalacheck.Gen

class CardsSuite extends ScalaCheckSuite {
  val `2ofSpade` = Card(Rank.Two, Color.Spade)

  property("Red joker is the most valuable card in the deck") {
    forAll { (card: Card) =>
      assertEquals(List(card, redJoker).max, redJoker)
    }
  }

  property("2 of Spade is the least valuable card in the deck") {
    forAll { (card: Card) =>
      assertEquals(List(card, `2ofSpade`).min, `2ofSpade`)
    }
  }

  property("There are 52 cards in the standard deck") {
    forAll(Gen.const(deck)) { (deck: List[Card]) =>
      assertEquals(deck.length, 52)
    }
  }

  property("There are 54 cards in the deck with jokers") {
    forAll(Gen.const(deckWithJokers)) { (deck: List[Card]) =>
      assertEquals(deck.length, 54)
    }
  }
}
