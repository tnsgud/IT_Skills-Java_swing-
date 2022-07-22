package view;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;

import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class MyPage extends BaseFrame {
	JComboBox<String> com = new JComboBox<String>("예매,스토어".split(","));
	DefaultTableModel m1 = model("번호,회원번호,영화제목,영화관,등급,포멧,인원수,석번호,상영날짜,상여시간".split(","));
	DefaultTableModel m2 = model("번호,회원번호,상품이름,상품정보,상품가격,구매날짜".split(","));
	JTable t = table(m1);
	JScrollPane scr = new JScrollPane(t);

	public MyPage() {
		super("마이페이지", 800, 350);

		add(n = new JPanel(new BorderLayout()), "North");
		add(scr);

		n.add(lbl("예매 내역", 2, 20), "West");
		n.add(com, "East");

		com.addActionListener(a -> search());

		search();

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() != 3 || t.getSelectedRow() == -1)
					return;

				var pop = new JPopupMenu();
				var i1 = new JMenuItem("삭제");
				var i2 = new JMenuItem("취소");

				i1.addActionListener(a -> {
					if (com.getSelectedIndex() == 0) {
						execute("delete from reservation where r_no = ?", t.getValueAt(t.getSelectedRow(), 0));
						m1.removeRow(t.getSelectedRow());
					} else {
						execute("delete from orderlist where o_no = ?", t.getValueAt(t.getSelectedRow(), 0));
						m2.removeRow(t.getSelectedRow());
					}
				});
				i2.addActionListener(a -> {
					execute("delete from reservation where r_no = ?", t.getValueAt(t.getSelectedRow(), 0));
					m1.removeRow(t.getSelectedRow());
				});

				if (t.getModel() == m1) {
					pop.add(LocalDateTime.of(LocalDate.parse(t.getValueAt(t.getSelectedRow(), 8).toString()),
							LocalTime.parse("09:00:00")).isBefore(LocalDateTime.now()) ? i1 : i2);
				} else {
					pop.add(i1);
				}

				pop.show(t, e.getX(), e.getY());
			}
		});

		setVisible(true);
	}

	void search() {
		(com.getSelectedIndex() == 0 ? m1 : m2).setRowCount(0);

		String sql = "";
		if (com.getSelectedIndex() == 0) {
			sql = "SELECT r.r_no, r.u_no, m.m_name, t.t_name, r.r_division, p.p_name, r.r_people, r.r_seat, sc.sc_date, sc.sc_time FROM movie.reservation r, schedule sc, movie m, theater t, pomaes p where r.sc_no = sc.sc_no and sc.t_no = t.t_no and sc.m_no = m.m_no and sc.p_no = p.p_no and r.u_no=?";
		} else {
			sql = "SELECT o.o_no, o.u_no, s.s_name, s.s_explanation, s.s_price, o.o_date FROM movie.orderlist o, store s where o.s_no = s.s_no and o.u_no=?";
		}
		var rs = getRows(sql, user.get(0));
		for (var r : rs) {
			var row = new Object[(com.getSelectedIndex() == 0 ? m1.getColumnCount() : m2.getColumnCount())];

			for (int i = 0; i < row.length; i++) {
				row[i] = r.get(i);
				if (com.getSelectedIndex() == 0 && i == 4) {
					String div[] = row[i].toString().split(", ");
					row[i] = String.join(",", Arrays.stream(div).map(x -> division[toInt(x)]).toArray(String[]::new));
				}
			}

			(com.getSelectedIndex() == 0 ? m1 : m2).addRow(row);
		}

		t.setModel(com.getSelectedIndex() == 0 ? m1 : m2);

		scr.repaint();
		scr.revalidate();
	}

	public static void main(String[] args) {
		new Main();
	}
}
