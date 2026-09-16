package game;

public class Board {
    private final int rows;
    private final int cols;
    private final int[][] grid;
    private int lastPlayer = 0;
    private int lastRow = -1;
    private int lastCol = -1;


    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getCell(int row, int col) {
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
        this.lastPlayer = other.lastPlayer;
        this.lastRow = other.lastRow;
        this.lastCol = other.lastCol;
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
                lastPlayer = player;
                lastRow = i;
                lastCol = column;
                return;
            }
        }
    }

    public int[] get_valid_moves() {
        int count = 0;
        for (int j = 0; j < this.cols; j++) {
            if (this.grid[0][j] == 0) count++;
        }
        int[] valid = new int[count];
        int idx = 0;
        for (int j = 0; j < this.cols; j++) {
            if (this.grid[0][j] == 0) valid[idx++] = j;
        }
        return valid;
    }


    // optimizacija: samo oko poslednjeg cvora
    public boolean is_winner(int player) {
        if (player != lastPlayer || lastRow < 0) return false;
        for (Direction dir : Direction.values()) {
            int count = 1 + countDirection(dir.dr, dir.dc) + countDirection(-dir.dr, -dir.dc);
            if (count >= 4) return true;
        }
        return false;
    }
    
    private int countDirection(int dr, int dc) {
        int count = 0;
        int r = lastRow + dr;
        int c = lastCol + dc;
        while (r >= 0 && r < rows && c >= 0 && c < cols && grid[r][c] == lastPlayer) {
            count++;
            r += dr;
            c += dc;
        }
        return count;
    }

    public boolean is_full() {
        for (int j = 0; j < this.cols; j++) {
            if (grid[0][j] == 0) return false;
        }
        return true;
    }

}
