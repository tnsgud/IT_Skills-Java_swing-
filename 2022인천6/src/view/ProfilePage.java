package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

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
	DefaultTableModel m1 = model("병원이름,내용,평점".split(",")), m2 = model("구분,백신 종류,병원,가격".split(","));
	JTable t1 = table(m1), t2 = table(m2);
	JTextField txt[] = new JTextField[4];
	JComboBox<String> com = new JComboBox<>(
			getRows("select name from building where type=2").stream().flatMap(a -> a.stream()).toArray(String[]::new));

	public ProfilePage() {
		user = getRows("select * from user where no=1").get(0);

		setLayout(new BorderLayout(20, 0));
		setBorder(new EmptyBorder(10, 20, 10, 20));

		add(w = sz(new JPanel(new GridLayout(0, 1, 5, 5)), 450, 0), "West");
		add(c = new JPanel(new BorderLayout()));
		add(hyplbl("메인으로", 2, 20, Color.orange, e -> mf.swapPage(new MainPage())), "South");

		w.setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(5, 5, 5, 5)));

		w.add(lbl("Profile", 0, 25));

		var cap = "아이디,이름,전화번호,생년월일,거주지".split(",");
		for (int i = 0; i < cap.length; i++) {
			w.add(lbl(cap[i], 2, 20));

			if (i == 4) {
				w.add(com);
			} else {
				w.add(txt[i] = new JTextField());

				if (i == 0 || i == 3) {
					txt[i].setEditable(false);
				}
			}
		}
		w.add(btn("수정", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty()) {
					eMsg("빈칸이 있습니다.");
					return;
				}
			}

			if (!txt[2].getText().matches("^\\d{3}-\\d{4}-\\d{4}$")) {
				eMsg("전화번호 형식이 잘못되었습니다.");
				return;
			}

			iMsg("수정이 완료되었습니다.");
			execute("update user set name=?, phone=?, building=? where no = ?", txt[1].getText(), txt[2].getText(),
					com.getSelectedIndex() + 1, user.get(0));
		}));

		var rs = getRows(
				"select id, u.name, phone, birth, b.name from user u, building b where u.building = b.no and u.no = ?",
				user.get(0)).get(0);
		for (var r : rs) {
			var idx = rs.indexOf(r);

			if (idx == 4) {
				com.setSelectedItem(r.toString());
			} else {
				txt[idx].setText(r.toString());
			}
		}

		c.add(new JScrollPane(t1));
		c.add(sz(new JScrollPane(t2), 0, 100), "South");

		for (var r : getRows(
				"select b.name, review, round(avg(rate), 0) from building b, rate r where b.no = r.building and r.user=? group by b.no",
				user.get(0))) {
			m1.addRow(r.toArray());
		}

		for (var r : getRows(
				"select shot, v.name, b.name, format(price, '#,##0') from building b, vaccine v, purchase p where b.no = p.building and v.no = p.vaccine and p.user=?",
				user.get(0))) {
			r.set(0, r.get(0) + "차 접종");
			r.set(3, r.get(3) + "원");
			m2.addRow(r.toArray());
		}
	}

	public static void main(String[] args) {
		mf.swapPage(new ProfilePage());
		mf.setVisible(true);
	}
}
