package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class AirlineTicket extends BasePage {
	DefaultTableModel m = model("번호,출발지,도착지,출발시간,도착시간,가격,잔여좌석".split(","));
	DefaultTableCellRenderer r = new DefaultTableCellRenderer() {
		@Override
		public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			var com = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			if (column != 3) {
				return com;
			}

			var lbl = (JLabel) com;
			var stime = LocalTime.parse(value.toString());
			var etime = LocalTime.parse(table.getValueAt(row, column + 1).toString());

			lbl.setToolTipText(String.format("%d시간 %d분 소요", etime.getHour() - stime.getHour(),
					etime.getMinute() - stime.getMinute()));
//			var distance = getOne(
//					"select round(ST_distance_Sphere(point(a1.a_longitude, a1.a_latitude), point(a2.a_longitude, a2.a_latitude)) * 0.001, -2) from v1, airport a1, airport a2 where v1.a1_code = a1.a_code and v1.a2_code = a2.a_code and v1.s_no = ?",
//					table.getValueAt(row, 0));
			return lbl;
		}
	};
	JTable t = table(m);
	int[] sNo;
	JLabel lblDate;
	LocalDate date;

	public AirlineTicket(LocalDate date, int[] sNo) {
		this.date = date;
		this.sNo = sNo;

		r_date = date;

		setLayout(new BorderLayout(10, 10));

		t.getColumn("출발시간").setCellRenderer(r);

		add(n = new JPanel(new BorderLayout()), "North");
		add(new JScrollPane(t));
		add(s = new JPanel(new FlowLayout(2)), "South");

		var lbl = new JLabel(getIcon("./datafiles/달력.png", 50, 50));
		lbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				new Cal(AirlineTicket.this);
			}
		});

		var week = "일,월,화,수,목,금,토".split(",");
		n.add(lblDate = lbl(String.format("%02d.%02d (%s)", date.getMonthValue(), date.getDayOfMonth(),
				week[date.getDayOfWeek().getValue()]), 2, 20), "North");
		n.add(lbl, "East");

		s.add(btn("확인", a -> {
			if (t.getSelectedRow() == -1) {
				eMsg("항공권을 선택해주세요.");
				return;
			}

			if (toInt(t.getValueAt(t.getSelectedRow(), 6)) < peoples.size()) {
				eMsg("좌석이 부족합니다.");
				return;
			}

			BaseFrame.sNo = toInt(t.getValueAt(t.getSelectedRow(), 0));
			mf.swap(new PeopleInfo());
		}));

		addRow();
	}

	public void addRow() {
		m.setRowCount(0);
		for (var sno : sNo) {
			var rs = getRows(
					"select s_no, a1_name, a2_name, time_format(s_time, '%h:%m'), round(ST_distance_Sphere(point(a1.a_longitude, a1.a_latitude), point(a2.a_longitude, a2.a_latitude)) * 0.001, -2) distance, format(s_price, '#,##0'), 140-(select count(*) from reservation r, companion c where r.r_no = c.r_no and r.s_no = v1.s_no and r_date = '2022-09-01') from v1, airport a1, airport a2 where v1.a1_no = a1.a_no and v1.a2_no=a2.a_no and v1.s_no = ?",
					sno).get(0);
			var sTime = LocalTime.parse(rs.get(3).toString());
			rs.set(4, sTime.plusMinutes(toInt(rs.get(4)) / 10));
			
			m.addRow(rs.toArray());
		}
	}
}
