import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Panel extends JPanel implements Runnable {
    Thread gameThread;

    Image image;
    Graphics graphics;
    Game game = new Game();

    Panel() {
        this.setVisible(true);
        this.setFocusable(true);
        this.setPreferredSize(new Dimension(Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT));
        this.addMouseListener(new MouseControl());

        gameThread = new Thread(this);
        gameThread.start();
    }

    private void draw(Graphics g) {
        game.draw(g);
    }


    public void paint(Graphics g) {
        image = createImage(getWidth(), getHeight());
        graphics = image.getGraphics();
        draw(graphics);
        g.drawImage(image, 0, 0, this);
    }


    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        float time = 0.0f;

        while (true) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if (delta < 1) continue;
            delta--;
            time += 1/60f;
            if (time > 1) {
                time = 0;
                game.minesweeper.makeMove();
            }

            game.update();
            repaint();
        }
    }


    class MouseControl extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            boolean isFlag = e.getButton() == MouseEvent.BUTTON1 ? false : true;
            game.clickSquare(e.getX(), e.getY(), isFlag);
        }
    }
}