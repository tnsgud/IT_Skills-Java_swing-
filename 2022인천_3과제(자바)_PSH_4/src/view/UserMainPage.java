package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class UserMainPage extends BasePage {
	public UserMainPage() {
		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel());
		add(s = new JPanel(new GridLayout(1, 0, 5, 5)), "South");

		for (var cap : "검색,프로필,로그아웃,종료".split(",")) {
			s.add(btn(cap, a -> {
				switch (a.getActionCommand()) {
				case "검색":
					mf.swap(new SearchPage());
					break;
				case "프로필":
					mf.swap(new ProfilePage());
					break;
				case "로그아웃":
					mf.swap(new LoginPage());
					break;
				case "종료":
					System.exit(0);
					break;
				}
			}));
		}

		setBorder(new EmptyBorder(5, 5, 5, 5));
	}

	public static void main(String[] args) {
		mf = new MainFrame();
		mf.swap(new LoginPage());
		mf.setVisible(true);
	}
}
