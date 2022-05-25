package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Iterator;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class MyPage extends BaseFrame {
	JComboBox<String> com = new JComboBox<>();
	DefaultTableModel m = model("rno,날짜,시간,카페 이름,테마명,인원수,가격".split(","));
	JTable t = table(m);
	JLabel price;
	int tot = 0;

	public MyPage() {
		super("마이페이지", 750, 350);

		ui();
		data();
		event();

		setVisible(true);
	}

	private void event() {
		com.addActionListener(a -> {
			if (getOne("select * from reservation where month(r_date) = ?",
					com.getSelectedIndex() == 0 ? true : com.getSelectedIndex()).isEmpty()) {
				eMsg("예약현황이 없습니다.");
				return;
			}
			data();
		});
	}

	private void data() {
		addRow(m,
				"select r_no, r_date, r_time, c_name, t_name, r_people, format(c_price * r_people, '#,##0') as price  from reservation r, cafe c, theme t where r.u_no=? and c.c_no = r.c_no and t.t_no = r.t_no "
						+ (com.getSelectedIndex() == 0 ? "" : "and month(r_date) = " + com.getSelectedIndex()),
				uno);
		for (int i = 0; i < m.getRowCount(); i++) {
			tot += toInt(getOne("select (c_price * r_people) from reservation r, cafe c where r.c_no = c.c_no and r.r_no=?",
					t.getValueAt(i, 0)));
		}
		price.setText("총 금액:" + format.format(tot));
	}

	private void ui() {
		setLayout(new BorderLayout(5, 5));

		var nw = new JPanel(new FlowLayout(0));

		add(n = new JPanel(new BorderLayout()), "North");
		add(new JScrollPane(t));
		add(price = lblB("", 4, 15), "South");

		n.add(nw, "West");
		n.add(btn("삭제", a -> {
			if (t.getSelectedRow() == -1) {
				eMsg("삭제할 레코드를 선택하세요.");
				return;
			}

			if (LocalDate.parse(t.getValueAt(t.getSelectedRow(), 1) + "").isBefore(now)) {
				eMsg("지난 예약은 삭제할 수 없습니다.");
				return;
			}

			iMsg("삭제가 완료되었습니다.");
			execute("delete from reservation where r_no=?", t.getValueAt(t.getSelectedRow(), 0));
			data();
		}), "East");

		nw.add(lblB("날짜 :", 2, 15));
		nw.add(com);

		com.addItem("전체");
		for (int i = 0; i < 12; i++) {
			com.addItem(i + 1 + "월");
		}

		t.getColumnModel().getColumn(0).setMinWidth(0);
		t.getColumnModel().getColumn(0).setMaxWidth(0);

		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
	}

	public static void main(String[] args) {
		uno = 1;
		new MyPage();
	}
}
