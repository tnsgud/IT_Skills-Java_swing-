package view;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class LookUp extends BaseDialog {
	DefaultTableModel m = model("sno,no,출발지,도착지,도착시간,출발날짜".split(","));
	JTable t = table(m);
	JScrollPane scr = new JScrollPane(t);
	JPopupMenu pop = new JPopupMenu();
	JMenuItem item = new JMenuItem("취소");

	public LookUp() {
		super("예매조회", 600, 450);

		setLayout(new BorderLayout(5, 5));

		add(lbl("예매조회", 2, 35), "North");
		add(scr);

		t.getColumnModel().getColumn(0).setMinWidth(0);
		t.getColumnModel().getColumn(0).setMaxWidth(0);

		data();

		pop.add(item);
		item.addActionListener(a -> {
			var date = LocalDate.parse(t.getValueAt(t.getSelectedRow(), 5) + "",
					DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

			if (LocalDate.parse("2021-10-06").isAfter(date)) {
				eMsg("취소가 불가능한 일정입니다.");
				return;
			}
			
			iMsg("예매 취소가 완료되었습니다.");

			execute("delete from reservation where no=?", t.getValueAt(t.getSelectedRow(), 0));
			data();
		});

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == 3 && t.getSelectedRow() != -1) {
					pop.show(t, e.getX(), e.getY());
				}
			}
		});

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		setVisible(true);
	}

	private void data() {
		m.setRowCount(0);

		var rs = rs(
				"select r.no, concat(l11.name, ' ', l21.name), concat(l12.name, ' ', l22.name), time_format(addtime(s.date,  s.elapsed_time), '%H:%m%i'), s.date from schedule s, reservation r, location l11, location2 l12, location l21, location2 l22 where l11.no = l12.location_no and l21.no = l22.location_no and s.departure_location2_no = l12.no and s.arrival_location2_no = l22.no and s.no = r.schedule_no and r.user_no = ? order by s.date asc",
				BaseFrame.uno);
		try {
			while (rs.next()) {
				var row = new Object[m.getColumnCount()];
				for (int i = 0, idx = 1; i < row.length; i++) {
					if (i != 1) {
						row[i] = rs.getString(idx);
						idx++;
					}
				}
				row[1] = rs.getRow();
				m.addRow(row);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		BaseFrame.uno = 1;
		new LookUp();
	}
}
