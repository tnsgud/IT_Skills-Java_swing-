package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class MainPage extends BasePage {

	int curH = 0;

	public MainPage() {
		ui();
	}

	private void ui() {
		var list = new ArrayList<>();
		var max = toInt(getOne(
				"select count(*), month(date) from purchase where month(date) >= 1 and month(date) <= month('2022-08-31') group by month(date) order by count(*) desc"));
		for (var getRows : getRows(
				"select count(*), month(date) from purchase where month(date) >= 1 and month(date) <= month('2022-08-31') group by month(date) order by count(*)")) {
			list.add(getRows.get(0) + "," + getRows.get(1));
		}

		setBorder(new EmptyBorder(5, 5, 5, 5));

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				var g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				for (int i = 0; i < list.size(); i++) {
					var str = list.get(i).toString().split(",");
					var h = (int) (toInt(str[0]) / (double) max * 250);

					if (h > curH) {
						h = curH;
					}

					g2.setFont(new Font("", Font.BOLD, 20));
					g2.drawString("월간 접종자 추이", 5, 20);
					g2.setColor(Color.orange);
					g2.fillRect(30 + i * 120, 300 - h, 50, h);
					g2.setColor(Color.black);
					g2.drawRect(30 + i * 120, 300 - h, 50, h);
					g2.setColor(Color.gray);
					g2.drawString(str[0], 40 + i * 120, 290 - h);
					g2.drawString(str[1] + "월", 40 + i * 120, 350);
				}
			}
		});
		add(s = new JPanel(new GridLayout(1, 0)), "South");

		n.add(lbl("<html><font color='gray'>예방접종현황", 2), "North");
		n.add(nc = new JPanel(new GridLayout(1, 0)));

		for (var cap : "길찾기,프로필,로그아웃,종료".split(",")) {
			s.add(btn(cap, a -> {
				if(cap.equals("길찾기")) {
					mf.swapPage(new SearchPage());
				}else if(cap.equals("프로필")) {
					mf.swapPage(new ProfilePage());
				}else if(cap.equals("로그아웃")) {
					mf.swapPage(new LoginPage());
				}else {
					System.exit(0);
				}
			}));
		}

		var tot = (double) toInt(getOne("select count(*) from purchase where date <= '2022-08-31'"));
		for (int i = 0; i < 4; i++) {
			var tmp = sz(new JPanel(new BorderLayout(20, 10)), 1, 100);
			var tmp_c = new JPanel(new FlowLayout(1));
			var cnt = toInt(getOne("select count(*) from purchase where shot=? and date <= '2022-08-31'", i + 1));
			var l = lbl(i + 1 + "차 접종", 0, 20);

			l.setForeground(Color.white);
			l.setBackground(Color.orange);
			l.setOpaque(true);

			tmp.add(l, "North");
			tmp.add(tmp_c);
			tmp_c.add(lbl("<html><font color='orange'>" + String.format("%.1f", ((double) cnt / tot) * 100.0)
					+ "%", 4, 15), "West");
			tmp_c.add(lbl("<html><center>누적 " + cnt + "<br>신규 " + getOne(
					"select ifnull((select count(user) from purchase where date=now() group by user having count(user) >0), 0) as cnt from purchase group by cnt"),
					2, 15));
			nc.add(tmp);
		}

		new Thread(() -> {
			for (int i = 1; i < 251; i++) {
				curH = i;

				c.repaint();

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
		mf.swapPage(new MainPage());
		mf.setVisible(true);
	}
}
