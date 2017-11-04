package evolution.prisoner.strategies;

import evolution.prisoner.Result;
import evolution.prisoner.Strategy;

public class qwertyuiop extends Strategy {

    java.util.Random rnd = new java.util.Random();

    // Current strategy: 0-coop, 1-tft
    int myStrat = 1;

    // Limits for switching strategies
    double forgiveChance = 1 / 10;
    double trustLimit = 2;

    int oppTitForTatMoves = 0;

    Move myNextMove = Move.COOPERATE;
    Move myLastMove = Move.COOPERATE;

    @Override
    public Move nextMove() {
        return myNextMove;
    }

    @Override
    public void reward(Result res) {
        Move oppMove = res.getOponentsMove();

        switch (myStrat) {
            case 0: {
                // Betrayed => TitForTat
                if (oppMove == Move.DECEIVE) myStrat = 1;
                break;
            }
            case 1: {

                // Opponent TitForTat => remember
                if (oppMove == myLastMove) oppTitForTatMoves++;

                // Opponent breaks TitForTat by Cooperate => forgive instantly
                else if (oppMove == Move.COOPERATE) myStrat = 0;

                // Opponent breaks TitForTat by Deceive => reset TitForTat cooperation counter
                else oppTitForTatMoves = 0;

                // Opponent plays TitForTat too => try to switch to cooperate:
                if (oppTitForTatMoves > trustLimit) {

                    // Cooperate in tft => instant switch to cooperate
                    // Deceive in tft => forgive only by chance
                    if (oppMove == Move.COOPERATE || (oppMove == Move.DECEIVE && rnd.nextDouble() < forgiveChance)) {
                        oppTitForTatMoves = 0;
                        myStrat = 0;
                        break;
                    }
                }
            }
        }

        // strat switch
        if (myStrat == 0) myNextMove = Move.COOPERATE;
        if (myStrat == 1) myNextMove = oppMove;

        myLastMove = res.getMyMove();
    }


    @Override
    public String authorName() {
        return "Vladislav Vancak";
    }

    @Override
    public String getName() {
        return "qwertyuiop";
    }

    @Override
    public void reset() {
        myNextMove = Move.COOPERATE;
        myLastMove = Move.COOPERATE;
        oppTitForTatMoves = 0;
    }
}