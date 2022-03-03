package view;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDate;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class MyPage extends BaseFrame {
	DefaultTableModel m = model("tno,날짜,공연명,좌석,금액".split(","));
	JTable t = table(m);
	JScrollPane scr = new JScrollPane(t);

	JPopupMenu pop = new JPopupMenu();
	String[] icap = "취소,수정".split(",");
	JMenuItem item[] = new JMenuItem[icap.length];

	public MyPage() {
		super("마이페이지", 600, 350);

		ui();
		data();
		event();

		setVisible(true);
	}

	private void event() {
		for (int i = 0; i < item.length; i++) {
			item[i].addActionListener(a -> {
				if (a.getActionCommand().equals("취소")) {
					execute("delete from ticket where t_no=?", t.getValueAt(t.getSelectedRow(), 0));
					iMsg("취소되었습니다.");
					data();
				} else {
					if (LocalDate.parse("2021-10-06")
							.isAfter(LocalDate.parse(t.getValueAt(t.getSelectedRow(), 1).toString()))) {
						eMsg("수정이 불가합니다.");
						return;
					}

					new Stage(t.getValueAt(t.getSelectedRow(), 0).toString())
							.addWindowListener(new Before(MyPage.this));
				}
			});
		}
		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(e.getButton() == 3 && t.getSelectedRow() != -1) {
					pop.show(t, e.getX(), e.getY());
				}
			}
		});
	}

	private void data() {
		m.setRowCount(0);

		var rs = rs(
				"select t_no, p_date, p_name, t_seat, t_discount, p_price from ticket t, perform p where t.p_no = p.p_no and t.u_no = ? order by p_date asc",
				uno);
		try {
			while (rs.next()) {
				var row = new Object[t.getColumnCount()];
				for (int i = 0; i < row.length - 1; i++) {
					row[i] = rs.getString(i + 1);
				}

				var price = 0;
				var tdis = rs.getString(5).split(",");
				for (var dis : tdis) {
					var d = toInt(dis);
					price += (int) (rs.getInt(6) * (d == 0 ? 1 : d == 1 ? 0.8 : d == 2 ? 0.6 : 0.5));
				}
				row[4] = df.format(price);
				m.addRow(row);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void ui() {
		setLayout(new BorderLayout(10, 10));

		add(lbl("회원 : " + getOne("select u_name from user where u_no=?", uno), 2, 35), "North");
		add(scr);

		t.getColumnModel().getColumn(0).setMinWidth(0);
		t.getColumnModel().getColumn(0).setMaxWidth(0);

		for (int i = 0; i < icap.length; i++) {
			item[i] = new JMenuItem(icap[i]);
			pop.add(item[i]);
		}
	}
}
