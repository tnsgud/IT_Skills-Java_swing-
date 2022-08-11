package view;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
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

	@Override
	public void setVisible(boolean b) {
		op((JComponent) getContentPane());
		super.setVisible(b);
	}

	void op(JComponent comp) {
		for (var com : comp.getComponents()) {
			if (com instanceof JPanel && com.isOpaque()) {
				((JPanel) com).setOpaque(false);
				op((JComponent) com);
			}
		}
	}

	public BaseFrame(String t, int w, int h) {
		super(t);
		setSize(w, h);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(2);
		execute("use 2022전국");
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
