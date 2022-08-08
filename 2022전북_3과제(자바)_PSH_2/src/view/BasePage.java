package view;

import java.awt.BorderLayout;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.JPanel;

import model.Bag;
import model.People;
import tool.Tool;

public class BasePage extends JPanel implements Tool{
	JPanel n, w, c, e, s;
	JPanel nn, nw, nc, ne, ns;
	JPanel wn, ww, wc, we, ws;
	JPanel cn, cw, cc, ce, cs;
	JPanel en, ew, ec, ee, es;
	JPanel sn, sw, sc, se, ss;
	
	static MainFrame mf;
	static LocalDate r_date;
	static ArrayList<People> peoples = new ArrayList<>();
	static ArrayList<Bag> bags = new ArrayList<>();
	
	public BasePage() {
		super(new BorderLayout());
	}
}
