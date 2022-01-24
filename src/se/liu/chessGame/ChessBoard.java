package se.liu.chessGame;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import se.liu.chessGame.pieces.Bishop;
import se.liu.chessGame.pieces.ChessPiece;
import se.liu.chessGame.pieces.King;
import se.liu.chessGame.pieces.Knight;
import se.liu.chessGame.pieces.Pawn;
import se.liu.chessGame.pieces.Queen;
import se.liu.chessGame.pieces.Rook;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;


/**
 * This class handles the ChessBoards functionality.
 * The board is represented as a two-dimensional ArrayList of squares.
 * The square has a pointer to a specific chesspiece if there is a piece on that square in the game.
 * The board takes the MouseInputs and handles them through the tick method.
 */

public class ChessBoard implements MouseInputListener
{
    private ChessPiece[][] board = null;
    private boolean isFirstPress;
    private ChessPiece inputPiece;
    @Expose
    private boolean playerWhiteTurn;
    private GameStatus gameStatus;

    private List<ChessPiece> blackSide = null;
    private List<ChessPiece> whiteSide = null;

    private BoardListener boardListener = null;
    private BoardListener viewerListener = null;

    public ChessBoard() throws IOException, MalformedURLException {
        initFromFile(true);
        this.isFirstPress = true;
        this.inputPiece = null;
        this.playerWhiteTurn = true;
        this.gameStatus = GameStatus.RUNNING;
    }

    public void resetBoard(boolean newGame) throws IOException, MalformedURLException {
        initFromFile(newGame);
        boardListener.boardChanged();
    }

