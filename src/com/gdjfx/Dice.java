package com.gdjfx;

import com.gdjfx.deprecated.Model;
import com.gdjfx.deprecated.ModelTexture;
import org.apache.commons.lang3.ArrayUtils;
import java.util.Arrays;

public class Dice { // todo: might need to store position of dice (rotation, location, etc.); store here or in the actual "environment"?
    public int sideCount;
    public int selectedValue; // Num of sides to dice (for use in rolling / 3d model generator) // calculated value of dice roll
    int[] sideValues; // Values for each side. Array length must equal sideCount (asserted)
    Model model;
    ModelTexture[] sideTextures; // Normally the Model object would hold its own textures to parse. The Dice has add'l transparent textures for the dot thingies on each side separate from the Model's textures. Must match sideCount.

    public Dice() { // Default constr.; creates a regular dice.
        sideValues = new int[]{1,2,3,4,5,6};
        sideCount = sideValues.length;
        model = generateDiceModel();
        sideTextures = null; // Just have some default textures lying around I dunno
        selectedValue = -1;
    }

    public Dice(int[] sv, Model m, ModelTexture[] st) {
        assert (sv.length == st.length) : "Error: Dice object of name " + this + " has invalid # of side values or side textures!\n# of side values: " + sv.length + "\n# of side textures: " + st.length;
        assert (Arrays.stream(sv).min().getAsInt() > 0) : "Error: Dice object of name " + this + " has invalid side values; found minimum of " + Arrays.stream(sv).min() + " is less than 0";
        sideValues = sv;
        sideCount = sv.length;
        model = m;
        sideTextures = st;
        selectedValue = -1; // 2nd assertion guarantees no negatives; this allows for safely checking if selectedValue was not set yet.
    }

    public Model generateDiceModel() {
        return new Model(); // based on side count generate a regular prism... thingy... whatever it's called
    }

    public int getRealSelectedValue(int[] coords, int[] rotation) {
        return -1; // Based on side from dice with highest Y value (e.g. point X is highest, which lies on side Z so that side is selected).
    }

    public int getTheoreticalSelectedValue() { // Based on hard, cold equal probabilities.
        return Referee.weightedRandom(ArrayUtils.toObject(sideValues), new double[sideValues.length], true);
    }

    public void roll() {
        selectedValue = getTheoreticalSelectedValue();
    }
}
