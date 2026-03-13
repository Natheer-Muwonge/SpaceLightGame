package game;

import java.awt.*;
import java.awt.event.*;

/**
 * A painted canvas in its own window, updated every tenth of a second.
 * Extended by game classes to implement specific game logic.
 */
abstract class Game extends Canvas {
  protected boolean on = true;
  protected int width, height;
  protected Image buffer;

  /**
   * Creates a new game window with the given name and dimensions.
   * @param name the title of the window
   * @param inWidth the width of the window
   * @param inHeight the height of the window
   */
  public Game(String name, int inWidth, int inHeight) {
    width = inWidth;
    height = inHeight;

    Frame frame = new Frame(name);
    frame.add(this);
    frame.setSize(width, height);
    frame.setVisible(true);
    frame.setResizable(false);
    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });

    buffer = createImage(width, height);
  }

  /**
   * Draws the current game state to the screen.
   * Called every tenth of a second while the game is running.
   * @param brush the graphics context used for drawing
   */
  abstract public void paint(Graphics brush);

  /**
   * Paints to a buffer then draws it to the screen, then schedules the next repaint.
   * @param brush the graphics context used for drawing
   */
  public void update(Graphics brush) {
    paint(buffer.getGraphics());
    brush.drawImage(buffer, 0, 0, this);
    if (on) {
      sleep(10);
      repaint();
    }
  }

  /**
   * Pauses execution for the given number of milliseconds.
   * @param time the number of milliseconds to sleep
   */
  private void sleep(int time) {
    try {
      Thread.sleep(time);
    } catch (Exception exc) {}
  }
}
