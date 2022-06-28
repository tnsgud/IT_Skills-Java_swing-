package view;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import tool.Tool;

public class BaseFrame extends JFrame implements Tool {

	JPanel n, w, c, e, s;
	JPanel nn, nw, nc, ne, ns;
	JPanel wn, ww, wc, we, ws;
	JPanel cn, cw, cc, ce, cs;
	JPanel en, ew, ec, ee, es;
	JPanel sn, sw, sc, se, ss;

	static ArrayList<Object> user;
	static ArrayList<People> peoples = new ArrayList<>();
	static boolean idSave = false, login = false;
	static int t_no, sc_no, m_no;

	public BaseFrame(String t, int w, int h) {
		super(t);
		setSize(w, h);
		setDefaultCloseOperation(2);
		setLocationRelativeTo(null);
		setResizable(false);
		execute("use airline");

	}

	class People {
		int div;
		String fname, lname;
		String seat;
		int price;

		public People(int div, String fname, String lname) {
			this.div = div;
			this.fname = fname;
			this.lname = lname;
		}
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
