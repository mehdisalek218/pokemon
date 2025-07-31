import java.util.ArrayList;
import java.util.List;
import MG2D.geometrie.Point;

public class Board {
    private Pokemon[][] grid;
    private int size;

    public Board(int size) {
        this.size = size;
        grid = new Pokemon[size][size];
    }

    public void placePokemon(int row, int col, Pokemon pokemon) {
        grid[row][col] = pokemon;
    }

    public Pokemon getPokemon(int row, int col) {
        if (row < 0 || row >= size || col < 0 || col >= size) return null;
        return grid[row][col];
    }

    public List<Point> getValidMoves(int row, int col) {
        List<Point> moves = new ArrayList<>();
        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = col - 1; c <= col + 1; c++) {
                if (r >= 0 && r < size && c >= 0 && c < size && getPokemon(r, c) == null) {
                    moves.add(new Point(c, r));
                }
            }
        }
        return moves;
    }

    public void move(int fromRow, int fromCol, int toRow, int toCol) {
        grid[toRow][toCol] = grid[fromRow][fromCol];
        grid[fromRow][fromCol] = null;
    }

    public void attack(int attackerRow, int attackerCol, int targetRow, int targetCol) {
        Pokemon attacker = grid[attackerRow][attackerCol];
        Pokemon target = grid[targetRow][targetCol];
        
        // Only the attacker attacks the target, no counter-attack
        attacker.attaque(target);

        // Remove fainted Pokemon
        if (target.getHp() <= 0) {
            grid[targetRow][targetCol] = null;
        }
    }

    public boolean bothMewtwoAlive() {
        boolean p1Mewtwo = false;
        boolean p2Mewtwo = false;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Pokemon p = grid[i][j];
                if (p != null) {
                    if (p.getNumero() == 151) p1Mewtwo = true; // Mew for Player 1
                    if (p.getNumero() == 150) p2Mewtwo = true; // Mewtwo for Player 2
                }
            }
        }
        return p1Mewtwo && p2Mewtwo;
    }

    public boolean isMewAlive() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Pokemon p = grid[i][j];
                if (p != null && p.getNumero() == 151) {
                    return true;
                }
            }
        }
        return false;
    }
}
