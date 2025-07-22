import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import MG2D.geometrie.Point;

public class VisualPokemonChess {
    private List<Point> validMoves = new ArrayList<>();
    private static final int BOARD_SIZE = 9;
    private static final int TILE_SIZE = 80;
    private Board board;
    private JFrame frame;
    private JPanel chessPanel;
    private JLabel statusLabel;
    private int selectedRow = -1, selectedCol = -1;
    private boolean player1Turn = true;
    private Map<String, BufferedImage> pokemonImages = new HashMap<>();
    private List<String> allPokemonNames = new ArrayList<>();
    

    public static void main(String[] args) {
        new VisualPokemonChess();
    }

    public VisualPokemonChess() {
        loadPokemonImages();
        board = new Board(BOARD_SIZE);
        setupDefaultBoard();
        createGUI();
    }

    private void setupDefaultBoard() {
        // Player 2 (Top)
        board.placePokemon(0, 0, new Pokemon(1, "Bulbizarre", Type.PLANTE, Type.POISON, 45, 49, 49, 45, false));
        board.placePokemon(0, 1, new Pokemon(4, "Salamèche", Type.FEU, -1, 39, 52, 43, 65, false));
        board.placePokemon(0, 2, new Pokemon(7, "Carapuce", Type.EAU, -1, 44, 48, 65, 43, false));
        board.placePokemon(0, 3, new Pokemon(25, "Pikachu", Type.NORMAL, -1, 35, 55, 40, 90, false));
        board.placePokemon(0, 4, new Pokemon(150, "Mewtwo", Type.SPECTRE, -1, 106, 110, 90, 130, false));
        board.placePokemon(0, 5, new Pokemon(25, "Pikachu", Type.NORMAL, -1, 35, 55, 40, 90, false));
        board.placePokemon(0, 6, new Pokemon(7, "Carapuce", Type.EAU, -1, 44, 48, 65, 43, false));
        board.placePokemon(0, 7, new Pokemon(4, "Salamèche", Type.FEU, -1, 39, 52, 43, 65, false));
        board.placePokemon(0, 8, new Pokemon(1, "Bulbizarre", Type.PLANTE, Type.POISON, 45, 49, 49, 45, false));

        for (int i = 0; i < 9; i++) {
            board.placePokemon(1, i, new Pokemon(10 + i, "Pawn", Type.NORMAL, -1, 50, 20, 10, 20, false));
        }

        // Player 1 (Bottom)
        board.placePokemon(8, 0, new Pokemon(1, "Bulbizarre", Type.PLANTE, Type.POISON, 45, 49, 49, 45, true));
        board.placePokemon(8, 1, new Pokemon(4, "Salamèche", Type.FEU, -1, 39, 52, 43, 65, true));
        board.placePokemon(8, 2, new Pokemon(7, "Carapuce", Type.EAU, -1, 44, 48, 65, 43, true));
        board.placePokemon(8, 3, new Pokemon(25, "Pikachu", Type.NORMAL, -1, 35, 55, 40, 90, true));
        board.placePokemon(8, 4, new Pokemon(151, "Mew", Type.SPECTRE, -1, 100, 100, 100, 100, true));
        board.placePokemon(8, 5, new Pokemon(25, "Pikachu", Type.NORMAL, -1, 35, 55, 40, 90, true));
        board.placePokemon(8, 6, new Pokemon(7, "Carapuce", Type.EAU, -1, 44, 48, 65, 43, true));
        board.placePokemon(8, 7, new Pokemon(4, "Salamèche", Type.FEU, -1, 39, 52, 43, 65, true));
        board.placePokemon(8, 8, new Pokemon(1, "Bulbizarre", Type.PLANTE, Type.POISON, 45, 49, 49, 45, true));

        for (int i = 0; i < 9; i++) {
            board.placePokemon(7, i, new Pokemon(40 + i, "Pawn", Type.NORMAL, -1, 50, 20, 10, 20, true));
        }
    }

