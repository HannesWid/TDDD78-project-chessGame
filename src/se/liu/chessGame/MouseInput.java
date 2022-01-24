package se.liu.chessGame;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Recieves all wanted Mouseinputs and notifies its listener.
 * Extends the abstract class MouseAdapter
 */

public class MouseInput extends MouseAdapter
{
    private MouseInputListener listener = null;

    public void setListener(final MouseInputListener listener) {
        this.listener = listener;
    }

    @Override public void mousePressed(final MouseEvent e) {
        listener.handleInput(e.getPoint());
    }
}
