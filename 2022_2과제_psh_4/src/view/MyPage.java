package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class MyPage extends BaseFrame {
	DefaultTableModel m = model("날짜,시간,카페이름,테마명,인원수,가격,rno".split(","));
	JTable t = table(m, "");
	JComboBox<String> com = new JComboBox<>();
	JLabel price;

	public MyPage() {
		super("마이페이지", 800, 400);

		setLayout(new BorderLayout(5, 5));

		add(n = new JPanel(new BorderLayout()), "North");
		add(new JScrollPane(t));
		add(price = lbl("", 4, 15), "South");

		n.add(nw = new JPanel(new FlowLayout(0)), "West");
		n.add(btn("삭제", a -> {
			if (t.getSelectedRow() == -1) {
				eMsg("삭제할 레코드를 선택하세요.");
				return;
			}

			if (LocalDate.parse(t.getValueAt(t.getSelectedRow(), 0) + "").isBefore(LocalDate.now())) {
				eMsg("지난 예약은 삭제할 수 없습니다.");
				return;
			}

			iMsg("삭제가 완료되었습니다.");
			execute("delete from reservation where r_no=?", t.getValueAt(t.getSelectedRow(), 6));
			dispose();
		}), "East");
		nw.add(lbl("날짜 : ", 2));
		nw.add(com);
		com.addItem("전체");
		for (int i = 0; i < 12; i++) {
			com.addItem(i + 1 + "월");
		}
		com.addItemListener(a -> load());

		load();

		setVisible(true);
	}

	private void load() {
		m.setRowCount(0);
		int tot = 0;
		var rs = rs(
				"select r_date, r_time, c_name, t_name, r_people, format(c_price * r_people, '#,##0'), r_no from reservation r, cafe c, theme t where r.c_no = c.c_no and r.t_no = t.t_no and u_no=? "
						+ (com.getSelectedIndex() == 0 ? "" : "and month(r_date) = " + com.getSelectedIndex()),
				user.get(0));
		for (var r : rs) {
			tot += toInt(r.get(5));
			m.addRow(r.toArray());
		}

		price.setText("총 금액:" + format.format(tot));
	}

	public static void main(String[] args) {
		user.add(1);
		new MyPage();
	}
}

