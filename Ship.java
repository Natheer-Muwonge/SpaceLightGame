package game;

import java.awt.*;

/**
 * Represents the player's ship.
 * Handles movement, rotation, and drawing based on keyboard input.
 */
class Ship extends Polygon {

    private static final double SPEED = 4.0;
    private static final double ROTATE_SPEED = 5.0;

    private boolean forwardPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    /**
     * Creates a new Ship with the given shape, position, and rotation.
     * @param shape the points defining the ship's shape
     * @param position the starting position of the ship
     * @param rotation the initial rotation in degrees
     */
    public Ship(Point[] shape, Point position, double rotation) {
        super(shape, position, rotation);
    }

    /**
     * Sets whether the forward key is pressed.
     * @param b true if forward is pressed
     */
    public void setForwardPressed(boolean b) {
        forwardPressed = b;
    }

    /**
     * Sets whether the left key is pressed.
     * @param b true if left is pressed
     */
    public void setLeftPressed(boolean b) {
        leftPressed = b;
    }

    /**
     * Sets whether the right key is pressed.
     * @param b true if right is pressed
     */
    public void setRightPressed(boolean b) {
        rightPressed = b;
    }

    /**
     * Updates the ship's position and rotation based on current key input.
     */
    public void move() {
        if (leftPressed) {
            rotation -= ROTATE_SPEED;
        }
        if (rightPressed) {
            rotation += ROTATE_SPEED;
        }
        if (forwardPressed) {
            position.x += Math.cos(Math.toRadians(rotation)) * SPEED;
            position.y += Math.sin(Math.toRadians(rotation)) * SPEED;
        }
    }

    /**
     * Wraps the ship to the opposite side of the screen if it goes off edge.
     * @param width the screen width
     * @param height the screen height
     */
    public void wrapAround(int width, int height) {
        if (position.x < 0) {
            position.x = width;
        }
        if (position.x > width) {
            position.x = 0;
        }
        if (position.y < 0) {
            position.y = height;
        }
        if (position.y > height) {
            position.y = 0;
        }
    }

    /**
     * Draws the ship as a white outline.
     * @param brush the graphics context used for drawing
     */
    public void draw(Graphics brush) {
        brush.setColor(Color.white);
        Point[] points = getPoints();
        for (int i = 0; i < points.length; i++) {
            int next = (i + 1) % points.length;
            brush.drawLine(
                (int) points[i].x,
                (int) points[i].y,
                (int) points[next].x,
                (int) points[next].y
            );
        }
    }
}
