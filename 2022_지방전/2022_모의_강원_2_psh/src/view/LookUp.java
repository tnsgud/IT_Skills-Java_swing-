package view;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import db.DB;
import tool.Tool;

public class LookUp extends BaseDialog implements Tool {
	DefaultTableModel m = model("sno,no,출발지,도착지,도착시간,출발날짜".split(","));
	JTable t = table(m);
	JPopupMenu pop = new JPopupMenu();
	JMenuItem item;

	public LookUp() {
		super("", 600, 450);

		pop.add(item = new JMenuItem("취소"));

		ui();
		data();
		evnet();

		setVisible(true);
	}

	private void evnet() {
		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (t.getSelectedRow() != -1 && e.getButton() == 3) {
					pop.show(t, e.getX(), e.getY());
				}
			}
		});

		item.addActionListener(a -> {
			var date = LocalDateTime.parse(t.getValueAt(t.getSelectedRow(), 5) + "",
					DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			var now = LocalDateTime.parse(LocalDateTime.now().toString(),
					DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			if (!now.isAfter(date)) {
				eMsg("취소 불가능한 일정입니다.");
				return;
			}

			iMsg("예매 취소가 완료되었습니다.");
			DB.execute("delete from reservation where no=?", t.getValueAt(t.getSelectedRow(), 0));
			
			data();
		});
	}

	private void data() {
		m.setRowCount(0);
		var rs = DB.rs(
				"select s.no, concat(v1.l11name, ' ', v1.l21name), concat(v1.l12name, ' ', v1.l22name), time_format(addtime(s.date, s.elapsed_time), '%H:%i:%s'), s.date from v1, schedule s, reservation r "
				+ "where r.user_no = ? and s.no = v1.sno and s.no = r.schedule_no order by s.date asc",
				BaseFrame.no);
		try {
			while (rs.next()) {
				var data = new ArrayList<String>();
				data.add(rs.getString(1));
				data.add(rs.getRow() + "");
				for (int i = 0; i < 4; i++) {
					data.add(rs.getString(i + 2));
				}
				m.addRow(data.toArray());
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void ui() {
		setLayout(new BorderLayout(20, 20));
		add(lbl("예매조회", 2, 35), "North");
		add(new JScrollPane(t));

		t.getColumnModel().getColumn(0).setMinWidth(0);
		t.getColumnModel().getColumn(0).setMaxWidth(0);

		for (int i = 2; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setWidth(100);
		}

		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
	}

	public static void main(String[] args) {
//		BaseFrame.no = 1;
//		new UserMain();
	}
}
