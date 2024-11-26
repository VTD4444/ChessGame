import java.awt.*;
import javax.swing.*;

public class GameFrame extends JFrame {
    public static GameFrame Instance;
    public TopPanel topPanel;
    public CenterPanel centerPanel;
    public GameFrame() {
        Instance = this;
        this.setSize(640, 700);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setLayout(new BorderLayout());
        this.setTitle("Chess");

        topPanel = new TopPanel();
        centerPanel = new CenterPanel();

        this.getContentPane().add(topPanel, BorderLayout.NORTH);
        this.getContentPane().add(centerPanel, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }
    
}
