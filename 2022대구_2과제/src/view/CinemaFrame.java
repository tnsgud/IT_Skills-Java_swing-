package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
		super("Movie", 1250, 800);

		add(n = sz(new JPanel(new BorderLayout()), 0, 80), "North");
		add(scr = scroll(null));

		n.add(nw = new JPanel(new FlowLayout(0, 40, 10)), "West");
		n.add(ne = new JPanel(new FlowLayout(2)), "East");

		var l =lblB("cinema", 2, 30); 
		
		l.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				dispose();
			}
		});
		
		nw.add(l);

		var cap = "Movie,Reservation,Mypage".split(",");
		for (int i = 0; i < cap.length; i++) {
			nw.add(lbl[i] = lbl(cap[i], 2, 15));
		}

		lblState = lbl(user != null ? "Logout" : "Login", 0, 15);
		ne.add(lblState);

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
