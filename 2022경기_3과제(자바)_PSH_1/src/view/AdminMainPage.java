package view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class AdminMainPage extends BasePage {
	public AdminMainPage() {
		super("관리자 메인");

		((JLabel) mf.n.getComponent(0)).setText("관리자 메뉴");

		add(c = new JPanel(new FlowLayout(1, 50, 150)));

		var cap = "게임관리,거래내역,차트".split(",");
		for (int i = 0; i < cap.length; i++) {
			var l = lblImg(cap[i], 0, "./datafiles/기본사진/" + (i + 5) + ".png", 150, 150, e -> {
				var me = (JLabel) e.getSource();

				if (me.getText().contains("게임관리")) {
					new SearchPage();
				} else if (me.getText().contains("거래내역")) {
					new DealPage();
				} else {
					new ChartPage();
				}
			});
			c.add(l);
		}

		setBackground(Color.black);

		mf.repaint();
	}

	public static void main(String[] args) {
		BasePage.mf = new MainFrame();
		new AdminMainPage();
	}
}
