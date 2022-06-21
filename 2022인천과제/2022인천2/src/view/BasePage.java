package view;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JPanel;

import tool.Tool;

public class BasePage extends JPanel implements Tool {
	static MainFrame mf = new MainFrame();
	static ArrayList<Object> user;

	JPanel n, w, c, e, s;
	JPanel nn, nw, nc, ne, ns;
	JPanel wn, ww, wc, we, ws;
	JPanel cn, cw, cc, ce, cs;
	JPanel en, ew, ec, ee, es;
	JPanel sn, sw, sc, se, ss;

	public BasePage() {
		super(new BorderLayout());
	}
}
