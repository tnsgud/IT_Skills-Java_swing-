package view;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JPanel;

public class BasePage extends JPanel {
	public JPanel n, w, c, e, s;
	public JPanel nn, nw, nc, ne, ns;
	public JPanel wn, ww, wc, we, ws;
	public JPanel cn, cw, cc, ce, cs;
	public JPanel en, ew, ec, ee, es;
	
	static ArrayList<Object> user;
	static CinemaFrame cf;
	
	public BasePage() {
		super(new BorderLayout());
	}
}
