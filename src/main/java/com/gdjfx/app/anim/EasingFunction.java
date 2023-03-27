package com.gdjfx.app.anim;

import java.util.function.Function;

// All functions (and formulas) credit to https://easings.net, by Andrey Sitnik and Ivan Solovev.
public enum EasingFunction {
    LINEAR(t -> t),
    EASE_IN_SINE(t -> 1-Math.cos((t * Math.PI) / 2)),
    EASE_IN_QUADRATIC(t -> Math.pow(t, 2)),
    EASE_IN_CUBIC(t -> Math.pow(t, 3)),
    EASE_IN_QUARTIC(t -> Math.pow(t, 4)),
    EASE_IN_QUINTIC(t -> Math.pow(t, 5)),
    EASE_IN_EXPONENTIAL(t -> (t == 0) ? 0 : Math.pow(2, 10 * t - 10)),
    EASE_IN_CIRCULAR(t -> 1 - Math.sqrt(1 - Math.pow(t,2))),
    EASE_IN_BACK(t -> {
        double c1 = 1.70158;
        double c3 = c1 + 1;

        return c3 * Math.pow(t, 3) - c1 * Math.pow(t, 2);
    }),
    EASE_IN_ELASTIC(t -> {
        double c4 = (2 * Math.PI) / 3;
        return (t == 0)
                ? 0
                : (t == 1)
                ? 1
                : -Math.pow(2, 10 * t - 10) * Math.sin((t * 10 - 10.75) * c4);
    }),
    //
    // NOTE: Bounce ease function requires recursion, which you can't exactly do with lambdas. Fix later? It looks a little outdated anyways, so perhaps just cut it.
    //



    EASE_OUT_SINE(t -> Math.sin((t * Math.PI) / 2)),
    EASE_OUT_QUADRATIC(t -> 1 - Math.pow(1 - t, 2)),
    EASE_OUT_CUBIC(t -> 1 - Math.pow(1 - t, 3)),
    EASE_OUT_QUARTIC(t -> 1 - Math.pow(1 - t, 4)),
    EASE_OUT_QUINTIC(t -> 1 - Math.pow(1 - t, 5)),
    EASE_OUT_EXPONENTIAL(t -> (t == 1) ? 1 : 1 - Math.pow(2, -10 * t)),
    EASE_OUT_CIRCULAR(t -> Math.sqrt(1 - Math.pow(t - 1, 2))),
    EASE_OUT_BACK(t -> {
        double c1 = 1.70158;
        double c3 = c1 + 1;

        return 1 + c3 * Math.pow(t - 1, 3) + c1 * Math.pow(t - 1, 2);
    }),
    EASE_OUT_ELASTIC(t -> {
        double c4 = (2 * Math.PI) / 3;

        return t == 0
                ? 0
                : t == 1
                ? 1
                : Math.pow(2, -10 * t) * Math.sin((t * 10 - 0.75) * c4) + 1;
    }),




    EASE_IN_OUT_SINE(t -> -(Math.cos(Math.PI * t) - 1) / 2),
    EASE_IN_OUT_QUADRATIC(t -> (t < 0.5) ? 2 * Math.pow(t, 2) : 1 - Math.pow(-2 * t + 2, 2) / 2),
    EASE_IN_OUT_CUBIC(t -> (t < 0.5) ? 4 * Math.pow(t, 3) : 1 - Math.pow(-2 * t + 2, 3) / 2),
    EASE_IN_OUT_QUARTIC(t -> (t < 0.5) ? 8 * Math.pow(t, 4) : 1 - Math.pow(-2 * t + 2, 4) / 2),
    EASE_IN_OUT_QUINTIC(t -> (t < 0.5) ? 16 * Math.pow(t, 5) : 1 - Math.pow(-2 * t + 2, 5) / 2),
    EASE_IN_OUT_EXPONENTIAL(t -> {
        return t == 0
                ? 0
                : t == 1
                ? 1
                : t < 0.5 ? Math.pow(2, 20 * t - 10) / 2
                : (2 - Math.pow(2, -20 * t + 10)) / 2;
    }),
    EASE_IN_OUT_BACK(t -> {
        double c1 = 1.70158;
        double c2 = c1 * 1.525;

        return t < 0.5
                ? (Math.pow(2 * t, 2) * ((c2 + 1) * 2 * t - c2)) / 2
                : (Math.pow(2 * t - 2, 2) * ((c2 + 1) * (t * 2 - 2) + c2) + 2) / 2;
    }),
    EASE_IN_OUT_CIRCULAR(t -> {
        return t < 0.5
                ? (1 - Math.sqrt(1 - Math.pow(2 * t, 2))) / 2
                : (Math.sqrt(1 - Math.pow(-2 * t + 2, 2)) + 1) / 2;
    }),
    EASE_IN_OUT_ELASTIC (t -> {
        double c5 = (2 * Math.PI) / 4.5;

        return t == 0
                ? 0
                : t == 1
                ? 1
                : t < 0.5
                ? -(Math.pow(2, 20 * t - 10) * Math.sin((20 * t - 11.125) * c5)) / 2
                : (Math.pow(2, -20 * t + 10) * Math.sin((20 * t - 11.125) * c5)) / 2 + 1;
    });


    public double getValue(double t) {
        return this.formula.apply(t);
    }

    EasingFunction(Function<Double, Double> formula) {
        this.formula = formula;
    }

    private Function<Double, Double> formula;
}
