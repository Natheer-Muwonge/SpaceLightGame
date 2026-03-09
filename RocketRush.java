package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Main game controller for Rocket Rush.
 * This class manages state, keyboard input, score, spawning, collisions,
 * and rendering.
 */
public class RocketRush extends Game {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int STARTING_LIVES = 3;

    private Ship ship;
    private ArrayList<Asteroid> asteroids;
    private ArrayList<SpaceOrbs> orbs;

    private int lives;
    private GameState gameState;
    private Spawner spawner;
    private ScoreManager scoreManager;
    private ArrayList<ScorePopup> popups;

    /**
     * Creates a new Rocket Rush game window and initializes the game.
     */
    public RocketRush() {
        super("Rocket Rush", WIDTH, HEIGHT);

        asteroids = new ArrayList<>();
        orbs = new ArrayList<>();
        popups = new ArrayList<>();
        spawner = new Spawner(45, 180);
        scoreManager = new ScoreManager();
        lives = STARTING_LIVES;
        gameState = GameState.START;

        initializeShip();

        this.setFocusable(true);

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();

                if (code == KeyEvent.VK_ENTER && gameState == GameState.START) {
                    gameState = GameState.PLAYING;
                }

                if (code == KeyEvent.VK_R && gameState == GameState.GAME_OVER) {
                    resetGame();
                }

                if (code == KeyEvent.VK_P && gameState == GameState.PLAYING) {
                    gameState = GameState.PAUSED;
                } else if (code == KeyEvent.VK_P && gameState == GameState.PAUSED) {
                    gameState = GameState.PLAYING;
                }

                if (ship != null) {
                    if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                        ship.setForwardPressed(true);
                    }
                    if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
                        ship.setLeftPressed(true);
                    }
                    if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
                        ship.setRightPressed(true);
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int code = e.getKeyCode();

