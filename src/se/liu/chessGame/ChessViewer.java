package se.liu.chessGame;

import se.liu.chessGame.pieces.ChessPiece;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

/**
 * Creates all the necessary objects of classes in the game and displays them on a frame.
 * The ChessViewer class contains a JFrame to display the GUI to the user.
 * It also contains a ChessBoard to handle the chessBoards functionality.
 * The ChessComponent displays the board on the JFrame.
 * The JMenuBar lets the user ask the board to give certain information or change the board in different ways.
 */

public class ChessViewer implements BoardListener
{
    private JFrame chessFrame;
    private ChessBoard board = null;
    private ChessComponent component;
    private JMenuBar menuBar;

    public ChessViewer() {
        this.chessFrame = createFrame("Chess");
        try {
            this.board = new ChessBoard();
        } catch (IOException ioException) { // If we can´t load the board we cant really play chess, therefore we exit the game
            ExceptionLogger.getExceptionLogger().logException(Level.SEVERE, "Game could not be initiated from the chessStartFile", ioException);
            System.exit(1);
        }
        this.component = new ChessComponent(board);
        MouseInput inputs = new MouseInput();
        inputs.setListener(board);
        component.addMouseListener(inputs);
        board.setBoardListener(component);
        board.setViewerListener(this);
        initMenuBar();
        chessFrame.setJMenuBar(menuBar);
    }

    public static void main(String[] args) {
        ChessViewer chess = new ChessViewer();
        chess.show();
    }

    public void show(){
        chessFrame.setLayout(new BorderLayout());
        chessFrame.add(component, BorderLayout.CENTER);
        chessFrame.pack();
        chessFrame.setVisible(true);
    }

    private JFrame createFrame(String frameName){
        return new JFrame(frameName);
    }

    private void initMenuBar(){
        /**
         * Creates a menubar and adds different functionality to it.
         **/
        menuBar = new JMenuBar();
        final JMenuItem quit = new JMenuItem("Quit");
        menuBar.add(quit);
        quit.addActionListener(e -> {
            if(askUserInput("Do you really want to exit the game?")){
                System.exit(0);
            }
        });
        final JMenuItem newBoard = new JMenuItem("New Game");
        menuBar.add(newBoard);
        newBoard.addActionListener(e -> {
            if(askUserInput("Do you really want to start a new game?")){
                try {
                    board.resetBoard(true);
                    displayUserMessage("New game created");
                } catch (IOException ioException) { // If we can´t load the board we can´t really play chess, therefore we exit the game
                    ExceptionLogger.getExceptionLogger().logException(Level.SEVERE, "Game could not be initiated from the chessStartFile", ioException);
                    System.exit(1);
                }
            }
        });
        final JMenuItem drawButton = new JMenuItem("Draw game");
        menuBar.add(drawButton);
        drawButton.addActionListener(e -> {
            if(askUserInput("Does player white wants to draw?") &&
               askUserInput("Does player black wants to draw?")) {
                board.setGameStatus(GameStatus.DRAW);
                handleGameOver();
            }
        }
        );
        final JMenuItem howToPlayButton = new JMenuItem("How to play");
        menuBar.add(howToPlayButton);
        howToPlayButton.addActionListener(e -> displayUserMessage("Click on the piece you want to move to select it. \n" +
                                                              "After selecting a piece, press the square you want to move it to. \n" +
                                                              "If the move is valid, the piece moves and your turn is over.")
                                     );

        final JMenuItem saveGameButton = new JMenuItem("Save game");
        menuBar.add(saveGameButton);
        saveGameButton.addActionListener(e -> saveGame());

        final JMenuItem loadGameButton = new JMenuItem("Load game");
        menuBar.add(loadGameButton);
        loadGameButton.addActionListener(e -> {
                                             if (board.saveFileExists()) {
                                                 if (askUserInput("Are you sure you want to reset current game \n" + "and load saved game?")) {
                                                     try {
                                                         board.resetBoard(false);
                                                         displayUserMessage("Saved game was loaded");
                                                     } catch (IOException ioException) { // If we cant load a game we think its a FINE exception and nothing else needs to be done besides to log it.
                                                         displayUserMessage("No saveFile could be found");
                                                         ExceptionLogger.getExceptionLogger()
                                                                 .logException(Level.FINE, "Could not found a game saved on a file called saveFile.json", ioException);

                                                     }
                                                 }
                                             }
                                             else{
                                                 displayUserMessage("No saveFile exists.");
                                             }
                                         }
        );
    }

