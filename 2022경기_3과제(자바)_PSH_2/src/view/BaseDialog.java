package view;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JDialog;
import javax.swing.JPanel;

import tool.Tool;

public class BaseDialog extends JDialog implements Tool{
	JPanel n, w, c, e, s;
	JPanel nn, nw, nc, ne, ns;
	JPanel wn, ww, wc, we, ws;
	JPanel cn, cw, cc, ce, cs;
	JPanel en, ew, ec, ee, es;
	JPanel sn, sw, sc, se, ss;

	public BaseDialog(String t, int w, int h) {
		setTitle(t);
		getContentPane().setBackground(new Color(0, 100, 200));
		setModal(true);
		setSize(w, h);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
	}
}
