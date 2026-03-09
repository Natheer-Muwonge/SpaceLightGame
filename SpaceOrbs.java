package game;

import java.awt.*;
import java.util.ArrayList;

class SpaceOrbs extends Polygon implements Collectable {

    private static final int POINT_VALUE = 5;
    private static final int RADIUS = 10;

    private boolean collected = false;
    private int framesLeft = 125;

    public SpaceOrbs(Point position) {
        super(
            new Point[]{
                new Point(-RADIUS, 0),
                new Point(0, -RADIUS),
                new Point(RADIUS, 0),
                new Point(0, RADIUS)
            },
            position,
            0
        );
    }

    @Override
    public int getPointValue() {
        return POINT_VALUE;
    }

    @Override
    public boolean isCollected() {
        return collected;
    }

    @Override
    public void collect() {
        collected = true;
    }

    public void draw(Graphics brush) {
        if (collected) {
            return;
        }
        brush.setColor(Color.yellow);
        brush.fillOval(
            (int) position.x - RADIUS,
            (int) position.y - RADIUS,
            RADIUS * 2,
            RADIUS * 2
        );
        brush.setColor(new Color(200, 160, 0));
        brush.drawOval(
            (int) position.x - RADIUS,
            (int) position.y - RADIUS,
            RADIUS * 2,
            RADIUS * 2
        );
    }

    public static void updateOrbs(ArrayList<SpaceOrbs> orbs) {
        for (int i = orbs.size() - 1; i >= 0; i--) {
            SpaceOrbs orb = orbs.get(i);
            orb.framesLeft--;
            if (orb.isCollected() || orb.framesLeft <= 0) {
                orbs.remove(i);
            }
        }
    }
}
