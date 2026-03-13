package game;

import java.awt.*;

class Ship extends Polygon {

    private static final double SPEED = 4.0;
    private static final double ROTATE_SPEED = 5.0;

    private boolean forwardPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    public Ship(Point[] shape, Point position, double rotation) {
        super(shape, position, rotation);
    }

    public void setForwardPressed(boolean b) { forwardPressed = b; }
    public void setLeftPressed(boolean b) { leftPressed = b; }
    public void setRightPressed(boolean b) { rightPressed = b; }

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
