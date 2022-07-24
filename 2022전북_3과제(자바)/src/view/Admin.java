package view;

import java.awt.GridLayout;

import javax.swing.JPanel;

public class Admin extends BaseFrame {
	public Admin() {
		super("관리자", 500, 500);

		add(c = new JPanel(new GridLayout(0, 1)));

		for (var cap : "공항등록,항공일정등록,탑승자분석,로그아웃".split(",")) {
			c.add(btn(cap, a -> {
				if(cap.equals("공항등록")) {
					new Airport();
				}else if(cap.equals("항공일정등록")) {
					new Schedule();
				}else if(cap.equals("탑승자분석")) {
					new Chart();
				}else {
					dispose();
				}
			}));
		}

		setVisible(true);
	}
}
