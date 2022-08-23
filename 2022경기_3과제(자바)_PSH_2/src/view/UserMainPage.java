package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.stream.Stream;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.border.LineBorder;

public class UserMainPage extends BasePage {
	Worker worker;
	int curidx = 0, toidx = 1, bu = -1;
	JLabel lbl[] = Stream.generate(() -> lbl("■", 0, 0, 12, e -> {
		toidx = toInt(((JLabel) e.getSource()).getName());

		if (curidx <= toidx)
			run(-1, 1600);
		else
			run(1, -400);
	})).limit(5).toArray(JLabel[]::new);
	JPanel ccc;

	public UserMainPage() {
		super("사용자메인");

		add(c = new JPanel(new BorderLayout()));
		add(s = new JPanel(new FlowLayout(1)), "South");

		c.add(cc = new JPanel(new FlowLayout(1, 5, 5)));
		c.add(cs = new JPanel(new FlowLayout(1)), "South");

		cc.add(btn("◀", a -> {
			toidx = curidx - 1 < 0 ? 4 : curidx - 1;
			run(1, -400);
		}));
		cc.add(ccc = sz(new JPanel(null), 400, 300));
		cc.add(btn("▶", a -> {
			toidx = (curidx + 1) % 5;
			run(-1, 1600);
		}));

		Stream.of(lbl).forEach(l -> {
			l.setName(Arrays.asList(lbl).indexOf(l) + "");
			l.setForeground(Color.gray);
			cs.add(l);
		});

		var rs = getRows(
				"select g_no, g_img, g_name, rate, format(g_price, '#,##0'), dc_price, g_sale, g_gd, g_age from v1 order by rand() limit 5");

		for (var r : rs) {
			System.out.println(r);
			var i = rs.indexOf(r);
			var tmp = new JPanel(new BorderLayout());
			var img = new JLabel(getIcon(r.get(1), 400, 200));
			var text = "<html>게임명 : " + r.get(2) + "<br>평점 : " + r.get(3) + "<br>가격 : "
					+ (r.get(4).toString().equals("0") ? "무료" : r.get(4) + "원");

			if (toInt(r.get(6)) != 0) {
				text += "-> " + r.get(5) + "원(" + r.get(6) + "% 할인중) 대상 :" + g_gd[toInt(r.get(7))];
			}

			tmp.setName(i + "");

			tmp.add(img);
			tmp.add(lbl(text, 2), "South");

			img.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					System.out.println(toInt(g_age[toInt(r.get(8))]));
					gNo = toInt(r.get(0));
					new GamePage();
				}
			});

			tmp.setBorder(new LineBorder(Color.black));

			ccc.add(sz(tmp, 400, 250)).setBounds((ccc.getComponentCount() - 1) * 400, 0, 400, 250);
		}

		var cap = "검색,장터,종료".split(",");
		for (int i = 0; i < cap.length; i++) {
			s.add(lblImg(cap[i], 0, "./datafiles/기본사진/" + (i + 2) + ".png", 150, 150, e -> {
				var text = ((JLabel) e.getSource()).getText();

				if (text.equals("검색")) {
					new SearchPage();
				} else if (text.equals("장터")) {
					new MarketPage();
				} else {
					System.exit(0);
				}
			}));
		}

		mf.repaint();
		mf.revalidate();

		lbl[0].setForeground(Color.white);

		worker = new Worker();
		worker.execute();
	}

	void run(int bu, int endX) {
		worker.cancel(true);
		worker = null;

		for (int i = 0; i < 5; i++) {
			ccc.getComponent((curidx + i) % 5).setLocation(i * 400, 0);
		}
		ccc.getComponent((curidx + 4) % 5).setLocation(endX, 0);

		this.bu = bu;

		worker = new Worker();
		worker.execute();
	}

	class Worker extends SwingWorker {
		
		@Override
		protected Object doInBackground() throws Exception {
			Thread.sleep(1000);
			var flag = false;
			while (true) {
				while (true) {
					while (true) {
						for (var com : ccc.getComponents()) {
							int x = com.getX();
							com.setLocation(x += 10 * bu, 0);

							if (x == -400 || x == 1600) {
								com.setLocation(bu == -1 ? 1600 : -400, 0);
								flag = true;
							}
						}

						Thread.sleep(5);

						if (flag) {
							break;
						}
					}
					if (bu == -1)
						curidx = (curidx + 1) % 5;
					else
						curidx = curidx == 0 ? 4 : curidx - 1;

					flag = false;
					Stream.of(lbl).forEach(l->l.setForeground(Color.gray));
					lbl[curidx].setForeground(Color.white);
					
					if (curidx == toidx)
						break;
				}
				bu = -1;
				toidx = (curidx + 1) % 5;
				
				Thread.sleep(1000);
			}
		}
	}

	public static void main(String[] args) {
		var log = new LoginFrame();
		log.txt[0].setText("abc1");
		log.txt[1].setText("Qq1!");

	}
}
