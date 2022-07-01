package view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

public class Option extends BasePage {
	public Option() {
		var l = lbl("옵션선택", 0, 30);

		l.setBorder(new MatteBorder(0, 0, 3, 0, Color.black));

		add(l, "North");
		add(c = new JPanel(new GridLayout(0, 1, 5, 5)));
		add(s = new JPanel(), "South");

		var t = "좌석배정,수하물 사전 구매".split(",");
		var con = "좌석배정을 통해 여정에 편안함을 더하세요.,부치는 짐이 있으시다면 수하물을 미리 구매하세요.".split(",");
		for (int i = 0; i < 2; i++) {
			c.add(new Item("./datafiles/" + (i == 0 ? "좌석" : "수하물") + ".png", t[i], con[i], "배정,구매".split(",")[i]));
		}

		s.add(btn("확인", a -> {
			for (var peo : BaseFrame.peoples) {
				if (peo.getSeat() == null) {
					eMsg("좌석을 배정해주세요.");
					return;
				} 
			}

			if (BaseFrame.bag.isEmpty()) {
				eMsg("수하물을 구매해주세요.");
				return;
			}
			
			main.swap(new Purchase());
		}));
	}

	class Item extends BasePage {

		public Item(String icon, String title, String content, String cap) {

			add(new JLabel(getIcon(icon, 150, 150)), "West");
			add(c = new JPanel(new GridLayout(0, 1)));

			c.add(lbl(title, 2));
			c.add(lbl(content, 2));
			c.add(cs = new JPanel(new FlowLayout(0)));

			cs.add(btn(cap + "하기", a -> {
				if (cap.equals("배정")) {
					new Seat();
				} else {
					new Baggage();
				}
			}));

			setBorder(new MatteBorder(0, 0, 3, 0, Color.black));
		}
	}

	public static void main(String[] args) {
		main = new Main();
		main.swap(new Option());
	}
}
