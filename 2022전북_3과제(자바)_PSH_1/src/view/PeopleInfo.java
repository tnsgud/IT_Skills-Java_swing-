package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import model.People;

public class PeopleInfo extends BasePage {
	int cnt1, cnt2, cnt3;
	ArrayList<Item> items = new ArrayList<>();
	JTextField txt[] = new JTextField[4];

	public PeopleInfo() {
		add(new JScrollPane(c = new JPanel()));
		add(s = new JPanel(), "South");

		c.setLayout(new BoxLayout(c, BoxLayout.PAGE_AXIS));

		for (var peo : BaseFrame.peoples) {
			var i = new Item(peo);
			c.add(i);
			items.add(i);
		}

		{
			var tmp = new JPanel(new BorderLayout());
			var tmp_c = new JPanel(new GridLayout(0, 1, 5, 5));

			tmp.add(lbl("예약자정보", 2, 20), "North");
			tmp.add(tmp_c);

			var cap = "이메일,전화번호,비밀번호,생년월일".split(",");
			for (int i = 0; i < cap.length; i++) {
				var temp = new JPanel(new BorderLayout());

				temp.add(sz(lbl(cap[i], 2, 10), 80, 20), "West");
				temp.add(txt[i] = new JTextField());

				tmp_c.add(temp);
			}

			tmp_c.setBorder(new LineBorder(Color.black));

			c.add(tmp);
		}

		{
			var idx = new int[] { 7, 6, 2, 5 };
			for (int i = 0; i < idx.length; i++) {
				txt[i].setText(BaseFrame.user.get(idx[i]).toString());
			}
		}

		s.add(btn("확인", a -> {
			for (var i : items) {
				for (var t : i.txt) {
					if (t.getText().isEmpty() || i.com.getSelectedIndex() == -1) {
						eMsg("빈칸이 존재합니다.");
						return;
					}
				}

				for (int j = 0; j < 2; j++) {
					if (i.txt[j].getText().matches(".*[가-힣].*")) {
						eMsg("이름은 영문으로만 입력하주세요.");
						return;
					}
				}

				try {
					var date = LocalDate.parse(i.txt[2].getText());
					var age = LocalDate.now().getYear() - date.getYear() + (date.isBefore(LocalDate.now()) ? 1 : 0);

					if (i.people.getDiv() == 1 && age <= 12) {
						eMsg("생년월일을 확인해주세요.1");
						return;
					} else if (i.people.getDiv() == 2 && (age < 2 || age >= 12)) {
						eMsg("생년월일 확인해주세요.2");
						return;
					} else if (i.people.getDiv() == 3 && age > 2) {
						eMsg("생년월일 확인해주세요.3");
						return;
					}
				} catch (Exception e) {
					eMsg("이름은 영문으로만 입력하주세요.");
				}

				var idx = new int[] { 7, 6, 2, 5 };
				for (int j = 0; j < idx.length; j++) {
					if (!txt[j].getText().equals(BaseFrame.user.get(idx[j]).toString().trim())) {
						eMsg("예약자정보가 올바르지 않습니다.");
						return;
					}
				}

				var peo = BaseFrame.peoples.get(items.indexOf(i));
				var price = toInt(getOne("select s_price from schedule where s_no=?", BaseFrame.s_no));

				peo.setSex(i.com.getSelectedIndex());
				peo.setFname(i.txt[0].getText());
				peo.setLname(i.txt[1].getText());
				peo.setBirth(i.txt[2].getText());
				peo.setPrice(
						peo.getDiv() == 1 ? price : peo.getDiv() == 2 ? (int) (price * 0.85) : (int) (price * 0.7));

			}

			iMsg("입력이 완료되었습니다.");
			main.swap(new Option());
		}));
	}

	class Item extends BasePage {
		People people;
		JComboBox<String> com = new JComboBox<>("남,여".split(","));
		JTextField txt[] = new JTextField[3];

		public Item(People people) {
			this.people = people;

			add(lbl(c_div[people.getDiv()] + (people.getDiv() == 1 ? ++cnt1 : people.getDiv() == 2 ? ++cnt2 : ++cnt3),
					2, 20), "North");
			add(c = new JPanel(new GridLayout(0, 1, 5, 5)));

			var cap = "성별,이름(영문),생년월일".split(",");
			for (int i = 0; i < cap.length; i++) {
				var tmp = new JPanel(new BorderLayout());

				tmp.add(sz(lbl(cap[i], 2, 12), 80, 20), "West");

				if (i == 0) {
					tmp.add(com);
					com.setSelectedIndex(-1);
				} else if (i == 1) {
					tmp.add(txt[0] = new JTextField());
					tmp.add(txt[1] = new JTextField(30), "East");

					txt[0].setToolTipText("성을 입력하세요.");
					txt[0].setToolTipText("이름을 입력하세요.");
				} else {
					tmp.add(txt[2] = new JTextField());
				}

				c.add(tmp);
			}

			c.setBorder(new LineBorder(Color.black));
		}
	}
}
