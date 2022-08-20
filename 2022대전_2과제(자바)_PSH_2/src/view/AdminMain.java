package view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JPanel;

import view.BaseFrame.Before;

public class AdminMain extends BaseFrame {
	public AdminMain() {
		super("관리자", 500, 300);
		setDefaultCloseOperation(3);

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new GridLayout(0, 3)));

		n.add(hylbl("<html><font color='green'>농수산물판매관리", 2, 1, 35), "West");
		n.add(hylbl("관리자로 로그인 하였습니다..", 4, 0, 12), "East");

		var cap = "로그아웃,농산물등록수정,날씨정보".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new BorderLayout());
			var icon = new JLabel(getIcon("./datafiles/메인이미지/" + cap[i] + ".jpg", 80, 80));

			tmp.add(icon);
			tmp.add(lbl(cap[i], 0), "South");

			evt(icon, e -> {
				var me = (JLabel) e.getSource();

				switch (me.getName()) {
				case "로그아웃":
					setVisible(false);
					new Main();
					break;
				case "농산물등록수정":
					new AdminEditApply().addWindowListener(new Before(this));
					break;
				case "날씨정보":
					new WeatherInfo().addWindowListener(new Before(this));
					break;
				}
			});

			c.add(tmp);
		}

		setVisible(true);
	}
}
