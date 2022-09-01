package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class ProfilePage extends BasePage {
	DefaultTableModel m1 = model("병원이름,내용,평점".split(",")), m2 = model("구분,백신종류,병원,가격".split(","));
	JTable t1 = table(m1), t2 = table(m2);
	JTextField txt[] = new JTextField[4];
	JComboBox<String> com = new JComboBox<>(getRows("select name from building where type = 2").stream()
			.map(a -> a.get(0).toString()).toArray(String[]::new));

	public ProfilePage() {
		user = getRows("select * from user where no = 1").get(0);

		ui();
		data();
	}

	private void data() {
		var idx = new int[] { 2, 1, 4, 5 };
		for (int i = 0; i < 4; i++) {
			txt[i].setText(user.get(idx[i]).toString());
		}

		com.setSelectedItem(getOne("select name from building where no = ?", user.get(6)));

		for (var rs : getRows(
				"select b.name, r.review, r.rate from building b, rate r where b.no = r.building and r.user = ?",
				user.get(0))) {
			m1.addRow(rs.toArray());
		}

		for (var rs : getRows(
				"select shot, v.name, b.name, format(v.price, '#,##0') from purchase p, vaccine v, building b where p.building = b.no and p.vaccine = v.no and p.user = ?",
				user.get(0))) {
			rs.set(0, rs.get(0) + "차 접종");
			rs.set(3, rs.get(3) + "원");
			m2.addRow(rs.toArray());
		}
	}

	private void ui() {
		setLayout(new BorderLayout(5, 5));

		add(w = sz(new JPanel(new GridLayout(0, 1, 5, 5)), 450, 0), "West");
		add(c = new JPanel(new BorderLayout()));
		add(lbl("메인으로", 2, 0, 15, Color.orange, e -> {
		}), "South");

		w.add(lbl("Profile", 0, 20));

		var cap = "아이디,이름,전화번호,생년월일,거주지".split(",");
		for (int i = 0; i < cap.length; i++) {
			w.add(lbl(cap[i], 2, 15));

			if (i == 4) {
				w.add(com);
			} else {
				w.add(txt[i] = new JTextField());
				txt[i].setEditable(i == 1 || i == 2);
			}
		}

		w.add(btn("수정", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty()) {
					eMsg("빈킨이 있습니다.");
					return;
				}
			}

			if (!txt[2].getText().matches("\\d{3}-\\d{4}-\\d{4}")) {
				eMsg("전화번혹 형식이 잘못되었습니다.");
				return;
			}

			iMsg("정보가 수정되었습니다.");
			execute("update user set name = ?, phone=?, building = ? where no = ?", txt[1].getText(), txt[2].getText(),
					getOne("select no from building where name = ?", com.getSelectedItem()), user.get(0));
		}));

		c.add(new JScrollPane(t1));
		c.add(sz(new JScrollPane(t2), 0, 100), "South");

		setBorder(new EmptyBorder(20, 20, 20, 20));
		w.setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(5, 5, 5, 5)));
	}
}
