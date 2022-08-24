package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class ProfilePage extends BasePage {
	DefaultTableModel m1 = model("병원이름,내용,평점".split(",")), m2 = model("구분,백신 종류,병원,가격".split(","));
	JTextField txt[] = new JTextField[4];
	JComboBox<String> com = new JComboBox<String>(getRows("select name from building where type = 2").stream()
			.map(a -> a.get(0).toString()).toArray(String[]::new));
	JTable t1 = table(m1), t2 = table(m2);
	
	public ProfilePage() {
		setLayout(new BorderLayout(20, 0));

		add(w = sz(new JPanel(new GridLayout(0, 1, 5, 5)), 400, 0), "West");
		add(c = new JPanel(new BorderLayout()));
		add(event(lbl("메인으로", 2, 15),  e -> mf.swap(new UserMainPage())), "South");

		c.add(new JScrollPane(t1));
		c.add(sz(new JScrollPane(t2), 0, 100), "South");

		w.add(lbl("Profile", 0, 30));
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

		w.add(btn("확인", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty()) {
					eMsg("빈칸이 존재합니다.");
					return;
				}
			}

			if (!txt[2].getText().matches("\\d{3}-\\d{4}-\\d{4}")) {
				eMsg("전화번호 형식이 잘못되었습니다.");
				return;
			}

			iMsg("숮엉이 완료되었습니다.");
			execute("update user set name =?,phone=?,building=? where no = ?", txt[1].getText(), txt[2].getText(),
					getOne("select no from building where name = ?", com.getSelectedItem()));
		}));

		var idx = new int[] { 2, 1, 4, 5 };
		for (int i = 0; i < idx.length; i++) {
			txt[i].setText(user.get(idx[i]).toString());
		}

		com.setSelectedItem(getOne("select name from building where no = ?", user.get(6)));

		for (var rs : getRows(
				"select b.name, r.review, r.rate from rate r, building b where r.building = b.no and r.user = ?",
				user.get(0))) {
			m1.addRow(rs.toArray());
		}

		for (var rs : getRows(
				"select shot, v.name, b.name, v.price from purchase p, vaccine v, building b where p.building = b.no and p.vaccine = v.no and p.user = ?",
				user.get(0))) {
			rs.set(0, rs.get(0) + "차 접종");
			m2.addRow(rs.toArray());
		}

		w.setBorder(new LineBorder(Color.black));

		setBorder(new EmptyBorder(10, 10, 10, 10));
	}
}
