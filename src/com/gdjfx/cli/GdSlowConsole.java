// Lucas Xie - P5 AP CSA - 1/26/23 - GDJFX
package com.gdjfx.cli;

import com.gdjfx.Card;
import com.gdjfx.Dice;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.gdjfx.Card.Suit;
import static com.gdjfx.cli.ProgramConsole.prompt;

public class GdSlowConsole implements ModeConsole {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";

    private final int TOTAL_ROUNDS = 10;
    private int currentRound, totalWins, totalLosses, doubleWins, quadWins, initLosses, doubleLosses, quadLosses;
    private long balance, netBalance, bet;
    private double totalWinRate, doubleWinRate, quadWinRate, totalWinLoseRatio, expectedValue;
    private List<Card> cardHistory = new ArrayList<>();
    private List<Integer> diceHistory = new ArrayList<>();
    private List<Integer> optDiceHistory = new ArrayList<>();

    public GdSlowConsole() {
        currentRound = 0;
        bet = 10;
        balance = 100;
        netBalance = 0;
    }

    public GdSlowConsole(long bet, int bal) {
        currentRound = 0;
        this.bet = bet;
        balance = bal;
        netBalance = 0;
    }

    // Rolls a full cycle (1 trial).
    // @param N/A
    // @return N/A
    public void rollCycle() throws InterruptedException, FileNotFoundException {
        long initialBal = balance;
        long netBets = 0;
        List<Double> outcomeChances = new ArrayList<>();

        balance -= 10;
        System.out.println("Preliminary cost was pocketed. You now have $" + balance + ".00. " + ANSI_RED + "(-$10.00)" + ANSI_RESET);

        for (int i = 0; i < TOTAL_ROUNDS; i++) {
            boolean hasLostRound = false;
            boolean hasEndedRound = false;
            long winAmount = 0;
            String lossMessage = "";
            currentRound++;
            outcomeChances.clear(); // Clear outcome chances as a new round has started.

            System.out.println(ANSI_BLUE + "---------------------------------------------------------------------");
            System.out.println("❖ Round " + currentRound + " has started.\n\n" + ANSI_RESET);
            bet = Long.parseLong(prompt(ANSI_CYAN + "➢ Choose a bet amount ($10 —— $50)." + ANSI_RESET, "Error: bet must be an increment of $10 within $10-$50.", new String[]{"10","20","30","40","50"}, false, false)); // BUG: you can (probably) select a bet that goes beyond your bal. Fix this later.
            balance -= bet;
            netBets += bet;
            System.out.println("Your bet has been withdrawn. You now have $" + balance + ".00. " + ANSI_RED + "(-$" + bet + ".00)");


            while (!hasLostRound && !hasEndedRound) { // WHILE NOT EVALUATING PROPERLY?
                fancyDelay(500, ANSI_CYAN + "\n➢ Rolling two fair dice..." + ANSI_RESET, "\b ✓", 3);
                boolean hasPassedInit = rollInitRound();
                System.out.println("You rolled a " + diceHistory.get(diceHistory.size() - 2) + " and a " + diceHistory.get(diceHistory.size() - 1) + ".");

                if (!hasPassedInit) {
                    initLosses++;
                    outcomeChances.add(0.111);
                    lossMessage = "11.1% chance of losing (neither number was prime)";
                    hasLostRound = true;
                }
                else {
                    outcomeChances.add(0.889);

                    // 2ND TEST
                    fancyDelay(400, ANSI_CYAN + "\n➢ Drawing a random card..." + ANSI_RESET, "\b ✓", 3);
                    boolean hasPassedDouble = rollDoubleRound();
                    System.out.println("You drew a " + toCamelCase(cardHistory.get(cardHistory.size() - 1).cardRank.toString(), true) + " of " + toCamelCase(cardHistory.get(cardHistory.size() - 1).cardSuit.toString(), true) + "s.");

                    if (!hasPassedDouble) {
                        outcomeChances.add(0.443);
                        doubleLosses++;
                        lossMessage = "44.3% chance of losing (club or non-JQK heart)";
                        hasLostRound = true;
                    }
                    else {
                        outcomeChances.add(0.557);
                        doubleWins++;

                        // 3RD TEST
                        switch (prompt(ANSI_YELLOW + "\n◇ You currently qualify for a 2x win " + ANSI_RESET + "(" + monetaryParse(balance, false, false, false) + " -> " + monetaryParse((balance + bet * 2), false, false, false) + ")" + ANSI_YELLOW + ".\nTry for a 4x win by trying to roll a pair " + ANSI_RESET + "(" + monetaryParse(balance, false, false, false) + " -> " + monetaryParse((balance + bet * 4), false, false, false) + ")" + ANSI_YELLOW + "? You will lose ALL profits if you lose." + ANSI_RESET, "Error: invalid choice.", new String[]{"yes", "no", "y", "n"}, false, false)) {
                            case "yes", "y" -> {
                                System.out.println(ANSI_GREEN + "[✔] Accepted 4x offer." + ANSI_RESET);

                                fancyDelay(400, ANSI_CYAN + "\n➢ Rolling two fair dice..." + ANSI_RESET, "\b ✓", 3);
                                boolean hasPassedQuad = rollQuadRound();
                                System.out.println("You rolled a " + optDiceHistory.get(optDiceHistory.size() - 2) + "...");
                                TimeUnit.MILLISECONDS.sleep(1000);
                                System.out.println("...and a " + optDiceHistory.get(optDiceHistory.size() - 1) + ".");
                                TimeUnit.MILLISECONDS.sleep(2000);

                                if (!hasPassedQuad) {
                                    outcomeChances.add(0.833);
                                    quadLosses++;
                                    lossMessage = "83.3% chance of losing (rolled different #s)";
                                    hasLostRound = true;
                                }
                                else {
                                    outcomeChances.add(0.167);
                                    quadWins++;
                                    winAmount += bet * 4;
                                }
                            }

                            case "no", "n" -> {
                                System.out.println(ANSI_RED + "[✘] Declined 4x offer." + ANSI_RESET);
                                winAmount += bet * 2;
                                hasEndedRound = true;
                            }
                        }
                    }
                }

                hasEndedRound = true; // End round.
            }


            if (hasLostRound) {
                System.out.println(ANSI_PURPLE + "\n\nLost the round! At that stage, you had a " + lossMessage + ".");
                totalLosses++;
            }
            else {
                System.out.println(ANSI_PURPLE + "\n\nWon the round!");
                totalWins++;
            }
            System.out.println("=======================================================================================\n" + ANSI_RESET);

            balance += winAmount;
            netBalance = balance - initialBal;
            expectedValue = truncate((double)(netBalance - netBets) / currentRound, 2);

            // Takes into account div by zero problem
            totalWinRate = (totalLosses != 0) ? (double)totalWins / currentRound : (double)totalWins;
            totalWinLoseRatio = (totalLosses != 0) ? (double)totalWins / totalLosses : 0;
            doubleWinRate = (doubleLosses != 0) ? (double)doubleWins / currentRound : (double)doubleWins;
            quadWinRate = (quadLosses != 0) ? (double)quadWins / currentRound : (double)quadWins;

            // ROUND STATISTICS
            // BALANCE, NET BALANCE
            // CHANCE OF GIVEN PATH
            // EXPECTED VALUE (CUMULATIVE)
            // W/L RATIOS (ALL OF THEM)
            System.out.println("➢ Balance: " + monetaryParse((balance + bet - winAmount), false, false, false) +  " -> " + ANSI_BLUE + monetaryParse(balance, false, false, false) + ANSI_RESET);
            System.out.println("  Net gain/loss (cumulative): " + monetaryParse(netBalance, true, true, true));
            System.out.println("  Expected value (cumulative): " + monetaryParse(expectedValue, true, true, true));
            System.out.println("  Chance of outcome: " + collectionToConjoinedString(outcomeChances, " * ") + " = " + truncate(getCollectionProduct(outcomeChances) * 100, 2) + "%");

            System.out.println("\n➢  Win rate (total): " + totalWins + " / " + currentRound + " = " + truncate(totalWinRate * 100, 2) + "%");
            System.out.println("   W/L ratio (total): " + totalWins + " / " + totalLosses + " = " + truncate(totalWinLoseRatio, 2));
            System.out.println("   Win rate (2x stage): " + doubleWins + " / " + currentRound + " = " + truncate(doubleWinRate * 100, 2) + "%");
            System.out.println("   Win rate (4x stage): " + quadWins + " / " + currentRound + " = " + truncate(quadWinRate * 100, 2) + "%\n\n\n\n\n\n\n\n");
        }
    }

