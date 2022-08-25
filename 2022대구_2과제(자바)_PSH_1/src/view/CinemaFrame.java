package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;

import view.BaseFrame.Before;

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

		var l = lblB("cinema", 2, 30);

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
				for (int j = 1; j < nw.getComponentCount(); j++) {
					nw.getComponent(j).setFont(new Font("맑은 고딕", 0, 15));
				}

				var me = (JLabel) e.getSource();

				me.setFont(new Font("맑은 고딕", 1, 15));

				if (me.getText().equals("Movie")) {
					swapPage(new MoviePage());
				} else if (me.getText().equals("Reservation")) {
					swapPage(new ReservationPage());
				} else {
					if (user == null) {
						var ans = JOptionPane.showConfirmDialog(null, "로그인이 필요한 작업입니다.\n로그인 하시겠습니까?", "질문",
								JOptionPane.YES_NO_OPTION);
						if (ans == JOptionPane.YES_OPTION) {
							new LoginFrame().addWindowListener(new Before(this));
							return;
						} else {
							return;
						}
					}

					swapPage(new MypagePage());
				}
			}));
		}

		lblState = lbl(user != null ? "Logout" : "Login", 0, 15, e -> {
			if(user != null) {
				MainFrame.logout();
			}else {
				new LoginFrame().addWindowListener(new Before(this));
			}
		});
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
