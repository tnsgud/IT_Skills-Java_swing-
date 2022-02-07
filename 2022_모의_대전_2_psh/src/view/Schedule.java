package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Stream;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import db.DB;
import tool.Tool;

public class Schedule extends BaseFrame implements Tool {
	LocalDate today = LocalDate.of(2021, 10, 5);
	JLabel prev = lbl("◀", 0, 20), cur = lbl(today.getMonthValue() + "월", 0, 20), next = lbl("▶", 0, 20);

	String filter = "", cap[] = "뮤지컬,오페라,콘서트".split(","), cap2[] = "일,월,화,수,목,금,토".split(",");

	TypeBox types[] = { new TypeBox("M", "뮤지컬"), new TypeBox("O", "오페라"), new TypeBox("C", "콘서트") };
	DayBox days[] = new DayBox[42];

	ArrayList<TypeBox> contents = new ArrayList<Schedule.TypeBox>();

	public Schedule() {
		super("월별 일정", 800, 800);

		ui();

		setVisible(true);
	}

	private void ui() {
		var nn = new JPanel(new FlowLayout(0));
		var nc = new JPanel(new FlowLayout(2));

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new GridLayout(0, 7)));

		n.add(nn, "North");
		n.add(nc);

		nn.add(prev);
		nn.add(cur);
		nn.add(next);

		for (var b : new JLabel[] { prev, next }) {
			b.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (e.getSource() == next) {
						if (today.getMonthValue() == 12) {
							return;
						}
						today = today.plusMonths(1);
					} else {
						if (today.getMonthValue() == 1) {
							return;
						}
						today = today.minusMonths(1);
					}
					setDay();
				}
			});
		}

		for (int i = 0; i < types.length; i++) {
			nc.add(types[i]);
		}

		nc.add(btn("전체", a -> {
		}));

		for (int i = 0; i < cap2.length; i++) {
			var l = lbl(cap2[i], 0, 15);
			if (i == 0) {
				l.setForeground(Color.red);
			} else if (i == 6) {
				l.setForeground(Color.blue);
			}

			c.add(l);
		}

		for (int i = 0; i < 42; i++) {
			c.add(days[i] = new DayBox(i));
			if (i % 7 == 0) {
				days[i].day.setForeground(Color.red);
			} else if (i % 7 == 6) {
				days[i].day.setForeground(Color.blue);
			}
		}

		setDay();
	}

	private void setDay() {
		contents.clear();
		var year = today.getYear();
		var month = today.getMonthValue();
		cur.setText(month + "월");

		var sdate = LocalDate.of(year, month, 1);
		var sday = sdate.getDayOfWeek().getValue() % 7;

		for (int i = 0; i < 42; i++) {
			var tmp = sdate.plusDays(i - sday);
			days[i].day.setText(tmp.getDayOfMonth() + "");

			if (tmp.getMonthValue() != month) {
				days[i].setVisible(false);
			} else {
				days[i].setVisible(true);
			}

			days[i].clearContents();

			for (var r : DB.getArray("select left(pf_no, 1), p_name ,p_no from perform where p_date = ?", tmp)) {
				final var pno = r.get(2);
				var t = new TypeBox(r.get(0), r.get(1));
				contents.add(t);
				days[i].addTypeBox(t);
				t.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						if (e.getClickCount() == 2) {
							BaseFrame.pno = toInt(pno);
							new Reserve().addWindowListener(new Before(Schedule.this));
						}
					}
				});
			}
		}

		if (!filter.equals("")) {
			contents.stream().filter(a -> !a.type.getText().equals(filter)).forEach(a -> a.setVisible(false));
		}
		
		repaint();
		revalidate();
	}

	class TypeBox extends JPanel {
		JLabel type, title;

		public TypeBox(String t, String tit) {
			type = lbl(t, 0);
			title = lbl(tit, 0);
			type.setOpaque(true);

			if (t.equals("M")) {
				type.setBackground(Color.magenta);
			} else if (t.equals("O")) {
				type.setBackground(Color.blue);
			} else {
				type.setBackground(Color.yellow);
			}

			type.setBorder(new LineBorder(Color.black));
			type.setForeground(Color.white);
			sz(type, 20, 20);

			add(type);
			add(title);
		}
	}

	class DayBox extends JPanel {
		JLabel day;
		JPanel c;

		public DayBox(int day) {
			setLayout(new BorderLayout());
			add(this.day = lbl(day + "", 4), "North");
			add(c = new JPanel(new FlowLayout(0)));
			setBorder(new LineBorder(Color.black));
		}

		void clearContents() {
			c.removeAll();
			c.setLayout(new FlowLayout(0));
		}

		void addTypeBox(TypeBox tb) {
			c.add(tb);
		}
	}

	public static void main(String[] args) {
		new Schedule();
	}
}