    private void displayUserMessage(String message){
        /**
         * Displays a message dialog to the player with the given message.
         **/
        JOptionPane.showMessageDialog(null, message);
    }

    private boolean askUserInput(String question) {
        /**
         * Dispalys a question dialog to the player with the given wuestion and returns the players input.
         **/
        return JOptionPane.showConfirmDialog(null, question, "", JOptionPane.YES_NO_OPTION) ==
               JOptionPane.YES_OPTION;
    }

    private void saveGame(){
        /**
         * Saves the current state of the game if the viewer wants to do it.
         **/
        boolean saveFile = false;
        if (board.saveFileExists()) {
            if (askUserInput("Do you want to overwrite your last saved game?")) {
                saveFile = true;
            }
        } else {
            saveFile = true;
        }
        if(saveFile) {
            try {
                board.saveGameToFile();
                displayUserMessage("Game was saved");
            } catch (IOException ioException) { // If we cant save a game we think its a FINE exception and nothing else needs to be done besides to log it.
                displayUserMessage("File could not be saved, try again");
                ExceptionLogger.getExceptionLogger().logException(Level.FINE, "Could not save the game", ioException);
            }
        }
    }

    private BoardPiece getPlayerPieceInput(){
        /**
         * When a pawn moves across the board it gets uppgraded.
         * This method asks the player with a question dialog what piece it wants to uppgrade it to.
         **/
        Object[] options = {BoardPiece.QUEEN, BoardPiece.ROOK, BoardPiece.KNIGHT , BoardPiece.BISHOP};
        int returnValue = JOptionPane.showOptionDialog(null,
                                                       "Choose which piece you want",
                                                       "Upgrade pawn",
                                                       JOptionPane.DEFAULT_OPTION,
                                                       JOptionPane.PLAIN_MESSAGE,
                                                       null,
                                                       options,
                                                       options[0]);
        if(returnValue ==-1){
            returnValue = 0;
        }
        return (BoardPiece) options[returnValue];
    }

    private void handleGameOver(){
        /**
         * When a game is either ended with a draw or if a player has won this method either restarts the game or end it
         * depending on the player input.
         **/
        String viewerMessage;
        if(board.getGameStatus() == GameStatus.DRAW){
            viewerMessage = "The game has ended with a draw";
        }
        else {
            if (!board.isPlayerWhiteTurn()) {
                viewerMessage = "Player white wins the game";
            }
            else{
                viewerMessage = "Player black wins the game";
            }
        }
        int returnValue = JOptionPane.showConfirmDialog(null, viewerMessage + "\n" + "Do you want to play again?",
                                                        "Gameover", JOptionPane.YES_NO_OPTION);
        if(returnValue == 0){
            try {
                board.resetBoard(true);
            } catch (IOException ioException) { // If we can´t load the board we cant really play chess, therefore we exit the game
                ExceptionLogger.getExceptionLogger().logException(Level.SEVERE, "Game could not be initiated from the chessStartFile", ioException);
                System.exit(1);
            }
        }
        else if(returnValue == 1){
            System.exit(0);
        }
    }

    @Override public void boardChanged() {
        if (board.getGameStatus() != GameStatus.RUNNING) {
            handleGameOver();
        }
        ChessPiece upgradePiece = null;
        boolean isWhite = !board.isPlayerWhiteTurn();
        List<ChessPiece> markedSide = board.getCurrentSide(isWhite);
        int selectedRow = board.getBackRow(!isWhite);
        for (ChessPiece piece : markedSide) {
            if (piece.getPieceY() == selectedRow && piece.getPieceEnum() == BoardPiece.PAWN) {
                upgradePiece = piece;
            }
        }
        if (upgradePiece != null) {
            board.createPieceAt(upgradePiece.getIsWhite(), getPlayerPieceInput(), upgradePiece.getPieceY(), upgradePiece.getPieceX());
        }
    }
}
