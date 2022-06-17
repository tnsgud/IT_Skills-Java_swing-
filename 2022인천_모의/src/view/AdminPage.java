package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public class AdminPage extends BasePage {

	public AdminPage() {
		setLayout(new BorderLayout(10, 10));

		add(w = sz(new JPanel(new FlowLayout(0)), 200, 240), "West");
		add(c = new JPanel(new BorderLayout()));

		w.setBackground(new Color(0, 123, 255));

		c.add(new User());

		for (var cap : "<html>&#128100 회원관리,<html>&#127968 건물관리,<html>&#128200 통계,<html>&#128275 로그아웃".split(",")) {
			var lbl = sz(hyplbl(cap, 2, 20, Color.orange, (e) -> {
				c.removeAll();

				var myself = (JLabel) e.getSource();
				for (var comp : w.getComponents())
					((JComponent) comp).setBorder(null);

				myself.setBorder(
						new CompoundBorder(new MatteBorder(0, 3, 0, 0, Color.ORANGE), new EmptyBorder(0, 5, 0, 0)));
				if (cap.contains("회원관리")) {
					c.add(new User());
				} else if (cap.contains("건물관리")) {
					c.add(new Building());
				} else if (cap.contains("통계")) {
					c.add(new Chart());
				} else {
					mf.swapPage(new LoginPage());
				}

				repaint();
				revalidate();
			}), 200, 40);
			lbl.setBorder(cap.contains("회원")
					? new CompoundBorder(new MatteBorder(0, 3, 0, 0, Color.ORANGE), new EmptyBorder(0, 5, 0, 0))
					: null);
			w.add(lbl);
		}
	}

	public static void main(String[] args) {
		mf.swapPage(new AdminPage());
		mf.setVisible(true);
	}
}
