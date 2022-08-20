package view;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

public class MyPage extends BaseFrame {
	DefaultTableModel m1 = model("번호,회원번호,영화제목,영화관,등급,포맷,인원수,좌석번호,상영날짜,상영시간".split(",")),
			m2 = model("번호,회원번호,상품이름,상품정보,상품가격,구매날짜".split(","));
	JTable t = table(m1);
	JComboBox<String> com = new JComboBox<String>("예매,스토어".split(","));
	String sql[] = {
			"select r_no, u_no, m_name,t_name, r_division, p_name, r_people, r_seat, sc_date, sc_time from reservation r, schedule sc, movie m, theater t, pomaes p where r.sc_no = sc.sc_no and sc.t_no = t.t_no and sc.p_no = p.p_no and sc.m_no = m.m_no and r.u_no = ?",
			"select o_no, u_no, s_name, s_explanation, format(s_price, '#,##0'), o_date from orderlist o, store s where o.s_no = s.s_no and u_no = ?" };

	public MyPage() {
		super("마이페이지", 800, 350);

		user = getRows("select * from user where u_no = 9").get(0);

		add(n = new JPanel(new BorderLayout()), "North");
		add(new JScrollPane(t));

		n.add(lbl("예매 내역", 2, 20), "West");
		n.add(com, "East");

		com.addActionListener(a -> data());

		data();

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				var pop = new JPopupMenu();
				var i1 = new JMenuItem("삭제");
				var i2 = new JMenuItem("수정");

				i1.addActionListener(a -> {
					execute("delete from " + (com.getSelectedIndex() == 0 ? "reservation" : "orderlist") + " where "
							+ (com.getSelectedIndex() == 0 ? "r_no" : "o_no") + "=?",
							t.getValueAt(t.getSelectedRow(), 0));
					data();
				});
				i2.addActionListener(a -> {
					execute("delete from reservation where r_no = ? ", t.getValueAt(t.getSelectedRow(), 0));
					data();
				});

				if (com.getSelectedIndex() == 0) {
					var ldt = LocalDateTime.of(LocalDate.parse(t.getValueAt(t.getSelectedRow(), 8).toString()),
							LocalTime.parse(t.getValueAt(t.getSelectedRow(), 9).toString().replace("\r\n", ""),
									DateTimeFormatter.ofPattern("HH:mm")));

					pop.add(ldt.isBefore(LocalDateTime.now()) ? i1 : i2);
				} else {
					pop.add(i1);
				}

				pop.show(t, e.getX(), e.getY());
			}

		});

		setVisible(true);
	}

	private void data() {
		(com.getSelectedIndex() == 0 ? m1 : m2).setRowCount(0);

		for (var rs : getRows(sql[com.getSelectedIndex()], user.get(0))) {
			if (com.getSelectedIndex() == 0) {
				rs.set(4, Stream.of(rs.get(4).toString().split(", ")).map(n -> div[toInt(n)])
						.collect(Collectors.joining(", ")));
			}

			(com.getSelectedIndex() == 0 ? m1 : m2).addRow(rs.toArray());
		}

		t.setModel((com.getSelectedIndex() == 0 ? m1 : m2));

		var r = new DefaultTableCellRenderer();
		r.setHorizontalAlignment(0);
		for (int i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(r);
		}

		t.repaint();
		t.revalidate();
	}

	public static void main(String[] args) {
		new MyPage();
	}
}
