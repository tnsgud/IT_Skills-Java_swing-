package view;

import javax.swing.JDialog;
import javax.swing.JPanel;

import tool.Tool;

public class BaseDialog extends JDialog implements Tool{
	public JPanel n, w, c, e, s;
	public JPanel nn, nw, nc, ne, ns;
	public JPanel wn, ww, wc, we, ws;
	public JPanel cn, cw, cc, ce, cs;
	public JPanel en, ew, ec, ee, es;
	public JPanel sn, sw, sc, se, ss;
	
	public BaseDialog(int w, int h) {
		super();
		setModal(true);
		setSize(w, h);
		setLocationRelativeTo(BasePage.mf);
		setDefaultCloseOperation(2);
		setUndecorated(true);
	}
}
