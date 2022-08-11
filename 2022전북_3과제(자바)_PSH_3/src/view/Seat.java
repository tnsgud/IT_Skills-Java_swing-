package view;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

public class Seat extends BaseFrame {
	public Seat() {
		super("좌석배정", 500, 500);

		addWindowFocusListener(new WindowAdapter() {
			@Override
			public void windowLostFocus(WindowEvent e) {
				if (e.getOppositeWindow() instanceof JDialog) {
					return;
				}

				dispose();
			}
		});

		setVisible(true);
	}
}
