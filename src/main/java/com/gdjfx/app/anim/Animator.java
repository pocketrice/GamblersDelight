package com.gdjfx.app.anim;

// i'm tired of trying to fix eu.iamgio's Animated.
// It's not modular so jlink breaks, and broken so it crashes javafx if jarred. It's a big mess. Urgh!!!

// Thus, this fills the role of Animated<X>; which interpolates numeric property values on change.
// Perhaps use node properties, but add functionality for css properties too.
// Each node should have 1 (one!) Animator. So, allow for the attaching of several watched properties.
// Use EJ #34 to implement the uber-readable 'settings'.
// Add'l features? easier-to-use enable/disable feature? (perhaps allow interpolation to fully finish before disable; fix marquee bug)


// validateCss (cssManager)
// General Transition object
// General Animator obj
public class Animator {
}
