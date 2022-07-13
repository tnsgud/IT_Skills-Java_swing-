package view;

import java.awt.GridLayout;

public class AdminFrame extends BaseFrame {
	public AdminFrame() {
		super("Admin", 500, 500);
		setLayout(new GridLayout(0, 1));

		for (var cap : "영화,극장,회원".split(",")) {
			add(btn(cap + " 편집", a -> {
				if (cap.contains("영화")) {
					new MovieManageFrame().addWindowListener(new Before(this));
				} else if (cap.contains("극장")) {
					new TheaterManageFrame().addWindowListener(new Before(this));
				} else {
					new UserManageFrame().addWindowListener(new Before(this));
				}
			}));
		}

		setVisible(true);
	}

	public static void main(String[] args) {
		new AdminFrame();
	}
}
