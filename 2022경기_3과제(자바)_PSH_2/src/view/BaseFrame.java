package view;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
		op((JPanel) getContentPane());
		super.setVisible(b);
	}
	
	@Override
	public void repaint() {
		op((JPanel) getContentPane());
		super.repaint();
	}

	void op(JComponent comp) {
		for (var com : comp.getComponents()) {
			if (com instanceof JPanel) {
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
		setIconImage(getIcon("./datfiles/기본사진/1.png", 100, 100).getImage());
		getContentPane().setBackground(new Color( 0, 50, 100));
		execute("use 2022전국_3");
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