    // Rolls the first round of the cycle (prime # test)
    // @param N/A
    // @return whether either dice rolls were prime
    public boolean rollInitRound() {
        // Roll 2x dice; move on if either is prime.
        Dice diceA = new Dice();
        Dice diceB = new Dice();
        diceA.roll();
        diceB.roll();

        this.diceHistory.add(diceA.selectedValue);
        this.diceHistory.add(diceB.selectedValue);

        return (isPrime(diceA.selectedValue) || isPrime(diceB.selectedValue));
    }

    // Rolls the second round of the cycle (card draw)
    // @param N/A
    // @return whether the drawn card fits specified criteria
    public boolean rollDoubleRound() {
        // Pick a card; move on if diamond, spade, or JQK of heart
        Card card = new Card();

        cardHistory.add(card);

        return (card.cardSuit.equals(Suit.DIAMOND) || card.cardSuit.equals(Suit.SPADE) || (card.cardSuit.equals(Suit.HEART) && card.cardRank.ordinal() > 9));
    }

    // Rolls the third round of the cycle (super snake eyes).
    // @param N/A
    // @return whether both dice had matching #s
    public boolean rollQuadRound() {
        // Roll 2x dice; win if both numbers are equal
        Dice diceA = new Dice();
        Dice diceB = new Dice();
        diceA.roll();
        diceB.roll();

        optDiceHistory.add(diceA.selectedValue);
        optDiceHistory.add(diceB.selectedValue);

        return (diceA.selectedValue == diceB.selectedValue);
    }


