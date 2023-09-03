import javax.swing.JFrame;

public class Frame extends JFrame {
    Panel panel;

    Frame() {
        panel = new Panel();
        this.add(panel);
        
        this.setResizable(false);
        this.setVisible(true);
        this.setTitle("Minesweeper");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        new Frame();
    }
}
