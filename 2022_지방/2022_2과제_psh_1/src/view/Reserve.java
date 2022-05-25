package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.DateFormatter;

public class Reserve extends BaseFrame {
	JLabel timelbl, datelbl, pricelbl;
	String[] cap = "날짜,지점,테마,시간,가격,인원수,총금액".split(",");
	Cal cal;
	TimeLine timeLine;
	JTextField txt = new JTextField(20);
	int tot;

	public Reserve() {
		super("예약", 800, 450);

		ui();
		event();

		setVisible(true);
	}

	private void event() {
		txt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				var input = toInt(txt.getText());
				var price = toInt(getOne("select t_personnel from theme where t_no=?", tno));

				if (input < 1) {
					eMsg("인원수를 확인하세요.");
					txt.setText("");
					txt.requestFocus();
					return;
				}

				if (input > price) {
					eMsg("인원을 초과하였습니다.");
					txt.setText("");
					txt.requestFocus();
					return;
				}

				tot = price * input;
				pricelbl.setText(format.format(tot));
			}
		});
	}

	private void ui() {
		var ec = new JPanel(new GridLayout(0, 1));
		var es = new JPanel(new FlowLayout(1, 5, 0));

		setLayout(new BorderLayout(10, 10));
		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new BorderLayout(10, 10)));
		c.add(cal = sz(new Cal(), 300, 300), "West");
		c.add(timeLine = sz(new TimeLine(), 10, 600));
		c.add(sz(e = new JPanel(new BorderLayout()), 350, 400), "East");

		n.add(lbl("방탈출 예약", 0, 35));
		n.add(lbl("Room Escape Reservation", 0), "South");

		e.add(ec);
		e.add(es, "South");

		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel(new FlowLayout(0));
			var c = sz(lbl(cap[i], 2), 100, 20);
			var l = new JLabel();
			if (i == 0) {
				l = datelbl = lbl(cal.date + "", 2);
			} else if (i == 3) {
				l = timelbl = lbl(LocalTime.of(LocalTime.now().getHour() + 1, 0) + "", 2);
			} else if (i == 6) {
				l = pricelbl = lbl("0", 2);
			} else {
				l = lbl(getOne(
						"select " + (i == 4 ? "format(c_price, '#,##0')" : (i == 1 ? "c_name" : "t_name"))
								+ " from cafe c, theme t where " + (i == 1 ? "c.c_no=?" : "t.t_no=?"),
						i == 1 ? cno : tno), 0);
			}

			p.add(c);
			p.add(i == 5 ? txt : l);
			ec.add(p);

			c.setForeground(Color.white);
			l.setForeground(Color.white);
			p.setBackground(Color.black);
		}

		for (var bcap : "예약,취소".split(",")) {
			es.add(btn(bcap, a -> {
				if(txt.getText().isEmpty()) {
					eMsg("빈칸이 있습니다.");
					return;
				}
				
				iMsg("예약이 완료되었습니다.");
				execute("insert into reservation values(0, ?, ?, ?, ?, ?, ?, ?)", uno, cno, tno, datelbl.getText(), timelbl.getText(), toInt(txt.getText()), 0);
				dispose();
			}));
		}

		es.setBackground(Color.black);

		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 0, 0, 0));
	}

	class Cal extends JPanel {
		JPanel n, c, cc;
		JLabel prev, d, next, days[] = new JLabel[42];
		LocalDate date = LocalDate.of(2022, 4, 5);

		public Cal() {
			setLayout(new BorderLayout(10, 10));

			var cn = new JPanel(new FlowLayout(1, 10, 0));

			add(sz(n = new JPanel(new BorderLayout()), 0, 30), "North");
			add(c = new JPanel(new BorderLayout(50, 50)));

			n.add(prev = lbl("◁", 2), "West");
			n.add(d = lbl("Apr, 5, 2022", 0));
			n.add(next = lbl("▷", 2), "East");

			c.add(cn, "North");
			c.add(cc = new JPanel(new GridLayout(0, 7)));

			for (int i = 0; i < DayOfWeek.values().length; i++) {
				cn.add(lbl(DayOfWeek.values()[i == 0 ? 6 : i - 1].getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
						.toUpperCase(), 2, 15));
			}

			for (int i = 0; i < days.length; i++) {
				cc.add(days[i] = new JLabel("", 0));
				days[i].addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						var l = ((JLabel) e.getSource());
						if (!l.isEnabled()) {
							return;
						}
						Arrays.stream(days).forEach(day -> day.setBackground(null));
						l.setOpaque(true);
						l.setBackground(Color.orange);

						var tmp = LocalDate.of(date.getYear(), date.getMonthValue(), toInt(l.getText()));
						date = tmp;
						d.setText(tmp.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + ", "
								+ tmp.getDayOfMonth() + ", " + tmp.getYear());
						datelbl.setText(date + "");
						timelbl.setText("");
						timeLine.time = LocalTime.of(0, 30);
						timeLine.setTimeLine();
						timeLine.times.forEach(lbl -> lbl.setBackground(null));
					}
				});
			}

			n.setBackground(Color.black);
			n.setBorder(new EmptyBorder(5, 5, 5, 5));

			prev.setForeground(Color.white);
			d.setForeground(Color.white);
			next.setForeground(Color.white);

			prev.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					date = date.minusMonths(1);
					drawCal();
				}
			});

			next.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					date = date.plusMonths(1);
					drawCal();
				}
			});

			drawCal();
		}

		private void drawCal() {
			var year = date.getYear();
			var month = date.getMonthValue();
			var startd = LocalDate.of(year, month, 1);
			var start = startd.getDayOfWeek().getValue();
			for (int i = 0; i < days.length; i++) {
				var ld = startd.plusDays(i - start);
				days[i].setOpaque(false);
				days[i].setForeground(null);
				days[i].setVisible(false);
				days[i].setEnabled(false);

				if (ld.getMonthValue() == date.getMonthValue()) {
					days[i].setText(ld.getDayOfMonth() + "");
					days[i].setVisible(true);
					var t = LocalDate.of(year, month, toInt(days[i].getText()));

					if (t.getDayOfMonth() >= now.getDayOfMonth()) {
						days[i].setEnabled(true);
					}
					if (t.getYear() > now.getYear() || now.getMonthValue() < t.getMonthValue()) {
						days[i].setEnabled(true);
					}
					if (now.getMonthValue() > t.getMonthValue()) {
						days[i].setEnabled(false);
					}
					if (now.toEpochDay() == t.toEpochDay()) {
						days[i].setOpaque(true);
						days[i].setBackground(Color.orange);
					}
				}
			}
		}
	}

	class TimeLine extends JPanel {
		JPanel w, scroll;
		LocalTime time = LocalTime.of(LocalTime.now().getHour() + 1, 0);
		LocalTime now = LocalTime.now();
		JLabel tit;
		JLabel prev, next;
		ArrayList<JLabel> times = new ArrayList<JLabel>();

		public TimeLine() {
			ui();
		}

		private void ui() {
			setLayout(new GridLayout(1, 0, 5, 5));

			add(w = new JPanel(new GridLayout(16, 1)));
			add(scroll = new JPanel(new BorderLayout()));

			scroll.add(prev = lbl("▲", 0), "North");
			scroll.add(next = lbl("▼", 0), "South");

			prev.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (time.minusHours(7).getHour() - 1 == now.getHour()) {
						return;
					}
					time = time.minusHours(15);
					setTimeLine();
				}
			});
			next.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					setTimeLine();
				}
			});

			setTimeLine();

			scroll.setBorder(new LineBorder(Color.black));
		}

		void setTimeLine() {
			w.removeAll();
			w.add(sz(tit = lbl("시간", 0), 0, 100));
			while (w.getComponentCount() < 16 && time.isAfter(LocalTime.of(0, 0))) {
				var l = lbl(time + "", 0);
				w.add(l);
				times.add(l);
				l.setOpaque(true);
				l.setBackground(time.getHour() == now.getHour() + 1 && time.getMinute() == 0 ? Color.orange : null);
				l.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						var l = ((JLabel) e.getSource());
						times.forEach(lbl -> lbl.setBackground(null));
						l.setBackground(Color.orange);
						timelbl.setText(l.getText());
					}
				});
				time = time.plusMinutes(30);
			}

			tit.setOpaque(true);

			tit.setForeground(Color.white);
			tit.setBackground(Color.black);
			w.repaint();
			w.revalidate();
		}
	}

	public static void main(String[] args) {
		cno = "N-021";
		tno = 45;
		new Reserve();
	}
}
