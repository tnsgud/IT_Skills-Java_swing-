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
	JTextField txt;
	LocalDate now = LocalDate.now(), today = LocalDate.now();

	JLabel datelbl, prev, next;

	JButton btn[] = new JButton[42];

	int year, month;

	boolean isAfter;

	public DatePicker(JTextField txt, boolean isAfter) {
		this.txt = txt;
		this.isAfter = isAfter;

		sz(this, 300, 300);

		var n = new JPanel(new BorderLayout());
		var ne = new JPanel(new FlowLayout(2));
		var ns = new JPanel(new GridLayout(1, 0));
		var c = new JPanel(new GridLayout(0, 7));

		setLayout(new BorderLayout());

		add(n, "North");
		add(c);

		n.add(datelbl = lbl("", 2), "West");
		n.add(ne, "East");
		n.add(ns, "South");

		ne.add(prev = lbl("<", 0));
		ne.add(next = lbl(">", 2));

		var cap = "일,월,화,수,목,금,토".split(",");
		for (int i = 0; i < cap.length; i++) {
			var l = lbl(cap[i], 0);
			l.setForeground(i % 7 == 0 ? Color.red : i % 7 == 6 ? Color.blue : Color.black);
			ns.add(l);
		}

		for (int i = 0; i < btn.length; i++) {
			int idx = i;
			c.add(btn[i] = btn("", a -> {
				txt.setText(year + "-" + String.format("%02d", month) + "-"
						+ String.format("%02d", toInt(btn[idx].getText())));

				setVisible(false);
			}));
			btn[i].setForeground(i % 7 == 0 ? Color.red : i % 7 == 6 ? Color.red : Color.black);
			btn[i].setBackground(Color.white);
			btn[i].setBorder(null);
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
		var sday = sdate.getDayOfWeek().getValue() % 7;

		for (int i = 0; i < btn.length; i++) {
			var tmp = sdate.plusDays(i - sday);
			btn[i].setEnabled(isAfter ? now.isAfter(tmp) : now.isBefore(tmp));
			btn[i].setText(tmp.getDayOfMonth() + "");
			btn[i].setVisible(tmp.getMonthValue() == today.getMonthValue());
		}
	}
}
