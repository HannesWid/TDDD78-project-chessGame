package se.liu.chessGame;

import se.liu.chessGame.pieces.ChessPiece;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * This class handles the chessBoards GUI and how it's represented to the viewer.
 * The ChessComponent contains a board to display on itself.
 * The components main function painComponent loops through the board and paints it visually for the viewer.
 */

public class ChessComponent extends JComponent implements BoardListener
{
    private ChessBoard board;
    private int squareSize;
    private Image[] images;
    private EnumMap<BoardPiece, Integer> pieceIndexes;
    private Map<Integer, String> rowLetters;
    private int outWidth = 1;


    public ChessComponent(final ChessBoard chessBoard) {
	this.board = chessBoard;
	this.squareSize = 80;
	this.images = createImagesList();
	this.pieceIndexes = createTextureMap();
	this.rowLetters = createRowLetterMap();
    }

    @Override
    protected void paintComponent(Graphics g) {
	/**
	 * This method creates how the chessboard visually is showed to the viewer.
	 * For every position on the board, we first of paint the colour of the square
	 * and then we place the texture of the piece if one exists on that specific square.
	 * The method also paints a visual string to show which players turn it is and rows and columns
	 * numbers.
	 **/
	super.paintComponent(g);
	final Graphics2D g2d = (Graphics2D) g;
	boolean isWhite = false;
	g2d.setColor(Color.black);
	String printString;
	int halfSquare = squareSize / 2;
	if(board.isPlayerWhiteTurn()){
	    printString = "White's turn";
	}
	else{
	    printString = "Black's turn";
	}
	g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	final int stringXCord = 10;
	g2d.drawString(printString,stringXCord, halfSquare);

	for (int y = 0; y < board.getLength() + outWidth; y++) {
	    isWhite = !isWhite;
	    for (int x = 0; x < board.getLength() + outWidth; x++) {
		if (y < outWidth || x < outWidth) {
		    g2d.setColor(Color.black);
		    if(y == 0){
		        printString = rowLetters.get(x);
		    }
		    else{
			printString = String.valueOf(y);
		    }
		    g2d.drawString(printString, x * squareSize + halfSquare, y * squareSize + halfSquare);
		}
		else {
		    ChessPiece pieceAt = board.getPieceAt(y- outWidth, x- outWidth);
		    if (pieceAt != null && pieceAt.isMarkedPiece()) {
			g2d.setColor(Color.green);
		    } else {
			if (isWhite) {
			    g2d.setColor(Color.white);
			} else {
			    g2d.setColor(Color.gray);
			}
		    }
		    int guiX = x * squareSize;
		    int guiY = y * squareSize;

		    g2d.fillRect(guiX , guiY , squareSize, squareSize);
		    isWhite = !isWhite;

		    if (pieceAt != null) {
			BoardPiece pieceName = pieceAt.getPieceEnum();
			int pieceIndex = pieceIndexes.get(pieceName);
			if (!pieceAt.getIsWhite()) {
			    final int whitePiecesLength = 6;
			    pieceIndex +=whitePiecesLength;
			}
			final int posAdjuster = 4;
			g2d.drawImage(images[pieceIndex], guiX  + posAdjuster, guiY  + posAdjuster, this);
		    }
		}
	    }
	}
    }

    public Dimension getPreferredSize(){
	int boardLength = (board.getLength()+outWidth) * squareSize;
	return new Dimension(boardLength, boardLength);
    }

    private EnumMap<BoardPiece, Integer> createTextureMap() {
	EnumMap<BoardPiece, Integer> returnTextureMap = new EnumMap<>(BoardPiece.class);
	returnTextureMap.put(BoardPiece.KING, 0);
	returnTextureMap.put(BoardPiece.QUEEN, 1);
	returnTextureMap.put(BoardPiece.BISHOP, 2);
	returnTextureMap.put(BoardPiece.KNIGHT, 3);
	returnTextureMap.put(BoardPiece.ROOK, 4);
	returnTextureMap.put(BoardPiece.PAWN, 5);
	return returnTextureMap;
    }

    private Map<Integer, String> createRowLetterMap(){
        Map<Integer, String> returnRowLetterMap = new HashMap<>();
	returnRowLetterMap.put(0, "");
	returnRowLetterMap.put(1, "A");
	returnRowLetterMap.put(2, "B");
	returnRowLetterMap.put(3, "C");
	returnRowLetterMap.put(4, "D");
	returnRowLetterMap.put(5, "E");
	returnRowLetterMap.put(6, "F");
	returnRowLetterMap.put(7, "G");
	returnRowLetterMap.put(8, "H");
	return returnRowLetterMap;
    }

    private Image[] createImagesList() {
	/**
	 * Creates a list of the chess pieces texture displayed on the screen.
	 */
        final int subImages = 12;
	Image[] images = new Image[subImages];
	BufferedImage buffered = null;
	try {
	    buffered = ImageIO.read(ClassLoader.getSystemResource("images/ChessPiecesImage.png"));
	} catch (IOException ioException) { // If we can´t load the textures of the chesspieces we cant really play chess, therefore we exit the game.
	    ExceptionLogger.getExceptionLogger().logException(Level.SEVERE, "The piece images could not be loaded from the ChessPiecesImage", ioException);
	    System.exit(1);
	}
	final int pictureRows = 2;
	final int pictureColumns = 6;
	final int pieceImageAdjustor = -8;
	int imageWidth = buffered.getWidth() - (buffered.getWidth() % pictureColumns);
	int imageHeight = buffered.getHeight() - (buffered.getHeight() % pictureRows);
	int index = 0;
	for (int y = 0; y < imageHeight; y += imageHeight / pictureRows) {
	    for (int x = 0; x < imageWidth; x += imageWidth / pictureColumns) {
		images[index] = buffered.getSubimage(x, y, imageWidth / pictureColumns, imageHeight / pictureRows).getScaledInstance(squareSize + pieceImageAdjustor, squareSize + pieceImageAdjustor, BufferedImage.SCALE_SMOOTH);
		index++;
	    }
	}
	return images;
    }

    @Override public void boardChanged() {
	repaint();
    }
}
