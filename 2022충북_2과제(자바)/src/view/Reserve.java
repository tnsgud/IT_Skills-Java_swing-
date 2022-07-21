package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.TextStyle;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class Reserve extends BaseFrame {
	LocalDate date = now;

	public Reserve() {
		super("예매", 1200, 500);

		add(sz(w = new JPanel(new BorderLayout()), 300, 0), "West");
		add(c = new JPanel(new BorderLayout()));
		add(e = sz(new JPanel(new BorderLayout()), 300, 0), "East");

		w.add(wc = new JPanel(new BorderLayout()));
		w.add(we = new JPanel(new BorderLayout()), "East");

		var wc1 = new JPanel(new GridLayout(0, 1));
		var wc2 = new JPanel(new GridLayout(0, 1));

		wc.add(lbl("영화관", 0, 25), "North");
		wc.add(wc1, "West");
		wc.add(new JScrollPane(wc2));

		for (var rs : getRows("select * from area")) {
			var l = lbl(rs.get(1).toString(), 2, 15);

			l.setOpaque(true);
			l.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					for (var lbl : wc1.getComponents()) {
						lbl.setBackground(null);
					}

					((JLabel) e.getSource()).setBackground(Color.white);

					setTheater(wc2, toInt(rs.get(0)));
				}
			});

			wc1.add(sz(l, 150, 20));
		}

		var we1 = new JPanel(new GridLayout(0, 1));

		we.add(lbl("날짜", 0, 25), "North");
		we.add(we1);

		for (int i = 0; i < 7; i++) {
			var d = date.plusDays(i);
			var tmp = new JPanel(new FlowLayout(0));
			var tmp_w = new JPanel(new GridLayout(0, 1));

			tmp.add(tmp_w);
			tmp.add(lbl(String.format("%02d", d.getDayOfMonth()), 2, 15));

			tmp_w.add(lbl(String.format("%02d", d.getMonthValue()), 2, 12));
			tmp_w.add(lbl(d.getDayOfWeek().getDisplayName(TextStyle.SHORT, getLocale()), 2, 12));

			tmp.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					for (var tmp : we1.getComponents()) {
						((JPanel) tmp).setBorder(null);
					}

					((JPanel) e.getSource()).setBorder(new LineBorder(Color.black));

					date = d;
				}
			});

			we1.add(tmp);
		}

		c.add(lbl("영화 선택", 0, 30), "North");
		c.add(new JScrollPane(cc = new JPanel(new GridLayout(0, 1))));

		e.add(lbl("시간", 0, 30), "North");
		e.add(ec = new JPanel(new GridLayout(0, 1)));

		((JLabel) wc1.getComponent(0)).setBackground(Color.white);
		((JPanel) we1.getComponent(0)).setBorder(new LineBorder(Color.black));

		setTheater(wc2, 1);
		resetMovie();

		setVisible(true);
	}

	private void resetMovie() {
		cc.removeAll();

		for (var rs : getRows("select m_no, m_rating, m_name from movie")) {
			var tmp = new JPanel(new BorderLayout(5, 5));

			tmp.add(new JLabel(getIcon("./datafile/아이콘/" + rs.get(1) + ".png", 25, 25)), "West");
			tmp.add(lbl(rs.get(2).toString(), 2));

			tmp.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					eMsg("지점을 먼저 선택해주세요.");
				}
			});

			cc.add(tmp);
		}

		cc.repaint();
		cc.revalidate();
	}

	private void setMovie() {
		cc.removeAll();

		for (var rs : getRows(
				"select m.m_no, m_rating, m_name, s.sc_no from movie m, schedule s where m.m_no = s.m_no and t_no = ? and sc_date = ? group by m.m_no",
				t_no, date.toString())) {
			var tmp = new JPanel(new BorderLayout(5, 5));

			tmp.add(new JLabel(getIcon("./datafile/아이콘/" + rs.get(1) + ".png", 25, 25)), "West");
			tmp.add(lbl(rs.get(2).toString(), 2));

			tmp.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					for (var com : cc.getComponents()) {
						com.setBackground(null);
					}

					((JPanel) e.getSource()).setBackground(Color.white);

					m_no = toInt(rs.get(0));

					ec.removeAll();

					for (var rs : getRows(
							"select p_name, sc_theater from pomaes p, schedule sc where sc.p_no = p.p_no and t_no=? and sc_date=? and m_no=? group by sc_theater order by sc_theater asc",
							t_no, date.toString(), m_no)) {
						var tmp = new JPanel(new FlowLayout(0, 5, 5));

						tmp.setBorder(new TitledBorder(new LineBorder(Color.black), rs.get(0) + " " + rs.get(1)));

						for (var r : getRows(
								"select sc_no, sc_time from schedule where t_no=? and sc_date=? and m_no=? and sc_theater = ?",
								t_no, date.toString(), m_no, rs.get(1))) {
							var temp = new JPanel(new GridLayout(0, 1));

							temp.add(lbl(r.get(1).toString(), 0, 15));
							temp.add(lbl("<html><font color='blue'>"
									+ getOne("select 60-ifnull(sum(r_people), 0) from reservation where sc_no=?",
											r.get(0))
									+ "/60", 0));
							temp.setName(r.get(0).toString());

							temp.addMouseListener(new MouseAdapter() {
								@Override
								public void mousePressed(MouseEvent e) {
									if (!isLogin) {
										var ans = JOptionPane.showConfirmDialog(null, "로그인이 필요한 서비스 입니다.\n로그인 하시겠습니까?",
												"로그인", JOptionPane.YES_NO_OPTION);
										if (ans == JOptionPane.YES_OPTION) {
											new Login().addWindowListener(new Before(Reserve.this));
										}

										return;
									}

									sc_no = toInt(((JPanel) e.getSource()).getName());
									new SheetSelect().addWindowListener(new Before(Reserve.this));
								};
							});

							temp.setBorder(new LineBorder(Color.black));

							tmp.add(sz(temp, 60, 30));
						}

						ec.add(tmp);
					}

					ec.repaint();
					ec.revalidate();
				}
			});

			cc.add(tmp);
		}

		cc.repaint();
		cc.revalidate();
	}

	void setTheater(JPanel p, int a_no) {
		p.removeAll();

		for (var r : getRows("select * from theater where a_no=?", a_no)) {
			var l = lbl(r.get(2).toString(), 2);

			l.setOpaque(true);
			l.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					t_no = toInt(r.get(0));

					((JLabel) e.getSource()).setBackground(Color.white);

					setMovie();
				}
			});

			p.add(l);
		}

		repaint();
		revalidate();
	}

	public static void main(String[] args) {
		new Reserve();
	}
}
