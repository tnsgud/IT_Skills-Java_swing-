package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class MainPage extends BasePage {

	int curH = 0;

	public MainPage() {
		ui();
	}

	private void ui() {
		setBorder(new EmptyBorder(10, 10, 10, 10));

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new BorderLayout()));
		add(s = new JPanel(new GridLayout(1, 0)), "South");

		var list = new ArrayList<>();
		var max = toInt(getOne(
				"select count(*), month(date) from purchase where month(date) >= 1 and month(date) <= month('2022-08-31') group by month(date) order by count(*) desc"));
		for (var rs : rs(
				"select count(*), month(date) from purchase where month(date) >= 1 and month(date) <= month('2022-08-31') group by month(date) order by count(*)")) {
			list.add(rs.get(0) + ", " + rs.get(1));
		}

		var chart = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				var g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				for (int i = 0; i < list.size(); i++) {
					var str = list.get(i).toString().split(",");
					var height = (int) (toInt(str[0]) / (double) max * 250);

					if (height > curH) {
						height = curH;
					}

					g2.setFont(new Font("", Font.BOLD, 20));
					g2.drawString("월간 접종자 추이", 5, 20);
					g2.setColor(Color.orange);
					g2.fillRect(30 + i * 120, 300 - height, 50, height);
					g2.setColor(Color.black);
					g2.drawRect(30 + i * 120, 300 - height, 50, height);
					g2.setColor(Color.gray);
					g2.drawString(str[0], 45 + i * 120, 290 - height);
					g2.drawString(str[1] + "월", 40 + i * 120, 350);
				}
			}
		};

		n.add(lbl("<html><font color='gray'>예방접종현황", 2, 20), "North");
		n.add(nc = new JPanel(new GridLayout(1, 0)), "South");

		c.add(chart);

		for (var cap : "길찾기,프로필,로그아웃,종료".split(",")) {
			s.add(btn(cap, a -> {
				if (cap.equals("길찾기")) {
					mf.swap(new SearchPage());
				} else if (cap.equals("프로필")) {
					mf.swap(new ProfilePage());
				} else if (cap.equals("로그아웃")) {
					user = null;
					mf.swap(new LoginPage());
				} else {
					System.exit(0);
				}
			}));
		}

		var tot = (double) toInt(getOne("select count(*) from purchase where date <= '2022-08-31'"));
		for (int i = 0; i < 4; i++) {
			var tmp = sz(new JPanel(new BorderLayout(20, 10)), 1, 100);
			var cnt = toInt(
					getOne("select count(*) from purchase where shot = ? and date(date) <= '2022-08-31'", i + 1));
			var l = lbl(i + 1 + "차 접종", 0, 20);

			l.setForeground(Color.white);
			l.setBackground(Color.orange);
			l.setOpaque(true);

			tmp.add(l, "North");
			tmp.add(lbl("<html><font color='orange'><center>" + String.format("%.1f", ((double) cnt / tot) * 100.0) + "%", 0,
					15), "West");
			tmp.add(lbl("<html><center>누적 " + cnt + "<br>신규 " + cnt, 2, 15));

			nc.add(tmp);
		}

		new Thread(() -> {
			for (int i = 1; i <= 250; i++) {
				curH = i;

				chart.repaint();
				chart.revalidate();

				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	public static void main(String[] args) {
		mf.swap(new MainPage());
		mf.setVisible(true);
	}
}
