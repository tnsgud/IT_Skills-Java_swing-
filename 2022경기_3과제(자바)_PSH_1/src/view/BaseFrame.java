package view;

import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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

	public BaseFrame(String t, int w, int h) {
		super(t);
		setSize(w, h);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(2);
		setIconImage(Toolkit.getDefaultToolkit().getImage("./datafiles/기본사진/1.png"));
		getContentPane().setBackground(back);
		execute("use 2022전국_3");
	}
	
	@Override
	public void repaint() {
		opaque((JPanel)getContentPane(), false);
		super.repaint();
	}
	
	@Override
	public void setVisible(boolean b) {
		opaque((JPanel)getContentPane(), false);
		super.setVisible(b);
	}

	class Before extends WindowAdapter {
		BaseFrame b;

		public Before(BaseFrame b) {
			this.b = b;
			b.setVisible(false);
		}

		@Override
		public void windowClosed(WindowEvent e) {
			b.setVisible(true);
		}
	}
}
