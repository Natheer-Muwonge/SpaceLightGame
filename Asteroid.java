package game;

import java.awt.*;
import java.util.ArrayList;

class Asteroid extends Polygon {

    private double speed;

    public Asteroid(Point[] shape, Point position, double rotation, double speed) {
        super(shape, position, rotation);
        this.speed = speed;
    }

    public void move() {
        position.x += Math.cos(Math.toRadians(rotation)) * speed;
        position.y += Math.sin(Math.toRadians(rotation)) * speed;
    }

    public boolean isOffScreen(int width, int height) {
        if (position.x < -120) {
            return true;
        }
        if (position.x > width + 120) {
            return true;
        }
        if (position.y < -120) {
            return true;
        }
        if (position.y > height + 120) {
            return true;
        }
        return false;
    }

    public void draw(Graphics brush) {
        Point[] points = getPoints();
        int[] xp = new int[points.length];
        int[] yp = new int[points.length];
        for (int i = 0; i < points.length; i++) {
            xp[i] = (int) points[i].x;
            yp[i] = (int) points[i].y;
        }
        brush.setColor(Color.gray);
        brush.fillPolygon(xp, yp, points.length);
        brush.setColor(Color.darkGray);
        brush.drawPolygon(xp, yp, points.length);
    }

    public static void updateAsteroids(ArrayList<Asteroid> asteroids, int width, int height) {
        for (int i = asteroids.size() - 1; i >= 0; i--) {
            if (asteroids.get(i).isOffScreen(width, height)) {
                asteroids.remove(i);
            }
        }
        for (int i = 0; i < asteroids.size(); i++) {
            asteroids.get(i).move();
        }
    }
}
