package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
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

	DefaultTableModel m1 = model("구분,백신 종류,병원,가격".split(","), false);
	DefaultTableModel m2 = model("병원 이름,내용,평점".split(","), false);
	JTable t1 = table(m1);
	JTable t2 = table(m2);
	JTextField txt[] = new JTextField[4];
	JComboBox<String> com;

	public ProfilePage() {
		user = map("select * from user where no=?", 1).get(0);
		ui();
	}

	private void ui() {
		setLayout(new GridLayout(1, 0, 10, 10));

		setBorder(new EmptyBorder(10, 10, 10, 10));

		add(w = new JPanel(new GridLayout(0, 1)));
		add(c = new JPanel(new BorderLayout(5, 5)));

		{
			w.add(lbl("Profile", 0, 30));
			var rs = rs("select u.id,u.name,u.phone,u.resident,b.name from building b, user u where b.no = u.building and u.no=?", user.get("no")).get(0);
			var cap = "아이디,이름,전화번호,생년월일,거주기".split(",");
			for (int i = 0; i < cap.length; i++) {
				w.add(lbl(cap[i], 2, 15));

				if (i == 4) {
					w.add(com = new JComboBox<String>(rs("select name from building where type=2").stream()
							.flatMap(a -> a.stream()).toArray(String[]::new)));
					com.setSelectedItem(rs.get(i)+"");
				} else {
					w.add(txt[i] = new JTextField());
					txt[i].setText(rs.get(i)+"");
					txt[i].setEnabled(i != 3);
				}
			}

			var tmp = new JPanel(new FlowLayout(0));
			for (var c : "수정,취소".split(",")) {
				tmp.add(btn(c, a -> {
					if (c.equals("수정")) {
						for (var t : txt) {
							if (t.getText().isEmpty()) {
								eMsg("빈칸이 있습니다.");
							}
						}

						if (!txt[2].getText().matches("^\\d{3}-\\d{4}-\\d{4}$")) {
							eMsg("전화번호를 확인해주세요.");
							return;
						}
						
						iMsg("수정이 완료되었습니다.");
						var row = new ArrayList<>();
						for (int i = 0; i < 3; i++) {
							row.add(txt[i].getText());
						}
						row.add(com.getSelectedIndex() + 1);
						row.add(user.get("no"));
						execute("update user set id=?,name=?,phone=?,building=? where no=?", row.toArray());
					} else {
						mf.swap(new MainPage());
					}
				}));
			}

			w.add(tmp);

			w.setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(10, 10, 10, 10)));
		}

		{
			c.add(new JScrollPane(t1));
			c.add(new JScrollPane(t2), "South");

			for (var rs : rs(
					"select p.no, v.name, b.name, format(v.price, '#,##0') from purchase p, vaccine v, building b where p.vaccine = v.no and p.building = b.no and user=?",
					user.get("no"))) {
				var row = new ArrayList<>();
				row.add(rs.get(0) + "차 접종");
				row.add(rs.get(1));
				row.add(rs.get(2));
				row.add(rs.get(3) + "원");

				m1.addRow(row.toArray());
			}

			for (var rs : rs(
					"select b.name, r.review, r.rate from rate r, building b where r.building = b.no and r.user=?",
					user.get("no"))) {
				var row = new ArrayList<>();
				row.add(rs.get(0));
				row.add(rs.get(1).toString());
				row.add(rs.get(2));

				m2.addRow(row.toArray());
			}
		}
	}

	public static void main(String[] args) {
		mf.swap(new ProfilePage());
		mf.setVisible(true);
	}
}
