package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Locale;

import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

public class CalendarDialog extends BaseDialog {
	JPanel n, c;
	JTextField txt;
	LocalDate today = LocalDate.now();

	JLabel datelbl = null, prvlbl, nxtlbl;
	JToggleButton dbtn[] = new JToggleButton[42];
	ButtonGroup bg = new ButtonGroup();

	int year, month;

	public CalendarDialog(JTextField txt) {
		super("", 300, 300);
		this.txt = txt;

		ui();
		setDay();
		events();
	}

	void ui() {
		var ne = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new GridLayout(0, 7)));

		n.setBackground(Color.DARK_GRAY);
		c.setBackground(Color.DARK_GRAY);
		ne.setBackground(Color.DARK_GRAY);

		n.add(datelbl = BasePage.lbl("yyyy년 mm월", JLabel.LEFT, 13), "West");
		n.add(ne);

		ne.add(nxtlbl = BasePage.lbl("△", JLabel.CENTER, 15));
		ne.add(prvlbl = BasePage.lbl("▽", JLabel.CENTER, 15));

		setUndecorated(true);

		for (var cap : "일,월,화,수,목,금,토".split(",")) {
			var lbl = new JLabel(cap, JLabel.CENTER);
			lbl.setForeground(Color.WHITE);
			c.add(lbl);
		}

		for (int i = 0; i < dbtn.length; i++) {
			c.add(dbtn[i] = new JToggleButton(i + ""));
			dbtn[i].setBackground(Color.DARK_GRAY);
			dbtn[i].setForeground(Color.WHITE);
			dbtn[i].setBorder(null);

			int idx = i;
			dbtn[i].addActionListener(event -> {
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
				LocalDateTime ldt = LocalDateTime.now();
				txt.setText(year + "-" + month + "-" + dbtn[idx].getText() + " " + ldt.format(dtf));
				dispose();
			});

			bg.add(dbtn[i]);
		}

		datelbl.setForeground(Color.WHITE);
		prvlbl.setForeground(Color.WHITE);
		nxtlbl.setForeground(Color.WHITE);
	}

	void events() {
		addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				var dur = e.getWheelRotation();
				if (dur > 0) {
					today = today.plusMonths(1);
				} else {
					today = today.plusMonths(-1);
				}
				e.consume();
				setDay();
			}
		});

		prvlbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				today = today.plusMonths(-1);
				setDay();
			}
		});

		nxtlbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				today = today.plusMonths(1);
				setDay();
			}
		});
	}

	void setDay() {
		year = today.getYear();
		month = today.getMonthValue();
		datelbl.setText(year + "년 " + month + "월");
		var sdate = LocalDate.of(year, month, 1);
		int sday = (sdate.getDayOfWeek().getValue() % 7);

		for (int i = 0; i < 42; i++) {
			var tmp = sdate.plusDays(i - sday);
			if (tmp.getMonthValue() != month)
				dbtn[i].setEnabled(false);
			else
				dbtn[i].setEnabled(true);
			dbtn[i].setText(tmp.getDayOfMonth() + "");
		}

	}
}
