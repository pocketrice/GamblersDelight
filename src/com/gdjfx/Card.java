// Lucas Xie - P5 AP CSA - 1/26/23 - GDJFX

package com.gdjfx;

import java.util.Arrays;
import java.util.stream.DoubleStream;

public class Card {

    // Effective Java #35: instance fields should replace any use of ordinal().
    public enum Suit {
        CLUB(0),
        DIAMOND(1),
        HEART(2),
        SPADE(3);

        private final int suitValue;
        Suit(int value) {
            this.suitValue = value;
        }

        public int getValue() {
            return suitValue;
        }
    }

    public enum Rank {
        ACE(1),
        TWO(2),
        THREE(3),
        FOUR(4),
        FIVE(5),
        SIX(6),
        SEVEN(7),
        EIGHT(8),
        NINE(9),
        TEN(10),
        JACK(11),
        QUEEN(12),
        KING(13);

        private final int rankValue;
        Rank(int value) {
            this.rankValue = value;
        }

        public int getValue() {
            return rankValue;
        }
    }

    public Suit cardSuit;
    int cardValue;
    public Rank cardRank;

    public Card() { // "Get a random card" -- to actually make sure they match the confines of a real deck, use the "drawCard" method in cardDeck
        cardSuit = weightedRandom(Suit.values(), new double[4], true);
        cardRank = weightedRandom(Rank.values(), new double[13], true);
        cardValue = cardRank.rankValue;
    }

    public Card(Suit suit, int value) {
        cardSuit = suit;
        cardValue = value;
        cardRank = Rank.values()[value-1];
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

        return choices[choices.length-1];
    }
}
