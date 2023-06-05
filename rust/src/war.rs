use std::collections::HashMap;

use crate::cards::{Deck, PlayerState};
use rand::{seq::IteratorRandom, thread_rng};

#[derive(Debug)]
pub struct GameState {
    pub players: HashMap<u32, PlayerState>,
    pub losers: Vec<u32>,
    pub turn: u32,
    max_turns: u32,
}

impl GameState {
    pub fn new(players: Vec<u32>, max_turns: u32) -> GameState {
        assert!(players.len() > 1);
        GameState {
            players: players
                .into_iter()
                .map(|id| (id, PlayerState::new(id)))
                .collect(),
            losers: vec![],
            turn: 0,
            max_turns,
        }
    }

    pub fn deal_cards(mut self) -> GameState {
        let mut deck = Deck::full().shuffle();
        let remainder = deck.len() % self.players.len();

        while deck.len() > remainder {
            for player in self.players.values_mut() {
                player.draw(&mut deck);
            }
        }

        let mut rng = thread_rng();
        for player in self
            .players
            .values_mut()
            .choose_multiple(&mut rng, remainder)
        {
            player.draw(&mut deck);
        }

        self
    }

    /// Returns the winner if the game is over
    pub fn play_round(&mut self) -> Option<u32> {
        let mut played_cards = vec![];
        let mut new_losers = vec![];

        for player in self.players.values_mut() {
            if let Some(card) = player.play_last_card() {
                played_cards.push(card);
            } else {
                new_losers.push(player.player_id);
            }
        }

        for loser in new_losers {
            if let Some(player) = self.players.remove(&loser) {
                self.losers.push(player.player_id);
            }
        }

        let best_card_owner = played_cards
            .iter()
            .max_by(|a, b| a.card.cmp(&b.card))
            .map(|card| card.owner);

        let won_cards = played_cards.into_iter().map(|owned| owned.card).collect();
        if let Some(owner) = best_card_owner {
            self.players
                .get_mut(&owner)
                .expect("Invalid player index")
                .add_cards_to_hand(won_cards);
        }

        self.turn += 1;
        self.get_winner()
    }

    pub fn get_winner(&mut self) -> Option<u32> {
        if self.players.len() == 1 {
            Some(
                self.players
                    .values()
                    .next()
                    .expect("Player index error")
                    .player_id,
            )
        } else if self.turn >= self.max_turns {
            Some(self.get_winer_by_hand())
        } else {
            None
        }
    }

    pub fn get_winer_by_hand(&mut self) -> u32 {
        let best_players = self.get_players_with_largest_hands();
        if best_players.len() == 1 {
            return best_players
                .first()
                .expect("Invalid player index")
                .player_id;
        }

        Self::pick_player_with_highest_card(best_players)
    }

    pub fn get_players_with_largest_hands(&mut self) -> Vec<&mut PlayerState> {
        let mut players = vec![];
        let mut max_cards = 0;
        for player in self.players.values_mut() {
            let len = player.hand.len();
            if len > max_cards {
                players.clear();
                players.push(player);
                max_cards = len;
            } else if len == max_cards {
                players.push(player)
            }
        }
        players
    }

    pub fn pick_player_with_highest_card(best_players: Vec<&mut PlayerState>) -> u32 {
        let mut all_cards = vec![];
        for player in best_players {
            while let Some(card) = player.play_last_card() {
                all_cards.push(card);
            }
        }

        let best_card_owner = all_cards
            .iter()
            .max_by(|a, b| a.card.cmp(&b.card))
            .map(|card| card.owner);

        best_card_owner.expect("Invalid winner")
    }
}

#[cfg(test)]
mod tests {
    use crate::cards::{Card, Color};

    use super::GameState;

    #[test]
    fn even_split_2_players() {
        let game = GameState::new(vec![1, 2], 1000).deal_cards();
        assert_eq!(game.players[&1].hand.len(), game.players[&2].hand.len())
    }

    #[test]
    fn pick_player_with_highest_card() {
        let mut game = GameState::new(vec![1, 2], 1000).deal_cards();
        let mut player_with_red_joker = 1;
        'outer: for player in game.players.values() {
            for card in player.hand.iter() {
                if *card == Card::Joker(Color::Red) {
                    player_with_red_joker = player.player_id;
                    break 'outer;
                }
            }
        }
        assert_eq!(game.get_winer_by_hand(), player_with_red_joker);
    }
}
