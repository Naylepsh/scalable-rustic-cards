import scala.util.Random

object Cards:
  enum Color:
    case Spade, Club, Heart, Diamond
  object Color:
    given Ordering[Color] with
      def compare(x: Color, y: Color): Int = x.ordinal compare y.ordinal

  enum Rank:
    case Two, Three, For, Fives, Six, Seven, Eight, Nine, Ten, Jack, Queen,
      King, Ace, Joker
  object Rank:
    given Ordering[Rank] with
      def compare(x: Rank, y: Rank): Int =
        x.ordinal compare y.ordinal

  case class Card(rank: Rank, color: Color)
  object Card:
    given Ordering[Card] with
      def compare(x: Card, y: Card): Int =
        val rankDiff = x.rank.ordinal compare y.rank.ordinal
        val colorDiff = x.color.ordinal compare y.color.ordinal

        if rankDiff == 0 then colorDiff
        else rankDiff

  val redJoker = Card(Rank.Joker, Color.Diamond)
  val blackJoker = Card(Rank.Joker, Color.Club)

  val deck: List[Card] =
    (for
      rank <- Rank.values.filterNot(_ == Rank.Joker)
      color <- Color.values
    yield Card(rank, color)).toList

  val deckWithJokers: List[Card] = redJoker :: blackJoker :: deck

  extension (deck: List[Card]) def shuffled: List[Card] = Random.shuffle(deck)
