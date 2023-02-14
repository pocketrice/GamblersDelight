// Lucas Xie - P5 AP CSA - 1/26/23 - GDJFX

package com.gdjfx;

import org.apache.commons.lang3.ArrayUtils;
import java.util.Arrays;
import java.util.stream.DoubleStream;

public class Dice {
    public int sideCount;
    public int selectedValue; // Calculated value of dice roll
    int[] sideValues; // Values for each side. Array length must equal sideCount (asserted)

    public Dice() { // Default constr.; creates a regular dice.
        sideValues = new int[]{1,2,3,4,5,6};
        sideCount = sideValues.length;
        selectedValue = -1;
    }

    public Dice(int[] sv) {
        assert (Arrays.stream(sv).min().getAsInt() > 0) : "Error: Dice object of name " + this + " has invalid side values; the found minimum of " + Arrays.stream(sv).min() + " is less than 0.";
        sideValues = sv;
        sideCount = sv.length;
        selectedValue = -1; // 2nd assertion guarantees no negatives; this allows for safely checking if selectedValue was not set yet.
    }

    // Generate a theoretical dice roll value. This was intended to be paired with getPhysicalSelectedValue, where based on
    // the highest side of the dice (determined by finding the highest point) a side was selected (which would not be perfectly equal randomness).
    // @param N/A
    // @return theoretical dice roll value
    public int getTheoreticalSelectedValue() { // Based on hard, cold equal probabilities.
        return weightedRandom(ArrayUtils.toObject(sideValues), new double[sideValues.length], true);
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

    // Tracks and rolls the dice.
    // @param N/A
    // @return N/A
    public void roll() {
        selectedValue = getTheoreticalSelectedValue();
    }
}
