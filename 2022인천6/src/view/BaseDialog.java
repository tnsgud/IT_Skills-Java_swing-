package view;

import javax.swing.JDialog;
import javax.swing.JPanel;

import tool.Tool;

public class BaseDialog extends JDialog implements Tool {
	JPanel n, w, c, e, s;
	JPanel nn, nw, nc, ne, ns;
	JPanel wn, ww, wc, we, ws;
	JPanel cn, cw, cc, ce, cs;
	JPanel en, ew, ec, ee, es;
	JPanel sn, sw, sc, se, ss;

	public BaseDialog(int w, int h) {
		setSize(w, h);
		setModal(true);
		setUndecorated(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(2);
	}
}
