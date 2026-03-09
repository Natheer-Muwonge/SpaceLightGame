package game;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

class YourGameName extends Game {

    private Ship ship;
    private ArrayList<Asteroid> asteroids = new ArrayList<>();
    private ArrayList<SpaceOrbs> orbs = new ArrayList<>();

    int lives = 3;
    static int counter = 0;

    public YourGameName() {
        super("Rocket Rush", 800, 600);
        this.setFocusable(true);
        this.requestFocus();

        Point[] shipShape = {
            new Point(0, -12),
            new Point(24, 0),
            new Point(0, 12)
        };
        ship = new Ship(shipShape, new Point(400, 300), 0);

        spawnAsteroid(100, 100, 45, 2.0);
        spawnAsteroid(700, 100, 135, 2.5);
        spawnAsteroid(700, 500, 225, 1.8);
        spawnAsteroid(100, 500, 315, 2.2);

        orbs.add(new SpaceOrbs(new Point(300, 200)));
        orbs.add(new SpaceOrbs(new Point(500, 400)));
    }

    private void spawnAsteroid(double x, double y, double angle, double speed) {
        Point[] shape = {
            new Point(0, -30),
            new Point(20, -20),
            new Point(30, 0),
            new Point(15, 25),
            new Point(-10, 30),
            new Point(-30, 10),
            new Point(-25, -20)
        };
        asteroids.add(new Asteroid(shape, new Point(x, y), angle, speed));
    }

    public void paint(Graphics brush) {
        brush.setColor(Color.black);
        brush.fillRect(0, 0, width, height);

        counter++;

        ship.wrapAround(width, height);
        ship.draw(brush);

        Asteroid.updateAsteroids(asteroids, width, height);
        for (int i = 0; i < asteroids.size(); i++) {
            asteroids.get(i).draw(brush);
        }

        SpaceOrbs.updateOrbs(orbs);
        for (int i = 0; i < orbs.size(); i++) {
            orbs.get(i).draw(brush);
        }

        for (int i = 0; i < lives; i++) {
            LifeIcon icon = new LifeIcon(new Point(20 + i * 30, 20));
            icon.draw(brush);
        }
    }

    class LifeIcon extends Polygon {

        private static final int SCALE = 8;

        public LifeIcon(Point position) {
            super(
                new Point[]{
                    new Point(0, -SCALE),
                    new Point(SCALE * 2, 0),
                    new Point(0, SCALE)
                },
                position,
                0
            );
        }

        public void draw(Graphics brush) {
            brush.setColor(new Color(100, 200, 255));
            Point[] pts = getPoints();
            for (int i = 0; i < pts.length; i++) {
                int next = (i + 1) % pts.length;
                brush.drawLine(
                    (int) pts[i].x,
                    (int) pts[i].y,
                    (int) pts[next].x,
                    (int) pts[next].y
                );
            }
        }
    }

    public static void main(String[] args) {
        YourGameName game = new YourGameName();
        game.repaint();
    }
}
