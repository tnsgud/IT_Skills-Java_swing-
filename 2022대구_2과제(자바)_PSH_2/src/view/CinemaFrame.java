package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.stream.Stream;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import view.BaseFrame.Before;

public class CinemaFrame extends BaseFrame {
	JLabel lbl[] = new JLabel[4];
	JScrollPane scr;

	@Override
	public JLabel lbl(String c, int a, int st, int sz) {
		var l = super.lbl(c, a, st, sz);
		l.setForeground(Color.white);
		return l;
	}

	public CinemaFrame() {
		super("cinema", 1250, 800);

		add(n = sz(new JPanel(new BorderLayout()) {
			@Override
			public void setOpaque(boolean isOpaque) {
				super.setOpaque(true);
			}
		}, 0, 80), "North");
		add(scr = scroll(null));

		n.add(nw = new JPanel(new FlowLayout(0, 40, 10)), "West");
		n.add(ne = new JPanel(new FlowLayout(0, 40, 10)), "East");

		var l = lbl("cinema", 2, 30);
		l.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				dispose();
			}
		});

		nw.add(l);

		var cap = "Movie,Reservation,Mypage".split(",");
		for (int i = 0; i < cap.length; i++) {
			nw.add(lbl[i] = lbl(cap[i], 2, 15, e -> {
				Stream.of(nw.getComponents()).forEach(c -> c.setFont(new Font("맑은 고딕", 0, 15)));

				var me = (JLabel) e.getSource();

				me.setFont(new Font("맑은 고딕", 1, 15));

				if (me.getText().equals("Movie")) {
					swap(new MoviePage());
				} else if (me.getText().equals("Reservation")) {
					swap(new ReservationPage());
				} else {
					if (BasePage.user == null) {
						if (loginCheck() == JOptionPane.YES_OPTION) {
							new LoginFrame().addWindowListener(new Before(CinemaFrame.this));
							return;
						} else {
							return;
						}
					}

					swap(new MypagePage());
				}
			}));
		}

		ne.add(lbl[3] = lbl("Login", 2, 20, e -> {
			var text = ((JLabel) e.getSource()).getText();

			if (text.equals("Login")) {
				new LoginFrame().addWindowListener(new Before(this));
			} else {
				logout();
			}
		}));

		setVisible(true);

		n.setBackground(red);
	}

	void login() {
		lbl[3].setText("Logout");
	}

	void logout() {
		lbl[3].setText("Login");
		((MainFrame) ((Before) getWindowListeners()[0]).b).logout();
	}

	void swap(BasePage p) {
		scr.setViewportView(p);
		scr.repaint();
		scr.revalidate();
	}
}
