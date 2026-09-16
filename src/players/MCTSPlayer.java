package players;

import game.Board;

import java.util.Random;

// MCTS (UCT) prema: https://www.baeldung.com/java-monte-carlo-tree-search
// Cetiri faze: selection, expansion, simulation, backpropagation
public class MCTSPlayer extends Player {
    private static final double EXPLORATION_CONSTANT = Math.sqrt(2);
    private static final int DEFAULT_ITERATIONS = 2000;

    private final int iterations;
    private final long timeLimitMillis;
    private final boolean timed;
    private final Random random = new Random();
    private long nodesCreated = 0;

    public MCTSPlayer(int player) {
        this(player, DEFAULT_ITERATIONS);
    }

    // Netajmirano: fiksan broj iteracija
    public MCTSPlayer(int player, int iterations) {
        super(player);
        this.iterations = iterations;
        this.timeLimitMillis = 0;
        this.timed = false;
    }

    // Tajmirano: radi dok ne istekne vremenski limit, umesto fiksnog broja iteracija
    public MCTSPlayer(int player, long timeLimitMillis) {
        super(player);
        this.iterations = 0;
        this.timeLimitMillis = timeLimitMillis;
        this.timed = true;
    }

    @Override
    public int getMove(Board board) {
        MCTSNode root = new MCTSNode(new Board(board), null, -1, 3 - player);

        if (timed) {
            long deadline = System.currentTimeMillis() + timeLimitMillis;
            while (System.currentTimeMillis() < deadline) {
                runIteration(root);
            }
        } else {
            for (int i = 0; i < iterations; i++) {
                runIteration(root);
            }
        }

        return root.mostVisitedChild().getMoveThatLedHere();
    }

    // Jedna MCTS iteracija kroz sve 4 faze
    private void runIteration(MCTSNode root) {
        MCTSNode node = select(root);
        node = expand(node);
        int winner = simulate(node);
        backpropagate(node, winner);
    }

    // FAZA 1 - SELECTION: silazi kroz stablo dok je cvor potpuno razvijen
    private MCTSNode select(MCTSNode node) {
        while (!node.isTerminal() && node.isFullyExpanded()) {
            node = node.selectChild(EXPLORATION_CONSTANT);
        }
        return node;
    }

    // FAZA 2 - EXPANSION: dodaje jedno novo dete za neisprobani potez
    private MCTSNode expand(MCTSNode node) {
        if (node.isTerminal()) {
            return node;
        }
        nodesCreated++;
        return node.expand();
    }

    // FAZA 3 - SIMULATION: nasumicna partija do kraja, vraca pobednika
    private int simulate(MCTSNode node) {
        Board rolloutBoard = new Board(node.getBoard());
        int currentPlayer = 3 - node.getPlayerJustMoved();

        while (!rolloutBoard.is_full() && !rolloutBoard.is_winner(1) && !rolloutBoard.is_winner(2)) {
            int[] validMoves = rolloutBoard.get_valid_moves();
            int move = validMoves[random.nextInt(validMoves.length)];
            rolloutBoard.make_move(currentPlayer, move);
            currentPlayer = 3 - currentPlayer;
        }

        if (rolloutBoard.is_winner(1)) return 1;
        if (rolloutBoard.is_winner(2)) return 2;
        return 0;
    }

    // FAZA 4 - BACKPROPAGATION: rezultat se penje do korena
    private void backpropagate(MCTSNode node, int winner) {
        node.backpropagate(winner);
    }

    public long getNodesCreated() {
        return nodesCreated;
    }
}
