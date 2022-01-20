package view;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import db.DB;

public class LookUp extends BaseDialog {
	DefaultTableModel m = BaseFrame.model("sno,no,출발지,도착지,도착시간,출발날짜".split(","));
	JTable t = BaseFrame.table(m);
	JPopupMenu menu = new JPopupMenu();
	JMenuItem item;

	public LookUp(JFrame jf) {
		super(jf, "예매조회", 700, 450);

		menu.add(item = new JMenuItem("취소"));

		ui();
		data();
		event();

		setVisible(true);
	}

	private void event() {
		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (t.getSelectedRow() != -1 && e.getButton() == 3) {
					menu.show(t, e.getX(), e.getY());
				}
			}
		});

		item.addActionListener(a -> {
			var date = LocalDateTime.parse(t.getValueAt(t.getSelectedRow(), 5)+"", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			var now = LocalDateTime.parse("2021-10-06 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			if(!date.isAfter(now)) {
				BaseFrame.eMsg("취소 불가능한 일정입니다.");
				return;
			}
			
			BaseFrame.iMsg("예매 취소가 완료되었습니다.");
			DB.execute("delete from reservation where no=?", t.getValueAt(t.getSelectedRow(), 0));
			
			data();
		});
	}

	private void data() {
		m.setRowCount(0);
		try {
			var rs1 = DB.rs(
					"select s.no, r.no from schedule s, reservation r where r.schedule_no = s.no and r.user_no=? order by s.date asc;",
					BaseFrame.no);
			while (rs1.next()) {
				var rs2 = DB.rs(
						"select concat(l11.name, ' ', l21.name), concat(l12.name, ' ', l22.name), time_format(addtime(s.date, s.elapsed_time), '%H:%i:%s'), s.date from location l11, location l12, location2 l21, location2 l22, schedule s where l11.no = l21.location_no and l12.no = l22.location_no and l21.no = s.departure_location2_no and l22.no = s.arrival_location2_no and s.no = ?",
						rs1.getInt(1));
				if (rs2.next()) {
					var data = new ArrayList<String>();
					data.add(rs1.getString(2));
					data.add(rs1.getRow() + "");
					for (int i = 0; i < 4; i++) {
						data.add(rs2.getString(i + 1));
					}
					m.addRow(data.toArray());
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void ui() {
		setLayout(new BorderLayout(20, 20));
		add(BaseFrame.lbl("예매조회", 2, 35), "North");
		add(new JScrollPane(t));

		t.getColumnModel().getColumn(0).setMinWidth(0);
		t.getColumnModel().getColumn(0).setMaxWidth(0);

		for (int i = 2; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setWidth(100);
		}

		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
	}

	public static void main(String[] args) {
		BaseFrame.no = 1;
		new UserMain();
	}
}
