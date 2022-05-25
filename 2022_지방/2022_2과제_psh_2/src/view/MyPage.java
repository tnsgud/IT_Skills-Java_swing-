package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class MyPage extends BaseFrame {
	JComboBox<String> com = new JComboBox<String>();
	DefaultTableModel m = model("날짜,시간,카페 이름,테마명,인원수,가격,rno".split(","));
	JTable t = table(m, "");
	JLabel price;
	int tot = 0;

	public MyPage() {
		super("마이페이지", 750, 350);

		ui();
		data();
		com.addActionListener(a -> data());

		setVisible(true);
	}

	private void data() {
		tot = 0;
		var rs = getResult(
				"select r_date, r_time, c_name, t_name, r_people, format(c_price * r_people, '#,##0') as priced from reservation r, cafe c, theme t where r.u_no=? and c.c_no = r.c_no and t.t_no = r.t_no "
						+ (com.getSelectedIndex() == 0 ? "" : "and month(r_date)=" + com.getSelectedIndex()),
				uno);
		if (rs.isEmpty()) {
			eMsg("예약현황이 없습니다.");
			com.setSelectedIndex(0);
			data();
			return;
		}
		addRow(m, rs);
		for (var r : rs) {
			tot += toInt(r.get(5));
		}
		price.setText("총 금액 :" + format.format(tot));
	}

	private void ui() {
		setLayout(new BorderLayout(5, 5));

		add(n = new JPanel(new BorderLayout()), "North");
		add(new JScrollPane(t));
		add(price = lbl("", 4, 12), "South");

		n.add(nw = new JPanel(new FlowLayout(1)), "West");
		n.add(btn("삭제", a -> {
			if (t.getSelectedRow() == -1) {
				eMsg("삭제할 레코드를 선택하세요.");
				return;
			}

			if (LocalDate.now().isBefore(LocalDate.parse(t.getValueAt(t.getSelectedRow(), 0).toString()))) {
				eMsg("지난 예약은 삭제할 수 없습니다.");
				return;
			}
			
			iMsg("삭제가 완료되었습니다.");
			execute("delete from reservation where r_no=?", t.getValueAt(t.getSelectedRow(), 6));
			data();
		}), "East");

		nw.add(lbl("날짜 :", 0, 15));
		nw.add(com);

		var cap = "날짜,시간,인원수,가격".split(",");
		for (int i = 0; i < cap.length; i++) {
			t.getColumn(cap[i]).setMinWidth(80);
			t.getColumn(cap[i]).setMaxWidth(80);
		}
		t.getColumn("rno").setMinWidth(0);
		t.getColumn("rno").setMaxWidth(0);

		com.addItem("전체");
		for (int i = 0; i < 12; i++) {
			com.addItem((i + 1) + "월");
		}

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
	}

	public static void main(String[] args) {
		uno = 1;
		new MyPage();
	}
}
