package view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Lookup extends BaseDialog {

	DefaultTableModel m = BaseFrame.model("no,출발지,도착지,도착시간,출발날짜".split(","));
	JTable t = BaseFrame.table(m);

	public Lookup() {
		super("예매조회", 400, 400);
		try {
			BaseFrame.dataInit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		add(BaseFrame.lbl("예매조회", JLabel.LEFT, 20), "North");
		add(new JScrollPane(t));
		var pop = new JPopupMenu();
		var item = new JMenuItem("취소");
		pop.add(item);
		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (t.getSelectedRow() == -1)
					return;
				if (e.getButton() == 3) {
					pop.show(t, e.getX() - 15, e.getY() - 15);
				}
				super.mousePressed(e);
			}
		});

		item.addActionListener(a -> {
			if (t.getSelectedRow() == -1) {
				return;
			}

			var p = LocalTime.parse(t.getValueAt(t.getSelectedRow(), 3).toString());
			var q = LocalDateTime.parse(t.getValueAt(t.getSelectedRow(), 4).toString(),
					DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			if (q.isBefore(LocalDateTime.now())) {
				BaseFrame.eMsg("취소 불가능한 일정입니다.");
				return;

			}

			int point = 0;
			if (p.getHour() <= 1) {
				point += 100;
			} else if (p.getHour() <= 2) {
				point += 300;
			} else if (p.getHour() <= 3) {
				point += 500;
			} else {
				point += 700;
			}
			BaseFrame.execute("update user set point = point + " + point + " where no = " + BaseFrame.uno);
			BaseFrame.iMsg("예매 취소가 완료되었습니다.");
			BaseFrame.execute("delete from reservation where no  = " + t.getValueAt(t.getSelectedRow(), 0));
			setData();
		});

		setData();
	}

	void setData() {

		m.setRowCount(0);
		try {
			var rs = BaseFrame.stmt.executeQuery(
					"select r.no, s.departure_location2_no , s.arrival_location2_no,  time(date_add(s.date, interval elapsed_time hour_second)), s.date from reservation r inner join schedule s on r.schedule_no = s.no and r.user_no = "
							+ BaseFrame.uno);
			while (rs.next()) {
				Object row[] = new Object[m.getColumnCount()];
				row[0] = rs.getInt(1);
				row[1] = BaseFrame.loc2List.get(rs.getInt(2)) + " "
						+ BaseFrame.loc1List.get(BaseFrame.locDim[rs.getInt(2)]);
				row[2] = BaseFrame.loc2List.get(rs.getInt(3)) + " "
						+ BaseFrame.loc1List.get(BaseFrame.locDim[rs.getInt(3)]);
				row[3] = rs.getString(4);
				row[4] = rs.getString(5);
				m.addRow(row);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
