package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public class SeatFrame extends BaseFrame {
	JLabel seats[][] = new JLabel[10][10], costs[][] = new JLabel[3][8], lblCost[] = new JLabel[2], lblCnt;

	String t_no, range, theater, txt1, txt2;
	ArrayList<Object> movie;
	LocalDateTime date;

	int cnt[] = new int[3];

	public SeatFrame(String t_no, ArrayList<Object> movie, LocalDateTime date) {
		super("좌석 선택", 1300, 800);
		this.t_no = t_no;
		this.movie = movie;
		this.date = date;

		ui();

		setVisible(true);
	}

	private void ui() {
		add(c = new JPanel(new BorderLayout(20, 20)));
		add(s = new JPanel(new FlowLayout(0, 10, 5)), "South");
		c.add(cc = new JPanel(new GridLayout(10, 10, 5, 5)));

		var rs = getRows("select * from reservation where r_time = ? and r_date = ? and m_no = ?",
				date.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")), date.toLocalDate(), movie.get(0));

		var lblScreen = lbl("SCREEN", 0, 13);
		c.add(lblScreen, "North");

		for (char i = 'A'; i <= 'J'; i++) {
			for (int j = 0; j < 10; j++) {
				var seat = i + String.format("%02d", j + 1);
				var flag = rs.stream().map(x -> x.get(6).toString())
						.filter(x -> Arrays.asList(x.split("\\.")).contains(seat)).findFirst().isPresent();
				seats[i - 'A'][j] = new JLabel(seat, 0) {
					@Override
					protected void paintComponent(Graphics g) {
						super.paintComponent(g);
						var g2 = (Graphics2D) g;

						if (flag) {
							g2.setColor(Color.white);
							g2.drawLine(0, 0, getWidth(), getHeight());
							g2.drawLine(getWidth(), 0, 0, getHeight());
						}
					}
				};

				var lblSeat = seats[i - 'A'][j];

				if (flag) {
					lblSeat.setBackground(Color.white);
				} else {
					lblSeat.addMouseListener(new MouseAdapter() {
						@Override
						public void mousePressed(MouseEvent e) {
							var selCount = Stream.of(seats).map(Arrays::asList).flatMap(x -> x.stream())
									.filter(x -> x.getName() != null).count();
							int maxCount = IntStream.of(cnt).sum();

							if (lblSeat.getName() == null) {
								if (maxCount == 0) {
									eMsg("관람 인원을 선택해주세요.");
									return;
								}

								if (selCount == maxCount) {
									eMsg("이미 좌석을 선택했습니다.");
									return;
								}

								lblSeat.setBackground(red);
								lblSeat.setName("A");
							} else {
								lblSeat.setBackground(Color.gray);
								lblSeat.setName(null);
							}
						}

						@Override
						public void mouseEntered(MouseEvent e) {
							if (lblSeat.getName() != null)
								return;

							lblSeat.setBackground(red);
						}

						@Override
						public void mouseExited(MouseEvent e) {
							if (lblSeat.getName() != null)
								return;

							lblSeat.setBackground(Color.gray);
						};
					});

					lblSeat.setBackground(Color.gray);
				}

				lblSeat.setOpaque(true);
				lblSeat.setForeground(Color.white);
			}
		}

		for (int i = 0; i < 10; i++) {
			for (int j = 0, k = 0; j < 11; j++) {
				if (j == 2) {
					var box = Box.createGlue();
					box.setBackground(Color.DARK_GRAY);
					cc.add(box);
				} else {
					cc.add(seats[i][k++]);
				}
			}
		}

		s.add(new JLabel(getIcon("./지급자료/image/movie/" + movie.get(0) + ".jpg", 100, 150)));
		{
			var tmp = new JPanel(new GridLayout(0, 1));

			tmp.add(lbl("<html><font color='white'>" + movie.get(1), 2, 13));
			tmp.add(lbl("<html><font color='white'>" + m_age[toInt(movie.get(5))], 2));

			tmp.setBorder(new MatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));

			s.add(sz(tmp, 250, 150));
		}

		{
			var tmp = new JPanel(new GridLayout(0, 1));
			var cap = "극장,일시,인원".split(",");
			var str = new String[] { theater = getOne("select t_name from theater where t_no = ?", t_no),
					range = date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd(E) HH:mm")) + "-"
							+ date.plusMinutes(toInt(movie.get(4))).format(DateTimeFormatter.ofPattern("HH:mm")),
					"0 명" };

			for (int i = 0; i < cap.length; i++) {
				var temp = new JPanel(new BorderLayout(10, 10));
				var l1 = lbl(cap[i], 2);
				var l2 = lbl(str[i], 2);

				temp.add(l1, "West");
				temp.add(lblCnt = l2);

				l1.setForeground(Color.LIGHT_GRAY);
				l2.setForeground(Color.lightGray);

				tmp.add(temp);
			}

			tmp.setBorder(new MatteBorder(0, 0, 0, 1, Color.lightGray));

			s.add(sz(tmp, 250, 150));
		}

		{
			var tmp = new JPanel(new GridLayout(0, 1, 5, 5));

			var cap = "일반,청소년,우대".split(",");
			for (int i = 0; i < cap.length; i++) {
				int idx1 = i;
				var temp = new JPanel(new BorderLayout());
				var tempC = new JPanel(new GridLayout(1, 0, 5, 0));

				temp.add(sz(lbl("<html><font color='white'>" + cap[i], 2), 60, 30), "West");
				temp.add(tempC);

				for (int j = 0; j < 8; j++) {
					int idx2 = j + 1;
					costs[i][j] = new JLabel(idx2 + "", 0) {
						@Override
						public void setOpaque(boolean isOpaque) {
							super.setOpaque(true);
						};
					};
					costs[i][j].addMouseListener(new MouseAdapter() {
						int ridx = idx1, nums = idx2;

						@Override
						public void mousePressed(MouseEvent e) {
							cnt[ridx] = nums;

							price();

							if (e.getButton() == 1) {
								var me = (JLabel) e.getSource();

								if (IntStream.of(cnt).sum() > 8) {
									eMsg("관람인원은 최대 8명입니다.");

									for (var r : costs) {
										for (var c : r) {
											c.setBackground(Color.gray);
											c.setForeground(Color.LIGHT_GRAY);
										}
									}

									cnt[0] = cnt[1] = cnt[2] = 0;

									price();
									return;
								}

								for (var p : tempC.getComponents()) {
									p.setBackground(Color.gray);
									p.setForeground(Color.LIGHT_GRAY);
								}

								me.setBackground(Color.LIGHT_GRAY);
								me.setForeground(Color.gray);
							}
						}
					});

					costs[i][j].setBackground(Color.gray);
					costs[i][j].setForeground(Color.LIGHT_GRAY);

					tempC.add(sz(costs[i][j], 30, 30));
				}
				tmp.add(temp);
			}
			tmp.add(lbl("<html><font color='red'>최대 8명 선택가능", 0));

			tmp.setBorder(new MatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));

			s.add(tmp);
		}

		{
			var tmp = new JPanel(new GridLayout(1, 0, 10, 10));

			for (int k = 0; k < lblCost.length; k++) {
				lblCost[k] = lbl("", (k + 1) * 2);
				lblCost[k].setForeground(Color.LIGHT_GRAY);

				tmp.add(lblCost[k]);
			}

			s.add(tmp);
		}

		s.add(btnRound("<html><center>><br>결제하기", a -> {
			if (user == null) {
				var ans = JOptionPane.showConfirmDialog(null, "로그인이 필요한 작업입니다.\n로그인 하시곘습니까?", "질문",
						JOptionPane.YES_NO_OPTION);
				if (ans == JOptionPane.YES_OPTION) {
					new LoginFrame().addWindowListener(new Before(this));
				}

				return;
			}

			int sum = IntStream.of(cnt).sum();
			var selCnt = Stream.of(seats).map(Arrays::asList).flatMap(x -> x.stream()).filter(x -> x.getName() != null)
					.count();

			if (sum == 0) {
				eMsg("관람 인원을 선택해주세요.");
				return;
			}

			if (selCnt != sum) {
				eMsg("관람 인원과 좌석 수가 일치하지 않습니다.");
				return;
			}

			var tmp = new JPanel(new BorderLayout(10, 10));
			var tmpC = new JPanel(new GridLayout(1, 0));
			var tmpS = new JPanel(new GridLayout(0, 1));
			var l1 = lbl("<html>" + movie.get(1) + "<br>" + theater + "<br>" + range, 2);
			var dc = 1 - toInt(getOne("select gr_criteria from grade where gr_no = ?", user.get(6))) * 0.01;
			var cost = new int[] { 14000, 10000, 5000 };
			var seat = Stream.of(seats).map(Arrays::asList).flatMap(x -> x.stream()).filter(x -> x.getName() != null)
					.map(JLabel::getText).collect(Collectors.joining(","));

			sum = 0;
			for (int i = 0; i < cost.length; i++) {
				sum += cost[i] * cnt[i];
			}

			tmp.add(l1, "North");
			tmp.add(tmpC);
			tmp.add(tmpS, "South");

			txt1 += getOne("select gr_name from grade where g_no = ?", user.get(6));
			txt2 += (1 - dc) * 100 + "% 할인";

			tmpC.add(lbl(txt1, 2));
			tmpC.add(lbl(txt2, 4));

			tmpS.add(lbl("총 : " + new DecimalFormat("#,##0").format(sum * dc), 2));
			tmpS.add(lbl("결제하시겠습니까?", 2));

			var ans = JOptionPane.showConfirmDialog(null, tmp, "질문", JOptionPane.YES_NO_OPTION);
			if (ans == JOptionPane.YES_OPTION) {
				execute("insert reservation values(0, ?, ?, ?, ?, ?, ?, ?)", user.get(0), movie.get(0), t_no,
						date.toLocalDate(), date.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")), seat, sum);
				iMsg("예매가 완료되었습니다.");
				dispose();
			}
		}));

		price();

		lblScreen.setBackground(Color.LIGHT_GRAY);
		c.setBorder(new EmptyBorder(10, 300, 30, 300));
		c.setBackground(Color.DARK_GRAY);
		s.setBorder(new EmptyBorder(5, 20, 5, 20));
		s.setBackground(Color.black);

		opaque(c, false);
		opaque(s, false);
		opaque(cc, true);
		lblScreen.setOpaque(true);
	}

	private void price() {
		var txt1 = "<html>";
		var txt2 = "<html><p align='right'>";
		var cap = "일반,청소년,우대".split(",");
		var cost = new int[] { 14000, 10000, 5000 };
		int tot = 0;

		for (int i = 0; i < cap.length; i++) {
			if (cnt[i] == 0)
				continue;

			txt1 += cap[i] + "<br>";
			txt2 += new DecimalFormat("#,##0").format(cost[i]) + "원 * " + cnt[i] + "<br>";

			tot += cost[i];
		}

		this.txt1 = txt1;
		this.txt2 = txt2;

		txt1 += "총금액";
		txt2 += "<font color='red'>" + new DecimalFormat("#,##0").format(tot) + "원";

		lblCnt.setText(IntStream.of(cnt).sum() + " 명");
		lblCost[0].setText(txt1);
		lblCost[1].setText(txt2);
	}
}
