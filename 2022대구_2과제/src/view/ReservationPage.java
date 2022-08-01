package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Arrays;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

import view.BaseFrame.Before;

public class ReservationPage extends BasePage {
	JLabel lblArea[] = new JLabel[9], lblTheater;
	JPanel jpDate[] = new JPanel[15];

	public static void main(String[] args) {
		new MainFrame();
	}

	public ReservationPage() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		System.out.println(LocalDate.now());

		ui();
	}

	private void ui() {
		add(n = sz(new JPanel(new BorderLayout(0, 0)), 1100, 180));
		add(Box.createVerticalStrut(20));
		add(c = sz(new JPanel(new BorderLayout()), 800, 120));
		add(Box.createVerticalStrut(20));
		add(s = new JPanel(new GridLayout(0, 1, 10, 10)));

		n.add(nn = new JPanel(new GridLayout(0, 9, 0, 0)), "North");
		n.add(nc = new JPanel(new GridLayout(0, 11)));

		var rs = getRows("select * from area");
		for (var r : rs) {
			int i = rs.indexOf(r);

			lblArea[i] = lbl(r.get(1).toString(), 0, 13, e -> {
				var me = lblArea[i];
				for (var l : lblArea) {
					l.setBorder(null);
				}

				me.setBorder(new LineBorder(Color.white, 3));

				addTheater(r.get(0).toString());
			});
			lblArea[i].setOpaque(false);

			lblArea[i].setForeground(Color.white);
			nn.add(sz(lblArea[i], 120, 30));
		}

		lblArea[0].setBorder(new LineBorder(Color.white, 5));

		addTheater(rs.get(0).get(0).toString());

		c.add(lblTheater = lbl(" ", 2, 25), "North");
		c.add(cc = sz(new JPanel(null), 60, 60));

		var now = LocalDate.now();
		for (int i = 0; i < 15; i++) {
			var cur = now;
			var item = new JPanel(new BorderLayout());

			String month = String.format("%02d", cur.getMonthValue()),
					week = cur.getDayOfWeek().getDisplayName(TextStyle.SHORT, getLocale()),
					day = String.format("%02d", cur.getDayOfMonth());

			var l1 = lbl("<html>" + month + "월" + week, 2, 13);
			var l2 = lbl(day, 0, 30);

			l1.setForeground(Color.lightGray);
			l2.setForeground(Color.LIGHT_GRAY);

			item.add(sz(l1, 30, 0), "West");
			item.add(l2);

			cc.add(item).setBounds(i * 80 + 10, 0, 80, 80);

			jpDate[i] = item;

			item.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (lblTheater.getText().trim().isEmpty())
						return;

					if (e.getButton() == 1) {
						for (var i : cc.getComponents()) {
							((JLabel) ((JPanel) i).getComponent(0)).setForeground(Color.LIGHT_GRAY);
							((JLabel) ((JPanel) i).getComponent(1)).setForeground(Color.LIGHT_GRAY);
						}

						timeTable(cur);

						((JLabel) item.getComponent(0)).setForeground(Color.black);
						((JLabel) item.getComponent(1)).setForeground(Color.black);
					}
				}
			});

			now = now.plusDays(1);
		}

		n.setBackground(navy);
		n.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), new LineBorder(Color.WHITE)));
		opaque(n, false);
		n.setOpaque(true);

		c.setBorder(new MatteBorder(0, 0, 2, 0, Color.black));
		c.setAlignmentX(CENTER_ALIGNMENT);

		s.setAlignmentX(CENTER_ALIGNMENT);
	}

	private void timeTable(LocalDate date) {
		s.removeAll();

		var movies = getRows(
				"select m.* from theater t, movie m where find_in_set(m.m_no, replace(t.m_no, '.',',')) > 0 and t_no = ?",
				lblTheater.getName());

		System.out.println("cur:"+date);
		
		for (var m : movies) {
			var jpTitle = new JPanel(new FlowLayout(0));
			var tmp = new JPanel(new BorderLayout());
			var tmpc = new JPanel(new GridLayout(0, 4, 0, 20));
			var genre = mapToGenre(m.get(3).toString());
			var lblSummray = lbl(genre + " / " + m.get(4) + "분 / " + m.get(6), 2, 12);
			var sdate = LocalDateTime.of(date, LocalTime.of(6, 0));
			var edate = LocalDateTime.of(date.plusDays(1), LocalTime.of(0, 0));

			tmp.add(jpTitle, "North");
			tmp.add(tmpc);

			jpTitle.add(lblAgeLimit(m.get(5).toString()));
			jpTitle.add(lbl(m.get(1).toString(), 2, 18));
			jpTitle.add(lblSummray);

			lblSummray.setForeground(Color.LIGHT_GRAY);

			while (!sdate.isAfter(edate)) {
				var d = sdate;

				if (sdate.isAfter(LocalDateTime.now())) {
					var jpScreen = new JPanel(new BorderLayout());
					var left = 100 - getRows("select * from reservation where r_time = ? and r_date = ? and m_no = ?",
							sdate.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")), date,
							m.get(0)).stream().flatMap(a -> Arrays.asList(a.get(6).toString().split("\\.")).stream())
									.distinct().count();
					var lblTime = lbl(sdate.toLocalTime().toString(), 0, 20);
					var lblSeat = lbl(left + "석", 0, 15);

					lblSeat.setForeground(new Color(0, 123, 255));

					if (left == 0) {
						lblTime.setForeground(Color.LIGHT_GRAY);
						lblSeat.setForeground(Color.LIGHT_GRAY);
						lblSeat.setText("예매종료");
					}

					jpScreen.add(lblTime);
					jpScreen.add(lblSeat, "South");

					tmpc.add(sz(jpScreen, 70, 70));

					jpScreen.setBorder(new LineBorder(Color.LIGHT_GRAY));
					jpScreen.addMouseListener(new MouseAdapter() {
						@Override
						public void mousePressed(MouseEvent e) {
							if (lblTime.getForeground().equals(Color.LIGHT_GRAY))
								return;

							if (BaseFrame.user == null) {
								var ans = JOptionPane.showConfirmDialog(null, "로그인이 필요한 작업입니다.\n로그인 하시겠습니까?", "질문",
										JOptionPane.YES_NO_OPTION);
								if (ans == JOptionPane.YES_OPTION) {
									new LoginFrame().addWindowListener(new Before(BasePage.cf));
									return;
								} else {
									return;
								}
							}
							
							new SeatFrame(lblTheater.getName(), m, d).addWindowListener(new Before(BasePage.cf));
						}
					});
				}
				sdate = sdate.plusMinutes(30 + toInt(m.get(4)));
			}

			tmp.setBorder(new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
			s.add(sz(tmp, 800, 200));
		}

		s.repaint();
		s.revalidate();
	}

	private void addTheater(String a_no) {
		nc.removeAll();

		for (var r : getRows("select * from theater where a_no = ?", a_no)) {
			var lbl = lbl(r.get(1).toString(), 0, 13, e -> {
				if (e.getButton() == 1) {
					lblTheater.setName(r.get(0).toString());
					lblTheater.setText(r.get(1).toString());
				}

				for (int i = 0; i < jpDate.length; i++) {
					((JLabel) jpDate[i].getComponent(0)).setForeground(Color.LIGHT_GRAY);
					((JLabel) jpDate[i].getComponent(1)).setForeground(Color.LIGHT_GRAY);
				}

				((JLabel) jpDate[0].getComponent(0)).setForeground(Color.BLACK);
				((JLabel) jpDate[0].getComponent(1)).setForeground(Color.BLACK);
				timeTable(LocalDate.now());
			});

			lbl.setForeground(Color.white);
			nc.add(sz(lbl, 120, 30));
		}

		nc.repaint();
		nc.revalidate();
	}
}
