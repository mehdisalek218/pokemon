import MG2D.*;
import MG2D.geometrie.*;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.*;

public class PokemonChess {
    private static final int TILE_SIZE = 64;
    private static final int BOARD_SIZE = 9;
    private Fenetre f;
    private Panneau p;
    private Rectangle turnIndicator;
    private Texte turnLabel;
    private Texte statusText;

    private HashMap<String, Image> pokemonImages = new HashMap<>();
    private List<Dessin> listeDessins = Collections.synchronizedList(new ArrayList<>());
    private List<Point> possibleMoves = new ArrayList<>();
    private String[][] board = new String[BOARD_SIZE][BOARD_SIZE];
    private boolean[][] isWhitePiece = new boolean[BOARD_SIZE][BOARD_SIZE];
    private String[][] pieceType = new String[BOARD_SIZE][BOARD_SIZE];
    private HashMap<String, Integer> pokemonHP = new HashMap<>();
    private int[][] hp = new int[BOARD_SIZE][BOARD_SIZE];
    private int selectedRow = -1, selectedCol = -1;
    private boolean whiteTurn = true;

    private List<String> allPokemons = Arrays.asList(
        "Bulbasaur", "Charmander", "Squirtle", "Pikachu", "Jigglypuff", "Meowth", "Psyduck", "Machop", "Magnemite",
        "Gastly", "Onix", "Cubone", "Koffing", "Rhyhorn", "Horsea", "Staryu", "Scyther", "Pinsir", "Tauros", "Magikarp",
        "Lapras", "Eevee", "Snorlax", "Dratini", "Mewtwo", "Mew", "Gengar", "Alakazam", "Gyarados", "Dragonite"
    );

