package view;

import java.awt.GridLayout;

import javax.swing.JPanel;

public class Admin extends BaseFrame {
	public Admin() {
		super("관리자메인", 500, 500);

		add(c = new JPanel(new GridLayout(0, 1)));

		for (var cap : "구매내역,영화등록,영화수정,영화 예매률 TOP5".split(",")) {
			c.add(btn(cap, a -> {
				if (cap.equals("구매내역")) {
					new PurchaseList().addWindowListener(new Before(this));
				} else if (cap.equals("영화등록")) {
					new MovieRegister().addWindowListener(new Before(this));
				} else if (cap.equals("영화수정")) {
					new MovieEdit().addWindowListener(new Before(this));
				} else {
					new AdminChart().addWindowListener(new Before(this));
				}
			}));
		}

		setVisible(true);
	}
}