    // Checks a number for primeness.
    // @param num - number to be checked
    // @return whether or not the number was prime
    public static boolean isPrime(int num) { // Prime if only divisible by itself and 1.
        for (int i = 2; i < num; i++) {
            if ((double)num / i == (double)(num / i)) // Produces an integer
                return false;
        }
        return true;
    }

    // A purely aesthetic fancy spinning "progress indicator" for dramatic effect.
    // @param delay - how long each interval waits
    // @param loadMessage - message to display alongside indicator
    // @param completionMessage - message to display after done
    // @param iterations - how many times to spin
    // @return N/A
    public static void fancyDelay(long delay, String loadMessage, String completionMessage, int iterations) throws InterruptedException { // Yoinked from SchudawgCannoneer
        int recursionCount = 0;
        System.out.print(loadMessage + " /");

        while (recursionCount < iterations) {
            TimeUnit.MILLISECONDS.sleep(delay);
            System.out.print("\b—");
            TimeUnit.MILLISECONDS.sleep(delay);
            System.out.print("\b\\");
            TimeUnit.MILLISECONDS.sleep(delay);
            System.out.print("\b|");
            TimeUnit.MILLISECONDS.sleep(delay);
            System.out.print("\b/");
            recursionCount++;
        }
        if (!completionMessage.isBlank()) System.out.print("\b" + completionMessage + "\n" + ANSI_RESET);
        else System.out.println();
    }