    public PokemonChess() {
        f = new Fenetre("Pokemon Chess", BOARD_SIZE * TILE_SIZE, BOARD_SIZE * TILE_SIZE + 50);
        p = f.getP();

        chargerImagesPokemon();
        definirHP();
        placerPokemonsAleatoirement();
        ajouterElementsGraphiques();

        // Turn indicator and label
        turnIndicator = new Rectangle(Couleur.BLANC, new Point(30, 670), 40, 40, true);
        turnLabel = new Texte(Couleur.NOIR, "White", new Font("Arial", Font.BOLD, 16), new Point(80, 680));
        p.ajouter(turnIndicator);
        p.ajouter(turnLabel);

        updateStatusText();
        updateDisplay();

        p.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                handleClick(e.getX(), p.getHeight() - e.getY());
            }
        });
    }

    private void chargerImagesPokemon() {
        try {
            for (int i = 0; i < allPokemons.size(); i++) {
                String name = allPokemons.get(i);
                String path = "../images/" + (i + 1) + ".png";
                File file = new File(path);
                if (file.exists()) {
                    Image img = new Image(Couleur.TRANSPARENT, path, new Point(0, 0));
                    img.redimensionner(TILE_SIZE - 10, TILE_SIZE - 10);
                    pokemonImages.put(name, img);
                } else {
                    System.out.println("Image manquante : " + path);
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement images : " + e.getMessage());
}
}

    private void definirHP() {
        Random r = new Random();
        for (String name : allPokemons) {
            pokemonHP.put(name, 60 + r.nextInt(91)); // HP between 60 and 150
        }
    }

    private void placerPokemonsAleatoirement() {
        String[] roles = {"rook", "knight", "bishop", "queen", "king", "bishop", "knight", "rook", "pawn"};
        Random rand = new Random();

        List<String> pokemonsWhite = new ArrayList<>(allPokemons);
        List<String> pokemonsBlack = new ArrayList<>(allPokemons);
        Collections.shuffle(pokemonsWhite, rand);
        Collections.shuffle(pokemonsBlack, rand);

        for (int col = 0; col < BOARD_SIZE; col++) {
            board[0][col] = pokemonsWhite.get(col);
            isWhitePiece[0][col] = true;
            pieceType[0][col] = roles[col % roles.length];
            hp[0][col] = pokemonHP.get(board[0][col]);
            board[1][col] = pokemonsWhite.get(col + BOARD_SIZE);
            isWhitePiece[1][col] = true;
            pieceType[1][col] = "pawn";
            hp[1][col] = pokemonHP.get(board[1][col]);
        }
        for (int col = 0; col < BOARD_SIZE; col++) {
            board[8][col] = pokemonsBlack.get(col);
            isWhitePiece[8][col] = false;
            pieceType[8][col] = roles[col % roles.length];
            hp[8][col] = pokemonHP.get(board[8][col]);
            board[7][col] = pokemonsBlack.get(col + BOARD_SIZE);
            isWhitePiece[7][col] = false;
            pieceType[7][col] = "pawn";
            hp[7][col] = pokemonHP.get(board[7][col]);
        }
        for (int row = 2; row < 7; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[row][col] = null;
                hp[row][col] = 0;
            }
        }
    }

    private void ajouterElementsGraphiques() {
        statusText = new Texte(Couleur.NOIR, "Player 1's Turn",
                new Font("Arial", Font.BOLD, 16),
                new Point(150, BOARD_SIZE * TILE_SIZE + 20));
        ajouter(statusText);
    }

    private void updateStatusText() {
        if (whiteTurn) {
            statusText.setTexte("Player 1's Turn");
            statusText.setCouleur(Couleur.VERT);
            turnIndicator.setCouleur(Couleur.BLANC);
            turnLabel.setTexte("White");
            turnLabel.setCouleur(Couleur.NOIR);
        } else {
            statusText.setTexte("Player 2's Turn");
            statusText.setCouleur(Couleur.ROUGE);
            turnIndicator.setCouleur(Couleur.NOIR);
            turnLabel.setTexte("Black");
            turnLabel.setCouleur(Couleur.BLANC);
        }
    }

    private void updateDisplay() {
        listeDessins.clear();
        drawGrid();
        drawPokemons();
        ajouter(statusText); // Re-add status text every redraw
        dessiner();
    }

    private void drawGrid() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Couleur color = (row + col) % 2 == 0
                              ? new Couleur(173, 216, 230)
                              : new Couleur(169, 169, 169);
                Rectangle tile = new Rectangle(
                    color,
                    new Point(col * TILE_SIZE, (BOARD_SIZE - 1 - row) * TILE_SIZE),
                    TILE_SIZE,
                    TILE_SIZE,
                    true
                );
                ajouter(tile);
            }
        }
        if (selectedRow != -1) {
            Rectangle highlight = new Rectangle(
                new Couleur(255, 255, 0, 100),
                new Point(selectedCol * TILE_SIZE, (BOARD_SIZE - 1 - selectedRow) * TILE_SIZE),
                TILE_SIZE,
                TILE_SIZE,
                true
            );
            ajouter(highlight);
        }
        for (Point move : possibleMoves) {
            Rectangle hint = new Rectangle(
                new Couleur(0, 255, 0, 80),
                new Point(move.getX() * TILE_SIZE, (BOARD_SIZE - 1 - move.getY()) * TILE_SIZE),
                TILE_SIZE,
                TILE_SIZE,
                true
            );
            ajouter(hint);
        }
    }

    private void drawPokemons() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col] != null) {
                    String pokeName = board[row][col];
                    Image pokemon = pokemonImages.get(pokeName);
                    if (pokemon != null) {
                        Image clone = new Image(
                            Couleur.TRANSPARENT,
                            pokemon.getChemin(),
                            new Point(col * TILE_SIZE + 5, (BOARD_SIZE - 1 - row) * TILE_SIZE + 5)
                        );
                        clone.redimensionner(TILE_SIZE - 10, TILE_SIZE - 10);
                        if (!isWhitePiece[row][col]) clone.setCouleur(new Couleur(100, 100, 100, 255));
                        ajouter(clone);
                    }
                    Integer maxHp = pokemonHP.get(pokeName);
                    if (maxHp == null) maxHp = 100;
                    int hpWidth = (int)((TILE_SIZE - 10) * ((double)hp[row][col] / maxHp));
                    Rectangle hpBar = new Rectangle(
                        Couleur.VERT,
                        new Point(col * TILE_SIZE + 5, (BOARD_SIZE - 1 - row) * TILE_SIZE + 2),
                        hpWidth,
                        6,
                        true
                    );
                    ajouter(hpBar);

                    Texte hpText = new Texte(
                        Couleur.NOIR,
                        hp[row][col] + " HP",
                        new Font("Arial", Font.PLAIN, 12),
                        new Point(col * TILE_SIZE + TILE_SIZE / 2, (BOARD_SIZE - 1 - row) * TILE_SIZE - 5)
                    );
                    ajouter(hpText);
                }
            }
        }
    }

    private boolean isFacing(int srcRow, int srcCol, int dstRow, int dstCol) {
        if (srcRow == dstRow) {
            int min = Math.min(srcCol, dstCol) + 1;
            int max = Math.max(srcCol, dstCol);
            for (int c = min; c < max; c++) {
                if (board[srcRow][c] != null) return false;
            }
            return true;
        }
        if (srcCol == dstCol) {
            int min = Math.min(srcRow, dstRow) + 1;
            int max = Math.max(srcRow, dstRow);
            for (int r = min; r < max; r++) {
                if (board[r][srcCol] != null) return false;
            }
            return true;
        }
        if (Math.abs(srcRow - dstRow) == Math.abs(srcCol - dstCol)) {
            int rowStep = (dstRow > srcRow) ? 1 : -1;
            int colStep = (dstCol > srcCol) ? 1 : -1;
            int r = srcRow + rowStep;
            int c = srcCol + colStep;
            while (r != dstRow && c != dstCol) {
                if (board[r][c] != null) return false;
                r += rowStep;
                c += colStep;
            }
            return true;
        }
        return false;
    }
