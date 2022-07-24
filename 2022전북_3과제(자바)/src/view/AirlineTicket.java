package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;

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
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			if (column == 3) {
				JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
						column);

				var stime = LocalTime.parse(value.toString());
				var etime = LocalTime.parse(table.getValueAt(row, column + 1).toString());

				var tip = String.format("%d시간 %d분 소요", etime.getHour() - stime.getHour(),
						etime.getMinute() - stime.getMinute());

				lbl.setToolTipText(tip);

				return lbl;
			} else {
				return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			}
		}

		public int getHorizontalAlignment() {
			return 0;
		};
	};
	JTable t = table(m);
	Object[] s_nos;
	LocalDate date;
	JLabel lblDate;

	public AirlineTicket(LocalDate date, Object[] s_nos) {
		s_nos = getRows(
				"select s_no from schedule s, airport a1, airport a2 where s.s_depart = a1.a_no and s.s_arrival = a2.a_no and s.s_depart = 1 and s.s_arrival = 4")
						.stream().map(a -> a.get(0)).toArray();
		this.date = date;
		this.s_nos = s_nos;
		
		r_date = date;

		setLayout(new BorderLayout(10, 10));

		t.getColumn("출발시간").setCellRenderer(r);

		add(n = new JPanel(new BorderLayout(5, 5)), "North");
		add(new JScrollPane(t));
		add(s = new JPanel(new FlowLayout(2)), "South");

		var lbl = new JLabel(getIcon("./datafiles/달력.png", 50, 50));

		lbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				new Cal(AirlineTicket.this).setVisible(true);
			}
		});

		n.add(lblDate = lbl(String.format("%02d.%02d (%s)", date.getMonthValue(), date.getDayOfMonth(),
				date.getDayOfWeek().getDisplayName(TextStyle.SHORT, getLocale())), 2, 20), "West");
		n.add(lbl, "East");

		s.add(btn("확인", a -> {
			if (t.getSelectedRow() == -1) {
				eMsg("항공권을 선택해주세요.");
				return;
			}
			
			

			if (toInt(t.getValueAt(t.getSelectedRow(), 6)) < BaseFrame.peoples.size()) {
				eMsg("좌석이 부족합니다.");
				return;
			}

			BaseFrame.s_no = toInt(t.getValueAt(t.getSelectedRow(), 0));

			main.swap(new PeopleInfo());
		}));

		addRow();
	}

	void addRow() {
		m.setRowCount(0);
		for (var s_no : s_nos) {
			var rs = getRows(
					"select s_no, a1.a_name, a2.a_name, time_format(s_time, '%h:%m'), a1.a_latitude, a1.a_longitude, a2.a_latitude, a2.a_longitude, format(s_price, '#,##0'), 140-(select count(*) from reservation r, companion c where r.r_no = c.r_no and r.s_no = s.s_no and r_date = '2022-09-01') from schedule s, airport a1, airport a2 where s.s_depart = a1.a_no and s.s_arrival = a2.a_no and "
							+ (date.toEpochDay() == LocalDate.now().toEpochDay()
									? "s_time>'" + LocalTime.now().format(DateTimeFormatter.ofPattern("h:mm"))
											+ "' and "
									: "")
							+ "s.s_no=?",
					s_no);
			if (rs.isEmpty()) {
				return;
			}

			var r = rs.get(0);

			var stime = LocalTime.parse(r.get(3).toString());
			var etime = stime.plusMinutes(
					(int) distance((Double) r.get(4), (Double) r.get(5), (Double) r.get(6), (Double) r	.get(7)) / 10);
			m.addRow(new Object[] { r.get(0), r.get(1), r.get(2), r.get(3), etime, r.get(8), r.get(9) });
		}
	}
}
