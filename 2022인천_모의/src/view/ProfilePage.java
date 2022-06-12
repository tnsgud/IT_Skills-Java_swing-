package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

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

	JTextField txt[] = new JTextField[4];
	JComboBox<String> com = new JComboBox<String>(
			getRows("select name from building where type = 2").stream().flatMap(a -> a.stream()).toArray(String[]::new));

	DefaultTableModel m1 = model("병원 이름,내용,평점".split(",")), m2 = model("구분,백신 종류,병원,가격".split(","));
	JTable t1 = table(m1), t2 = table(m2);

	public ProfilePage() {
		user = getRows("select * from user where no=1").get(0);

		setLayout(new BorderLayout(5, 5));

		add(w = new JPanel(new GridLayout(0, 1, 5, 5)));
		add(c = new JPanel(new BorderLayout(5, 5)), "East");
		add(hyplbl("메인으로", 2, 15, Color.orange, () -> mf.swapPage(new MainPage())), "South");

		w.add(lbl("Profile", 0, 30));

		var getRows = getRows(
				"select id, u.name, phone, birth, b.type from user u, building b where u.building = b.no and u.no=?",
				user.get(0)).get(0);
		var cap = "아이디,이름,전화번호,생년월일,거주기".split(",");
		for (int i = 0; i < cap.length; i++) {
			w.add(lbl(cap[i], 2, 15));

			if (i == 4) {
				w.add(com);
				com.setSelectedIndex(toInt(getRows.get(i)));
			} else {
				w.add(txt[i] = new JTextField());
				txt[i].setRequestFocusEnabled(i != 3);
				txt[i].setText(getRows.get(i) + "");
			}
		}

		w.add(btn("수정", a -> {
			var data = new ArrayList<>();
			Stream.of(txt).filter(t -> Arrays.asList(txt).indexOf(t) != 3).forEach(t -> data.add(t.getText()));
			data.add(user.get(0));
			execute("update user set id=?, name=?, phone=? where no=?", data.toArray());
		}));

		c.add(new JScrollPane(t1));
		c.add(new JScrollPane(t2), "South");

		for (var r : getRows("select b.name, r.review, r.rate from rate r, building b where r.building = b.no and user=?",
				user.get(0))) {
			m1.addRow(r.toArray());
		}

		for (var r : getRows(
				"select p.shot, v.name, b.name, format(v.price, '#,##0') from purchase p, vaccine v, building b where p.building = b.no and p.vaccine = v.no and p.user =?",
				user.get(0))) {
			r.set(0, r.get(0) + "차");
			r.set(3, r.get(3) + "원");
			m2.addRow(r.toArray());
		}

		setBorder(new EmptyBorder(20, 20, 20, 20));
		w.setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(5, 5, 5, 5)));
	}
}
