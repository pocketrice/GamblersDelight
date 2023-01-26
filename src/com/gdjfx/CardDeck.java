// Lucas Xie - P5 AP CSA - 1/26/23 - GDJFX

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

    public CardDeck(Card[] cards, Color color) {
        this.cards = cards;
        workingCards = cards;
        cardCount = cards.length;
        deckColor = color;
    }


    // Generates a suit of cards. This is useful if you want to generate a custom suit more systematically (e.g. easier to trim off all 3s from deck).
    // No jokers are included.
    // @param suit - card suit type to generate
    // @return full 13-card suit
    public Card[] generateSuit(Card.Suit suit) {
        Card[] newSuit = new Card[13];
        for (int i = 0; i < newSuit.length; i++) {
            newSuit[i] = new Card(suit, i+1);
        }

        return newSuit;
    }

    // Generates a full, 52-card valid deck of cards. No jokers are included.
    // @param N/A
    // @return full 52-card deck
    public Card[] generateValidDeck() {
        List<Card> newDeck = new ArrayList<>();
        newDeck.addAll(Arrays.asList(generateSuit(Card.Suit.CLUB)));
        newDeck.addAll(Arrays.asList(generateSuit(Card.Suit.DIAMOND)));
        newDeck.addAll(Arrays.asList(generateSuit(Card.Suit.HEART)));
        newDeck.addAll(Arrays.asList(generateSuit(Card.Suit.SPADE)));

        return newDeck.toArray(new Card[]{});
    }

    // Generates a random rgb color. deckColor is not used so this method has no use.
    // @param N/A
    // @return randomly generated rgb color
    public Color generateRandomColor() {
        return new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255));
    }

    // Resets working cards deck (see above for info).
    // @param N/A
    // @return N/A
    public void resetWorkingCards() { // Resets working cards
        workingCards = cards;
    }

}
