package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class AdminMain extends BaseFrame {
	public AdminMain() {
		super("관리자", 500, 200);
		setDefaultCloseOperation(3);

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new GridLayout(0, 3)));

		n.add(hylbl("<html><font color='green'>농수산물판매관리", 2, 25), "West");
		n.add(hylbl("관리자로 로그인 하였습니다.", 4, 15), "East");

		var cap = "로그아웃,농산물등록수정,날씨정보".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new BorderLayout());
			var img = new JLabel(getIcon("./datafiles/메인이미지/" + cap[i] + ".jpg", 80, 80));
			var lbl = lbl(cap[i], 0);

			tmp.add(img);
			tmp.add(lbl, "South");

			img.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (lbl.getText().equals("로그아웃")) {
						setVisible(false);
						new Main();
					} else if (lbl.getText().equals("농산물등록수정")) {
						new AdminEditApply().addWindowListener(new Before(AdminMain.this));
					} else {
						new WeatherInfo().addWindowListener(new Before(AdminMain.this));
					}
				}
			});
			
			c.add(tmp);
		}

		setVisible(true);
	}
}
