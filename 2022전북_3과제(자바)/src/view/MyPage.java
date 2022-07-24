package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class MyPage extends BasePage {
	public MyPage() {
		var l = lbl("마이페이지", 0, 30);

		add(l, "North");
		add(c = new JPanel(new GridLayout(0, 1, 5, 5)));

		var cap = "정보수정,마일리지".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new BorderLayout());
			var tmp_c = new JPanel(new GridLayout(0, 1));

			tmp.add(new JLabel(getIcon("./datafiles/" + cap[i] + ".png", 200, 200)), "West");
			tmp.add(tmp_c);

			tmp_c.add(lbl(cap[i] + (i == 1 ? " 내역" : ""), 2, 15));
			tmp_c.add(btn(i == 0 ? "수정하기" : "내역보기", a -> {
				if(a.getActionCommand().equals("수정하기")) {
					new EditInfo();
				}else {
					new Mileage();
				}
			}));
			
			c.add(tmp);
		}
		
		
	}
}
