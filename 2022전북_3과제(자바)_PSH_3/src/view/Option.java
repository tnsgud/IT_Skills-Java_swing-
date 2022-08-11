package view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class Option extends BasePage {
	public Option() {
		var l = lbl("옵션선택", 0, 30);

		l.setBorder(new LineBorder(Color.black));

		add(l, "North");
		add(c = new JPanel(new GridLayout(0, 1, 5, 5)));
		add(s = new JPanel(), "South");

		var t = "좌석배정,수하물 사전 구매".split(",");
		var con = "좌석배정을 통해 여정에 편안함을 더하세요.,부치는 짐이 있으시다면 수하물을 미리 구매하세요.".split(",");
		for (int i = 0; i < 2; i++) {
			c.add(new Item("./datafiles/" + "좌석,수하물".split(",")[i] + ".png", t[i], con[i], "배정,구매".split(",")[i]));
		}

		s.add(btn("확인", a -> {
			for (var peo : peoples) {
				if (peo.seat == null) {
					eMsg("좌석을 배정해주세요.");
					return;
				}
			}

			if (bags.isEmpty()) {
				eMsg("수하물을 선택하세요.");
				return;
			}

			mf.swap(new Purchase());
		}));
	}

	class Item extends BasePage {
		public Item(String icon, String title, String content, String cap) {
			add(new JLabel(getIcon(icon, 150, 150)), "North");
			add(c = new JPanel(new GridLayout(0, 1)));

			c.add(lbl(title, 2));
			c.add(lbl(content, 2));
			c.add(cs = new JPanel(new FlowLayout(0)));

			cs.add(btn(cap, a -> {
				if (cap.equals("배정")) {
					new Seat();
				} else {
					new Baggage();
				}
			}));
		}
	}
}
