package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.stream.Stream;

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
	JComboBox com = new JComboBox<>("전체".split(" "));
	JLabel price;

	public MyPage() {
		super("마이페이지", 800, 300);

		setLayout(new BorderLayout(5, 5));

		add(n = new JPanel(new BorderLayout()), "North");
		add(new JScrollPane(t));
		add(price = lbl("총 금액", 4, 12), "South");

		n.add(nw = new JPanel(new FlowLayout(0)), "West");
		n.add(btn("삭제", a -> {
			if (t.getSelectedRow() == -1) {
				eMsg("삭제할 레코드를 선택하세요.");
				return;
			}

			if (LocalDate.parse(t.getValueAt(t.getSelectedRow(), 0) + "").isBefore(LocalDate.now())) {
				eMsg("지난 예역은 삭제할 수 없습니다.");
				return;
			}

			iMsg("삭제가 완료되었습니다.");
			execute("delete from reservation where r_no=?", t.getValueAt(t.getSelectedRow(), 6));
			load();
		}), "East");
		nw.add(lbl("날짜 :", 0, 12));
		nw.add(com);
		com.addActionListener(a -> {
			load();
		});
		for (int i = 0; i < 12; i++) {
			com.addItem(i + 1 + "월");
		}

		load();

		t.getColumn("rno").setMinWidth(0);
		t.getColumn("rno").setMaxWidth(0);

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		setVisible(true);
	}

	private void load() {
		m.setRowCount(0);
		var rs = rs(
				"select r_date, r_time, c_name, t_name, r_people, format(r_people * c_price, '#,##0'), r.r_no  from reservation r, user u,cafe c, genre g, theme t where c.c_no = r.c_no and g.g_no=t.g_no and t.t_no = r.t_no and u.u_no=r.u_no and u.u_no=? "
						+ (com.getSelectedIndex() == 0 ? "" : "and month(r_date) =" + com.getSelectedIndex()),
				user.get(0));
		if (rs.isEmpty()) {
			eMsg("예약현황이 없습니다.");
			com.setSelectedIndex(0);
			load();
			return;
		}
		int tot = 0;
		for (var r : rs) {
			m.addRow(r.toArray());
			tot += toInt(r.get(5));
		}
		price.setText("총 금액 : " + new DecimalFormat("#,##0").format(tot));
	}

	public static void main(String[] args) {
		new MyPage();
	}
}
