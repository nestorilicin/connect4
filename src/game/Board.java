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
        // TODO ubrazanje proveriti samo oko poteza koji se odigrao
        // horizontal
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols - 3; j++) {
                if (grid[i][j] == player && grid[i][j + 1] == player && grid[i][j + 2] == player && grid[i][j + 3] == player)
                    return true;
            }
        }
        // vertical
        for (int i = 0; i < this.rows - 3; i++) {
            for (int j = 0; j < this.cols; j++) {
                if (grid[i][j] == player && grid[i + 1][j] == player && grid[i + 2][j] == player && grid[i + 3][j] == player)
                    return true;
            }
        }
        // diag \
        for (int i = 0; i < this.rows - 3; i++) {
            for (int j = 0; j < this.cols - 3; j++) {
                if (grid[i][j] == player && grid[i + 1][j + 1] == player && grid[i + 2][j + 2] == player && grid[i + 3][j + 3] == player)
                    return true;
            }
        }
        // diag /
        for (int i = 3; i < this.rows; i++) {
            for (int j = 0; j < this.cols - 3; j++) {
                if (grid[i][j] == player && grid[i - 1][j + 1] == player && grid[i - 2][j + 2] == player && grid[i - 3][j + 3] == player)
                    return true;
            }
        }
        return false;
    }
    
    public boolean is_full(){
        for (int j = 0; j < this.cols; j++) {
            if (grid[0][j] == 0)
                return false;
        }
        return true;
    }

}