    public boolean isPlayerWhiteTurn() {
        return playerWhiteTurn;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(final GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public void setBoardListener(final BoardListener boardListener) {
        this.boardListener = boardListener;
    }

    public void setViewerListener(final BoardListener viewListener) {
        this.viewerListener = viewListener;
    }

    public int getLength(){
        return board.length;
    }

    public ChessPiece getPieceAt(int y, int x){
        return board[y][x];
    }

    public void setPieceAt(int y, int x, ChessPiece piece) {
        board[y][x] = piece;
    }

    public int getBackRow(boolean isWhite){
        if(!isWhite){
            final int blackBackRow = 0;
            return blackBackRow;
        }
        else{
            final int whiteBackRow = board.length -1;
            return whiteBackRow;
        }
    }

    public List<ChessPiece> getCurrentSide(boolean isWhite){
        /**
         * Returns the list of pieces of the requested side.
         **/
        if(isWhite) {
            return whiteSide;
        }
        else {
            return blackSide;
        }
    }

    public King getKingPiece(boolean isWhite) throws NoSuchElementException {
        /**
         * Returns the king of the requested side.
         **/
        for (final ChessPiece piece : getCurrentSide(isWhite)) {
            if (piece.getPieceEnum() == BoardPiece.KING) {
                return (King) piece;
            }
        }
        /**
         * If somehow one or both of the kings are gone, we log the problem and  end the game as a DRAW.
         **/
        ExceptionLogger.getExceptionLogger().logException(Level.SEVERE, "No king detected",
                 new NoSuchElementException("There seems to be that at least one the side does not have a King."));
        gameStatus = GameStatus.DRAW;
        viewerListener.boardChanged();
        isFirstPress = true;
        return null;
    }

    public boolean isOccupied(int y, int x){
        return board[y][x] !=null;
    }

    public void removeFromList(ChessPiece piece){
        getCurrentSide(piece.getIsWhite()).remove(piece);
    }

    private void addToList(ChessPiece piece){
        getCurrentSide(piece.getIsWhite()).add(piece);
    }

    public void tick(int clickedY, int clickedX){
        /**
         * Every time a valid mouseclick is pressed, the tick method handles the press.
         * The method moves a piece if the first and the second press is valid and
         * if the move is valid.
         **/
        if(isFirstPress) {
            inputPiece = board[clickedY][clickedX];
            if(isOccupied(clickedY, clickedX) && inputPiece.getIsWhite() == playerWhiteTurn) {
                isFirstPress = !isFirstPress;
                inputPiece.setMarkedPiece(true);
            }
        }
        else{
            inputPiece.setMarkedPiece(false);
            if(!inputPiece.equals(getPieceAt(clickedY, clickedX)) &&
               inputPiece.isValidMove(clickedY, clickedX, true)){
                inputPiece.move(clickedY, clickedX);
                if (isCheckmate(!playerWhiteTurn)) {
                    gameStatus = GameStatus.WON;
                }
                else if(isDraw(!playerWhiteTurn)) {
                    gameStatus = GameStatus.DRAW;
                }
                boardListener.boardChanged();
                playerWhiteTurn = !playerWhiteTurn;
                viewerListener.boardChanged();
            }
            isFirstPress = !isFirstPress;
        }
        boardListener.boardChanged();
    }

    private boolean isCheckmate(boolean isWhite) {
        /**
         * Checks if the one king is in checkMate position.
         **/
        boolean checkmate = false;
        boolean isCheck = getKingPiece(isWhite).isCheck(isWhite);
        if (isCheck) {
            checkmate = true;
            for (ChessPiece piece : getCurrentSide(isWhite)) {
                if (hasValidMove(piece)) {
                    checkmate = false;
                    }
                }
            }
        return checkmate;
    }

    private boolean isDraw(boolean isWhite) {
        /**
         * Checks if the game is in a draw position.
         **/
        boolean drawGame = false;
        if (isDeadPosition()){
            drawGame = true;
            return drawGame;
        }
        boolean isCheck = getKingPiece(isWhite).isCheck(isWhite);
        if (!isCheck) {
            drawGame = true;
            for (ChessPiece piece : getCurrentSide(isWhite)) {
                if (hasValidMove(piece)) {
                    drawGame = false;
                }
            }
        }
        return drawGame;
    }

    private boolean isDeadPosition() {
        /**
         * Checks if the game is in a dead position where neither players can lose nor win the game.
         **/
        List<ChessPiece> allPieces = new ArrayList<>();
        allPieces.addAll(whiteSide);
        allPieces.addAll(blackSide);
        int bishopCount = 0;
        int knightCount = 0;
        List<Bishop> bishops = new ArrayList<>();
        for (ChessPiece piece : allPieces) {
            BoardPiece pieceEnum = piece.getPieceEnum();
            if(pieceEnum == BoardPiece.QUEEN ||
               pieceEnum == BoardPiece.ROOK ||
               pieceEnum == BoardPiece.PAWN){
                return false;
            }
            if(pieceEnum == BoardPiece.BISHOP){
                bishopCount += 1;
                bishops.add((Bishop) piece);
            }
            if(pieceEnum == BoardPiece.KNIGHT){
                knightCount += 1;
            }
        }
        int knightAndBishops = knightCount + bishopCount;
        final int maxBishops = 2;
        if((knightAndBishops <= 1) || (bishopCount == maxBishops && knightCount == 0 &&
                                       bishopsDifferentSide(bishops.get(0), bishops.get(1)))) {
            return true;

        }
        return false;
    }

    private boolean bishopsDifferentSide(Bishop bishop1, Bishop bishop2){
        /**
         * Checks if the remaining 2 bishops are standing on the same coloured square on the board.
         * If so, returns true, else false.
        **/
        int bishop1Value = bishop1.getPieceY() + bishop1.getPieceX();
        int bishop2Value = bishop2.getPieceY() + bishop2.getPieceX();
        if(bishop1Value % 2 == bishop2Value % 2){
            return true;
        }
        return false;
    }

    private boolean hasValidMove(ChessPiece currentPiece){
        /**
         * Checks if a piece has a valid move.
         **/
        boolean validMove = false;
        for (int y2 = 0; y2 < board.length; y2++) {
            for (int x2 = 0; x2 < board.length; x2++) {
                if (!currentPiece.equals(getPieceAt(y2,x2)) && currentPiece.isValidMove(y2, x2, true)) {
                    validMove = true;
                    break;
                }
            }
            if(validMove){
                break;
            }
        }
        return validMove;
    }

    public void handleInput(Point clicked){
        /**
         * Handles the input from the mouse click and if it is on the board, calculates which square has been pressed.
         * Calls upon the tick function which handles the press on the selected square.
         **/
        final int squareSize = 80;
        if(clicked.y > squareSize &&
           clicked.x > squareSize &&
           clicked.y < (squareSize + squareSize * board.length) &&
           clicked.x < (squareSize + squareSize * board.length))
        {
            int clickedY = (clicked.y)/squareSize-1;
            int clickedX = (clicked.x)/squareSize-1;
            tick(clickedY, clickedX);
        }
    }

    private void initPieceLists(){
        /**
         * Initiates the piecelists with all the pieces of the same color
         **/
        this.blackSide = new ArrayList<>();
        this.whiteSide = new ArrayList<>();
        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board.length; x++) {
                if(isOccupied(y,x)) {
                    if(board[y][x].getIsWhite()) {
                        whiteSide.add(board[y][x]);
                    }
                    else {
                        blackSide.add(board[y][x]);
                    }
                }
            }
        }
    }

