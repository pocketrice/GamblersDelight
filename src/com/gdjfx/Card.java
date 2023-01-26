// Lucas Xie - P5 AP CSA - 1/26/23 - GDJFX

package com.gdjfx;

import java.util.Arrays;
import java.util.stream.DoubleStream;

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
        cardSuit = weightedRandom(Suit.values(), new double[4], true);
        cardRank = weightedRandom(Rank.values(), new double[13], true);
        cardValue = cardRank.ordinal() + 1;
    }

    public Card(Suit cs, int cv) {
        cardSuit = cs;
        cardValue = cv;
        cardRank = Rank.values()[cv-1];
    }

    // Randomly select a choice out of a given pool either equally or individually weighted.
    // @param choices - generic array of objects
    // @param weights - array of weights of equal length to choices corresponding to each index
    // @param autoEqualize - should weights be overwritten with an array of equalized weights?
    // @return randomly selected object from objects
    public static <T> T weightedRandom(T[] choices, double[] weights, boolean autoEqualize)
    {
        double rng = Math.random();

        if (autoEqualize) {
            Arrays.fill(weights, 1.0 / choices.length);
        }

        assert (DoubleStream.of(weights).sum() != 1) : "Error: weightedRandom weights do not add up to 1 (= " + DoubleStream.of(weights).sum() + ")!";
        assert (choices.length == weights.length) : "Error: weightedRandom choice (" + choices.length + ") and weights (" + weights.length + ") array are not the same length!";

        for (int i = 0; i < weights.length; i++) {
            if (rng < weights[i])
                return choices[i];
            else
                rng -= weights[i];
        }

        return null;
    }
}
