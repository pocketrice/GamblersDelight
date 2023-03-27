// Lucas Xie - P5 AP CSA - 1/26/23 - GDJFX
package com.gdjfx.cli;

import java.io.FileNotFoundException;
import java.util.Scanner;

import static com.gdjfx.AnsiCode.*;


public class ProgramConsole { // Console-based GDJFX.
    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        System.out.println(ANSI_BLUE + "Welcome to Gambler's Delight. To get started, pick an option by inputting its keyword." + ANSI_RESET);
        System.out.println(ANSI_CYAN + "You may...");
        System.out.println("\t* gamble responsibly " + ANSI_RESET + "(slow mode)");
        System.out.println(ANSI_CYAN + "\t* gamble your life savings away " + ANSI_RESET + "(fast mode)");
        System.out.println(ANSI_CYAN + "\t* read the instruction manual " + ANSI_RESET + "(help)");
        System.out.println(ANSI_CYAN + "\t* exit the program " + ANSI_RESET + "(exit)");

        while (true) {
            switch (prompt(ANSI_PURPLE + "\n\n◇ Select an option (slow, fast, help, exit)." + ANSI_RESET, "Error: invalid keyword. You may pick from SLOWMODE/SLOW, FASTMODE/FAST, HELP, or EXIT.", new String[]{"slowmode", "slow", "fast", "fastmode", "help", "exit"}, false, false)) {
                case "slowmode", "slow" -> {
                    System.out.println(ANSI_GREEN + "[✔] Entered slow mode.");
                    System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n" + ANSI_RESET);
                    GdSlowConsole gdslow = new GdSlowConsole();
                    gdslow.rollCycle();
                }

                case "fastmode", "fast" -> {
                    int trials = 0;

                    System.out.println(ANSI_GREEN + "[✔] Entered fast mode.\n" + ANSI_RESET);
                    switch (prompt("◇ Set custom trial count?", "Error: invalid option.", new String[]{"yes", "no", "y", "n"}, false, false)) {
                        case "yes", "y" -> {
                            trials = (int) prompt("➢ Set the # of trials to run.", "Error: invalid number.", 0, Integer.MAX_VALUE, true);
                        }

                        case "no", "n" -> {
                            trials = 5000;
                        }
                    }
                    GdFastConsole gdfast = new GdFastConsole(trials);
                    gdfast.rollCycle();
                }

                case "help" -> {
                    System.out.println(ANSI_BLUE + "\n\nThe premise of this game is simple: try to make the most money by making strategic bets." + ANSI_RESET);
                    System.out.println("\n * Firstly, a preliminary deposit of $10.00 is taken for every session played.");
                    System.out.println(" * Bets consist of any increment of $10 between $10 and $50.");
                    System.out.println(" * Once a bet is placed, it undergoes three 'stages' to determine whether you make no profit, get 2x profit, or 4x profit. Your bet is included in that compensation.");
                    System.out.println(ANSI_CYAN + "\n[!] Pass the second stage to win 2x your bet, and the third stage for 4x your bet!" + ANSI_RESET);

                    System.out.println(ANSI_YELLOW + "\n➢ First stage: prime dice roll" + ANSI_RESET);
                    System.out.println("Roll two dice. If either are prime, move on. Otherwise, you lose.");
                    System.out.println("Success chance: 88.9%");

                    System.out.println(ANSI_YELLOW + "\n➢ Second stage: lucky card" + ANSI_RESET);
                    System.out.println("Draw a card. If its suit is diamond, spade, or it is a JQK of hearts, move on with 2x win. Otherwise, you lose.");
                    System.out.println("Success chance: 55.7%");

                    System.out.println(ANSI_YELLOW + "\n➢ Third stage: snake eyes" + ANSI_RESET);
                    System.out.println("Roll two dice. If both values match, end with 4x win. Otherwise, you lose.");
                    System.out.println("Success chance: 16.7%\n\n\n");
                }

                case "exit" -> {
                    System.out.println(ANSI_RED + "Safely exited program. Tip: 90% of gamblers quit just before they hit it big!" + ANSI_RESET);
                    System.exit(0);
                }
            }
        }
    }


   /* public static void validateGamePieces() {
        Card c = new Card();
        System.out.println("Drawed a " + c.cardRank + " of " + c.cardSuit + "S.");
        CardDeck cd = new CardDeck();
        System.out.println("Made deck of " + cd.cardCount + " cards...");
        for (Card card : cd.cards) {
            System.out.println("Got a " + card.cardRank + " of " + card.cardSuit + "S!");
        }
        Dice d = new Dice();
        Dice d2 = new Dice(new int[]{1,2,3,4,5,6,7,8,9}, null, null);
        for (int i = 0; i < 10; i++) {
            d.roll();
            System.out.println(d.sideCount + "-sided dice rolled a " + d.selectedValue + ".");
        }
        System.out.println();
        for (int i = 0; i < 10; i++) {
            d2.roll();
            System.out.println(d2.sideCount + "-sided dice rolled a " + d2.selectedValue + ".");
        }
    }*/

    // Prompts the user with a message and returns user input; allows for restraints, case sensitivity, a toggle for next() or nextLine(), and handles error messages.
    // @param message - initial text message to display to the user
    // @param errorMessage - message to display in the event the input is invalid (does not match bounds)
    // @param bounds - array of strings that rep. a whitelist of acceptable inputs
    // @param lineMode - whether or not to read next() or nextLine()
    // @param isCaseSensitive - whether or not to ignore case
    // @return user-inputted string that is valid
    public static String prompt(String message, String errorMessage, String[] bounds, boolean lineMode, boolean isCaseSensitive) // <+> APM
    {
        Scanner input = new Scanner(System.in);
        String nextInput;

        while (true)
        {
            System.out.print(message);
            if (!message.equals(""))
                System.out.println();

            if (lineMode) {
                input.nextLine();
                nextInput = input.nextLine();
            }
            else {
                nextInput = input.next();
            }

            if (!isCaseSensitive)
            {
                nextInput = nextInput.toLowerCase();

                for (int i = 0; i < bounds.length; i++)
                    bounds[i] = bounds[i].toLowerCase();
            }

            if (nextInput.matches(String.join("|", bounds)) || bounds[0].equals("")) {
                return nextInput;
            } else {
                System.out.println(ANSI_RED + errorMessage + ANSI_RESET);
            }

        }
    }

    // The "numeric equivalent" of the prompt outlined previously. Bounds are instead numbers rather than a whitelist, a toggle between integer and double is available.
    // @param message - same as prev prompt
    // @param errorMessage - same as prev prompt
    // @param min - lowest bound, exclusive
    // @param max - highest bound, exclusive
    // @param isIntegerMode - whether or not to only accept integers
    // @return user-inputted double/integer that is valid
    public static double prompt(String message, String errorMessage, double min, double max, boolean isIntegerMode)
    {
        Scanner input = new Scanner(System.in);
        String nextInput;
        double parsedInput = 0;
        boolean isValid;

        while (true) {
            System.out.print(message);
            if (!message.equals(""))
                System.out.println();

            nextInput = input.next();
            try {

                if (!isIntegerMode) {
                    parsedInput = Double.parseDouble(nextInput);
                } else {
                    parsedInput = Integer.parseInt(nextInput);
                }

                input.nextLine();
                isValid = true;
            } catch (Exception e) {
                isValid = false;
            }

            if (parsedInput >= min && parsedInput <= max && isValid) {
                return parsedInput;
            } else {
                System.out.println(ANSI_RED + errorMessage + ANSI_RESET);
            }
        }
    }
}
