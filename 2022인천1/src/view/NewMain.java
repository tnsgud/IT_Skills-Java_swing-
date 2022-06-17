package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class NewMain extends BasePage {

	JPanel nc, ns;

	String str[] = "구분,신규 입원,확진".split(",");
	String bstr[] = "길찾기,프로필,로그아웃,종료".split(",");
	double totalValue = toInt(getOne("select count(*) from purchase where date(date) <= '2022-08-31'"));
	int h = 0;

	JPanel chart;
	Thread thread;

	ArrayList list = new ArrayList();
	int max = 0, curHeight = 0, maxHeight = 250;

	public NewMain() {
		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new BorderLayout()));
		add(s = new JPanel(new GridLayout(1, 0)), "South");

		setChart();

		n.add(lbl("<html><font color='gray'>예방접종현황", 2, 20), "North");
		n.add(nc = new JPanel(new GridLayout(1, 0, 50, 10)));
		n.add(ns = new JPanel(new GridLayout(1, 0, 50, 10)), "South");

		for (int i = 0; i < bstr.length; i++) {
			var lbl = lbl(bstr[i], 0, 15);

			lbl.setBorder(new LineBorder(Color.LIGHT_GRAY));

			s.add(lbl);
		}

		for (int i = 0; i < 4; i++) {
			nc.add(lbl("<html><font color='white'>" + (i + 1) + "차 접종", 0, 20));

			var tmp = sz(new JPanel(new BorderLayout(20, 10)), 1, 100);
			var rs = getRows(
					"select count(*) from purchase where shot = " + (i + 1) + " and date(date) <= '2022-08-31'");

			tmp.add(lbl(
					"<html><font color='orange'>"
							+ String.format("%.1f", ((double) toInt(rs.get(0)) / (double) totalValue) * 100.0) + "%",
					0, 15), "West");
			tmp.add(lbl("<html>누적 " + rs.get(0) + "<br>신규 " + getOne(
					"SELECT count(*) FROM covid.purchase where date(date) = '2022-08-31' and shot = " + (i + 1)), 2,
					15));
			tmp.setBorder(new EmptyBorder(0, 10, 0, 0));

			ns.add(tmp);
		}

		nc.setBackground(Color.ORANGE);
		setBorder(new EmptyBorder(10, 10, 10, 10));
	}

	void setChart() {
		list.clear();

		max = toInt(getOne(
				"select count(*), month(date) from purchase where month(date) >= 1 and month(date) <= month('2022-08-31') group by month(date) order by count(*) desc"));

		for (var rs : getRows(
				"select count(*), month(date) from purchase where month(date) >= 1 and month(date) <= month('2022-08-31') group by month(date) order by month(date)")) {
			list.add(rs.get(0)+", "+rs.get(1));
		}

		chart = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				for (int i = 0; i < list.size(); i++) {
					String strs[] = list.get(i).toString().split(",");
					int myHeight = (int) (Integer.parseInt(strs[0]) / (double) max * maxHeight);

					if (myHeight > curHeight)
						myHeight = curHeight;

					g2d.setFont(new Font("", Font.BOLD, 20));
					g2d.drawString("월간 확진자 추이", 5, 20);
					g2d.setColor(Color.ORANGE);
					g2d.fillRect(30 + i * 120, 330 - myHeight, 50, myHeight);
					g2d.setColor(Color.BLACK);
					g2d.drawRect(30 + i * 120, 330 - myHeight, 50, myHeight);
					g2d.setColor(Color.GRAY);
					g2d.drawString(strs[0], 45 + i * 120, 320 - myHeight);
					g2d.drawString(strs[1] + "월", 40 + i * 120, 350);
				}
			}
		};

		c.add(chart);

		thread = new Thread(() -> {
			for (int i = 1; i <= maxHeight; i++) {
				curHeight = i;

				chart.repaint();
				chart.revalidate();

				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

	public static void main(String[] args) {
		BasePage.mf.swap(new NewMain());
		BasePage.mf.setVisible(true);
	}
}
