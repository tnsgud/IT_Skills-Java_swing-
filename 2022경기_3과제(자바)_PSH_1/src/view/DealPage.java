package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import view.DealPage.Day;

public class DealPage extends BasePage {
	int year, month;
	LocalDate date = LocalDate.now(), now = LocalDate.now();
	JLabel lblDate = lbl("", 0, 25), prev = lbl("◀", 0, 25), next = lbl("▶", 0, 25);
	Day days[] = new Day[42];

	@Override
	public JLabel lbl(String c, int a, int st, int sz) {
		var l = super.lbl(c, a, st, sz);
		l.setForeground(Color.black);
		return l;
	}

	public DealPage() {
		super("거래내역");

		add(n = new JPanel(new FlowLayout(1)), "North");
		add(c = new JPanel(new GridLayout(0, 7)));

		n.add(prev);
		n.add(lblDate);
		n.add(next);

		var cap = "일,월,화,수,목,금,토".split(",");
		for (int i = 0; i < cap.length; i++) {
			var lbl = lbl(cap[i], 0, 20);
			lbl.setForeground(i % 7 == 0 ? Color.red : i % 7 == 6 ? Color.blue : Color.black);
			c.add(lbl);
		}

		for (int i = 0; i < days.length; i++) {
			days[i] = new Day();
			c.add(days[i]);
		}

		prev.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				date = date.minusMonths(1);
				setCal();
			}
		});
		next.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				date = date.plusMonths(1);
				setCal();
			}
		});

		setCal();

		setBackground(Color.white);
		setOpaque(true);
	}
	
	private void setCal() {
		year = date.getYear();
		month = date.getMonthValue();
		lblDate.setText(year + "년 " + month + "월");
		var sdate = LocalDate.of(year, month, 1);
		var sday = sdate.getDayOfWeek().getValue() % 7;
		for (int i = 0; i < days.length; i++) {
			var tmp = sdate.plusDays(i - sday);

			days[i].date = tmp;
			days[i].day.setText(tmp.getDayOfMonth() + "");
			days[i].day.setForeground(i % 7 == 0 ? Color.red : i % 7 == 6 ? Color.blue : Color.black);
			days[i].setVisible(tmp.getMonthValue() == month);
		}
	}
	
	class Day extends JPanel {
		JButton btn;
		JLabel day = lbl("", 0, 15);
		LocalDate date;

		public Day() {
			setLayout(new BorderLayout());

			var n = new JPanel(new FlowLayout(2));
			var s = new JPanel(new FlowLayout(1));

			add(n, "North");
			add(s);

			n.add(day);

			s.add(btn = new JButton("보기"));

			btn.addActionListener(a -> {
				new DealDialog(date).setVisible(true);
			});

			setBorder(new LineBorder(Color.black));
		}

		@Override
		public void setVisible(boolean aFlag) {
			super.setVisible(aFlag);

			btn.setVisible(!getOne("select * from deal where d_date = ?", date.toString()).isEmpty());
		}
	}
}
