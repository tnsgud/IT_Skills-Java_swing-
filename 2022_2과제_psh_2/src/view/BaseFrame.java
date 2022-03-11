package view;

import java.text.DecimalFormat;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import tool.Tool;

public class BaseFrame extends JFrame implements Tool {
	JPanel n, c, w, e, s;
	JPanel nn, nc, nw, ne, ns;
	JPanel cn, cc, cw, ce, cs;
	JPanel wn, wc, ww, we, ws;
	JPanel sn, sc, sw, se, ss;
	JPanel en, ec, ew, ee, es;
	static int uno, tno, rno, qno;
	static String cno;
	static DecimalFormat format = new DecimalFormat("#,##0");

	public BaseFrame(String t, int w, int h) {
		super(t);
		setSize(w, h);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(2);
		execute("use 2022지방_1");
	}
}
