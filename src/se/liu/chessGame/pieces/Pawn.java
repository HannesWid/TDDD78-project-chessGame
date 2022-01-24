package se.liu.chessGame.pieces;

import se.liu.chessGame.BoardPiece;
import se.liu.chessGame.ChessBoard;

/**
 * This class extends ChessPiece with specific movement pattern for the Pawn.
 */

public class Pawn extends ChessPiece
{
    public Pawn(final boolean isWhite, final ChessBoard chessBoard, int y, int x) {
	super(isWhite, BoardPiece.PAWN, chessBoard,y,x);
    }

    @Override public boolean isValidMove(int y, int x, boolean isOuter) {
	/**
	 * Checks if the current move is valid according to the pawns move-pattern.
	 **/
	int diffY = pieceY - y;
	int diffX = pieceX - x;
	if(isValidDirection(isWhite, diffY) && (!isOuter || !isCheckedAfterMove(y,x))) {
	    if (diffX == 0 && !board.isOccupied(y, x)) {
		if(diffY == 1 || diffY == -1){
		    return true;
		}
		final int doubleMovement = 2;
		final int whiteStartPos = 6;
		final int blackStartPos = 1;
		if(((diffY == doubleMovement && pieceY == whiteStartPos) || (diffY == -doubleMovement && pieceY == blackStartPos)) && !isPathBlocked(y, x)) {
		    return true;
		}
	    }
	    if ((diffX == -1 || diffX == 1) &&
		(board.isOccupied(y, x) && isWhite != board.getPieceAt(y,x).isWhite)) {
		if(diffY == 1 || diffY == -1) {
		    return true;
		}
	    }
	}
	return false;
    }

    private boolean isValidDirection(boolean isWhite, int diffY){
	/**
	 * returns true if the pawn is moving towards the opponents side.
	 **/
	return ((isWhite && diffY > 0) || (!isWhite && diffY < 0));
    }
}
