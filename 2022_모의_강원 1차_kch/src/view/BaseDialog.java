package view;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JPanel;

public class BaseDialog extends JDialog {
	JPanel n, c, w, e, s;
	JPanel nn, nc, nw, ne, ns;
	JPanel cn, cc, cw, ce, cs;
	JPanel wn, wc, ww, we, ws;
	JPanel sn, sc, sw, se, ss;
	JPanel en, ec, ew, ee, es;

	public BaseDialog(String title, int w, int h) {
		setTitle(title);
		setSize(w, h);
		setModal(true);
		setLayout(new BorderLayout());
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
}
