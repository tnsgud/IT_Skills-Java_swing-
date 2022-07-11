package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class CinemaFrame extends BaseFrame {
	JLabel lbl[] = new JLabel[3];
	JScrollPane scr;
	static JLabel lblState;

	@Override
	public JLabel lbl(String c, int a, int st, int sz) {
		var l = super.lbl(c, a, st, sz);
		l.setForeground(Color.white);
		return l;
	}

	public CinemaFrame() {
		super("Movie", 1200, 800);

		add(n = sz(new JPanel(new BorderLayout()), 0, 80), "North");
		add(scr = new JScrollPane());

		n.add(nw = new JPanel(new FlowLayout(0, 40, 10)), "West");
		n.add(ne = new JPanel(new FlowLayout(2)), "East");

		nw.add(lblB("cinema", 2, 30));

		var cap = "Movie,Reservation,Mypage".split(",");
		for (int i = 0; i < cap.length; i++) {
			int idx = i;

			nw.add(lbl[i] = lbl(cap[i], 2, 15, e -> {
			}));
		}

		lblState = lbl(user != null ? "Logout" : "Login", 0, 15, e -> {
		});

		var b1 = new BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				this.thumbColor = Color.lightGray;
			}

			@Override
			protected JButton createDecreaseButton(int orientation) {
				return sz(new JButton(), 0, 0);
			}

			@Override
			protected JButton createIncreaseButton(int orientation) {
				return sz(new JButton(), 0, 0);
			}
		};
		var b2 = new BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				this.thumbColor = Color.lightGray;
			}

			@Override
			protected JButton createDecreaseButton(int orientation) {
				return sz(new JButton(), 0, 0);
			}

			@Override
			protected JButton createIncreaseButton(int orientation) {
				return sz(new JButton(), 0, 0);
			}
		};

		scr.setBorder(null);
		scr.getVerticalScrollBar().setUI(b1);
		scr.getHorizontalScrollBar().setUI(b2);

		n.setBorder(new EmptyBorder(10, 40, 10, 40));
		ne.setBorder(new EmptyBorder(10, 0, 0, 0));

		n.setBackground(red);

		nw.setOpaque(false);
		ne.setOpaque(false);

		setVisible(true);
	}

	void swapPage(Component c) {
		scr.setViewportView(c);
		scr.repaint();
		scr.revalidate();
	}
}
