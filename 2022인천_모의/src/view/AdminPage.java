package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JPanel;

public class AdminPage extends BasePage {

	public AdminPage() {
		setLayout(new BorderLayout(10, 10));

		add(w =sz( new JPanel(new FlowLayout(2)), 200, 240), "West");
		add(c = new JPanel(new BorderLayout()));
		
		c.add(new User());

		for (var cap : "회원관리,건물관리,통계".split(",")) {
			w.add(sz(hyplbl(cap, 2, 20, Color.orange, () -> {
				c.removeAll();

				if (cap.equals("회원관리")) {
					c.add(new User());
				} else if (cap.equals("건물관리")) {
					c.add(new Building());
				} else {
					c.add(new Chart());
				}
				
				repaint();
				revalidate();
			}), 180, 80));
		}
	}

	public static void main(String[] args) {
		mf.swapPage(new AdminPage());
		mf.setVisible(true);
	}
}
