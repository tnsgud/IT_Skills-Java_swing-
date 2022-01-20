package view;

import javax.swing.JDialog;
import javax.swing.JPanel;

public class BaseDialog extends JDialog {
	JPanel n, c, s, w, e;
	JPanel nn, nc, ns, nw, ne;
	JPanel cn, cc, cs, cw, ce;
	JPanel en, ec, es, ew, ee;
	JPanel sn, sc, ss, sw, se;
	JPanel wn, wc, ws, ww, we;

	public BaseDialog(String title, int w, int h) {
		setTitle(title);
		setSize(w, h);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModal(true);
	}
}
