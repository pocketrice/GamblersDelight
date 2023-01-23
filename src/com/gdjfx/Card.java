package com.gdjfx;

public class Card {
    public enum Suit {
        CLUB,
        DIAMOND,
        HEART,
        SPADE
    }

    public enum Rank {
        ACE,
        TWO,
        THREE,
        FOUR,
        FIVE,
        SIX,
        SEVEN,
        EIGHT,
        NINE,
        TEN,
        JACK,
        QUEEN,
        KING
    }

    public Suit cardSuit;
    int cardValue;
    public Rank cardRank; // Use cardvalue to calculate this

    public Card() { // "Get a random card" -- to actually make sure they match the confines of a real deck, use the "drawCard" method in cardDeck
        cardSuit = Referee.weightedRandom(Suit.values(), new double[4], true);
        cardRank = Referee.weightedRandom(Rank.values(), new double[13], true);
        cardValue = cardRank.ordinal() + 1;
    }

    public Card(Suit cs, int cv) {
        cardSuit = cs;
        cardValue = cv;
        cardRank = Rank.values()[cv-1];
    }
}
