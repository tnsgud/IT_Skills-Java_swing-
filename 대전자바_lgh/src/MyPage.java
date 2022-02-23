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

	JPopupMenu pop = new JPopupMenu();
	JMenuItem item[] = { new JMenuItem("취소"), new JMenuItem("수정") };

	DefaultTableModel m = model("날짜,공연명,좌석,금액,tno".split(","));
	JTable t = table(m);
	JScrollPane jsc = new JScrollPane(t);

	public MyPage() {
		super("검색", 500, 500);

		this.add(lbl("회원 : " + getone("select u_name from user where u_no = " + uno), 2, 30), "North");
		this.add(jsc);

		try {
			var rs = stmt.executeQuery(
					"SELECT p.p_date, p.p_name, t.t_seat, p.p_price, t.t_discount, t.t_no FROM 2021전국.ticket t, perform p where t.p_no = p.p_no and t.u_no = "
							+ uno + " group by p.p_no order by p.p_date");
			int tot = 0;
			while (rs.next()) {
				var row = new Object[m.getColumnCount()];
				row[0] = rs.getString(1);
				row[1] = rs.getString(2);
				row[2] = rs.getString(3);

				tot = 0;
				String dis[] = rs.getString(5).trim().split(",");

				for (int i = 0; i < dis.length; i++) {
					tot += rs.getInt(4)
							* (toInt(dis[i]) == 0 ? 1 : toInt(dis[i]) == 1 ? 0.8 : toInt(dis[i]) == 2 ? 0.6 : 0.5);
				}
				row[3] = df.format(tot);
				row[4] = rs.getInt(6);
				m.addRow(row);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		pop.add(item[0]);
		pop.add(item[1]);

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == 3) {
					pop.show(t, e.getX(), e.getY());
				}
			}
		});

		item[0].addActionListener(e -> {
			execute("delete from ticket where t_no = " + t.getValueAt(t.getSelectedRow(), 4));
			m.removeRow(t.getSelectedRow());
			iMsg("취소되었습니다.");
		});

		item[1].addActionListener(e -> {
			if (LocalDate.parse(t.getValueAt(t.getSelectedRow(), 0).toString()).toEpochDay() < LocalDate.now()
					.toEpochDay()) {
				eMsg("수정이 불가합니다.");
				return;
			} else {
				pno = getone("select p_no from perform where p_name = '" + t.getValueAt(t.getSelectedRow(), 1) + "'");
				new Stage(t.getValueAt(t.getSelectedRow(), 4).toString()).addWindowListener(new Before(MyPage.this));
			}
		});

		this.setVisible(true);
	}

	public static void main(String[] args) {
		uno = "1";
		new MyPage();
	}
}
