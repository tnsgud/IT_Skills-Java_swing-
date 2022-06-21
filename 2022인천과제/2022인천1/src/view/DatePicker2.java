package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import tool.Tool;

public class DatePicker2 extends JPopupMenu implements Tool {

	JPanel n, c;
	JPanel ne;
	JTextField txt;
	JLabel date, prev, next;
	JToggleButton jtb[] = new JToggleButton[42];
	ButtonGroup bg = new ButtonGroup();

	LocalDate today = LocalDate.now();
	int year, month;

	// 기준 날짜

	LocalDate baseDate;

	public DatePicker2(JTextField txt, LocalDate baseDate) {
		setLayout(new BorderLayout());

		if (baseDate != null)
			this.baseDate = baseDate;
		this.txt = txt;
		setPreferredSize(new Dimension(300, 300));

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new GridLayout(0, 7)));

		n.add(date = lbl("yyyy년 mm월", 2, 13), "West");
		n.add(ne = new JPanel(new FlowLayout(2, 5, 5)));

		ne.add(prev = lbl("<", 0, 15));
		ne.add(next = lbl(">", 0, 15));

		var m = "일,월,화,수,목,금,토".split(",");

		for (int i = 0; i < m.length; i++) {
			JLabel lbl = new JLabel(m[i], 0);
			c.add(lbl);
			if (i % 7 == 6) {
				lbl.setForeground(Color.BLUE);
			} else if (i % 7 == 0) {
				lbl.setForeground(Color.red);
			}
		}

		for (int i = 0; i < jtb.length; i++) {
			c.add(jtb[i] = new JToggleButton(i + ""));
			jtb[i].setBorder(null);
			jtb[i].setBackground(Color.WHITE);

			if (i % 7 == 6) {
				jtb[i].setForeground(Color.BLUE);
			} else if (i % 7 == 0) {
				jtb[i].setForeground(Color.red);
			}
			int idx = i;
			jtb[i].addActionListener(e -> {
				txt.setText(LocalDate.parse(year + "-" + String.format("%02d", month) + "-"
						+ String.format("%02d", toInt(jtb[idx].getText()))).toString());
				setVisible(false);
			});

			bg.add(jtb[i]);
		}

		setCal();
		event();
	}

	void setCal() {
		year = today.getYear();
		month = today.getMonthValue();
		date.setText(year + "년 " + month + "월");

		LocalDate sdate = LocalDate.of(year, month, 1);
		int sday = sdate.getDayOfWeek().getValue() % 7;

		for (int i = 0; i < jtb.length; i++) {

			LocalDate tmp = sdate.plusDays(i - sday);

			if (baseDate != null)
				jtb[i].setEnabled(tmp.isAfter(baseDate));
			jtb[i].setVisible(tmp.getMonthValue() == today.getMonthValue());
			jtb[i].setText(tmp.getDayOfMonth() + "");
		}
	}

	void event() {
		prev.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				today = today.plusMonths(-1);
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
	}

}