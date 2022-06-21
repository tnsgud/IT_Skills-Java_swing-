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

	JLabel datelbl, prev = lbl("<", 0), next = lbl(">", 0);
	LocalDate now = LocalDate.now(), today = LocalDate.now();

	JButton btn[] = new JButton[42];

	int year, month;
	boolean isAfter;

	public DatePicker(JTextField txt, boolean isAfter) {
		this.txt = txt;
		this.isAfter = isAfter;

		var n = new JPanel(new BorderLayout());
		var c = new JPanel(new GridLayout(0, 7));
		var ne = new JPanel(new FlowLayout(2));

		setLayout(new BorderLayout());
		sz(this, txt.getWidth(), 250);

		add(n, "North");
		add(c);

		n.add(datelbl = lbl("", 2), "West");
		n.add(ne, "East");

		ne.add(prev);
		ne.add(next);

		var cap = "일,월,화,수,목,금,토".split(",");
		for (int i = 0; i < cap.length; i++) {
			var l = lbl(cap[i], 0);
			l.setForeground(i % 7 == 0 ? Color.red : i % 7 == 6 ? Color.blue : Color.black);
			c.add(l);
		}

		for (int i = 0; i < btn.length; i++) {
			btn[i] = btn(i + 1 + "", a -> {
				txt.setText(LocalDate.of(year, month, toInt(((JButton) a.getSource()).getText())).toString());

				setVisible(false);
			});
			btn[i].setBorder(null);
			btn[i].setForeground(i % 7 == 0 ? Color.red : i % 7 == 6 ? Color.blue : Color.black);
			btn[i].setBackground(Color.white);
			c.add(btn[i]);
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
		var sdate = LocalDate.of(year, month, 1);
		int sday = sdate.getDayOfWeek().getValue() % 7;

		for (int i = 0; i < btn.length; i++) {
			var tmp = sdate.plusDays(i - sday);

			btn[i].setText(tmp.getDayOfMonth() + "");
			btn[i].setVisible(tmp.getMonthValue() == today.getMonthValue());
			btn[i].setEnabled(isAfter ? tmp.isAfter(now) : tmp.isBefore(now));
		}
	}
}
