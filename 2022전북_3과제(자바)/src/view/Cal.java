package view;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.format.TextStyle;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class Cal extends BaseFrame {
	int year, month;
	LocalDate date = LocalDate.now(), now = LocalDate.now(), selDate, d;
	JTextField txt;
	JLabel prev, datelbl, next;
	JLabel days[] = new JLabel[42];
	AirlineTicket airlineTicket;

	public Cal(LocalDate d) {
		super("날짜 선택", 500, 500);

		this.selDate = d;

		add(n = new JPanel(), "North");
		add(c = new JPanel(new GridLayout(0, 7)));

		n.add(prev = lbl("◀", 0));
		n.add(datelbl = lbl(String.format("%d년 %d월", date.getYear(), date.getMonthValue()), 0));
		n.add(next = lbl("▶", 0));

		var cap = "일,월,화,수,목,금,토".split(",");
		for (int i = 0; i < cap.length; i++) {
			var lbl = lbl(cap[i], 0);

			lbl.setForeground(i % 7 == 0 ? Color.red : i % 7 == 6 ? Color.blue : Color.black);

			c.add(lbl);
		}

		for (int i = 0; i < days.length; i++) {
			c.add(days[i] = lbl("", 0, 15));
			days[i].setBorder(new LineBorder(Color.black));
			days[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					var tmp = LocalDate.of(year, month, toInt(((JLabel) e.getSource()).getText()));
					var me = (JLabel)e.getSource();
					
					if(!me.isEnabled()) return;
					
					if (now.isAfter(tmp)) {
						eMsg("이전 날짜는 선택이 불가능합니다.");
						return;
					}

					if(e.getClickCount() == 1) {
						for (var d: days) {
							d.setBorder(new LineBorder(Color.black));
						}
						
						me.setBorder(new LineBorder(Color.blue));
					}else {
						if (txt == null) {
							airlineTicket.date = tmp;
							airlineTicket.lblDate.setText(String.format("%02d.%02d (%s)", airlineTicket.date.getMonthValue(), airlineTicket.date.getDayOfMonth(),
									airlineTicket.date.getDayOfWeek().getDisplayName(TextStyle.SHORT, getLocale())));
						} else {
							txt.setText(tmp.toString());
						}
						
						dispose();
					}
				}
			});
		}

		prev.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (date.getMonthValue() == now.getMonthValue()) {
					prev.setEnabled(false);
				}

				date = date.minusMonths(1);

				setDay();
			}
		});
		next.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				date = date.plusMonths(1);

				prev.setEnabled(true);

				setDay();
			}
		});

		prev.setEnabled(false);

		setDay();

		addWindowFocusListener(new WindowAdapter() {
			@Override
			public void windowLostFocus(WindowEvent e) {
				if(e.getOppositeWindow() instanceof JDialog) {
					return;
				}
				
				dispose();
			}
		});

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				if(airlineTicket != null) {
					airlineTicket.addRow();
				}
			}
		});
	}

	public Cal(JTextField txt, LocalDate d) {
		this(d);
		this.txt = txt;
	}

	public Cal(AirlineTicket airlineTicket) {
		this(airlineTicket.date);

		this.airlineTicket = airlineTicket;
	}

	private void setDay() {
		datelbl.setText(String.format("%d년 %d월", date.getYear(), date.getMonthValue()));
		year = date.getYear();
		month = date.getMonthValue();
		var sdate = LocalDate.of(year, month, 1);
		var sday = sdate.getDayOfWeek().getValue() % 7;

		for (int i = 0; i < days.length; i++) {
			var tmp = sdate.plusDays(i - sday);

			days[i].setBorder(
					new LineBorder(selDate == null ? Color.black : selDate.equals(tmp) ? Color.blue : Color.black));
			days[i].setEnabled(tmp.getMonthValue() == month);
			days[i].setText(tmp.getDayOfMonth() + "");
		}
	}
}
