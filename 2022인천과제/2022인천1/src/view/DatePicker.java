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
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import tool.Tool;

public class DatePicker extends JPopupMenu implements Tool {

	int year, month;

	JTextField txt;
	LocalDate date;

	LocalDate today = LocalDate.now();

	JLabel datelbl, prev, next;

	JButton btn[] = new JButton[42];

	public DatePicker(JTextField txt, LocalDate date) {
		this.txt = txt;
		if (date != null) {
			this.date = date;
		}

		setLayout(new BorderLayout());
		sz(this, 300, 300);

		var n = new JPanel(new BorderLayout());
		var c = new JPanel(new GridLayout(0, 7));
		var ne = new JPanel(new FlowLayout(2));
		var ns = new JPanel(new GridLayout(1, 0));

		add(n, "North");
		add(c);

		n.add(datelbl = lbl("", 2, 13), "West");
		n.add(ne, "East");
		n.add(ns, "South");

		ne.add(prev = lbl("<", 0, 15));
		ne.add(next = lbl(">", 0, 15));

		var m = "일,월,화,수,목,금,토".split(",");
		for (int i = 0; i < m.length; i++) {
			var l = lbl(m[i], 0);
			l.setForeground(i % 7 == 0 ? Color.red : i % 7 == 6 ? Color.blue : Color.black);
			c.add(l);
		}

		for (int i = 0; i < btn.length; i++) {
			int idx = i;

			c.add(btn[i] = new JButton(i + ""));

			btn[i].setBorder(null);
			btn[i].setBackground(Color.white);
			btn[i].setForeground(i % 7 == 0 ? Color.red : i % 7 == 6 ? Color.blue : Color.black);

			btn[i].addActionListener(a -> {
				txt.setText(LocalDate.parse(year + "-" + String.format("%02d", month) + "-"
						+ String.format("%02d", toInt(btn[idx].getText()))).toString());
				setVisible(false);
			});
		}

		prev.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				today = today.minusMonths(1);
				setCal();
			}
		});

		next.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				today = today.plusMonths(1);
				setCal();
			}
		});
		
		setCal();
	}

	private void setCal() {
		year = today.getYear();
		month = today.getMonthValue();
		datelbl.setText(year + "년 " + month + "월");

		var sdate = LocalDate.of(year, month, 1);
		int sday = sdate.getDayOfWeek().getValue() % 7;

		for (int i = 0; i < btn.length; i++) {
			var tmp = sdate.plusDays(i - sday);

			if (date != null) {
				btn[i].setEnabled(tmp.isAfter(date));
			}

			btn[i].setVisible(tmp.getMonthValue() == today.getMonthValue());
			btn[i].setText(tmp.getDayOfMonth() + "");
		}
	}
}
