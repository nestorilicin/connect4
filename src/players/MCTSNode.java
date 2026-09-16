package players;

import game.Board;

import java.util.ArrayList;
import java.util.List;

// Cvor MCTS stabla: stanje table, roditelj, deca i statistika poseta/pobeda
class MCTSNode {
    private final Board board;
    private final MCTSNode parent;
    private final int moveThatLedHere;
    private final int playerJustMoved;
    private final List<MCTSNode> children = new ArrayList<>();
    private final int[] untriedMoves;
    private int untriedCount;
    private int visits = 0;
    private double wins = 0;

    MCTSNode(Board board, MCTSNode parent, int moveThatLedHere, int playerJustMoved) {
        this.board = board;
        this.parent = parent;
        this.moveThatLedHere = moveThatLedHere;
        this.playerJustMoved = playerJustMoved;
        this.untriedMoves = board.get_valid_moves();
        this.untriedCount = untriedMoves.length;
    }

    boolean isTerminal() {
        return board.is_full() || board.is_winner(1) || board.is_winner(2);
    }

    boolean isFullyExpanded() {
        return untriedCount == 0;
    }

    // UCT: bira dete sa najvecom vrednoscu, balansira iskoriscenje i istrazivanje
    MCTSNode selectChild(double explorationConstant) {
        MCTSNode best = null;
        double bestValue = Double.NEGATIVE_INFINITY;
        for (MCTSNode child : children) {
            double uct = (child.wins / child.visits)
                    + explorationConstant * Math.sqrt(Math.log(visits) / child.visits);
            if (uct > bestValue) {
                bestValue = uct;
                best = child;
            }
        }
        return best;
    }

    // Dodaje jedno novo dete za poslednji neisprobani potez
    MCTSNode expand() {
        int move = untriedMoves[--untriedCount];
        Board childBoard = new Board(board);
        int mover = 3 - playerJustMoved;
        childBoard.make_move(mover, move);
        MCTSNode child = new MCTSNode(childBoard, this, move, mover);
        children.add(child);
        return child;
    }

    // Azurira posete/pobede ovog cvora i penje se do korena
    void backpropagate(int winner) {
        visits++;
        if (winner == 0) {
            wins += 0.5;
        } else if (winner == playerJustMoved) {
            wins += 1.0;
        }
        if (parent != null) {
            parent.backpropagate(winner);
        }
    }

    // Najposecenije dete je konacan izbor poteza, otpornije na sum od win-rate-a
    MCTSNode mostVisitedChild() {
        MCTSNode best = null;
        int bestVisits = -1;
        for (MCTSNode child : children) {
            if (child.visits > bestVisits) {
                bestVisits = child.visits;
                best = child;
            }
        }
        return best;
    }

    Board getBoard() {
        return board;
    }

    int getMoveThatLedHere() {
        return moveThatLedHere;
    }

    int getPlayerJustMoved() {
        return playerJustMoved;
    }
}
