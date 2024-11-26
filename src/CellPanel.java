import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class CellPanel extends JPanel {
    private final JLabel imageLabel;
    private final Color originColor;
    private final Color blueColor = new Color(110, 93, 217);
    private final Color greenColor = new Color(51, 255, 28);
    private final Color redColor = new Color(255,28,28);
    public int x, y;
    public ChessPiece currentChessPiece;
    public boolean isValidMove;
    public CellPanel(boolean isWhite, int x, int y)  {
        isValidMove = false;
        this.x = x;
        this.y = y;
        this.setBackground(isWhite ? Color.WHITE : Color.BLACK);
        originColor = isWhite ? Color.WHITE : Color.BLACK;
        imageLabel = new JLabel();
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(imageLabel);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                GameFrame.Instance.centerPanel.OnClickCellPanel(x, y);
            }
        });
    }
    public void AddImage(ChessPiece chessPiece) {
        currentChessPiece = chessPiece;
        BufferedImage pieceImage = GetBufferImageFromFile(chessPiece);
        Image image = pieceImage.getScaledInstance(60,60,Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(image));
        imageLabel.setVisible(true);
    }
    public BufferedImage GetBufferImageFromFile(ChessPiece chessPiece) {
        Path path = FileSystems.getDefault().getPath("").toAbsolutePath();
        String fileStr = path + "/image/";
        if (chessPiece.color == PieceColor.WHITE) {
            fileStr += "w_";
        }
        else {
            fileStr += "b_";
        }
        fileStr += chessPiece.type.toString().toLowerCase() + ".png";
        File file = new File(fileStr);
        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void Select() {
        setBackground(blueColor);
    }
    public void DeSelect() {
        setBackground(originColor);
        isValidMove = false;

    }
    public void SetColor(boolean isMove) {
        isValidMove = true;
        if (isMove) {
            setBackground(greenColor);
        }
        else setBackground(redColor);
    }
    public void RemovePiece() {
        currentChessPiece = null;
        imageLabel.setVisible(false);
    }
}
