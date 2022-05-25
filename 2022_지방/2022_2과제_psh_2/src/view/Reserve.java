package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Reserve extends BaseFrame {
	Cal cal;
	TimeLine timeline;
	JLabel datelbl, timelbl, pricelbl;
	JTextField txt = new JTextField();

	public Reserve() {
		super("예약", 800, 450);

		setLayout(new BorderLayout(10, 10));
		add(n = new JPanel(new BorderLayout()), "North");
		add(sz(cal = new Cal(), 330, 300), "West");
		add(timeline = new TimeLine());
		add(sz(e = new JPanel(new BorderLayout()), 330, 400), "East");

		n.add(lblH("방탈출 예약", 0, 35));
		n.add(lbl("Room Escape Reservation", 0, 12), "South");

		e.add(ec = new JPanel(new GridLayout(0, 1, 10, 10)));
		e.add(es = new JPanel(new FlowLayout(1, 5, 0)), "South");

		var cap = "날짜,지점,테마,시간,가격,인원수,총금액".split(",");
		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel(new BorderLayout());
			var c = sz(lbl(cap[i], 2), 100, 20);
			var l = new JLabel();

			if (i == 0) {
				l = datelbl = lbl(cal.date + "", 2);
			} else if (i == 3) {
				l = timelbl = lbl(timeline.now.plusHours(1).format(DateTimeFormatter.ofPattern("hh:mm")), 2);
			} else if (i == 6) {
				l = pricelbl = lbl("0", 2);
			} else {
				l = lbl(getResult(
						"select " + (i == 4 ? "format(c_price , '#,##0')" : (i == 1 ? "c_name" : "t_name"))
								+ " from cafe c, theme t where " + (i == 1 ? "c.c_no=?" : "t.t_no=?"),
						i == 1 ? cno : tno).get(0).get(0) + "", 2);
			}

			p.add(c, "West");
			p.add(i == 5 ? txt : l);
			ec.add(p);

			c.setForeground(Color.white);
			l.setForeground(Color.white);
			p.setBackground(Color.black);
		}
		ec.setBackground(Color.black);
		es.setBackground(Color.black);
		for (var c : "예약,취소".split(",")) {
			es.add(btn(c, a -> {
				if (a.getActionCommand().equals("취소")) {
					dispose();
				} else {
					if (txt.getText().isEmpty()) {
						eMsg("빈칸이 있습니다.");
						return;
					}

					iMsg("예약이 완료되었습니다.");
					execute("insert into reservation values(0, ?, ?, ?, ?, ?, ?, ?)", uno, cno, tno, datelbl.getText(),
							timelbl.getText(), toInt(txt.getText()), 0);
					dispose();
				}
			}));
		}

		txt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (txt.getText().isEmpty()) {
					return;
				}

				var in = toInt(txt.getText());
				var pe = toInt(getResult("select t_personnel  from theme where t_no=?", tno).get(0).get(0));
				var pr= toInt(getResult("select c_price from cafe where c_no=?", cno).get(0).get(0));
				
				if (in < 1) {
					eMsg("인원수를 확인하세요.");
					txt.setText("");
					txt.requestFocus();
					return;
				}

				if (in > pe) {
					eMsg("인원을 초과하였습니다.");
					txt.setText("");
					txt.requestFocus();
					;
					return;
				}

				pricelbl.setText(format.format(pr * in));
			}
		});

		setVisible(true);
	}

	class Cal extends JPanel {
		JPanel n, c, cc;
		JLabel prev, d, next, days[] = new JLabel[42];
		LocalDate now = LocalDate.now(), date = LocalDate.now();

		public Cal() {
			setLayout(new BorderLayout(10, 10));

			var cn = new JPanel(new FlowLayout(1, 10, 0));

			add(sz(n = new JPanel(new BorderLayout()), 0, 30), "North");
			add(c = new JPanel(new BorderLayout(50, 50)));

			n.add(prev = lbl("◁", 0), "West");
			n.add(d = lbl("", 0));
			n.add(next = lbl("▷", 0), "East");

			c.add(cn, "North");
			c.add(cc = new JPanel(new GridLayout(0, 7)));

			for (int i = 0; i < DayOfWeek.values().length; i++) {
				cn.add(lbl(DayOfWeek.values()[i == 0 ? 6 : i - 1].getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
						.toUpperCase(), 2, 15));
			}

			for (int i = 0; i < days.length; i++) {
				cc.add(days[i] = lbl("", 0, 15));
				days[i].addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						var l = (JLabel) e.getSource();
						if (!l.isEnabled()) {
							return;
						}

						Arrays.stream(days).forEach(a -> a.setBackground(null));
						l.setOpaque(true);
						l.setBackground(Color.orange);

						date = LocalDate.of(date.getYear(), date.getMonthValue(), toInt(l.getText()));
						d.setText(date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + ", "
								+ date.getDayOfMonth() + ", " + date.getYear());
						if (date.equals(now)) {
							timeline.time = LocalTime.of(timeline.now.getHour() + 1, 0);
						} else {
							timeline.time = LocalTime.of(0, 30);
						}
						datelbl.setText(date + "");
						timeline.setTimeline();
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
			int year = date.getYear(), month = date.getMonthValue();
			var startd = LocalDate.of(year, month, 1);
			var start = startd.getDayOfWeek().getValue();

			d.setText(date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + ", " + date.getDayOfMonth()
					+ ", " + year);
			for (int i = 0; i < days.length; i++) {
				var ld = startd.plusDays(i - start);
				days[i].setOpaque(false);
				days[i].setForeground(null);
				days[i].setVisible(false);
				days[i].setEnabled(false);

				if (ld.getMonthValue() == date.getMonthValue()) {
					days[i].setText(ld.getDayOfMonth() + "");
					days[i].setVisible(true);
					days[i].setOpaque(true);

					days[i].setEnabled(now.isBefore(ld) || now.isEqual(ld));
					days[i].setBackground(ld.isEqual(now) ? Color.orange : null);
				}
			}
		}
	}

	class TimeLine extends JPanel {
		JPanel w, scr;
		LocalTime now = LocalTime.now();
		LocalTime time = LocalTime.of(now.getHour() + 1, 0);
		JLabel tit, prev, next;
		ArrayList<JLabel> times = new ArrayList<>();

		public TimeLine() {
			setLayout(new GridLayout(1, 0, 10, 0));

			add(w = new JPanel(new GridLayout(0, 1)));
			add(scr = new JPanel(new BorderLayout()));

			scr.add(prev = lbl("▲", 0), "North");
			scr.add(next = lbl("▼", 0), "South");

			scr.setBorder(new LineBorder(Color.black));

			setTimeline();

			prev.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (time.minusHours(7).getHour() - 1 == now.getHour()) {
						return;
					}

					if (time.minusHours(15).isBefore(now) && cal.date.equals(cal.now)) {
						time = LocalTime.of(now.getHour() + 1, 0);
					} else {
						time = time.minusHours(15);
					}
					setTimeline();
				}
			});
			next.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					System.out.println(time);
					if (time.equals(LocalTime.of(0, 0)) || time.equals(LocalTime.of(23, 0))) {
						return;
					}
					setTimeline();
				}
			});

		}

		void setTimeline() {
			w.removeAll();
			w.add(sz(tit = lbl("시간", 0), 0, 100));

			while (w.getComponentCount() < 16 && time.isBefore(LocalTime.of(23, 40))
					&& !time.equals(LocalTime.of(0, 0))) {
				var l = lbl(time + "", 0);
				w.add(l);
				times.add(l);
				l.setOpaque(true);
				l.setBackground(time.getHour() == now.getHour() + 1 && time.getMinute() == 0 && cal.date.equals(cal.now)
						? Color.orange
						: null);
				l.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						var l = ((JLabel) e.getSource());
						times.forEach(a -> a.setBackground(null));
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