    // Converts a given string to either upper or lower camel case.
    // @param string
    // @param isUpperCamel - true for upper camel, false for lower camel.
    // @return camel-cased string
    @NotNull
    public static String toCamelCase(String string, boolean isUpperCamel) {
        // Camel case: remove all spaces (parse char after space as capitalized).
        StringBuilder cameledString = (isUpperCamel) ? new StringBuilder(String.valueOf(string.charAt(0)).toUpperCase() + string.substring(1).toLowerCase()) : new StringBuilder(string.toLowerCase());

        for (int i = 0; i < cameledString.length(); i++) {
            if (cameledString.charAt(i) == ' ') {
                cameledString.replace(i+1, i+2, String.valueOf(cameledString.charAt(i+1)).toUpperCase());
            }
        }

        return cameledString.toString().replaceAll(" ", "");
    }

    // Truncates a value to a specified mantissa length (# of decimal places).
    // @param value - any double
    // @param mantissaLength - non-negative integer
    // @return a double truncated to a specified mantissa length
    public static double truncate(double value, int mantissaLength) // <+> APM
    {
        return BigDecimal.valueOf(value).setScale(mantissaLength, RoundingMode.HALF_EVEN).doubleValue();
    }


    // Generates a string from a collection in a grammatical list.
    // @param collection - any collection of objects
    // @param conjunction - string to join the last two items
    // @return grammatically accurate list
    @NotNull
    public static <T extends Collection<K>, K> String collectionToConjoinedString(T collection, String conjunction) { // Joins a collection of any object together by toString()ing each obj and connecting with a conjunction.
        StringBuilder conjoinedString = new StringBuilder();

        for (K item : collection) {
            conjoinedString.append(item.toString()).append(conjunction);
        }

        conjoinedString.replace(conjoinedString.lastIndexOf(conjunction), conjoinedString.length(), ""); // Grammatical correction (remove last conjunction)
        return conjoinedString.toString();
    }


    // Gets the combined product of all numbers in the collection.
    // @param collection - any collection of numbers
    // @return product of all items
    public static <T extends Number> double getCollectionProduct(Collection<T> collection) {
        double product = 1.0;

        for (T num : collection) {
            product *= (double) num;
        }

        return product;
    }

    // Generates a string with proper grammar for monetary values.
    // @param num - raw monetary value
    // @param includeDecimal - should two decimal places be forcibly attached?
    // @param includeExplicitSign - should the sign of the value be explicitly added? (+ or -)
    // @param includeColor - should a corresponding ansi color be attached to the string?
    // @return monetary-parsed string
    public static String monetaryParse(double num, boolean includeDecimal, boolean includeExplicitSign, boolean includeColor) { // BUG: for some reason this doesn't work 100% (see 'expected value' on occassions)
        // TODO: refactor to be more readable / concise (StringBuilder?)
        String[] monetaryColors = (includeColor) ? new String[]{ANSI_GREEN, ANSI_BLUE, ANSI_RED} : new String[]{"","",""};

        String properTruncString;
        if (includeDecimal) {
            String rawTruncString = String.valueOf(truncate(Math.abs(num), 2)); // For non-decimals, this may produce only 1 decimal place (e.g. 120.0)
            properTruncString = (rawTruncString.substring(rawTruncString.indexOf('.') + 1).length() < 2) ? rawTruncString + "0" : rawTruncString; // Adds an extra 0 if necessary to string.
        }
        else {
           properTruncString = String.valueOf(Math.abs((int)num));
        }

        if (includeExplicitSign) {
            return (num > 0) ? monetaryColors[0] + "+$" + properTruncString + ANSI_RESET : ((num == 0) ? "$" + monetaryColors[1] + properTruncString + ANSI_RESET : monetaryColors[2] + "-$" + properTruncString + ANSI_RESET);
        }
        else {
            return (num > 0) ? monetaryColors[0] + "$" + properTruncString + ANSI_RESET : ((num == 0) ? "$" + monetaryColors[1] + properTruncString + ANSI_RESET : monetaryColors[2] + "-$" + properTruncString + ANSI_RESET);
        }
    }
}
