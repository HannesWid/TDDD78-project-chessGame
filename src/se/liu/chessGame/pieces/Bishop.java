package se.liu.chessGame.pieces;

import se.liu.chessGame.BoardPiece;
import se.liu.chessGame.ChessBoard;

/**
 * This class extends ChessPiece with specific movement pattern for the Bishop.
 */

public class Bishop extends ChessPiece
{
    public Bishop(final boolean isWhite, final  ChessBoard chessBoard, int y, int x) {
	super(isWhite, BoardPiece.BISHOP, chessBoard, y, x);
    }

    @Override public boolean isValidMove(int y, int x, boolean isOuter) {
	/**
	 * Checks if the current move is valid according to the bishops move-pattern.
	 **/
	int absY = Math.abs(pieceY - y);
	int absX = Math.abs(pieceX - x);
	return absY == absX && isNotSameSide(y, x) &&
	       !isPathBlocked(y, x) &&
	       (!isOuter || !isCheckedAfterMove(y, x));
    }
}
