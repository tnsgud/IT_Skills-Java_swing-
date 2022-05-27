package view;

import java.awt.BorderLayout;
import java.util.HashMap;

import javax.swing.JPanel;

import tool.Tool;

public class BasePage extends JPanel implements Tool {
	public static MainFrame mf = new MainFrame();

	JPanel m;
	JPanel n, w, c, e, s;
	JPanel nn, nw, nc, ne, ns;
	JPanel wn, ww, wc, we, ws;
	JPanel cn, cw, cc, ce, cs;
	JPanel en, ew, ec, ee, es;
	JPanel sn, sw, sc, se, ss;

	public static HashMap<String, Object> user;

	public BasePage() {
		super(new BorderLayout());
		execute("use covid");
	}
}
