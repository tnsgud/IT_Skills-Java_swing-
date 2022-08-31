package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class MainPage extends BasePage {
	ArrayList<String> list = new ArrayList<>();
	int max = 0;

	public MainPage() {
		data();
		ui();
	}

	private void ui() {
		add(n = new JPanel(new BorderLayout(5, 5)), "North");
		add(c = new JPanel(new BorderLayout()) {
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
					g2.setFont(new Font("맑은 고디", 1, 20));
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
			var tmp = new JPanel(new BorderLayout(5, 5));
			var tmpC = new JPanel(new GridLayout(1, 0));
			var lbl = lbl(i + 1 + "차 접종", 0, 15);
			var cnt = toInt(
					getOne("select count(*) from purchase where shot=? and date(date) <= ?", i + 1, LocalDate.now()));

			lbl.setForeground(Color.white);
			lbl.setBackground(Color.orange);
			lbl.setOpaque(true);

			tmp.add(lbl, "North");
			tmp.add(tmpC);

			tmpC.add(lbl("<html><font color='orange'>" + String.format("%.1f", ((double) cnt / tot) * 100) + "%", 0,
					20));
			tmpC.add(lbl(
					"<html>누적 " + cnt + "<br>신규 "
							+ getOne("select count(*) from purchase where date(date) = now() and shot=?", i + 1),
					0, 15));

			nc.add(tmp);
		}

		c.add(lbl("<html><font color='gray'>월간 접종자 추기", 2, 20), "North");

		for (var cap : "검색,프로필,로그아웃,종료".split(",")) {
			s.add(btn(cap, a -> {
				if (cap.equals("검색")) {
					mf.swap(new SearchPage());
				} else if (cap.equals("프로필")) {
					mf.swap(new ProfilePage());
				} else if (cap.equals("로그아웃")) {
					mf.swap(new LoginPage());
				} else {
					mf.dispose();
				}
			}));
		}
		
		setBorder(new EmptyBorder(5, 5, 5, 5));
	}

	private void data() {
		var date = LocalDate.now().minusMonths(5);

		for (int i = 0; i <= 5; i++) {
			var rs = getRows(
					"select count(*), ? from purchase where date(date) <= now() and month(date) = ? and year(date) = ?",
					date.getMonthValue(), date.getMonthValue(), date.getYear()).get(0).stream().map(a -> a.toString())
					.toArray(String[]::new);
			list.add(String.join(",", rs));
			date = date.plusMonths(1);
		}

		list.forEach(a -> {
			var str = a.split(",");
			max = Math.max(toInt(str[0]), max);
		});
	}

	public static void main(String[] args) {
		mf = new MainFrame();
		mf.swap(new MainPage());
		mf.setVisible(true);
	}
}
