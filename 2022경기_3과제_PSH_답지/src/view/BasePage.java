package view;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JPanel;

import tool.Tool;

public class BasePage extends JPanel implements Tool {
	JPanel n, w, c, e, s;
	JPanel nn, nw, nc, ne, ns;
	JPanel wn, ww, wc, we, ws;
	JPanel cn, cw, cc, ce, cs;
	JPanel en, ew, ec, ee, es;
	JPanel sn, sw, sc, se, ss;

	static MainFrame mf;
	static ArrayList<Object> user;
	static int g_no, u_gd, u_age;
	static boolean u_ageFilter = false, isAdmin = false;
	
	public BasePage(String name) {
		super(new BorderLayout());
		mf.addPage(this, name);
	}
}
