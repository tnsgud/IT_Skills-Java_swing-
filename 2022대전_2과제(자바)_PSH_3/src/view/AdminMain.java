package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class AdminMain extends BaseFrame {
	public AdminMain() {
		super("메인", 450, 200);
		setDefaultCloseOperation(3);

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new GridLayout(1, 0)));

		n.add(lbl("<html><font color='green'>농수산물판매관리", 0, 25), "West");
		n.add(lbl("관리자로 로그인 하였습니다.", 0, 15), "East");

		var cap = "로그아웃,농산물등록수정,날씨정보".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new BorderLayout());
			var l = event(new JLabel(getIcon("./datafiles/메인이미지/" + cap[i] + ".jpg", 100, 100)), e -> {
				var me = (JLabel) e.getSource();

				switch (me.getText()) {
				case "로그아웃":
					setDefaultCloseOperation(2);
					dispose();
					break;
				case "농산물등록수정":
					new BaseModifiy().addWindowListener(new Before(this));
					break;
				case "날씨정보":
					new WeatherInfo().addWindowListener(new Before(this));
					break;

				}
			});

			l.setName(cap[i]);

			tmp.add(l);
			tmp.add(lbl(cap[i], 0), "South");

			tmp.setBorder(new LineBorder(Color.black));

			c.add(tmp);
		}

		setVisible(true);
	}
}
