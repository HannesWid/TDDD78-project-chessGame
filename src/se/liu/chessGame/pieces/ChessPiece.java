package se.liu.chessGame.pieces;

import com.google.gson.annotations.Expose;
import se.liu.chessGame.BoardPiece;
import se.liu.chessGame.ChessBoard;

/**
 * This abstract class provides common functionality between the different ChessPieces, mostly the move-patterns.
 * A ChessPiece is given a ChessBoard, a BoardPiece enum to decide witch piece it is,
 * the colur of the piece and its x and y coordinates on the board.
 */

public abstract class ChessPiece
{
    protected ChessBoard board;
    @Expose
    protected BoardPiece pieceEnum;
    @Expose
    protected boolean isWhite;
    protected boolean markedPiece;
    @Expose
    protected int pieceY;
    @Expose
    protected int pieceX;

    protected ChessPiece(final boolean isWhite, final BoardPiece pieceEnum, final ChessBoard chessBoard, int y, int x) {
	this.board = chessBoard;
	this.pieceEnum = pieceEnum;
	this.isWhite = isWhite;
	this.markedPiece = false;
	this.pieceY = y;
	this.pieceX = x;
    }

    public abstract boolean isValidMove(int yDestination, int xDestination, boolean isOuter);
    /**
     * boolean isOuter makes sure that we do not check isCheckedAfterMove for positions potentially
     * checking the opponents king after the opponent has moved. Since we only want to see if the moving players
     * king is becoming checked after the move, isCheckedAfterMove for the next moves is irrelevant.
     */

    public int getPieceY() {
	return pieceY;
    }

    public int getPieceX() {
	return pieceX;
    }

    public boolean getIsWhite() {
	return isWhite;
    }

    public BoardPiece getPieceEnum() {
	return pieceEnum;
    }

    public boolean isMarkedPiece() {
	return markedPiece;
    }

    public void setMarkedPiece(final boolean markedPiece) {
	this.markedPiece = markedPiece;
    }

    public void move(int yDestination, int xDestination) {
	/**
	 * This method moves a piece to the y and x cordinates recieved as input.
	 */
	ChessPiece endPiece = board.getPieceAt(yDestination, xDestination);
	board.setPieceAt(pieceY, pieceX, null);
	board.setPieceAt(yDestination, xDestination, this);
	pieceY = yDestination;
	pieceX = xDestination;
	if (endPiece != null) {
	    board.removeFromList(endPiece);
	}
    }

    public boolean isCheck(boolean isWhite){
	/**
	 * This method checks if the KING piece on the same side is in a checked position.
	 */
	boolean isCheck = false;
	King currentKing = board.getKingPiece(isWhite);
	if(currentKing == null) {
	    boolean noKing = true;
	    return noKing;
	}
	for(int y = 0; y < board.getLength(); y++) {
	    for (int x = 0; x < board.getLength(); x++) {
		if(board.isOccupied(y,x) && board.getPieceAt(y, x).getIsWhite() !=isWhite){
		    if(board.getPieceAt(y, x).isValidMove(currentKing.getPieceY(), currentKing.getPieceX(), false)){
			isCheck = true;
		    }
		}
	    }
	}

	return isCheck;
    }

    protected boolean isCheckedAfterMove(int yTarget, int xTarget){
	/**
	 * This method is called when a piece tries to move to a new destination.
	 * It checks if the KING piece on the same side will be in a checked position after the piece has moved.
	 * In that case the move is invalid and returns true
	 */
	ChessPiece currentPiece = board.getPieceAt(yTarget, xTarget);
	board.setPieceAt(pieceY, pieceX, null);
	board.setPieceAt(yTarget, xTarget, this);
	int oldX = pieceX;
	int oldY = pieceY;
	pieceY = yTarget;
	pieceX = xTarget;
	boolean isCheckedAfter = isCheck(isWhite);
	pieceY = oldY;
	pieceX = oldX;
	board.setPieceAt(pieceY, pieceX, this);
	board.setPieceAt(yTarget, xTarget, currentPiece);
	return isCheckedAfter;
    }

    protected boolean isNotSameSide(int y, int x) {
	/**
	 * This method is called when a piece is trying to move to a new destination.
	 * It returns true if the destination is either null or another piece but not on the same side as the moving piece.
	 **/
	return (board.getPieceAt(y,x) == null || isWhite != board.getPieceAt(y,x).getIsWhite());
    }

    protected boolean isPathBlocked(int y, int x) {
	/**
	 * This method checks if the path is blocked when a piece tries to move to a new destination.
	 * There are 4 different methods depending on which direction the piece tries to move.
	 * Vertical, horizontal, topleft - bottomright and topright - bottomleft.
	 */
	int diffY = pieceY - y;
	int diffX = pieceX - x;
	int xStart = Math.min(pieceX, x);
	int xEnd = Math.max(pieceX, x);
	if (diffX == 0) {
	    return verticalCheck(y);
	}
	if (diffY == 0) {
	    return horizontalCheck(xStart, xEnd);
	}
	if ((diffY < 0 && diffX < 0) || (diffY > 0 && diffX > 0)) {
	    return topLeftBottomRightCheck(y,xStart, xEnd);
	} else {
	    return topRightBottomLeftCheck(y, xStart, xEnd);
	}
    }

    protected boolean verticalCheck(int yTarget){
	int yStart = Math.min(pieceY, yTarget);
	int yEnd = Math.max(pieceY, yTarget);
	boolean blocked = false;
	for(int y = yStart + 1; y<yEnd; y++){
	    if(board.isOccupied(y, pieceX)){
		blocked = true;
	    }
	}
	return blocked;
    }

    protected boolean horizontalCheck(int xStart, int xEnd){
	boolean blocked = false;
	for(int x = xStart + 1; x<xEnd; x++){
	    if(board.isOccupied(pieceY, x)){
		blocked = true;
	    }
	}
	return blocked;
    }

    protected boolean topLeftBottomRightCheck(int yTarget, int xStart, int xEnd){
	int y = Math.min(pieceY, yTarget) + 1;
	boolean blocked = false;
	for(int x = xStart + 1; x<xEnd; x++, y++){
	    if(board.isOccupied(y, x)){
		blocked = true;
	    }
	}
	return blocked;
    }

    protected boolean topRightBottomLeftCheck(int yTarget, int xStart, int xEnd){
	int y = Math.max(pieceY, yTarget) - 1;
	boolean blocked = false;
	for(int x = xStart +1 ; x<xEnd; x++, y--){
	    if(board.isOccupied(y, x)){
		blocked = true;
	    }
	}
	return blocked;
    }
}
