package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.font.LineBreakMeasurer;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import model.People;

public class PeopleInfo extends BasePage {
	ArrayList<Item> itemList = new ArrayList<>();
	JTextField txt[] = new JTextField[4];
	int cnt[] = new int[] { 0, 0, 0 };

	public PeopleInfo() {
		add(new JScrollPane(c = new JPanel()));
		add(s = new JPanel(), "South");
		c.setLayout(new BoxLayout(c, BoxLayout.PAGE_AXIS));

		for (var peo : peoples) {
			var i = new Item(peo);
			c.add(i);
			itemList.add(i);
		}

		c.add(cc = new JPanel(new BorderLayout()));

		var tmp = new JPanel(new GridLayout(0, 1, 5, 5));

		cc.add(lbl("예약자정보", 2, 20), "North");
		cc.add(tmp);

		var cap = "이메일,전화번호,비밀번호,생년월일".split(",");
		for (int i = 0; i < cap.length; i++) {
			var temp = new JPanel(new BorderLayout());

			temp.add(sz(lbl(cap[i], 2, 10), 80, 20), "West");
			temp.add(txt[i] = new JTextField());

			tmp.add(temp);
		}

		s.add(btn("확인", a -> {
			for (var i : itemList) {
				for (var t : i.txt) {
					if (t.getText().isEmpty() || i.com.getSelectedIndex() == -1) {
						eMsg("빈칸이 존재합니다.");
						return;
					}
				}

				for (int j = 0; j < 2; j++) {
					if (i.txt[j].getText().matches(".*[ㄱ-힣].*")) {
						eMsg("이름은 영문으로만 입력해주세요.");
						return;
					}
				}

				try {
					var date = LocalDate.parse(i.txt[2].getText());
					var age = LocalDate.now().getYear() - date.getYear() + (date.isBefore(LocalDate.now()) ? 1 : 0);

					if (i.people.div == 1 && age <= 12) {
						eMsg("생년월일을 확인해주세요.");
						return;
					} else if (i.people.div == 2 && (age < 2 || age >= 12)) {
						eMsg("생년월일을 확인해주세요.");
						return;
					} else if (i.people.div == 3 && age > 2) {
						eMsg("생년월일을 확인해주세요.");
						return;
					}
				} catch (Exception e) {
					eMsg("생년월일을 확인해주세요.");
					return;
				}

				var pr = toInt(getOne("select s_price from schedule where s_no = ?", BaseFrame.sNo));
				var price = new int[] { 0, pr, (int) (pr * 0.85), (int) (pr * 0.7) };

				i.people.sex = i.com.getSelectedIndex();
				i.people.fName = i.txt[0].getText();
				i.people.lName = i.txt[1].getText();
				i.people.birth = i.txt[2].getText();
				i.people.price = price[i.people.div];
			}

			var idx = new int[] { 7, 6, 2, 5 };
			for (int i = 0; i < idx.length; i++) {
				if (!txt[i].getText().equals(member.get(idx[i]).toString().trim())) {
					eMsg("예약자정보가 올바르지 않습니다.");
					return;
				}
			}

			iMsg("입력이 완료되었습니다.");
			mf.swap(new Option());
		}));

		tmp.setBorder(new LineBorder(Color.black));
	}

	class Item extends BasePage {
		People people;
		JComboBox<String> com = new JComboBox<String>("남,여".split(","));
		JTextField txt[] = new JTextField[3];

		public Item(People people) {
			this.people = people;

			add(lbl(division[people.div] + ++cnt[people.div-1], 2, 30), "North");
			add(c = new JPanel(new GridLayout(0, 1, 5, 5)));

			var cap = "성별,이름(영문),생년월일".split(",");
			for (int i = 0; i < cap.length; i++) {
				var tmp = new JPanel(new BorderLayout());

				tmp.add(sz(lbl(cap[i], 2, 12), 80, 20), "West");

				if (i == 0) {
					tmp.add(com);
				} else if (i == 1) {
					tmp.add(txt[0] = new JTextField());
					tmp.add(txt[1] = new JTextField(20), "East");

					txt[0].setToolTipText("성을 입력해주세요.");
					txt[1].setToolTipText("이름을 입력해주세요.");
				} else {
					tmp.add(txt[2] = new JTextField());
				}

				c.add(tmp);
			}

			c.setBorder(new LineBorder(Color.black));
		}
	}
}
