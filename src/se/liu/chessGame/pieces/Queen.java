package se.liu.chessGame.pieces;

import se.liu.chessGame.BoardPiece;
import se.liu.chessGame.ChessBoard;

/**
 * This class extends ChessPiece with specific movement pattern for the Queen.
 */

public class Queen extends ChessPiece
{
    public Queen(final boolean isWhite, final ChessBoard chessBoard, int y, int x) {
	super(isWhite, BoardPiece.QUEEN, chessBoard,y,x);
    }

    @Override public boolean isValidMove(int y, int x, boolean isOuter) {
	/**
	 * Checks if the current move is valid according to the queens move-pattern.
	 **/
	return isQueenMovePattern(y,x) &&
	       isNotSameSide(y, x) &&
	       !isPathBlocked(y, x) &&
	       (!isOuter || !isCheckedAfterMove(y, x));
    }

    private boolean isQueenMovePattern(int y, int x){
	int absY = Math.abs(pieceY - y);
	int absX = Math.abs(pieceX - x);
	int diffY = pieceY - y;
	int diffX = pieceX - x;
	return diffX == 0 || diffY == 0 || absY == absX;
    }
}
