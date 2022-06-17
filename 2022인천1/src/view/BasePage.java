package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;

import tool.Tool;

public class BasePage extends JPanel implements Tool {
	public static MainFrame mf = new MainFrame();
	public static ArrayList<Object> user;

	public JPanel n, w, c, e, s;
	public JPanel nn, nw, nc, ne, ns;
	public JPanel wn, ww, wc, we, ws;
	public JPanel cn, cw, cc, ce, cs;
	public JPanel en, ew, ec, ee, es;
	public JPanel sn, sw, sc, se, ss;

	public BasePage() {
		super(new BorderLayout());
		setBackground(Color.white);
		execute("use covid");
	}
}