                if (ship != null) {
                    if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                        ship.setForwardPressed(false);
                    }
                    if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
                        ship.setLeftPressed(false);
                    }
                    if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
                        ship.setRightPressed(false);
                    }
                }
            }
        });
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocus();
    }

    /**
     * Initializes the player's ship at the center of the screen.
     */
    private void initializeShip() {
        Point[] shipShape = {
            new Point(0, 20),
            new Point(15, 10),
            new Point(30, 20),
            new Point(15, 0)
        };

        ship = new Ship(shipShape, new Point(WIDTH / 2.0, HEIGHT / 2.0), -90);
    }

    /**
     * Resets all game data to start a new run.
     */
    private void resetGame() {
        asteroids.clear();
        orbs.clear();
        popups.clear();
        spawner.reset();
        scoreManager.resetCurrentScore();
        lives = STARTING_LIVES;
        gameState = GameState.START;
        initializeShip();
    }

    /**
     * Updates all gameplay logic for one frame.
     */
    private void updateGame() {
        spawnElements();

        if (ship != null) {
            ship.move();
            ship.wrapAround(WIDTH, HEIGHT);
        }

        Asteroid.updateAsteroids(asteroids, WIDTH, HEIGHT);
        SpaceOrbs.updateOrbs(orbs);

        Iterator<ScorePopup> popupIter = popups.iterator();
        while (popupIter.hasNext()) {
            ScorePopup p = popupIter.next();
            p.y -= 1;
            p.framesLeft--;
            if (p.framesLeft <= 0) popupIter.remove();
        }

        handleCollisions();
        scoreManager.updateTimeScore();
    }

    /**
     * Spawns asteroids and orbs based on the spawner's timers.
     */
    private void spawnElements() {
        if (spawner.shouldSpawnAsteroid()) {
            Point spawnPoint = spawner.randomEdgePoint(WIDTH, HEIGHT);

            Point[] asteroidShape = {
                new Point(0, 20),
                new Point(20, 0),
                new Point(50, 10),
                new Point(60, 40),
                new Point(30, 60),
                new Point(0, 50)
            };

            asteroids.add(new Asteroid(
                asteroidShape,
                spawnPoint,
                spawner.randomRotation(),
                2.5
            ));
        }

        if (spawner.shouldSpawnOrb()) {
            Point spawnPoint = spawner.randomInteriorPoint(WIDTH, HEIGHT, 60);
            orbs.add(new SpaceOrbs(spawnPoint));
        }
    }

    /**
     * Handles collisions between the ship and other elements.
     */
    private void handleCollisions() {
        Iterator<Asteroid> asteroidIterator = asteroids.iterator();
        while (asteroidIterator.hasNext()) {
            Asteroid asteroid = asteroidIterator.next();
            if (ship.collides(asteroid)) {
                asteroidIterator.remove();
                lives--;

                if (lives <= 0) {
                    gameState = GameState.GAME_OVER;
                    scoreManager.updateHighScore();
                    ship.setForwardPressed(false);
                    ship.setLeftPressed(false);
                    ship.setRightPressed(false);
                }
                break;
            }
        }

        Iterator<SpaceOrbs> orbIterator = orbs.iterator();
        while (orbIterator.hasNext()) {
            SpaceOrbs orb = orbIterator.next();
            if (ship.collides(orb)) {
                popups.add(new ScorePopup((int) orb.position.x, (int) orb.position.y));
                orbIterator.remove();
                scoreManager.addOrbBonus();
            }
        }
    }

    /**
     * Paints the entire game screen.
     *
     * @param brush graphics brush used to draw
     */
    @Override
    public void paint(Graphics brush) {
        brush.setColor(Color.black);
        brush.fillRect(0, 0, WIDTH, HEIGHT);

        if (gameState == GameState.PLAYING) {
            updateGame();
        }

        if (ship != null) {
            ship.draw(brush);
        }

        for (Asteroid asteroid : asteroids) {
            asteroid.draw(brush);
        }

        for (SpaceOrbs orb : orbs) {
            orb.draw(brush);
        }

        brush.setColor(new Color(255, 215, 0));
        brush.setFont(new Font("Arial", Font.BOLD, 18));
        for (ScorePopup p : popups) {
            brush.drawString("+100", p.x - 15, p.y);
        }

        drawHud(brush);

        if (gameState == GameState.START) {
            drawCenteredMessage(
                brush,
                "Rocket Rush - Press ENTER to Start",
                HEIGHT / 2
            );
        } else if (gameState == GameState.PAUSED) {
            drawCenteredMessage(
                brush,
                "Paused - Press P to Resume",
                HEIGHT / 2
            );
        } else if (gameState == GameState.GAME_OVER) {
            drawCenteredMessage(
                brush,
                "Game Over - Press R to Restart",
                HEIGHT / 2
            );
        }

        repaint();
    }

    /**
     * Draws the score, high score, lives, and state text.
     *
     * @param brush graphics brush used to draw
     */
    private void drawHud(Graphics brush) {
        brush.setColor(Color.white);
        brush.setFont(new Font("Arial", Font.PLAIN, 16));

        brush.drawString("Score: " + scoreManager.getScore(), 20, 30);
        brush.drawString("High Score: " + scoreManager.getHighScore(), 20, 55);

        for (int i = 0; i < lives; i++) {
            brush.setColor(Color.red);
            drawHeart(brush, 20 + i * 28, 75, 10);
        }
        for (int i = lives; i < STARTING_LIVES; i++) {
            brush.setColor(new Color(80, 0, 0));
            drawHeart(brush, 20 + i * 28, 75, 10);
        }

        brush.setColor(Color.white);
        brush.drawString("State: " + gameState, 20, 110);

        brush.drawString("Controls: W:Up, A: Left Turn, D: Right Turn, P: Pause Game",
                20, HEIGHT - 40);
    }

    /**
     * Draws a centered message on screen.
     *
     * @param brush graphics brush used to draw
     * @param message text to display
     * @param y y-coordinate for the message
     */
    private void drawCenteredMessage(Graphics brush, String message, int y) {
        brush.setColor(Color.white);
        brush.setFont(new Font("Arial", Font.BOLD, 24));
        int textWidth = brush.getFontMetrics().stringWidth(message);
        int x = (WIDTH - textWidth) / 2;
        brush.drawString(message, x, y);
    }

    /**
     * Main entry point for the program.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        RocketRush game = new RocketRush();
        game.repaint();
    }

    /**
     * Inner class that manages the current score and high score.
     * The current score increases by 1 every second while playing,
     * and increases by 5 whenever an orb is collected.
     */
    private void drawHeart(Graphics brush, int cx, int cy, int s) {
        brush.fillArc(cx - s, cy - s / 2, s, s, 0, 180);
        brush.fillArc(cx, cy - s / 2, s, s, 0, 180);
        int[] xp = {cx - s, cx + s, cx};
        int[] yp = {cy, cy, cy + s};
        brush.fillPolygon(xp, yp, 3);
    }

    private class ScorePopup {
        int x, y, framesLeft;
        ScorePopup(int x, int y) { this.x = x; this.y = y; this.framesLeft = 40; }
    }

    private class ScoreManager {
        private int score;
        private int highScore;
        private int frameCounter;

        /**
         * Creates a new score manager.
         */
        public ScoreManager() {
            score = 0;
            highScore = 0;
            frameCounter = 0;
        }

        /**
         * Updates the time-based score. Since the framework repaints
         * roughly every tenth of a second, 10 frames is approximately 1 second.
         */
        public void updateTimeScore() {
            frameCounter++;
            if (frameCounter >= 10) {
                score++;
                frameCounter = 0;
            }
        }

        /**
         * Adds the orb collection bonus.
         */
        public void addOrbBonus() {
            score += 100;
        }

        /**
         * Updates the high score if the current score is greater.
         */
        public void updateHighScore() {
            if (score > highScore) {
                highScore = score;
            }
        }

        /**
         * Resets only the current run's score.
         */
        public void resetCurrentScore() {
            score = 0;
            frameCounter = 0;
        }

        /**
         * Returns the current score.
         *
         * @return current score
         */
        public int getScore() {
            return score;
        }

        /**
         * Returns the high score.
         *
         * @return high score
         */
        public int getHighScore() {
            return highScore;
        }
    }
}