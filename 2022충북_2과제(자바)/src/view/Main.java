package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

public class Main extends BaseFrame {
	static JLabel lbl[] = new JLabel[4];
	ArrayList<JLabel> back = new ArrayList<>();
	ArrayList<JPanel> items = new ArrayList<>();
	Worker worker;
	int bu = 1, left = 0, right = 4, curIdx = 0;
	boolean run;

	public Main() {
		super("GGV", 940, 700);

		setLayout(new BorderLayout(10, 10));

		add(n = new JPanel(new BorderLayout(10, 10)), "North");
		add(c = new JPanel(null));
		add(s = new JPanel(new BorderLayout(5, 5)), "South");

		n.add(lbl("GGV MOVIE", 0, 25));
		n.add(ne = new JPanel(new FlowLayout(2, 10, 10)), "East");
		n.add(ns = new JPanel(new FlowLayout(1)), "South");

		var cap = "로그인,회원가입,마이페이지,통계".split(",");
		var icon = "Lock,Join,People,Analytics".split(",");

		for (int i = 0; i < cap.length; i++) {
			lbl[i] = imglbl(cap[i], 0, "./datafile/아이콘/" + icon[i] + ".png", 15, 15);

			lbl[i].setName(cap[i]);

			lbl[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					var name = ((JLabel) e.getSource()).getName();

					if (name.equals("로그인")) {
						new Login(login).addWindowListener(new Before(Main.this));
					} else if (name.equals("회원가입")) {
						new BaseFrame("회원가입", 300, 300).setVisible(true);
					} else if (name.equals("마이페이지")) {
						if (user == null) {
							eMsg("로그인을 먼저 해주세요.");
							return;
						}

						new BaseFrame("마이페이지", 300, 300).setVisible(true);
					} else {
						new BaseFrame("차트", 300, 300).setVisible(true);
					}
				}
			});

			ne.add(lbl[i]);
		}

		cap = "예매,영화,영화관,스토어".split(",");
		for (int i = 0; i < cap.length; i++) {
			var l = sz(lbl(cap[i], 0, 1, 12, e -> {
				var c = ((JLabel) e.getSource()).getText();

				if (c.equals("예매")) {
					new BaseFrame("예매", 500, 500).setVisible(true);
				} else if (c.equals("영화")) {
					new BaseFrame("영화", 500, 500).setVisible(true);
				} else if (c.equals("영화관")) {
					new BaseFrame("영화관", 500, 500).setVisible(true);
				} else {
					new BaseFrame("스토어", 500, 500).setVisible(true);
				}
			}), 100, 25);

			l.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					for (var com : ns.getComponents()) {
						var lbl = (JComponent) com;
						lbl.setBorder(null);
					}

					((JLabel) e.getSource()).setBorder(new MatteBorder(0, 0, 2, 0, Color.red));
				}

				@Override
				public void mouseExited(MouseEvent e) {
					((JLabel) e.getSource()).setBorder(null);
				}
			});

			ns.add(l);

			if (i < cap.length - 1) {
				ns.add(sz(new JSeparator(JSeparator.VERTICAL), 2, 25));
			}
		}

		for (var f : new File("./datafile/배경").listFiles()) {
			var l = new JLabel(getIcon(f.getPath(), 940, 250));
			c.add(l).setBounds(back.size() * 940, 0, 940, 250);
			back.add(l);
		}

		s.add(btn("<", a -> {
			for (int i = 0; i < items.size(); i++) {
				items.get((curIdx + i) % items.size()).setLocation(170 * i, 0);
			}
			items.get((curIdx + items.size() - 1) % items.size()).setLocation(-170, 0);

			bu = -1;
			run = true;

			if (worker != null && worker.isDone()) {
				worker.cancel(true);
				worker = null;
			}
			worker = new Worker();
			worker.execute();
		}), "West");

		s.add(sc = sz(new JPanel(null), 850, 270));

		s.add(btn(">", a -> {
			for (int i = 0; i < items.size(); i++) {
				items.get((curIdx + i) % items.size()).setLocation(170 * i, 0);
			}

			bu = 1;
			run = true;

			if (worker != null) {
				worker.cancel(true);
				worker = null;
			}

			worker = new Worker();
			worker.execute();
		}), "East");

		for (var rs : getRows(
				"select m.m_no, m.m_name from reservation r, schedule sc, movie m where r.sc_no = sc.sc_no and sc.m_no = m.m_no group by m.m_no order by count(*) desc limit 25")) {
			var tmp = new JPanel(new BorderLayout(10, 10));
			var img = new JLabel(getIcon("./datafile/영화/" + rs.get(1) + ".jpg", 150, 250));

			img.setBorder(new EmptyBorder(0, 10, 0, 10));

			tmp.add(img);
			tmp.add(lbl(rs.get(1).toString(), 0), "South");

			sc.add(tmp).setBounds(items.size() * 170, 0, 150, 270);
			items.add(tmp);
		}

		setVisible(true);

		CenterAnimation();
	}

	private void CenterAnimation() {
		new SwingWorker() {
			@Override
			protected Object doInBackground() throws Exception {
				Thread.sleep(3000);
				int idx = 0;
				while (true) {
					for (int i = 0; i < back.size(); i++) {
						int x = back.get(i).getX();
						back.get(i).setLocation(x -= 10, 0);
					}

					Thread.sleep(5);

					if (back.get(idx).getX() <= -940) {
						back.get(idx).setLocation(1880, 0);
						idx = (idx + 1) % back.size();
						Thread.sleep(3000);
					}
				}
			}
		}.execute();
	}

	class Worker extends SwingWorker {
		@Override
		protected Object doInBackground() throws Exception {
			while (true) {
				for (int i = 0; i < items.size(); i++) {
					int x = items.get((curIdx + i) % items.size()).getX();
					items.get((curIdx + i) % items.size()).setLocation(x -= 10 * bu, 0);
				}

				Thread.sleep(20);

				// ">" 처리
				if (bu == 1 && items.get(curIdx).getX() <= -170) {
					items.get(curIdx).setLocation(170 * (items.size() - 1), 0);
					curIdx = (curIdx + 1) % items.size();
					return null;
				}

				// "<" 처리
				if (bu == -1 && items.get(curIdx).getX() >= 170) {
					curIdx = curIdx - 1 < 0 ? items.size() - 1 : curIdx - 1;
					items.get((curIdx + items.size() - 1) % items.size()).setLocation(-170, 0);
					return null;
				}
			}
		}

		@Override
		protected void done() {
			left += -bu;
			right += -bu;
		}
	}

	public static void main(String[] args) {
		new Main();
	}
}
