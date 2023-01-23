package com.gdjfx;


import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CardDeck {
    public Card[] cards;
    Card[] workingCards; // cards contains ALL cards in deck. workingCards is a "pile" where the deck can be manipulated (e.g. remove cards)
    Color deckColor;
    public int cardCount;

    public CardDeck() {
        cards = generateValidDeck();
        workingCards = cards;
        cardCount = cards.length;
        deckColor = generateRandomColor();
    }
// "There's millions of people living on this planet that haven't even tried crappy ice cream"
    public CardDeck(Card[] cards, Color color) {
        this.cards = cards;
        workingCards = cards;
        cardCount = cards.length;
        deckColor = color;
    }

    public Card[] generateSuit(Card.Suit suit) { // Generates a suit of cards. This is useful if you want to generate a custom suit more systematically
        Card[] newSuit = new Card[13];
        for (int i = 0; i < newSuit.length; i++) {
            newSuit[i] = new Card(suit, i+1);
        }

        return newSuit;
    }

    public Card[] generateValidDeck() {
        List<Card> newDeck = new ArrayList<>();
        newDeck.addAll(Arrays.asList(generateSuit(Card.Suit.CLUB)));
        newDeck.addAll(Arrays.asList(generateSuit(Card.Suit.DIAMOND)));
        newDeck.addAll(Arrays.asList(generateSuit(Card.Suit.HEART)));
        newDeck.addAll(Arrays.asList(generateSuit(Card.Suit.SPADE)));

        return newDeck.toArray(new Card[]{});
    }

    public Color generateRandomColor() {
        return new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255));
    }

    public void resetWorkingCards() { // Resets working cards
        workingCards = cards;
    }

    public void shuffleDeck(int iterations) {

    }

}
