package game;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private int rows;
    private int cols;
    private int[][] grid;


    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getCell(int row, int col){
        return grid[row][col];
    }

    public Board() {
        this(6, 7);
    }

    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new int[rows][cols];
    }

    public Board(Board other) {
        this.rows = other.rows;
        this.cols = other.cols;
        this.grid = new int[rows][cols];
        for (int i = 0; i < other.rows; i++) {
            System.arraycopy(other.grid[i], 0, this.grid[i], 0, other.cols);
        }
    }

    public void print() {
        for (int[] row : grid) {
            for (int cell : row) {
                System.out.print(cell);
            }
            System.out.println();
        }
        System.out.println('\n');
    }

    public void make_move(int player, int column) {
        if (column < 0 || column >= this.cols) {
            return;
        }
        for (int i = this.rows - 1; i >= 0; i--) {
            if (this.grid[i][column] == 0) {
                this.grid[i][column] = player;
                return;
            }
        }
    }

    public List<Integer> get_valid_moves() {
        List<Integer> valid = new ArrayList<>();
        for (int j = 0; j < this.cols; j++) {
            if (this.grid[0][j] == 0) valid.add(j);
        }
        return valid;
    }

    public List<Board> get_successors(int player) {
        List<Board> result = new ArrayList<>();
        for (int j : this.get_valid_moves()) {
            Board newBoard = new Board(this);
            newBoard.make_move(player, j);
            result.add(newBoard);
        }
        return result;
    }

    public boolean is_winner(int player) {
        for (int row = 0; row < this.rows; row++) {
            for (int col = 0; col < this.cols; col++) {
                for (Direction dir : Direction.values()) {
                    if (hasFourInARow(player, row, col, dir)) return true;
                }
            }
        }
        return false;
    }

    private boolean hasFourInARow(int player, int row, int col, Direction dir) {
        int endRow = row + 3 * dir.dr;
        int endCol = col + 3 * dir.dc;
        if (endRow < 0 || endRow >= rows || endCol < 0 || endCol >= cols) return false;
        for (int i = 0; i < 4; i++) {
            if (grid[row + i * dir.dr][col + i * dir.dc] != player) return false;
        }
        return true;
    }

    public boolean is_full(){
        for (int j = 0; j < this.cols; j++) {
            if (grid[0][j] == 0)
                return false;
        }
        return true;
    }

}