    private ChessPiece createPiece(boolean isWhite, BoardPiece piece, int y, int x) {
            switch(piece){
                case QUEEN:
                    return new Queen(isWhite, this, y, x);
                case KING:
                    return new King(isWhite, this, y, x);
                case BISHOP:
                    return new Bishop(isWhite, this, y, x);
                case KNIGHT:
                    return new Knight(isWhite, this, y, x);
                case ROOK:
                    return new Rook(isWhite, this, y, x);
                case PAWN:
                    return new Pawn(isWhite, this, y, x);
            }
            return null;
    }

    public void createPieceAt(boolean isWhite, BoardPiece pieceEnum, int y, int x){
        /**
         * Creates a new chessPiece according to the boolean isWhite and which BoardPiece is given.
         * The newly created chessPiece is placed on the y and x given coordinates on the board.
         **/
        if(board[y][x] != null) {
            removeFromList(board[y][x]);
        }
        ChessPiece piece = createPiece(isWhite, pieceEnum, y, x);
        setPieceAt(y,x, piece);
        addToList(piece);
    }

    public void initFromFile(boolean newGame) throws IOException, MalformedURLException {
        /**
         * Reads in a board from a Json file and creates a new board
         * Resets isFirstPress to initial stage.
         **/
        URL urlFile;
        if(newGame) {
            urlFile = ClassLoader.getSystemResource("gameData/chessStartFile.json");
        }
        else {
            urlFile = new File(System.getProperty("user.dir") + File.separator + "saveFile.json").toURI().toURL();
        }
        if(urlFile == null){
            throw new IOException("Resource not found");
        }
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(urlFile.openStream()))) {
            JsonElement filePieces = JsonParser.parseReader(reader);
            JsonArray chessPieces = filePieces.getAsJsonArray();
            final int boardLength = 8;
            board = new ChessPiece[boardLength][boardLength];
            blackSide = new ArrayList<>();
            whiteSide = new ArrayList<>();
            boolean isInfo = true;
            gameStatus = GameStatus.RUNNING;
            for(JsonElement piece: chessPieces){
                JsonObject object = piece.getAsJsonObject();
                if(isInfo) {
                    boolean whiteTurn = Boolean.valueOf(object.get("playerWhiteTurn").toString());
                    playerWhiteTurn = whiteTurn;
                    isInfo = false;
                }
                else {
                    String enumString = object.get("pieceEnum").toString();
                    enumString = enumString.replace("\"", "");
                    BoardPiece pieceEnum = BoardPiece.valueOf(enumString);
                    boolean isWhite = Boolean.valueOf(object.get("isWhite").toString());
                    int y = Integer.valueOf(object.get("pieceY").toString());
                    int x = Integer.valueOf(object.get("pieceX").toString());
                    createPieceAt(isWhite, pieceEnum, y, x);
                }
            }
            initPieceLists();
        }
    }

    public void saveGameToFile() throws IOException {
        /**
         * Saves the current game-state on a json file to be loaded on a later occasion.
         **/
        String stringFile =  System.getProperty("user.dir") + File.separator + "saveFile.json";
        Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
        FileWriter jsonWriter = new FileWriter(stringFile);
        List<Object> importantInfo = new ArrayList<>();
        importantInfo.add(this);
        importantInfo.addAll(blackSide);
        importantInfo.addAll(whiteSide);
        gson.toJson(importantInfo, jsonWriter);
        jsonWriter.close();
    }

    public boolean saveFileExists(){
        /**
         * Checks if a saveFile already exists.
         **/
        String stringFile =  System.getProperty("user.dir") + File.separator + "saveFile.json";
        File file = new File(stringFile);
        if(file.exists()){
            return true;
        }
        else{
            return false;
        }
    }
}