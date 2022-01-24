package se.liu.chessGame.pieces;

import se.liu.chessGame.BoardPiece;
import se.liu.chessGame.ChessBoard;

/**
 * This class extends ChessPiece with specific movement pattern for the Rook.
 */

public class Rook extends ChessPiece
{
    public Rook(final boolean isWhite, final ChessBoard chessBoard, int y, int x) {
	super(isWhite, BoardPiece.ROOK, chessBoard, y, x);
    }

    @Override public boolean isValidMove(int y, int x, boolean isOuter) {
	/**
	 * Checks if the current move is valid according to the rooks move-pattern.
	 **/
	int diffY = pieceY - y;
	int diffX = pieceX - x;
	return (diffX == 0 || diffY == 0) &&
	       isNotSameSide(y, x) &&
	       !isPathBlocked(y, x) &&
	       (!isOuter || !isCheckedAfterMove(y, x));
	}
}
