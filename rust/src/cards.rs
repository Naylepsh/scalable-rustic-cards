use colored::{ColoredString, Colorize};
use rand::{seq::SliceRandom, thread_rng};
use std::fmt::Display;
use strum::{EnumIter, IntoEnumIterator};

#[derive(Debug, Clone, Copy, PartialEq, Eq, PartialOrd, Ord)]
pub enum Color {
    Black,
    Red,
}

impl Display for Color {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match self {
            Color::Red => write!(f, "{}", "▇".truecolor(230, 63, 63)),
            Color::Black => write!(f, "{}", "▇".truecolor(63, 63, 63)),
        }
    }
}

#[derive(Debug, Clone, Copy, PartialEq, Eq, PartialOrd, Ord, EnumIter)]
pub enum Suit {
    Spades,
    Clubs,
    Diamonds,
    Hearts,
}

impl Display for Suit {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match self {
            Suit::Spades => write!(f, "{}", "♠️".truecolor(63, 63, 63)),
            Suit::Clubs => write!(f, "{}", "♣️".truecolor(63, 63, 63)),
            Suit::Diamonds => write!(f, "{}", "♦️".truecolor(230, 63, 63)),
            Suit::Hearts => write!(f, "{}", "♥️".truecolor(230, 63, 63)),
        }
    }
}

impl Suit {
    fn format(&self, string: &str) -> ColoredString {
        match self {
            Suit::Spades | Suit::Clubs => format!("{string} {self}").truecolor(63, 63, 63),
            Suit::Diamonds | Suit::Hearts => format!("{string} {self}").truecolor(230, 63, 63),
        }
    }
}

#[derive(Debug, PartialEq, Eq, PartialOrd, Ord)]
pub enum Card {
    Two(Suit),
    Three(Suit),
    Four(Suit),
    Five(Suit),
    Six(Suit),
    Seven(Suit),
    Eight(Suit),
    Nine(Suit),
    Ten(Suit),
    Jack(Suit),
    Queen(Suit),
    King(Suit),
    Ace(Suit),
    Joker(Color),
}

impl Display for Card {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match self {
            Card::Two(suit) => write!(f, "{}", suit.format("2")),
            Card::Three(suit) => write!(f, "{}", suit.format("3")),
            Card::Four(suit) => write!(f, "{}", suit.format("4")),
            Card::Five(suit) => write!(f, "{}", suit.format("5")),
            Card::Six(suit) => write!(f, "{}", suit.format("6")),
            Card::Seven(suit) => write!(f, "{}", suit.format("7")),
            Card::Eight(suit) => write!(f, "{}", suit.format("8")),
            Card::Nine(suit) => write!(f, "{}", suit.format("9")),
            Card::Ten(suit) => write!(f, "{}", suit.format("10")),
            Card::Jack(suit) => write!(f, "{}", suit.format("J")),
            Card::Queen(suit) => write!(f, "{}", suit.format("Q")),
            Card::King(suit) => write!(f, "{}", suit.format("K")),
            Card::Ace(suit) => write!(f, "{}", suit.format("A")),
            Card::Joker(color) => write!(f, "{color}   "),
        }
    }
}

#[derive(Debug)]
pub struct OwnedCard {
    pub owner: u32,
    pub card: Card,
}

pub struct Deck {
    cards: Vec<Card>,
}

impl Deck {
    #[expect(dead_code)]
    pub fn new(cards: Vec<Card>) -> Deck {
        Deck { cards }
    }

    pub fn full() -> Deck {
        let mut cards = Suit::iter().fold(vec![], |mut deck, suit| {
            deck.append(&mut vec![
                Card::Two(suit),
                Card::Three(suit),
                Card::Four(suit),
                Card::Five(suit),
                Card::Six(suit),
                Card::Seven(suit),
                Card::Eight(suit),
                Card::Nine(suit),
                Card::Ten(suit),
                Card::Jack(suit),
                Card::Queen(suit),
                Card::King(suit),
                Card::Ace(suit),
            ]);
            deck
        });
        cards.push(Card::Joker(Color::Red));
        cards.push(Card::Joker(Color::Black));
        Deck { cards }
    }

    pub fn shuffle(mut self) -> Self {
        let mut rng = thread_rng();
        self.cards.shuffle(&mut rng);
        self
    }

    pub fn draw(&mut self) -> Option<Card> {
        self.cards.pop()
    }

    pub fn len(&self) -> usize {
        self.cards.len()
    }
}

#[derive(Debug)]
pub struct PlayerState {
    pub player_id: u32,
    pub hand: Vec<Card>,
}

impl PlayerState {
    pub fn new(player_id: u32) -> PlayerState {
        PlayerState {
            player_id,
            hand: Default::default(),
        }
    }

    pub fn draw(&mut self, deck: &mut Deck) -> Option<&Card> {
        self.hand.insert(0, deck.draw()?);
        self.hand.get(0)
    }

    pub fn play_last_card(&mut self) -> Option<OwnedCard> {
        Some(OwnedCard {
            owner: self.player_id,
            card: self.hand.pop()?,
        })
    }

    pub fn add_cards_to_hand(&mut self, mut cards: Vec<Card>) {
        while let Some(card) = cards.pop() {
            self.hand.insert(0, card);
        }
    }
}

#[cfg(test)]
mod tests {
    use super::Deck;

    #[test]
    fn card_count_in_full_deck() {
        assert_eq!(Deck::full().len(), 54);
    }
}
