package view;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import tool.Tool;

public class BaseFrame extends JFrame implements Tool {
	JPanel n, w, c, e, s;
	JPanel nn, nw, nc, ne, ns;
	JPanel wn, ww, wc, we, ws;
	JPanel cn, cw, cc, ce, cs;
	JPanel en, ew, ec, ee, es;
	JPanel sn, sw, sc, se, ss;

	@Override
	public void setVisible(boolean b) {
		op((JComponent) getContentPane());
		super.setVisible(b);
	}

	@Override
	public void repaint() {
		op((JComponent) getContentPane());
		super.repaint();
	}

	public BaseFrame(String t, int w, int h) {
		super(t);
		setSize(w, h);
		setDefaultCloseOperation(2);
		setLocationRelativeTo(null);
		execute("use 2022전국_3");
	}
}
