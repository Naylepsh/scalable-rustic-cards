import scala.util.Random

object Cards:
  enum Color:
    case Diamond, Heart, Club, Spade
  object Color:
    given Ordering[Color] with
      def compare(x: Color, y: Color): Int = x.ordinal compare y.ordinal

    val colors = List(Color.Diamond, Color.Heart, Color.Club, Color.Spade)

  enum Rank:
    case Numeric(value: Int)
    case Jack, Queen, King, Joker
  object Rank:
    given Ordering[Rank] with
      def compare(x: Rank, y: Rank): Int = x.ordinal compare y.ordinal

    val ranks = (2 to 10).toList.map(Rank.Numeric(_))
      ::: List(Rank.Jack, Rank.Queen, Rank.King)

  case class Card(rank: Rank, color: Color)
  object Card:
    given Ordering[Card] with
      def compare(x: Card, y: Card): Int =
        val rankDiff = x.rank.ordinal compare y.rank.ordinal
        val colorDiff = x.color.ordinal compare y.color.ordinal

        if rankDiff == 0 then colorDiff
        else rankDiff

  val jokers =
    List(Card(Rank.Joker, Color.Diamond), Card(Rank.Joker, Color.Club))

  def deck: List[Card] =
    for
      rank <- Rank.ranks
      color <- Color.colors
    yield Card(rank, color)

  def shuffledDeck: List[Card] = Random.shuffle(deck)
