package se.liu.chessGame.pieces;

import se.liu.chessGame.BoardPiece;
import se.liu.chessGame.ChessBoard;

/**
 * This class extends ChessPiece with specific movement pattern for the Knight.
 */

public class Knight extends ChessPiece
{
    public Knight(final boolean isWhite, final  ChessBoard chessBoard, int y, int x) {
	super(isWhite, BoardPiece.KNIGHT, chessBoard,y,x);
    }

    @Override public boolean isValidMove(int y, int x, boolean isOuter) {
	/**
	 * Checks if the current move is valid according to the knights move-pattern.
	 **/
	int absY = Math.abs(pieceY - y);
	int absX = Math.abs(pieceX - x);
	return isKnightMovePattern(absY, absX) &&
	       isNotSameSide(y, x)  &&
	       (!isOuter || !isCheckedAfterMove(y, x));
    }

    private boolean isKnightMovePattern(int absY, int absX){
        final int minimumMove = 1;
        final int maximumMove = 2;
        return (absX == minimumMove && absY == maximumMove) || (absX == maximumMove && absY == minimumMove);
    }
}