    private void loadPokemonImages() {
        File imagesDir = new File("images");
        if (!imagesDir.exists()) {
            boolean created = imagesDir.mkdir();
            if (!created) {
                JOptionPane.showMessageDialog(null,
                    "Could not create images directory!\n" +
                    "Please create an 'images' folder manually in:\n" +
                    new File("").getAbsolutePath(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }
    
        for (int i = 1; i <= 151; i++) {
            String name = String.valueOf(i); 
    
            try {
                File imageFile = new File("images/" + name + ".png");
    
                if (imageFile.exists()) {
                    BufferedImage img = ImageIO.read(imageFile);
                    if (img != null) {
                        pokemonImages.put(name, img);
                    } else {
                        System.out.println("Could not load: " + name + ".png (null image)");
                    }
                } else {
                    System.out.println("Missing image: " + name + ".png");
                }
    
                allPokemonNames.add(name);
    
            } catch (IOException e) {
                System.err.println("Error loading " + name + ".png: " + e.getMessage());
                allPokemonNames.add(name);
            }
        }
    }


    private void createGUI() {
        frame = new JFrame("Pokemon Chess");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(BOARD_SIZE * TILE_SIZE + 50, BOARD_SIZE * TILE_SIZE + 100);

        chessPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);
            }
        };
        chessPanel.setPreferredSize(new Dimension(BOARD_SIZE * TILE_SIZE, BOARD_SIZE * TILE_SIZE));
        chessPanel.addMouseListener(new ChessMouseListener());

        statusLabel = new JLabel("Player 1's Turn", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Verdana", Font.BOLD, 24));
        statusLabel.setForeground(Color.GREEN);
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(0, 0, 139)); // Dark Blue

        frame.add(chessPanel, BorderLayout.CENTER);
        frame.add(statusLabel, BorderLayout.NORTH);
        frame.setVisible(true);
    }

    private void drawBoard(Graphics g) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                // White tiles with a gray border
                g.setColor(Color.WHITE);
                g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                g.setColor(Color.LIGHT_GRAY);
                g.drawRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);

                if (row == selectedRow && col == selectedCol) {
                    g.setColor(new Color(0, 150, 255, 100)); // A light blue for selection
                    g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }

                Pokemon pokemon = board.getPokemon(row, col);
                if (pokemon != null) {
                    BufferedImage img = pokemonImages.get(String.valueOf(pokemon.getNumero()));
                    if (img != null) {
                        Image scaled = img.getScaledInstance(TILE_SIZE - 10, TILE_SIZE - 10, Image.SCALE_SMOOTH);
                        g.drawImage(scaled, col * TILE_SIZE + 5, row * TILE_SIZE, null);
                    }

                    // Draw health bar - green, positioned below the sprite
                    g.setColor(Color.GREEN);
                    g.fillRect(col * TILE_SIZE + 10, row * TILE_SIZE + TILE_SIZE - 20,
                            (int) ((TILE_SIZE - 20) * ((double) pokemon.getHp() / pokemon.getMaxHp())), 10);

                    // Draw health value
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Arial", Font.BOLD, 10));
                    String hpStr = String.valueOf(pokemon.getHp());
                    g.drawString(hpStr, col * TILE_SIZE + 15, row * TILE_SIZE + TILE_SIZE - 11);
                }
            }
        }
        
        // Draw valid moves
        for (Point move : validMoves) {
            g.setColor(new Color(0, 255, 0, 120)); // Green transparent
            g.fillRect((int)move.getX() * TILE_SIZE, (int)move.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }
    }

    private class ChessMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            int col = e.getX() / TILE_SIZE;
            int row = e.getY() / TILE_SIZE;
            
            if (selectedRow == -1) {
                Pokemon pokemon = board.getPokemon(row, col);
                if (pokemon != null && pokemon.isPlayer1() == player1Turn) {
                    selectedRow = row;
                    selectedCol = col;
                    validMoves = board.getValidMoves(row, col);
                    chessPanel.repaint();
                }
            } else {
                Pokemon selected = board.getPokemon(selectedRow, selectedCol);
                Pokemon target = board.getPokemon(row, col);
            
                if (target == null) {
                    // Check if this is a valid move
                    boolean validMove = false;
                    for (Point p : validMoves) {
                        if ((int)p.getX() == col && (int)p.getY() == row) {
                            validMove = true;
                            break;
                        }
                    }
                    
                    if (validMove) {
                        board.move(selectedRow, selectedCol, row, col);
                        player1Turn = !player1Turn;
                        statusLabel.setText((player1Turn ? "Player 1" : "Player 2") + "'s Turn");
                        statusLabel.setForeground(player1Turn ? Color.GREEN : Color.RED);
                    }
                } else if (target.isPlayer1() != selected.isPlayer1()) {
                    // Attack if adjacent
                    if (Math.abs(row - selectedRow) <= 1 && Math.abs(col - selectedCol) <= 1) {
                        board.attack(selectedRow, selectedCol, row, col);
                        player1Turn = !player1Turn;
                        statusLabel.setText((player1Turn ? "Player 1" : "Player 2") + "'s Turn");
                        statusLabel.setForeground(player1Turn ? Color.GREEN : Color.RED);
                    }
                }
            
                selectedRow = -1;
                selectedCol = -1;
                validMoves.clear(); 
                chessPanel.repaint();
                
                // Check if game is over
                if (!board.bothMewtwoAlive()) {
                    String winner = "Player 1";
                    if (!board.isMewAlive()) {
                        winner = "Player 2";
                    }
                    JOptionPane.showMessageDialog(frame, winner + " wins!");
                    frame.dispose();
                }
            }
        }
    }
}