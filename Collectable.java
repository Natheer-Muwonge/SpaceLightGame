package game;

interface Collectable {
    int getPointValue();
    boolean isCollected();
    void collect();
}
