package se.liu.chessGame.pieces;

import se.liu.chessGame.BoardPiece;
import se.liu.chessGame.ChessBoard;

/**
 * This class extends ChessPiece with specific movement pattern for the King.
 */

public class King extends ChessPiece
{
    public King(final boolean isWhite, final ChessBoard chessBoard, int y, int x) {
	super(isWhite, BoardPiece.KING, chessBoard,y,x);
    }

    @Override public boolean isValidMove(int y, int x, boolean isOuter) {
	/**
	 * Checks if the current move is valid according to the kings move-pattern.
	 **/
	int diffY = pieceY - y;
	int diffX = pieceX - x;
	return isKingMovePattern(diffY, diffX) &&
	       isNotSameSide(y, x) &&
	       (!isOuter || !isCheckedAfterMove(y, x));
	}

	private boolean isKingMovePattern(int diffY, int diffX){
        return diffX >=-1 &&
	       diffX <=1 &&
	       diffY >=-1 &&
	       diffY <=1;
	}
}
