package view;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import tool.Tool;

public class BaseFrame extends JFrame implements Tool {
	public JPanel n, w, c, e, s;
	public JPanel nn, nw, nc, ne, ns;
	public JPanel wn, ww, wc, we, ws;
	public JPanel cn, cw, cc, ce, cs;
	public JPanel en, ew, ec, ee, es;
	public JPanel sn, sw, sc, se, ss;
	
	static ArrayList<Object> user;

	public BaseFrame(String t, int w, int h) {
		super(t);
		execute("use 2022전국");
		setSize(w, h);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(2);
		((JPanel) getContentPane()).setBackground(Color.white);
	}
	
	static class Before extends WindowAdapter {
		BaseFrame b;
		
		public Before(BaseFrame b) {
			this.b =b;
			b.setVisible(false);
		}
		
		@Override
		public void windowClosed(WindowEvent e) {
			b.setVisible(true);
		}
	}
}
