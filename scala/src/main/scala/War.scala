import scala.util.Random

import Cards.{ *, given }
import cats.data.NonEmptyList
import cats.syntax.all.*

object War:
  case class PlayerState[A](
      id: A,
      cards: List[Card]
  ):
    def drawCard: (Option[Card], PlayerState[A]) =
      cards match
        case head :: next => (Some(head), PlayerState(id, next))
        case Nil          => (None, this)

  def distributeCards[A](deck: List[Card])(
      players: NonEmptyList[A]
  ): NonEmptyList[PlayerState[A]] =
    val groups = deck.grouped(players.length).toArray
    if groups.length == players.length + 1 then
      val leftoverCards = groups(players.length)
      val playerIds     = 1 to players.length
      Random
        .shuffle(playerIds)
        .zip(leftoverCards)
        .foreach: (playerIndex, card) =>
          groups(playerIndex) = card :: groups(playerIndex)
    players
      .zip(NonEmptyList.fromListUnsafe(groups.toList))
      .map(PlayerState(_, _))

  type Players[A]   = NonEmptyList[PlayerState[A]]
  type CardsOnStake = List[Card]
  type Winner[A]    = A

  private case class CardOnTable[A](
      thrownBy: A,
      card: Card
  )

  def playTurn[A](
      players: Players[A]
  ): Option[(Players[A], Winner[A], CardsOnStake)] =
    val playerMoves = players.map: player =>
      player.drawCard match
        case (Some(card), state) => (Some(player.id, card), state)
        case (None, state)       => (None, state)
    playerMoves.map(_._1).collect {
      case Some(id, card) =>
        CardOnTable(id, card)
    } match
      case Nil         => None
      case head :: Nil => None
      case cardsOnTable =>
        val newState = playerMoves.map(_._2)

        cardsOnTable.map(_.card).maxOption match
          case None => None
          case Some(winningCard) =>
            cardsOnTable
              .find(_.card == winningCard)
              .map: winner =>
                (newState, winner.thrownBy, cardsOnTable.map(_.card))

  @annotation.tailrec
  def playGame[A](players: Players[A], roundsLeft: Int): Players[A] =
    if roundsLeft == 0 then players
    else
      playTurn(players) match
        case None => players
        case Some(state, winner, cardsOnStake) =>
          val newstate = state.map {
            case PlayerState(`winner`, cards) =>
              PlayerState(winner, cards ::: cardsOnStake)
            case player => player
          }
          println(s"${players.map(player => (player.id, player.cards.length))}")
          playGame(newstate, roundsLeft - 1)

  private case class PlayerSummary[A](
      id: A,
      cardsWon: List[Card]
  ):
    val cardsWonCount = cardsWon.length

  def determineWinner[A](players: Players[A]): Option[A] =
    val summary = players.map: player =>
      PlayerSummary(player.id, player.cards)
    val mostCardsWon = summary.map(_.cardsWonCount).maximum
    for winner <- summary.filter(_.cardsWonCount == mostCardsWon) match
        case Nil              => None
        case winner :: Nil    => Some(winner)
        case potentialWinners => potentialWinners.maxByOption(_.cardsWon.max)
    yield winner.id

  def run(roundCap: Int) =
    distributeCards(deck.shuffled)
      .andThen(playGame(_, roundCap))
      .andThen(determineWinner)
