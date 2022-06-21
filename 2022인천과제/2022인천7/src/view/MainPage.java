package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class MainPage extends BasePage {
	ArrayList<String> list = new ArrayList<>();
	int max = 0;

	public MainPage() {
		data();
		ui();
	}

	private void data() {
		var date = LocalDate.now().minusMonths(5);

		for (int i = 0; i < 6; i++) {
			var rs = getRows(
					"select count(*), ? from purchase where date <= now() and month(date) =? and year(date) = ?",
					date.getMonthValue(), date.getMonthValue(), date.getYear()).get(0).stream().map(a -> a + "")
							.toArray(String[]::new);
			list.add(String.join(",", rs));
			date = date.plusMonths(1);
		}

		list.forEach(a -> {
			var str = a.split(",");
			max = max < toInt(str[0]) ? toInt(str[0]) : max;
		});
	}

	private void ui() {
		setBorder(new EmptyBorder(5, 5, 5, 5));

		add(n = new JPanel(new BorderLayout(5, 5)), "North");
		add(c = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				var g2 = (Graphics2D) g;

				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				for (int i = 0; i < list.size(); i++) {
					var str = list.get(i).split(",");
					var h = (int) (toInt(str[0]) / (double) max * 250);

					g2.setColor(Color.orange);
					g2.fillRect(120 + i * 130, 330 - h, 50, h);
					g2.setColor(Color.black);
					g2.drawRect(120 + i * 130, 330 - h, 50, h);
					g2.setColor(Color.gray);
					g2.setFont(new Font("", 1, 20));
					g2.drawString(str[0], 135 + i * 130, 320 - h);
					g2.drawString(str[1] + "월", 135 + i * 130, 370);
				}
			}
		});
		add(s = new JPanel(new GridLayout(1, 0, 5, 5)), "South");

		n.add(lbl("<html><font color='gray'>예방접종현황", 2, 20), "North");
		n.add(nc = new JPanel(new GridLayout(1, 0)));

		var tot = (double) toInt(getOne("select count(*) from user"));
		for (int i = 0; i < 4; i++) {
			var tmp = new JPanel(new BorderLayout());
			var tmp_c = new JPanel(new FlowLayout(1, 10, 10));
			var cnt = toInt(getOne("select count(*) from purchase where shot=? and date <= ?", i + 1, LocalDate.now()));
			var lbl = lbl(i + 1 + "차 접종", 0, 15);

			lbl.setForeground(Color.white);
			lbl.setBackground(Color.orange);
			lbl.setOpaque(true);

			tmp.add(lbl, "North");
			tmp.add(tmp_c);

			tmp_c.add(lbl("<html><font color='orange'>" + String.format("%.1f", ((double) cnt / tot) * 100.0) + "%", 0,
					20), "West");
			tmp_c.add(lbl(
					"<html>누적 " + cnt + "<br>신규 "
							+ getOne("select count(*) from purchase where date(date) = now() and shot=?", i + 1),
					0, 15));

			nc.add(tmp);
		}

		for (var cap : "검색,포르필,로그아웃,종료".split(",")) {
			s.add(btn(cap, a -> {
				if (cap.equals("검색")) {
					mf.swapPage(new SearchPage());
				} else if (cap.equals("프로필")) {
					mf.swapPage(new ProfilePage());
				} else if (cap.equals("로그아웃")) {
					mf.swapPage(new LoginPage());
				} else {
					System.exit(0);
				}
			}));
		}
	}

	public static void main(String[] args) {
		mf.swapPage(new MainPage());
		mf.setVisible(true);
	}
}
