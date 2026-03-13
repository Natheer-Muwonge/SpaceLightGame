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
 * Manages game state, keyboard input, score, spawning, collisions, and rendering.
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

                if (code == KeyEvent.VK_P) {
                    if (gameState == GameState.PLAYING) {
                        gameState = GameState.PAUSED;
                        stopShipInput();
                    } else if (gameState == GameState.PAUSED) {
                        gameState = GameState.PLAYING;
                    }
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

    /**
     * Requests focus so the game window receives keyboard input.
     */
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
            if (p.framesLeft <= 0) {
                popupIter.remove();
            }
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
     * Handles collisions between the ship and asteroids or orbs.
     */
    private void handleCollisions() {
        if (ship == null) {
            return;
        }
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
     * Paints the entire game screen each frame.
     * @param brush the graphics context used for drawing
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

        orbs.forEach(orb -> orb.draw(brush));

        brush.setColor(new Color(255, 215, 0));
        brush.setFont(new Font("Arial", Font.BOLD, 18));
        for (ScorePopup p : popups) {
            brush.drawString("+100", p.x - 15, p.y);
        }

        drawHud(brush);

        if (gameState == GameState.START) {
            drawCenteredMessage(brush, "Rocket Rush - Press ENTER to Start", HEIGHT / 2);
        } else if (gameState == GameState.PAUSED) {
            drawPauseMenu(brush);
        } else if (gameState == GameState.GAME_OVER) {
            drawCenteredMessage(brush, "Game Over - Press R to Restart", HEIGHT / 2);
        }
    }

    /**
     * Draws the HUD showing score, high score, lives, and controls.
     * @param brush the graphics context used for drawing
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
        brush.drawString("Controls: W:Up, A: Left Turn, D: Right Turn, P: Pause Game", 20, HEIGHT - 40);
    }

    /**
     * Draws a centered message on screen at the given y position.
     *
     * @param brush the graphics context used for drawing
     * @param message the text to display
     * @param y the y-coordinate for the message
     */
    private void drawCenteredMessage(Graphics brush, String message, int y) {
        brush.setColor(Color.white);
        brush.setFont(new Font("Arial", Font.BOLD, 24));
        int textWidth = brush.getFontMetrics().stringWidth(message);
        int x = (WIDTH - textWidth) / 2;
        brush.drawString(message, x, y);
    }

    /**
     * Stops all ship movement input, used when the game is paused.
     */
    private void stopShipInput() {
        if (ship != null) {
            ship.setForwardPressed(false);
            ship.setLeftPressed(false);
            ship.setRightPressed(false);
        }
    }

    /**
     * Draws the pause menu overlay showing score, high score, lives, and resume instructions.
     *
     * @param brush the graphics context used for drawing
     */
    private void drawPauseMenu(Graphics brush) {
        brush.setColor(new Color(0, 0, 0, 170));
        brush.fillRect(0, 0, WIDTH, HEIGHT);

        int boxWidth = 320;
        int boxHeight = 220;
        int boxX = (WIDTH - boxWidth) / 2;
        int boxY = (HEIGHT - boxHeight) / 2;

        brush.setColor(Color.darkGray);
        brush.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

        brush.setColor(Color.white);
        brush.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

        brush.setFont(new Font("Arial", Font.BOLD, 28));
        brush.drawString("PAUSED", boxX + 95, boxY + 40);

        brush.setFont(new Font("Arial", Font.PLAIN, 18));
        brush.drawString("Current Score: " + scoreManager.getScore(), boxX + 70, boxY + 90);
        brush.drawString("High Score: " + scoreManager.getHighScore(), boxX + 78, boxY + 125);
        brush.drawString("Lives Left: " + lives, boxX + 95, boxY + 160);

        brush.setFont(new Font("Arial", Font.PLAIN, 16));
        brush.drawString("Press P to Resume", boxX + 92, boxY + 195);
    }

    /**
     * Draws a heart shape at the given position, used for the lives display.
     *
     * @param brush the graphics context used for drawing
     * @param cx the x center of the heart
     * @param cy the y center of the heart
     * @param s the size of the heart
     */
    private void drawHeart(Graphics brush, int cx, int cy, int s) {
        brush.fillArc(cx - s, cy - s / 2, s, s, 0, 180);
        brush.fillArc(cx, cy - s / 2, s, s, 0, 180);
        int[] xp = {cx - s, cx + s, cx};
        int[] yp = {cy, cy, cy + s};
        brush.fillPolygon(xp, yp, 3);
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
     * Represents a floating score popup that appears when an orb is collected.
     */
    private class ScorePopup {
        int x, y, framesLeft;

        /**
         * Creates a new ScorePopup at the given position.
         * @param x the x position of the popup
         * @param y the y position of the popup
         */
        ScorePopup(int x, int y) {
            this.x = x;
            this.y = y;
            this.framesLeft = 40;
        }
    }

    /**
     * Manages the current score and high score for the game.
     */
    private class ScoreManager {
        private int score;
        private int highScore;
        private int frameCounter;

        /**
         * Creates a new ScoreManager with all values set to zero.
         */
        public ScoreManager() {
            score = 0;
            highScore = 0;
            frameCounter = 0;
        }

        /**
         * Increments the score by 1 approximately every second.
         */
        public void updateTimeScore() {
            frameCounter++;
            if (frameCounter >= 10) {
                score++;
                frameCounter = 0;
            }
        }

        /**
         * Adds the orb collection bonus to the score.
         */
        public void addOrbBonus() {
            score += 100;
        }

        /**
         * Updates the high score if the current score exceeds it.
         */
        public void updateHighScore() {
            if (score > highScore) {
                highScore = score;
            }
        }

        /**
         * Resets the current score and frame counter for a new run.
         */
        public void resetCurrentScore() {
            score = 0;
            frameCounter = 0;
        }

        /**
         * Returns the current score.
         * @return the current score
         */
        public int getScore() {
            return score;
        }

        /**
         * Returns the high score.
         * @return the high score
         */
        public int getHighScore() {
            return Math.max(highScore, score);
        }
    }
}
