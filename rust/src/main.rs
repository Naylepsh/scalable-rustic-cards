#![feature(lint_reasons)]

mod cards;
mod war;

use war::GameState;

use crate::cards::Card;

fn main() {
    let mut war = GameState::new(vec![1, 2, 3, 4, 5], 1000).deal_cards();
    print!("Starting hands:");
    loop {
        for player in war.players.values() {
            print!("\nP{} ", player.player_id);
            for card in &player.hand {
                if let Card::Ten(_) = card {
                    print!("{card} ");
                } else {
                    print!("{card}  ");
                }
            }
        }
        print!("\n");

        if let Some(winner) = war.play_round() {
            println!("\nGame over after {} turns, winner: P{}", war.turn, winner);
            break;
        }
    }
}
