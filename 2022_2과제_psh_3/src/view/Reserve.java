package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class Reserve extends BaseFrame {
	Cal cal;
	TimeLine time;
	JLabel datelbl, timelbl, pricelbl;
	JTextField txt;

	public Reserve() {
		super("예약", 800, 500);

		setLayout(new BorderLayout(5, 5));

		add(n = new JPanel(new BorderLayout()), "North");
		add(sz(cal = new Cal(), 330, 300), "West");
		add(time = new TimeLine());
		add(sz(e = new JPanel(new BorderLayout(0, 0)), 330, 300), "East");

		n.add(lblH("방탈출 예약", 0, 0, 35));
		n.add(lbl("Room Escape Resevation", 0, 12), "South");

		e.add(ec = new JPanel(new GridLayout(0, 1, 0, 0)));
		e.add(es = new JPanel(), "South");

		var rs = rs(
				"select c_name, t_name, format(c_price, '#,##0'), t_personnel from cafe c, theme t where c_no = ? and t.t_no=?",
				cno, tno).get(0);
		var cap = "날짜,지점,테마,시간,가격,인원수,총금액".split(",");
		for (int i = 0, idx = 0; i < cap.length; i++) {
			var p = new JPanel(new FlowLayout(0));
			var l1 = sz(lbl(cap[i], 2), 80, 15);
			l1.setForeground(Color.white);
			p.add(l1);
			if (i == 0) {
				p.add(datelbl = lbl(LocalDate.now() + "", 2));
				datelbl.setForeground(Color.white);
			} else if (i == 3) {
				p.add(timelbl = lbl(time.time + "", 2));
				timelbl.setForeground(Color.white);
			} else if (i == 5) {
				p.add(txt = new JTextField(20));
			} else if (i == 6) {
				p.add(pricelbl = lbl("0", 2));
				pricelbl.setForeground(Color.white);
			} else {
				var l = lbl(rs.get(idx++) + "", 2);
				p.add(l);
				l.setForeground(Color.white);
			}
			p.setBackground(Color.black);

			ec.add(p);
		}

		txt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				var in = toInt(txt.getText());
				if (in == -1 || (in + "").length() != txt.getText().length()) {
					eMsg("인원수를 확인하세요.");
					txt.setText("");
					txt.requestFocus();
					return;
				}

				if (in > toInt(rs.get(3))) {
					eMsg("인원을 초과하였습니다.");
					txt.setText("");
					txt.requestFocus();
					return;
				}

				var format = new DecimalFormat("#,##0");
				pricelbl.setText(format.format(toInt(txt.getText()) * toInt(rs.get(2))));
			}
		});

		for (var c : "예약,취소".split(",")) {
			es.add(btn(c, a -> {
				if (a.getActionCommand().equals("취소")) {
					dispose();
				} else {
					if (txt.getText().isEmpty()) {
						eMsg("빈칸이 있습니다.");
						return;
					}

					iMsg("에약이 완료되었습니다.");
					execute("insert into reservation values(0, ?, ?, ?, ? ,? ,? ,?)", uno, cno, tno, cal.date,
							time.time, txt.getText(), 0);
					dispose();
				}
			}));
		}

		es.setBackground(Color.black);
		e.setBackground(Color.black);
		setVisible(true);
	}

	class Cal extends JPanel {
		LocalDate date = LocalDate.now();
		JPanel n, c;
		JLabel prev, next, d, days[] = new JLabel[42];

		public Cal() {
			setLayout(new BorderLayout());

			add(n = new JPanel(new BorderLayout()), "North");
			add(c = new JPanel(new GridLayout(0, 7)));

			n.add(prev = lbl("◁", 0, 20), "West");
			n.add(d = lbl("asdfsdfdsfdsf", 0, 20));
			n.add(next = lbl("▷", 0, 20), "East");
			n.add(lblH("SUN MON TUE WED THU FRI SAT", 0, 0, 25), "South");

			for (int i = 0; i < days.length; i++) {
				c.add(days[i] = new JLabel("", 0));
				days[i].addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent e) {
						var l = (JLabel) e.getSource();
						if (!l.isEnabled())
							return;
						Stream.of(days).forEach(a -> a.setBackground(null));
						l.setOpaque(true);
						l.setBackground(Color.orange);

						date = LocalDate.of(date.getYear(), date.getMonthValue(), toInt(l.getText()));
						d.setText(date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + ", "
								+ date.getDayOfMonth() + ", " + date.getYear());
						if (date.equals(LocalDate.now())) {
							time.time = LocalTime.of(time.now.getHour() + 1, 0);
						} else {
							time.time = LocalTime.of(0, 30);
						}
						datelbl.setText(date + "");
						time.setTimeLine();
					};
				});
			}

			setCal();

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

			Stream.of(prev, next, d).forEach(l -> {
				l.setForeground(Color.white);
				l.setBackground(Color.black);
				l.setOpaque(true);
			});
		}

		private void setCal() {
			var start = LocalDate.of(date.getYear(), date.getMonthValue(), 1);
			var startd = start.getDayOfWeek().getValue() % 7;

			d.setText(date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + ", " + date.getDayOfMonth()
					+ ", " + date.getYear());
			for (int i = 0; i < days.length; i++) {
				var ld = start.plusDays(i - startd);
				days[i].setVisible(false);
				days[i].setOpaque(false);
				days[i].setEnabled(false);
				days[i].setForeground(null);

				if (ld.getMonthValue() == date.getMonthValue()) {
					days[i].setText(ld.getDayOfMonth() + "");
					days[i].setVisible(true);
					days[i].setOpaque(true);

					days[i].setEnabled(LocalDate.now().isBefore(ld) || LocalDate.now().isEqual(ld));
					days[i].setBackground(LocalDate.now().isEqual(ld) ? Color.orange : null);
				}
			}
		}
	}

	class TimeLine extends JPanel {
		JPanel w, scr;
		LocalTime now = LocalTime.now(), time = LocalTime.of(now.getHour() + 1, 0);
		JLabel prev, tit, next;
		ArrayList<JLabel> times = new ArrayList<>();

		public TimeLine() {
			setLayout(new GridLayout(1, 0, 10, 0));

			add(w = new JPanel(new GridLayout(0, 1)));
			add(scr = new JPanel(new BorderLayout()));

			scr.add(prev = lbl("▲", 0), "North");
			scr.add(next = lbl("▼", 0), "South");

			scr.setBorder(new LineBorder(Color.black));

			setTimeLine();

			prev.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (time.minusHours(7).getHour() - 1 == now.getHour()) {
						return;
					}

					if (time.minusHours(15).isBefore(now) && cal.date.equals(LocalDate.now())) {
						time = LocalTime.of(now.getHour() + 1, 0);
					} else {
						time = time.minusHours(15);
					}

					setTimeLine();
				}
			});

			next.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (time.equals(LocalTime.of(0, 0)) || time.equals(LocalTime.of(23, 30))) {
						return;
					}

					setTimeLine();
				}
			});
		}

		public void setTimeLine() {
			w.removeAll();
			w.add(sz(tit = lbl("시간", 0), 0, 100));

			while (w.getComponentCount() < 16 && time.isBefore(LocalTime.of(23, 40))
					&& !time.equals(LocalTime.of(0, 0))) {
				var l = lbl(time + "", 0);
				w.add(l);
				times.add(l);
				l.setOpaque(true);
				l.setBackground(
						time.getHour() == now.getHour() + 1 && time.getMinute() == 0 && cal.date.equals(LocalDate.now())
								? Color.orange
								: null);
				l.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						var l = (JLabel) e.getSource();
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
		cno = "A-001";
		tno = 14;
		uno = 1;
		new Reserve();
	}
}