public boolean canAttack(Pokemon attacker, Pokemon target, Pokemon[][] board) {

    int ax = attacker.getX();

    int ay = attacker.getY();

    int tx = target.getX();

    int ty = target.getY();



    // Horizontal check (same row)

    if (ay == ty) {

        int min = Math.min(ax, tx);

        int max = Math.max(ax, tx);

        for (int x = min + 1; x < max; x++) {

            if (board[x][ay] != null) {

                return false; // There's a piece in between

            }

        }

        return true;

    }



    // Vertical check (same column)

    if (ax == tx) {

        int min = Math.min(ay, ty);

        int max = Math.max(ay, ty);

        for (int y = min + 1; y < max; y++) {

            if (board[ax][y] != null) {

                return false; // There's a piece in between

            }

        }

        return true;

    }



    return false; // Not in line

}

    private void handleClick(int x, int y) {
        int col = x / TILE_SIZE;
        int row = y / TILE_SIZE;
        if (col >= BOARD_SIZE || row >= BOARD_SIZE) return;

        if (selectedRow == -1) {
            if (board[row][col] != null && isWhitePiece[row][col] == whiteTurn) {
                selectedRow = row;
                selectedCol = col;
                calculatePossibleMoves();
                updateDisplay();
            }
        } else {
            if (isValidMove(col, row) && board[row][col] == null) {
                board[row][col] = board[selectedRow][selectedCol];
                isWhitePiece[row][col] = isWhitePiece[selectedRow][selectedCol];
                pieceType[row][col] = pieceType[selectedRow][selectedCol];
                hp[row][col] = hp[selectedRow][selectedCol];
                board[selectedRow][selectedCol] = null;
                hp[selectedRow][selectedCol] = 0;
                pieceType[selectedRow][selectedCol] = null;
                whiteTurn = !whiteTurn;
                updateStatusText();
            }
            else if (isValidMove(col, row) && board[row][col] != null && isWhitePiece[row][col] != whiteTurn && isFacing(selectedRow, selectedCol, row, col)) {
                int attackerHP = hp[selectedRow][selectedCol];
                int defenderHP = hp[row][col];
                int attackerPower = 20 + new Random().nextInt(31);
                int defenderPower = 10 + new Random().nextInt(21);

                defenderHP -= attackerPower;
                if (defenderHP > 0) {
                    attackerHP -= defenderPower;
                }

                if (defenderHP <= 0 && attackerHP > 0) {
                    board[row][col] = board[selectedRow][selectedCol];
                    isWhitePiece[row][col] = isWhitePiece[selectedRow][selectedCol];
                    pieceType[row][col] = pieceType[selectedRow][selectedCol];
                    hp[row][col] = attackerHP;
                    board[selectedRow][selectedCol] = null;
                    hp[selectedRow][selectedCol] = 0;
                    pieceType[selectedRow][selectedCol] = null;
                } else if (defenderHP > 0 && attackerHP <= 0) {
                    hp[row][col] = defenderHP;
                    board[selectedRow][selectedCol] = null;
                    hp[selectedRow][selectedCol] = 0;
                    pieceType[selectedRow][selectedCol] = null;
                } else if (defenderHP <= 0 && attackerHP <= 0) {
                    board[row][col] = null;
                    hp[row][col] = 0;
                    pieceType[row][col] = null;
                    board[selectedRow][selectedCol] = null;
                    hp[selectedRow][selectedCol] = 0;
                    pieceType[selectedRow][selectedCol] = null;
                } else {
                    hp[row][col] = defenderHP;
                    hp[selectedRow][selectedCol] = attackerHP;
                }
                whiteTurn = !whiteTurn;
                updateStatusText();
            }
            selectedRow = -1;
            selectedCol = -1;
            possibleMoves.clear();
            updateDisplay();
        }
    }

    private void calculatePossibleMoves() {
        possibleMoves.clear();
        String role = pieceType[selectedRow][selectedCol];
        if (role == null) return;
        switch(role) {
            case "pawn": calculatePawnMoves(); break;
            case "knight": calculateKnightMoves(); break;
            case "bishop": calculateBishopMoves(); break;
            case "rook": calculateRookMoves(); break;
            case "queen": calculateQueenMoves(); break;
            case "king": calculateKingMoves(); break;
        }
    }

    private void calculatePawnMoves() {
        int direction = isWhitePiece[selectedRow][selectedCol] ? 1 : -1;
        int startRow = isWhitePiece[selectedRow][selectedCol] ? 1 : 7;
        int newRow = selectedRow + direction;
        if (isValidPosition(newRow, selectedCol) && board[newRow][selectedCol] == null) {
            possibleMoves.add(new Point(selectedCol, newRow));
            if (selectedRow == startRow && isValidPosition(newRow + direction, selectedCol) &&
                board[newRow + direction][selectedCol] == null) {
                possibleMoves.add(new Point(selectedCol, newRow + direction));
            }
        }
        int[] captureCols = {selectedCol - 1, selectedCol + 1};
        for (int col : captureCols) {
            if (isValidPosition(newRow, col)) {
                if (board[newRow][col] != null && isWhitePiece[newRow][col] != whiteTurn) {
                    possibleMoves.add(new Point(col, newRow));
                }
            }
        }
    }

    private void calculateKnightMoves() {
        int[][] jumps = {{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
                        {1, -2}, {1, 2}, {2, -1}, {2, 1}};
        for (int[] jump : jumps) {
            int newRow = selectedRow + jump[0];
            int newCol = selectedCol + jump[1];
            if (isValidPosition(newRow, newCol) &&
                (board[newRow][newCol] == null || isWhitePiece[newRow][newCol] != whiteTurn)) {
                possibleMoves.add(new Point(newCol, newRow));
            }
        }
    }

    private void calculateBishopMoves() {
        int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        for (int[] dir : directions) {
            for (int dist = 1; dist < BOARD_SIZE; dist++) {
                int newRow = selectedRow + dir[0] * dist;
                int newCol = selectedCol + dir[1] * dist;
                if (!isValidPosition(newRow, newCol)) break;
                if (board[newRow][newCol] == null) {
                    possibleMoves.add(new Point(newCol, newRow));
                } else {
                    if (isWhitePiece[newRow][newCol] != whiteTurn) {
                        possibleMoves.add(new Point(newCol, newRow));
                    }
                    break;
                }
            }
        }
    }

    private void calculateRookMoves() {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] dir : directions) {
            for (int dist = 1; dist < BOARD_SIZE; dist++) {
                int newRow = selectedRow + dir[0] * dist;
                int newCol = selectedCol + dir[1] * dist;
                if (!isValidPosition(newRow, newCol)) break;
                if (board[newRow][newCol] == null) {
                    possibleMoves.add(new Point(newCol, newRow));
                } else {
                    if (isWhitePiece[newRow][newCol] != whiteTurn) {
                        possibleMoves.add(new Point(newCol, newRow));
                    }
                    break;
                }
            }
        }
    }

    private void calculateQueenMoves() {
        calculateBishopMoves();
        calculateRookMoves();
    }

    private void calculateKingMoves() {
        int[][] directions = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1},
                         {0, 1}, {1, -1}, {1, 0}, {1, 1}};
        for (int[] dir : directions) {
            int newRow = selectedRow + dir[0];
            int newCol = selectedCol + dir[1];
            if (isValidPosition(newRow, newCol) &&
                (board[newRow][newCol] == null || isWhitePiece[newRow][newCol] != whiteTurn)) {
                possibleMoves.add(new Point(newCol, newRow));
            }
        }
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
    }

    private boolean isValidMove(int col, int row) {
        for (Point move : possibleMoves) {
            if (move.getX() == col && move.getY() == row) {
                return true;
            }
        }
        return false;
    }

    public void ajouter(Dessin d) {
        listeDessins.add(d);
    }

    protected void dessiner() {
        synchronized (listeDessins) {
    List<Dessin> safeCopy = new ArrayList<>(listeDessins);
    p.clear();
    p.ajouterTous(safeCopy);
}
p.repaint();
p.revalidate();
    }

    public static void main(String[] args) {
        new PokemonChess();
}
}
