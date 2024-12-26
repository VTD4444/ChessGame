import java.awt.*;
import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import javax.sound.sampled.*;
import javax.swing.*;

public class CenterPanel extends JPanel {
    public CellPanel[][] boardCells = new CellPanel[8][8];
    private BoardState boardState;
    private CellPanel selectedCell;
    private boolean isWhiteTurn, canCastleRightWhite, canCastleLeftWhite, canCastleRightBlack, canCastleLeftBlack;
    public CenterPanel() {
        CreateBoard();
    }
    public void OnClickCellPanel(int x, int y) {
        CellPanel clickCellPanel = boardCells[x][y];
        clickCellPanel.Select();
        if (boardState == BoardState.NONE_SELECT) {
            DeSelectAllCells();
            if (clickCellPanel.currentChessPiece != null) {
                if (CheckRightClickPiece(clickCellPanel.currentChessPiece)) {
                    switch (clickCellPanel.currentChessPiece.type) {
                        case TOT -> {
                            TOTCheck(x, y);
                            break;
                        }
                        case XE -> {
                            XECheck(x, y);
                            break;
                        }
                        case MA -> {
                            MACheck(x, y);
                            break;
                        }
                        case TUONG -> {
                            TUONGCheck(x, y);
                            break;
                        }
                        case HAU -> {
                            HAUCheck(x, y);
                            break;
                        }
                        case VUA -> {
                            VUACheck(x, y);
                            break;
                        }
                    }
                    boardState = BoardState.PIECE_SELECT;
                    selectedCell = clickCellPanel;
                }
            }
        }
        else if (boardState == BoardState.PIECE_SELECT) {
            if (clickCellPanel.isValidMove) {
                if (clickCellPanel.currentChessPiece != null) {
                    if (clickCellPanel.currentChessPiece.color != selectedCell.currentChessPiece.color) {
                        if (clickCellPanel.currentChessPiece.type == PieceType.VUA) {
                            GameOver();
                            return;
                        }
                    }
                }
                if (selectedCell.currentChessPiece.type == PieceType.TOT && ((clickCellPanel.x == 0 && selectedCell.currentChessPiece.color == PieceColor.WHITE) || (clickCellPanel.x == 7 && selectedCell.currentChessPiece.color == PieceColor.BLACK))) {
                    PlaySound("promotion");
                    clickCellPanel.AddImage(new ChessPiece(PieceType.HAU, selectedCell.currentChessPiece.color));
                }
                else if (selectedCell.currentChessPiece.type == PieceType.VUA && clickCellPanel.y == selectedCell.y + 2) {
                    PlaySound("castle");
                    clickCellPanel.AddImage(selectedCell.currentChessPiece);
                    boardCells[selectedCell.x][selectedCell.y + 1].AddImage(new ChessPiece(PieceType.XE, selectedCell.currentChessPiece.color));
                    boardCells[selectedCell.x][selectedCell.y + 3].RemovePiece();
                    boardCells[selectedCell.x][selectedCell.y + 3].currentChessPiece = null;
                }
                else if (selectedCell.currentChessPiece.type == PieceType.VUA && clickCellPanel.y == selectedCell.y - 2) {
                    PlaySound("castle");
                    clickCellPanel.AddImage(selectedCell.currentChessPiece);
                    boardCells[selectedCell.x][selectedCell.y - 1].AddImage(new ChessPiece(PieceType.XE, selectedCell.currentChessPiece.color));
                    boardCells[selectedCell.x][selectedCell.y - 4].RemovePiece();
                    boardCells[selectedCell.x][selectedCell.y - 4].currentChessPiece = null;
                }
                else {
                    PlaySound("move");
                    clickCellPanel.AddImage(selectedCell.currentChessPiece);
                    boardCells[clickCellPanel.x][clickCellPanel.y].currentChessPiece = selectedCell.currentChessPiece;
                }
                CheckCanCastle();
                selectedCell.RemovePiece();
                selectedCell.currentChessPiece = null;
                boardCells[selectedCell.x][selectedCell.y].currentChessPiece = null;
                System.out.println(selectedCell.x+" "+selectedCell.y);
                System.out.println(boardCells[selectedCell.x][selectedCell.y].currentChessPiece);
                isWhiteTurn = !isWhiteTurn;
            }
            boardState = BoardState.NONE_SELECT;
            DeSelectAllCells();
        }
    }
    public void DeSelectAllCells() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                boardCells[i][j].DeSelect();
            }
        }
    }
    private boolean CheckValidCoordinate(int n) {
        return n >= 0 && n < 8;
    }
    private boolean CheckValidCoordinate(int x, int y) {
        return CheckValidCoordinate(x) && CheckValidCoordinate(y);
    }
    private void TOTCheck(int x, int y) {
        ChessPiece thisPiece = boardCells[x][y].currentChessPiece;
        if (thisPiece.color == PieceColor.WHITE) {
            int maxMove = (x == 6) ? 2 : 1;
            for (int i = x - 1; i >= x - maxMove; i--) {
                if (!CheckValidCoordinate(i, y) || boardCells[i][y].currentChessPiece != null) break;
                boardCells[i][y].SetColor(true);
            }
            if (CheckValidCoordinate(x - 1, y-1)) {
                CellPanel cellPanel = boardCells[x - 1][y - 1];
                if (cellPanel.currentChessPiece != null) {
                    if (cellPanel.currentChessPiece.color != thisPiece.color) {
                        cellPanel.SetColor(false);
                    }
                }
            }
            if (CheckValidCoordinate(x - 1, y + 1)) {
                CellPanel cellPanel = boardCells[x - 1][y + 1];
                if (cellPanel.currentChessPiece != null) {
                    if (cellPanel.currentChessPiece.color != thisPiece.color) {
                        cellPanel.SetColor(false);
                    }
                }
            }
        }
        else {
            int maxMove = (x == 1) ? 2 : 1;
            for (int i = x + 1; i <= x + maxMove; i++) {
                if (!CheckValidCoordinate(i, y) || boardCells[i][y].currentChessPiece != null) break;
                boardCells[i][y].SetColor(true);
            }
            if (CheckValidCoordinate(x + 1, y - 1)) {
                CellPanel cellPanel = boardCells[x + 1][y - 1];
                if (cellPanel.currentChessPiece != null) {
                    if (cellPanel.currentChessPiece.color != thisPiece.color) {
                        cellPanel.SetColor(false);
                    }
                }
            }
            if (CheckValidCoordinate(x + 1, y + 1)) {
                CellPanel cellPanel = boardCells[x + 1][y + 1];
                if (cellPanel.currentChessPiece != null) {
                    if (cellPanel.currentChessPiece.color != thisPiece.color) {
                        cellPanel.SetColor(false);
                    }
                }
            }
        }
    }
    private void XECheck(int x, int y) {
        ChessPiece thisPiece = boardCells[x][y].currentChessPiece;
        ChessPiece chessPiece;
        for (int i = x + 1; i < 8; i++) {
            chessPiece = boardCells[i][y].currentChessPiece;
            if (chessPiece == null) boardCells[i][y].SetColor(true);
            else {
                if (chessPiece.color != thisPiece.color) boardCells[i][y].SetColor(false);
                break;
            }
        }
        for (int i = x - 1; i >= 0; i--) {
            chessPiece = boardCells[i][y].currentChessPiece;
            if (chessPiece == null) boardCells[i][y].SetColor(true);
            else {
                if (chessPiece.color != thisPiece.color) boardCells[i][y].SetColor(false);
                break;
            }
        }
        for (int i = y + 1; i < 8; i++) {
            chessPiece = boardCells[x][i].currentChessPiece;
            if (chessPiece == null) boardCells[x][i].SetColor(true);
            else {
                if (chessPiece.color != thisPiece.color) boardCells[x][i].SetColor(false);
                break;
            }
        }
        for (int i = y - 1; i >= 0; i--) {
            chessPiece = boardCells[x][i].currentChessPiece;
            if (chessPiece == null) boardCells[x][i].SetColor(true);
            else {
                if (chessPiece.color != thisPiece.color) boardCells[x][i].SetColor(false);
                break;
            }
        }
    }
    private void MACheck(int x, int y) {
        ChessPiece thisPiece = boardCells[x][y].currentChessPiece;
        int[] dx = {-2, -2, -1, 1, 2, 2, -1, 1};
        int[] dy = {-1, 1, -2, -2, -1, 1, 2, 2};
        for (int i = 0; i < 8; i++) {
            if (!CheckValidCoordinate(x + dx[i], y + dy[i])) continue;
            CellPanel cellPanel = boardCells[x + dx[i]][y + dy[i]];
            if (cellPanel.currentChessPiece != null) {
                if (cellPanel.currentChessPiece.color != thisPiece.color) {
                    cellPanel.SetColor(false);
                }
            }
            else cellPanel.SetColor(true);
        }
    }
    private void TUONGCheck(int x, int y) {
        ChessPiece thisPiece = boardCells[x][y].currentChessPiece;
        ChessPiece chessPiece;
        int i = 1;
        while (true) {
            if (!CheckValidCoordinate(x + i, y + i)) break;
            chessPiece = boardCells[x + i][y + i].currentChessPiece;
            if (chessPiece == null) boardCells[x + i][y + i].SetColor(true);
            else {
                if (chessPiece.color != thisPiece.color) boardCells[x + i][y + i].SetColor(false);
                break;
            }
            i++;
        }
        i = 1;
        while (true) {
            if (!CheckValidCoordinate(x - i, y + i)) break;
            chessPiece = boardCells[x - i][y + i].currentChessPiece;
            if (chessPiece == null) boardCells[x - i][y + i].SetColor(true);
            else {
                if (chessPiece.color != thisPiece.color) boardCells[x - i][y + i].SetColor(false);
                break;
            }
            i++;
        }
        i = 1;
        while (true) {
            if (!CheckValidCoordinate(x + i, y - i)) break;
            chessPiece = boardCells[x + i][y - i].currentChessPiece;
            if (chessPiece == null) boardCells[x + i][y - i].SetColor(true);
            else {
                if (chessPiece.color != thisPiece.color) boardCells[x + i][y - i].SetColor(false);
                break;
            }
            i++;
        }
        i = 1;
        while (true) {
            if (!CheckValidCoordinate(x - i, y - i)) break;
            chessPiece = boardCells[x - i][y - i].currentChessPiece;
            if (chessPiece == null) boardCells[x - i][y - i].SetColor(true);
            else {
                if (chessPiece.color != thisPiece.color) boardCells[x - i][y - i].SetColor(false);
                break;
            }
            i++;
        }
    }
    private void HAUCheck(int x, int y) {
        ChessPiece thisPiece = boardCells[x][y].currentChessPiece;
        ChessPiece chessPiece;
        for (int i = x + 1; i < 8; i++) {
            chessPiece = boardCells[i][y].currentChessPiece;
            if (chessPiece == null) boardCells[i][y].SetColor(true);
            else {
                if (chessPiece.color != thisPiece.color) boardCells[i][y].SetColor(false);
                break;
            }
        }
        for (int i = x - 1; i >= 0; i--) {
            chessPiece = boardCells[i][y].currentChessPiece;
            if (chessPiece == null) boardCells[i][y].SetColor(true);
            else {
                if (chessPiece.color != thisPiece.color) boardCells[i][y].SetColor(false);
                break;
            }
        }
        for (int i = y + 1; i < 8; i++) {
            chessPiece = boardCells[x][i].currentChessPiece;
            if (chessPiece == null) boardCells[x][i].SetColor(true);
            else {
                if (chessPiece.color != thisPiece.color) boardCells[x][i].SetColor(false);
                break;
            }
        }
        for (int i = y - 1; i >= 0; i--) {
            chessPiece = boardCells[x][i].currentChessPiece;
            if (chessPiece == null) boardCells[x][i].SetColor(true);
            else {
                if (chessPiece.color != thisPiece.color) boardCells[x][i].SetColor(false);
                break;
            }
        }
        int i = 1;
        while (true) {
            if (!CheckValidCoordinate(x + i, y + i)) break;
            chessPiece = boardCells[x + i][y + i].currentChessPiece;
            if (chessPiece == null) boardCells[x + i][y + i].SetColor(true);
            else {
                if (chessPiece.color != thisPiece.color) boardCells[x + i][y + i].SetColor(false);
                break;
            }
            i++;
        }
        i = 1;
        while (true) {
            if (!CheckValidCoordinate(x - i, y + i)) break;
            chessPiece = boardCells[x - i][y + i].currentChessPiece;
            if (chessPiece == null) boardCells[x - i][y + i].SetColor(true);
            else {
                if (chessPiece.color != thisPiece.color) boardCells[x - i][y + i].SetColor(false);
                break;
            }
            i++;
        }
        i = 1;
        while (true) {
            if (!CheckValidCoordinate(x + i, y - i)) break;
            chessPiece = boardCells[x + i][y - i].currentChessPiece;
            if (chessPiece == null) boardCells[x + i][y - i].SetColor(true);
            else {
                if (chessPiece.color != thisPiece.color) boardCells[x + i][y - i].SetColor(false);
                break;
            }
            i++;
        }
        i = 1;
        while (true) {
            if (!CheckValidCoordinate(x - i, y - i)) break;
            chessPiece = boardCells[x - i][y - i].currentChessPiece;
            if (chessPiece == null) boardCells[x - i][y - i].SetColor(true);
            else {
                if (chessPiece.color != thisPiece.color) boardCells[x - i][y - i].SetColor(false);
                break;
            }
            i++;
        }
    }
    private void VUACheck(int x, int y) {
        ChessPiece thisPiece = boardCells[x][y].currentChessPiece;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                if (!CheckValidCoordinate(x + i, y + j)) continue;
                CellPanel cellPanel = boardCells[x + i][y + j];
                if (cellPanel.currentChessPiece != null) {
                    if (cellPanel.currentChessPiece.color != thisPiece.color) {
                        cellPanel.SetColor(false);
                    }
                }
                else cellPanel.SetColor(true);
            }
        }
        if (thisPiece.color == PieceColor.WHITE) {
            if (canCastleRightWhite && boardCells[x][y + 1].currentChessPiece == null && boardCells[x][y + 2].currentChessPiece == null)
                boardCells[x][y + 2].SetColor(true);
            if (canCastleLeftWhite && boardCells[x][y - 1].currentChessPiece == null && boardCells[x][y - 2].currentChessPiece == null && boardCells[x][y - 3].currentChessPiece == null)
                boardCells[x][y - 2].SetColor(true);
        }
        else if (thisPiece.color == PieceColor.BLACK) {
            if (canCastleRightBlack && boardCells[x][y + 1].currentChessPiece == null && boardCells[x][y + 2].currentChessPiece == null)
                boardCells[x][y + 2].SetColor(true);
            if (canCastleLeftBlack && boardCells[x][y - 1].currentChessPiece == null && boardCells[x][y - 2].currentChessPiece == null && boardCells[x][y - 3].currentChessPiece == null)
                boardCells[x][y - 2].SetColor(true);
        }
    }
    private boolean CheckRightClickPiece(ChessPiece chessPiece) {
        if (isWhiteTurn)
            return chessPiece.color == PieceColor.WHITE;
        else return chessPiece.color == PieceColor.BLACK;
    }
    private void DestroyBoard() {
        this.removeAll();
        this.repaint();
    }
    public void CreateBoard() {
        PlaySound("gameStart");
        DestroyBoard();
        canCastleRightWhite = true;
        canCastleLeftWhite = true;
        canCastleRightBlack = true;
        canCastleLeftBlack = true;
        boardState = BoardState.NONE_SELECT;
        this.setLayout(new GridLayout(8,8));
        boolean isWhite = true;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                CellPanel cellPanel = new CellPanel(isWhite, i, j);
                if (i == 1 || i == 6) {
                    cellPanel.AddImage(new ChessPiece(PieceType.TOT, i == 1 ? PieceColor.BLACK : PieceColor.WHITE));
                }
                else if ((i == 0 && j == 0) || (i == 0 && j == 7) || (i == 7 && j == 0) || (i == 7 && j == 7)) {
                    cellPanel.AddImage(new ChessPiece(PieceType.XE, i == 0 ? PieceColor.BLACK : PieceColor.WHITE));
                }
                else if ((i == 0 && j == 1) || (i == 0 && j == 6) || (i == 7 && j == 1) || (i == 7 && j == 6)) {
                    cellPanel.AddImage(new ChessPiece(PieceType.MA, i == 0 ? PieceColor.BLACK : PieceColor.WHITE));
                }
                else if ((i == 0 && j == 2) || (i == 0 && j == 5) || (i == 7 && j == 2) || (i == 7 && j == 5)) {
                    cellPanel.AddImage(new ChessPiece(PieceType.TUONG, i == 0 ? PieceColor.BLACK : PieceColor.WHITE));
                }
                else if ((i == 0 && j == 3) || (i == 7 && j == 3)) {
                    cellPanel.AddImage(new ChessPiece(PieceType.HAU, i == 0 ? PieceColor.BLACK : PieceColor.WHITE));
                }
                else if ((i == 0 && j == 4) || (i == 7 && j == 4)) {
                    cellPanel.AddImage(new ChessPiece(PieceType.VUA, i == 0 ? PieceColor.BLACK : PieceColor.WHITE));
                }
                this.add(cellPanel);
                boardCells[i][j] = cellPanel;
                isWhite = !isWhite;
            }
            isWhite = !isWhite;
        }
        selectedCell = null;
        isWhiteTurn = true;
        this.revalidate();
        this.repaint();
    }
    private void GameOver() {
        PlaySound("gameOver");
        Object message = (isWhiteTurn ? "BLACK WIN!" : "WHITE WIN! ") + "\nDo you want play again ?";
        String title = "Game Over";
        int reply = JOptionPane.showConfirmDialog(null, message, title, JOptionPane. YES_NO_OPTION);
        if (reply == JOptionPane.YES_NO_OPTION) {
            CreateBoard();
        }
        else {
            System.exit(0);
        }
    }
    public void PlaySound(String sound) {
        Path path = FileSystems.getDefault().getPath("").toAbsolutePath();
        File file = new File("");
        if (sound == "move")
            file = new File(path + "/audio/move-self.wav");
        else if (sound == "gameOver")
            file = new File(path + "/audio/game-end.wav");
        else if (sound == "gameStart")
            file = new File(path + "/audio/game-start.wav");
        else if (sound == "castle")
            file = new File(path + "/audio/castle.wav");
        else if (sound == "promotion")
            file = new File(path + "/audio/notify.wav");

        if (file.exists()) {
            AudioInputStream audioInputStream = null;
            try {
                audioInputStream = AudioSystem.getAudioInputStream(file);
            } catch (UnsupportedAudioFileException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Clip clip = null;
            try {
                clip = AudioSystem.getClip();
            } catch (LineUnavailableException e) {
                throw new RuntimeException(e);
            }
            try {
                clip.open(audioInputStream);
            } catch (LineUnavailableException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(6F);
            clip.start();
        }
    }
    private void CheckCanCastle() {
        if (selectedCell.currentChessPiece.type == PieceType.VUA) {
            if (selectedCell.currentChessPiece.color == PieceColor.WHITE) {
                canCastleRightWhite = false;
                canCastleLeftWhite = false;
            }
            else {
                canCastleRightBlack = false;
                canCastleLeftBlack = false;
            }
        }
        else if (selectedCell.currentChessPiece.type == PieceType.XE) {
            if (selectedCell.currentChessPiece.color == PieceColor.WHITE && selectedCell.y == 0) canCastleLeftWhite = false;
            else if (selectedCell.currentChessPiece.color == PieceColor.WHITE && selectedCell.y == 7) canCastleRightWhite = false;
            else if (selectedCell.currentChessPiece.color == PieceColor.BLACK && selectedCell.y == 0) canCastleLeftBlack = false;
            else if (selectedCell.currentChessPiece.color == PieceColor.BLACK && selectedCell.y == 7) canCastleRightBlack = false;
        }
    }
    public void SaveBoardToFile() {
        JFileChooser fileChooser = new JFileChooser();
        File defaultDirectory = new File("save_game");
        fileChooser.setCurrentDirectory(defaultDirectory);
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        CellPanel cell = boardCells[i][j];
                        ChessPiece piece = cell.currentChessPiece;
                        if (piece != null) {
                            writer.write(i + "," + j + "," + piece.type + "," + piece.color);
                        } else {
                            writer.write(i + "," + j + ",null,null");
    
                        }
                        writer.newLine();
                        
                    }
                }
                JOptionPane.showMessageDialog(this, "Bàn cờ đã được lưu thành công!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu bàn cờ: " + e.getMessage());
            }
        }
    }
    public void LoadBoardFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        File defaultDirectory = new File("save_game");
        fileChooser.setCurrentDirectory(defaultDirectory);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                PlaySound("gameStart");
                DestroyBoard();
                canCastleRightWhite = true;
                canCastleLeftWhite = true;
                canCastleRightBlack = true;
                canCastleLeftBlack = true;
                boardState = BoardState.NONE_SELECT;
                this.setLayout(new GridLayout(8,8));
    
                String line;
                boolean isWhite = true; 
                int check=0;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    int x = Integer.parseInt(parts[0]);
                    int y = Integer.parseInt(parts[1]);
                    CellPanel cellPanel=new CellPanel(isWhite,x,y);
                    if (parts.length != 4) {
                        JOptionPane.showMessageDialog(this, "ERROR in line: " + line);
                        continue; // Bỏ qua dòng sai định dạng
                    }
                    if (!parts[2].equals("null")) {
    
                        PieceType type = PieceType.valueOf(parts[2]);
                        PieceColor color = PieceColor.valueOf(parts[3]);
    
                        ChessPiece piece = new ChessPiece(type, color);
                        cellPanel.AddImage(piece);
                    }
                    this.add(cellPanel);
                    boardCells[x][y]=cellPanel;
                    isWhite=!isWhite;
                    check++;
                    if(check==8) {
                        isWhite=!isWhite;
                        check=0;
                    }
                }
                this.revalidate();
                this.repaint();
                JOptionPane.showMessageDialog(this, "Chessboard loaded successfully!");
            }
            catch (IOException | IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, "ERROR opening file: " + e.getMessage());
            }
    }
}
}