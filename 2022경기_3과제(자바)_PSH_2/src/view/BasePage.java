package view;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JPanel;

import tool.Tool;

public class BasePage extends JPanel implements Tool {
	JPanel n, w, c, e, s;
	JPanel nn, nw, nc, ne, ns;
	JPanel wn, ww, wc, we, ws;
	JPanel cn, cw, cc, ce, cs;
	JPanel en, ew, ec, ee, es;
	JPanel sn, sw, sc, se, ss;

	static ArrayList<Object> user;
	static MainFrame mf;
	static int gNo, uGd, uAge, uExp;
	static boolean uAgeFilter = false;
	
	public BasePage(String name) {
		super(new BorderLayout());
		mf.add(this, name);
	}
}
