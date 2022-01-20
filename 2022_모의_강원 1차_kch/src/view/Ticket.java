package view;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class Ticket extends BaseDialog {

	DefaultTableModel m = BaseFrame.model("no,출발지,도착지,도착시간,출발날짜,s_no,걸린시간".split(","));
	JTable t = BaseFrame.table(m);

	JPopupMenu menu = new JPopupMenu();
	JMenuItem item = new JMenuItem("취소");

	public Ticket() {
		super("예매조회", 600, 400);
		add(BaseFrame.lbl("예매조회", JLabel.LEFT, 20), "North");
		add(new JScrollPane(t));
		setData();

		t.getColumnModel().getColumn(0).setMaxWidth(100);
		t.getColumnModel().getColumn(5).setMinWidth(0);
		t.getColumnModel().getColumn(5).setMaxWidth(0);
		t.getColumnModel().getColumn(6).setMinWidth(0);
		t.getColumnModel().getColumn(6).setMaxWidth(0);
		t.setComponentPopupMenu(menu);
		menu.add(item);
		item.addActionListener(a -> {
			if (t.getSelectedRow() == -1)
				return;

			var dv = t.getValueAt(t.getSelectedRow(), 4).toString();
			var dtv = LocalDateTime.parse(dv, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			if (dtv.isBefore(LocalDateTime.now())) {
				BaseFrame.eMsg("취소 불가능한 일정입니다.");
				return;
			}

			int point = 700;

			var et = t.getValueAt(t.getSelectedRow(), 6).toString();
			var let = LocalTime.parse(et, DateTimeFormatter.ofPattern("HH:mm:ss"));

			if (let.getHour() <= 3)
				point = 500;

			if (let.getHour() <= 2)
				point = 300;

			if (let.getHour() <= 1)
				point = 100;
			BaseFrame.execute("delete from reservation where user_no = " + BaseFrame.uno + " and schedule_no = "
					+ t.getValueAt(t.getSelectedRow(), 5));
			BaseFrame.execute("update user set point = point + " + point + " where no = " + BaseFrame.uno);

			BaseFrame.iMsg("예매 취소가 완료되었습니다.");
		});
		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
	}

	void setData() {
		m.setRowCount(0);
		try {
			BaseFrame.dataInit();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			var rs = BaseFrame.stmt.executeQuery(
					"select s.no, s.departure_location2_no , s.arrival_location2_no, time(date_add(s.date, interval elapsed_time hour_second)), s.date, elapsed_time from reservation r inner join schedule s on r.schedule_no = s.no and r.user_no = "
							+ BaseFrame.uno + " order by s.date");
			int i = 1;
			while (rs.next()) {
				Object row[] = new Object[7];
				row[0] = i;
				row[1] = BaseFrame.loc1[BaseFrame.locMap[rs.getInt(2)]] + " " + BaseFrame.loc2[rs.getInt(2)];
				row[2] = BaseFrame.loc1[BaseFrame.locMap[rs.getInt(3)]] + " " + BaseFrame.loc2[rs.getInt(3)];
				row[3] = rs.getString(4);
				row[4] = rs.getString(5);
				row[5] = rs.getString(1);
				row[6] = rs.getString(6);
				m.addRow(row);
				i++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		BaseFrame.uno = "1";
		new Ticket().setVisible(true);
	}

}
