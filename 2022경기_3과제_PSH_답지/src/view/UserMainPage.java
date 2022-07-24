package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

public class UserMainPage extends BasePage {
	JButton prev, next;
	Timer timer;
	ArrayList<JPanel> items = new ArrayList<>();
	int curidx = 0, toidx = 0, runidx = 0, bu = -1;
	JLabel[] lbl = new JLabel[5];
	JPanel ccc;
	SwingWorker<String, Object> worker;

	public UserMainPage() {
		super("사용자메인");

		add(c = new JPanel(new FlowLayout(1, 5, 5)));
		add(s = new JPanel(new FlowLayout(1, 0, 0)), "South");

		c.add(prev = btn("◀", a -> {
			worker.cancel(true);
			worker = null;

			if (bu == -1) {
				for (int i = 0; i < 5; i++)
					items.get((runidx + i) % 5).setLocation(i * 400, 5);
				items.get((runidx + 4) % 5).setLocation(-400, 5);
			}

			bu = 1;
			curidx = runidx - 1 < 0 ? 4 : runidx - 1;
			toidx = curidx;
			worker = new mySwingWorker();
			worker.execute();
		}));
		c.add(cc = new JPanel(new BorderLayout()));
		c.add(next = btn("▶", a -> {
			worker.cancel(true);
			worker = null;

			if (bu == 1) {
				for (int i = 0; i < 5; i++)
					items.get((runidx + i) % 5).setLocation(i * 400, 5);
				items.get((runidx + 4) % 5).setLocation(1600, 5);
			}

			bu = -1;
			curidx = (runidx + 1) % 5;
			toidx = curidx;
			worker = new mySwingWorker();
			worker.execute();
		}));

		cc.add(ccc = sz(new JPanel(), 400, 300));
		cc.add(cs = new JPanel(new FlowLayout(1, 10, 10)), "South");

		ccc.setLayout(null);

		var rs = getRows(
				"select g_img, g_name, round(avg(r_score), 0), format(g_price, '#,##0'), g_sale, g_gd, g.g_no from game g inner join review r on g.g_no = r.g_no group by g.g_no order by rand() limit 5");
		for (var r : rs) {
			var tmp = new JPanel(new BorderLayout());
			var text = "<html>게임명 : " + r.get(1) + "<br>평점 : " + String.format("%.1f", (double) toInt(r.get(2)))
					+ "점<br>가격 : " + (toInt(r.get(3)) == 0 ? "무료" : r.get(3) + "원");
			var img = new JLabel(getIcon(r.get(0), 400, 200));

			if (toInt(r.get(4)) != 0) {
				var price = toInt(r.get(3)) * (toInt(r.get(4)) * 0.01);
				text += " -> " + new DecimalFormat("#,##0").format(price) + "원(" + r.get(4) + "% 할인중) 대상 : "
						+ g_gd[toInt(r.get(5))];
			}

			img.setName(r.get(6).toString());

			img.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					var me = (JLabel) e.getSource();
					g_no = toInt(me.getName());
					
					new GamePage();
				}
			});

			int i = rs.indexOf(r);

			lbl[i] = lbl("■", 0);
			lbl[i].setForeground(i == 0 ? Color.white : Color.gray);
			lbl[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					worker.cancel(true);
					worker = null;

					curidx = runidx;
					toidx = i;

					if (runidx <= toidx) {
						bu = -1;
						for (int i = 0; i < 5; i++)
							items.get((runidx + i) % 5).setLocation(i * 400, 5);
						items.get((runidx + 4) % 5).setLocation(1600, 5);
					} else {
						bu = 1;
						for (int i = 0; i < 5; i++)
							items.get((runidx + i) % 5).setLocation(i * 400, 5);
						items.get((runidx + 4) % 5).setLocation(-400, 5);
					}

					worker = new mySwingWorker();
					worker.execute();
				}
			});

			cs.add(lbl[i]);

			tmp.add(img);
			tmp.add(lbl(text, 2), "South");

			ccc.add(tmp).setBounds(items.size() * 400, 5, 400, 250);
			items.add(tmp);
		}

		var cap = "검색,장터,종료".split(",");
		for (int i = 0; i < cap.length; i++) {
			var l = imglbl("<html><font color='white'>" + cap[i], 0, "./datafiles/기본사진/" + (i + 2) + ".png", 150, 150);

			int j = i;

			l.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (cap[j].equals("검색")) {
						new SearchPage();
					} else if (cap[j].equals("장터")) {
						new MarketPlacePage();
					} else {
						System.exit(0);
					}
				}
			});

			s.add(l);
		}

		ccc.setBorder(new LineBorder(Color.black));

		worker = new mySwingWorker();
		worker.execute();
	}

	class mySwingWorker extends SwingWorker {
		@Override
		protected Object doInBackground() throws Exception {
			boolean out;
			while (true) {
				out = false;

				// curIdx에서 toIdx까지 한 번에 이동한다.
				int i = curidx;
				while (true) {
					while (true) {
						// 이미지 5개를 -5/+5만큼 이동한다.
						for (int j = 0; j < items.size(); j++) {
							int x = items.get(j).getX();
							items.get(j).setLocation(x += 5 * bu, 5);

							// 왼쪽 한계를 벗어나면 맨 오른쪽으로 위치를 옮긴다.
							if (bu == -1 && x <= -400) {
								items.get(j).setLocation(1600, 5);
								out = true;
							}
							if (bu == 1 && x >= 1600) {
								items.get(j).setLocation(-400, 5);
								out = true;
							}
						}

						Thread.sleep(1);
						if (out == true)
							break;
					}

					out = false;
					Stream.of(lbl).forEach(l -> l.setForeground(Color.gray));
					lbl[i].setForeground(Color.white);

					// 움직인 이미지 번호
					runidx = i;

					// 다음 반복
					if (bu == -1) {
						if (i == toidx)
							break;
						i++;
					} else {
						if (i == toidx)
							break;
						i--;
					}
				}
				bu = -1;
				for (int j = 0; j < 5; j++)
					items.get((runidx + j) % 5).setLocation(j * 400, 5);

				curidx = (runidx + 1) % 5;
				toidx = curidx;

				Thread.sleep(1000);
			}
		}
	}
}
