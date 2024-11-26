import java.awt.*;
import javax.swing.*;

public class TopPanel extends JPanel {
    public TopPanel() {
        JButton btn = new JButton();
        btn.setText("Restart!");
        btn.setPreferredSize(new Dimension(100, 50));
        btn.addActionListener(e -> GameFrame.Instance.centerPanel.CreateBoard());

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> GameFrame.Instance.centerPanel.SaveBoardToFile());
        saveButton.setPreferredSize(new Dimension(100, 50));

        JButton loadButton = new JButton("Open");
        loadButton.addActionListener(e -> GameFrame.Instance.centerPanel.LoadBoardFromFile());
        loadButton.setPreferredSize(new Dimension(100, 50));

        setLayout(new FlowLayout());
        add(loadButton);
        add(btn);
        add(saveButton);
}
}