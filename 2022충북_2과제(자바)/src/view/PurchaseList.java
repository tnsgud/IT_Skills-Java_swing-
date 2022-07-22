package view;

import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class PurchaseList extends BaseFrame {
	JComboBox<String> com = new JComboBox<>("예매,스토어".split(","));
	DefaultTableModel m1 = model("번호,회원번호,등급,영화제목,인원 수,예약좌석,예약날짜,예약시간".split(","));
	DefaultTableModel m2 = model("번호,회원번호,상품명,구매개수,구매날짜,구매가격".split(","));
	JTable t = table(m1);

	public PurchaseList() {
		super("구매내역", 700, 500);

		add(n = new JPanel(new FlowLayout(0)), "North");
		add(new JScrollPane(t));

		n.add(com);

		search();

		com.addActionListener(a -> {
			t.setModel(com.getSelectedIndex() == 0 ? m1 : m2);

			var r = new DefaultTableCellRenderer();

			r.setHorizontalAlignment(0);

			for (int i = 0; i < t.getColumnCount(); i++) {
				t.getColumnModel().getColumn(i).setCellRenderer(r);
			}

			search();
		});

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() != 3 || t.getSelectedRow() == -1)
					return;

				var pop = new JPopupMenu();
				var i1 = new JMenuItem("삭제");

				pop.add(i1);

				i1.addActionListener(a -> {
					if (com.getSelectedIndex() == 0) {
						execute("delete from reservation where r_no = ?", t.getValueAt(t.getSelectedRow(), 0));
						m1.removeRow(t.getSelectedRow());
					} else {
						execute("delete from orderlist where o_no = ?", t.getValueAt(t.getSelectedRow(), 0));
						m2.removeRow(t.getSelectedRow());
					}
				});

				pop.show(t, e.getX(), e.getY());
			}
		});

		setVisible(true);
	}

	private void search() {
		m1.setRowCount(0);
		m2.setRowCount(0);

		var sql = "";
		if (com.getSelectedIndex() == 0) {
			sql = "SELECT r.r_no, r.u_no, r.r_division, m.m_name, r.r_people, r.r_seat, r.r_date, r.r_time FROM movie.reservation r, schedule sc, theater t, movie m where r.sc_no = sc.sc_no and sc.m_no = m.m_no group by r.r_no";
		} else {
			sql = "SELECT o.o_no, o.u_no, s.s_name, o.o_count, o.o_date, format(s.s_price, '#,##0') FROM movie.orderlist o, store s where o.s_no = s.s_no order by o.o_no";
		}
		var rs = getRows(sql);
		for (var r : rs) {
			r.set(2, com.getSelectedIndex() == 0 ? Stream.of(r.get(2).toString().split(","))
					.map(x -> division[toInt(x)]).collect(Collectors.joining(",")) : r.get(2));

			(com.getSelectedIndex() == 0 ? m1 : m2).addRow(r.toArray());
		}
	}

	public static void main(String[] args) {
		new PurchaseList();
	}
}
