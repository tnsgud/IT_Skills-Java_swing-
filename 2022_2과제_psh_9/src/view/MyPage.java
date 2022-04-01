package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class MyPage extends BaseFrame {
	DefaultTableModel m = model("날짜,시간,카페 이름,테마명,인원수,가격,rno".split(","));
	JTable t = table(m, "");
	JComboBox com = new JComboBox<>("전체".split(";"));
	JLabel price = lbl("", 4, 14);

	public MyPage() {
		super("마이페이지", 900, 400);

		setLayout(new BorderLayout(10, 10));

		add(n = new JPanel(new BorderLayout()), "North");
		add(new JScrollPane(t));
		add(price, "South");

		n.add(nw = new JPanel(new FlowLayout(0)), "West");
		n.add(btn("삭제", a -> {
			if (t.getSelectedRow() == -1) {
				eMsg("삭제할 레코드를 선택하세요.");
				return;
			}

			var date = LocalDate.parse(t.getValueAt(t.getSelectedRow(), 0) + "");
			var time = LocalTime.parse(t.getValueAt(t.getSelectedRow(), 1) + "");

			if (LocalDateTime.of(date, time).isBefore(LocalDateTime.now())) {
				eMsg("지난 예약은 삭제할 수 없습니다.");
				return;
			}

			iMsg("삭제가 완료되었습니다.");
			execute("delete from reservation where r_no=?", t.getValueAt(t.getSelectedRow(), 6));
			data();
		}), "East");
		nw.add(lbl("날짜 :", 2, 12));
		nw.add(com);
		com.addActionListener(a -> data());

		t.getColumn("rno").setMinWidth(0);
		t.getColumn("rno").setMaxWidth(0);

		for (int i = 0; i < 12; i++) {
			com.addItem(i + 1 + "월");
		}

		data();

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		setVisible(true);
	}

	private void data() {
		m.setRowCount(0);

		var t = com.getSelectedIndex() == 0;
		var rs = rs(
				"select r_date, r_time, c_name, t_name, r_people, format(r_people * c_price, '#,##0'), r_no from cafe c, reservation r, theme t where t.t_no=r.t_no and r.c_no = c.c_no and u_no=? "
						+ (t ? "" : "and month(r_date) = " + com.getSelectedIndex()),
				user.get(0));
		if (rs.isEmpty() && !t) {
			eMsg("예약현황이 없습니다.");
			com.setSelectedIndex(0);
			data();
			return;
		}

		int tot = 0;
		for (var r : rs) {
			m.addRow(r.toArray());
			tot += toInt(r.get(5));
		}

		price.setText("총 금액 : " + new DecimalFormat("#,##0").format(tot));
	}
}
