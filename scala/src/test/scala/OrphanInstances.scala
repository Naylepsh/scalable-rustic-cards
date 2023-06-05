import org.scalacheck.*

import Arbitrary.arbitrary

object OrphanInstances:
  import Cards.{ Card, Color, Rank }

  given colorGen: Gen[Color] = Gen.oneOf(Color.values)
  given rankGen: Gen[Rank]   = Gen.oneOf(Rank.values)
  given cardGen: Gen[Card] =
    for
      color <- colorGen
      rank  <- rankGen
    yield Card(rank, color)
  given Arbitrary[Card] = Arbitrary(cardGen)
