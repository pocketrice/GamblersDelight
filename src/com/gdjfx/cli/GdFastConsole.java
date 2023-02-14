// Lucas Xie - P5 AP CSA - 1/26/23 - GDJFX
package com.gdjfx.cli;

import com.gdjfx.Card;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static com.gdjfx.AnsiCode.*;

public class GdFastConsole extends GdSlowConsole implements ModeConsole {
    private int currentRound, totalWins, totalLosses, doubleWins, quadWins, initLosses, doubleLosses, quadLosses;
    private long balance, netBalance, trialCount;
    private double totalWinRate, totalWinLoseRatio, doubleWinRate, quadWinRate, expectedValue;

    private final List<Card> cardHistory = new ArrayList<>();
    private final List<Integer> diceHistory = new ArrayList<>();
    private final List<Integer> optDiceHistory = new ArrayList<>();

    public GdFastConsole() {
        super();
        balance = 100;
        trialCount = 5000;
    }

    public GdFastConsole(long tc) {
        super();
        balance = 100;
        trialCount = tc;
    }


    // Rolls a full cycle (1 trial). Overwritten to remove unnecessary console output and visual elements.
    // @param N/A
    // @return N/A
    @Override
    public void rollCycle() throws InterruptedException, FileNotFoundException {
        long initialBal = balance;
        long netBets = 0;

        balance -= 10;
        System.out.println("\n\n\nPreliminary cost was pocketed. You now have $" + balance + ".00. " + ANSI_RED + "(-$10.00)\n" + ANSI_RESET);

        long pretime = getMillisecSinceUnixEpoch();
        fancyDelay(400, ANSI_CYAN + "❖ Processing trials..." + ANSI_RESET, "Done!", 4);
        System.out.println(ANSI_PURPLE + "---------------------------------------------------------------------" + ANSI_RESET);

        for (int i = 0; i < trialCount; i++) {
            boolean hasLostRound = false;
            boolean hasEndedRound = false;
            long winAmount = 0;
            currentRound++;

            long bet = 10;
            balance -= bet;
            netBets += bet;


            while (!hasLostRound && !hasEndedRound) {
                if (!rollInitRound()) {
                    initLosses++;
                    hasLostRound = true;
                }
                else {
                    // 2ND TEST
                    if (!rollDoubleRound()) {
                        doubleLosses++;
                        hasLostRound = true;
                    }
                    else {
                        // 3RD TEST
                        doubleWins++;
                        switch ((int)(Math.random()*2)) {
                            case 0 -> {
                                if (!rollQuadRound()) {
                                    quadLosses++;
                                    hasLostRound = true;
                                }
                                else {
                                    quadWins++;
                                    winAmount += bet * 4;
                                }
                            }

                            case 1 -> {
                                winAmount += bet * 2;
                            }
                        }
                    }
                }

                hasEndedRound = true; // End round.
            }


            if (hasLostRound) {
                totalLosses++;
            }
            else {
                totalWins++;
            }

            balance += winAmount;
            netBalance = balance - initialBal;
            expectedValue = truncate((double)(netBalance - netBets) / currentRound, 2);

            // Evades divide by zero problem via hard-code for "denominator = 0" case.
            totalWinRate = (double)totalWins / trialCount;
            totalWinLoseRatio = (totalLosses != 0) ? (double)totalWins / totalLosses : 0;
            doubleWinRate = (double)doubleWins / trialCount;
            quadWinRate = (double)quadWins / trialCount;
        }
        long posttime = getMillisecSinceUnixEpoch();

        System.out.println("➢  Balance: " + monetaryParse(initialBal, false, false, false) + " -> " + ANSI_BLUE + monetaryParse(balance, false, false, false) + ANSI_RESET);
        System.out.println("   Net gain/loss: " + monetaryParse(netBalance, true, true, true));
        System.out.println("   Expected value: " + monetaryParse(expectedValue, true, true, true));

        System.out.println("\n➢  Win rate (total): " + totalWins + " / " + trialCount + " = " + truncate(totalWinRate * 100, 2) + "%");
        System.out.println("   W/L ratio (total): " + totalWins + " / " + totalLosses + " = " + truncate(totalWinLoseRatio, 2));
        System.out.println("   Win rate (2x stage): " + doubleWins + " / " + trialCount + " = " + truncate(doubleWinRate * 100, 2) + "%");
        System.out.println("   Win rate (4x stage): " + quadWins + " / " + trialCount + " = " + truncate(quadWinRate * 100, 2) + "%");
        System.out.println("\n➢  Trials run: " + trialCount + " trials");
        System.out.println("   Operation time: " + (posttime - pretime) + " ms\n\n\n\n\n");
    }

    // Returns milliseconds since 0:00 1/1/1970 (Unix epoch).
    // @param N/A
    // @return milliseconds since Unix epoch (0:00 1/1/1970)
    public static long getMillisecSinceUnixEpoch() { // unix epoch = 1/1/1970
        return System.currentTimeMillis();
    }
}
