package view;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JPanel;

public class BaseDialog extends JDialog {
	JPanel m; // masterPanel
	JPanel n, c, e, w, s;
	JPanel nn, nc, ne, nw, ns;
	JPanel en, ec, ee, ew, es;
	JPanel sn, sc, se, sw, ss;
	JPanel cn, cc, ce, cw, cs;
	JPanel wn, wc, we, ww, ws;
	
	public BaseDialog(String title, int w, int h) {
		setTitle(title);
		setSize(w, h);
		setModal(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
	}
}
