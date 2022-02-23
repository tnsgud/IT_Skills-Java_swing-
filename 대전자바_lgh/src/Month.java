import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Month extends BaseFrame {

	Type types[] = { new Type("M", "뮤지컬"), new Type("O", "오페라"), new Type("C", "콘서트") };
	DayBox box[] = new DayBox[42];
	JLabel prev, month, next;

	List<Type> list = new ArrayList<Month.Type>();

	LocalDate today = LocalDate.now();

	String filter = "";
	int y, m;

	public Month() {
		super("월별 일정", 850, 850);

		this.add(n = new JPanel(new BorderLayout()), "North");
		this.add(c = new JPanel(new GridLayout(0, 7)));

		var n_n = new JPanel(new FlowLayout(0));
		var n_c = new JPanel(new FlowLayout(2, 10, 10));

		n.add(n_n, "North");
		n.add(n_c);

		n_n.add(prev = lbl("◀", 0, 20));
		n_n.add(month = lbl(today.getMonthValue() + "월", 0, 20));
		n_n.add(next = lbl("▶", 0, 20));

		prev.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {

				if (month.getText().equals("1월")) {
					return;
				} else {
					today = today.plusMonths(-1);
					setCal();
				}

			}
		});

		next.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {

				if (month.getText().equals("12월")) {
					return;
				} else {
					today = today.plusMonths(1);
					setCal();
				}

			}
		});

		for (int i = 0; i < types.length; i++) {
			types[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (e.getClickCount() == 2) {
						filter = ((Type) e.getSource()).type.getText();
						list.forEach(a -> a.setVisible(true));
						list.stream().filter(a -> !a.type.getText().equals(filter)).forEach(b -> b.setVisible(false));
					}
				}
			});
		}

		Arrays.stream(types).forEach(n_c::add);

		n_c.add(btn("전체", e -> {
			filter = "";
			list.stream().forEach(a -> a.setVisible(true));
		}));

		for (var k : "일,월,화,수,목,금,토".split(",")) {
			var l = new JLabel(k, 0);
			c.add(l);

			if (l.getText().equals("일")) {
				l.setForeground(Color.RED);
			} else if (l.getText().equals("토")) {
				l.setForeground(Color.BLUE);
			}
		}

		for (int i = 0; i < box.length; i++) {
			c.add(box[i] = new DayBox(i));
		}

		setCal();

		this.setVisible(true);
	}

	void setCal() {
		list.clear();

		y = today.getYear();
		m = today.getMonthValue();

		month.setText(m + "월");

		var ld = LocalDate.of(y, m, 1);
		var sday = ld.getDayOfWeek().getValue() % 7;

		for (int i = 0; i < box.length; i++) {
			var tmp = ld.plusDays(i - sday);
			box[i].c.removeAll();
			box[i].days.setText(tmp.getDayOfMonth() + "");

			if (tmp.getMonthValue() != m) {
				box[i].c.setVisible(false);
				box[i].days.setVisible(false);
			} else {
				try {
					var rs = stmt.executeQuery("select * from perform where p_date = '" + tmp.toString() + "' limit 3");
					while (rs.next()) {
						var pfno = rs.getString("pf_no").split("")[0];
						var pname = rs.getString(3);
						var pno = rs.getString(1);
						var type = new Type(pfno, pname);

						box[i].c.add(type);
						list.add(type);
						
						type.addMouseListener(new MouseAdapter() {
							@Override
							public void mousePressed(MouseEvent e) {
								Month.pno = pno;
								new Reserve().addWindowListener(new Before(Month.this));
							}
						});

						sz(type, 110, 20);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				box[i].c.setVisible(true);
				box[i].days.setVisible(true);

				repaint();
				revalidate();
			}

		}
	}

	class Type extends JPanel {

		JLabel type;

		public Type(String type, String name) {
			super(new BorderLayout());

			this.add(this.type = sz(new JLabel(type, 0), 20, 20), "West");
			this.add(new JLabel(name, 2));

			if (type.equals("M")) {
				this.type.setBackground(Color.MAGENTA.brighter());
			} else if (type.equals("O")) {
				this.type.setBackground(Color.BLUE);
			} else if (type.equals("C")) {
				this.type.setBackground(Color.YELLOW);
			}

			this.type.setOpaque(true);
			this.type.setBorder(new LineBorder(Color.BLACK));
			this.type.setForeground(Color.WHITE);
		}

	}

	class DayBox extends JPanel {

		JPanel c;
		JLabel days;

		public DayBox(int day) {
			super(new BorderLayout());

			this.add(c = new JPanel(new FlowLayout(0)));
			this.add(days = new JLabel(day + "", 4), "North");

			this.setBorder(new LineBorder(Color.BLACK));
			c.setBorder(new EmptyBorder(5, 5, 5, 5));

			if (day % 7 == 0) {
				days.setForeground(Color.RED);
			} else if (day % 7 == 6) {
				days.setForeground(Color.BLUE);
			}
		}
	}
}
