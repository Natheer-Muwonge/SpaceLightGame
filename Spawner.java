package game;

import java.util.Random;

/**
 * Handles timed spawning decisions and spawn locations for game elements.
 * This class does not directly create asteroids or orbs; instead, it tells
 * the main game when and where to spawn them.
 */
public class Spawner {
    private int asteroidCounter;
    private int orbCounter;
    private final int asteroidSpawnDelay;
    private final int orbSpawnDelay;
    private final Random random;

    /**
     * Creates a spawner with the given frame delays.
     *
     * @param asteroidSpawnDelay number of frames between asteroid spawns
     * @param orbSpawnDelay number of frames between orb spawns
     */
    public Spawner(int asteroidSpawnDelay, int orbSpawnDelay) {
        this.asteroidSpawnDelay = asteroidSpawnDelay;
        this.orbSpawnDelay = orbSpawnDelay;
        this.random = new Random();
        this.asteroidCounter = 0;
        this.orbCounter = 0;
    }

    /**
     * Updates the asteroid timer and returns whether an asteroid should spawn.
     *
     * @return true if an asteroid should spawn this frame
     */
    public boolean shouldSpawnAsteroid() {
        asteroidCounter++;
        if (asteroidCounter >= asteroidSpawnDelay) {
            asteroidCounter = 0;
            return true;
        }
        return false;
    }

    /**
     * Updates the orb timer and returns whether an orb should spawn.
     *
     * @return true if an orb should spawn this frame
     */
    public boolean shouldSpawnOrb() {
        orbCounter++;
        if (orbCounter >= orbSpawnDelay) {
            orbCounter = 0;
            return true;
        }
        return false;
    }

    /**
     * Returns a random point on the edge of the screen.
     *
     * @param width game width
     * @param height game height
     * @return random spawn point on an edge
     */
    public Point randomEdgePoint(int width, int height) {
        int side = random.nextInt(4);

        switch (side) {
            case 0: return new Point(random.nextInt(width), 0);          // top
            case 1: return new Point(random.nextInt(width), height);     // bottom
            case 2: return new Point(0, random.nextInt(height));         // left
            default: return new Point(width, random.nextInt(height));    // right
        }
    }

    /**
     * Returns a random point inside the screen with a margin from the edges.
     *
     * @param width game width
     * @param height game height
     * @param margin minimum distance from edges
     * @return random interior point
     */
    public Point randomInteriorPoint(int width, int height, int margin) {
        double x = margin + random.nextInt(Math.max(1, width - 2 * margin));
        double y = margin + random.nextInt(Math.max(1, height - 2 * margin));
        return new Point(x, y);
    }

    /**
     * Returns a random rotation angle from 0 to 359 degrees.
     *
     * @return random rotation
     */
    public double randomRotation() {
        return random.nextInt(360);
    }

    /**
     * Resets the internal spawn counters.
     */
    public void reset() {
        asteroidCounter = 0;
        orbCounter = 0;
    }
}