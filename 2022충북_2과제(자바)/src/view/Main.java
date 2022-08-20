package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingWorker;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

public class Main extends BaseFrame {
	static JLabel lbl[] = new JLabel[4];
	ArrayList<JLabel> back = new ArrayList<>();
	ArrayList<JPanel> items = new ArrayList<>();
	Worker worker;
	int bu = 1, left = 0, right = 4;
	JLabel prev, next;

	public Main() {
		super("GGV", 900, 700);
		try {
			Preferences.userNodeForPackage(BaseFrame.class).clear();
		} catch (BackingStoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		setDefaultCloseOperation(3);

		setLayout(new BorderLayout(10, 10));

		add(n = new JPanel(new BorderLayout(10, 10)), "North");
		add(c = new JPanel(null));
		add(s = new JPanel(new BorderLayout(5, 5)), "South");

		n.add(nc = new JPanel());
		n.add(ne = new JPanel(new FlowLayout(2, 10, 10)), "East");
		n.add(ns = new JPanel(new FlowLayout(1)), "South");

		nc.add(lblHY("GGV", 0, 1, 25));
		nc.add(lblSerif("MOVIE", 0, 0, 20));

		var cap = "로그인,회원가입,마이페이지,통계".split(",");
		var icon = "Lock,Join,People,Analytics".split(",");

		for (int i = 0; i < cap.length; i++) {
			lbl[i] = imglbl(cap[i], 0, "./datafile/아이콘/" + icon[i] + ".png", 15, 15);

			lbl[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					var name = ((JLabel) e.getSource()).getText();

					if (name.equals("로그인")) {
						new Login().addWindowListener(new Before(Main.this));
					} else if (name.equals("로그아웃")) {
						lbl[0].setText("로그인");
						lbl[0].setIcon(getIcon("./datafile/아이콘/Lock.png", 15, 15));
						lbl[1].setVisible(true);
					} else if (name.equals("회원가입")) {
						new Sign().addWindowListener(new Before(Main.this));
					} else if (name.equals("마이페이지")) {
						if (!isLogin) {
							eMsg("로그인을 먼저 해주세요.");
							return;
						}

						new MyPage().addWindowListener(new Before(Main.this));
					} else {
						new Chart().addWindowListener(new Before(Main.this));
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
					new Reserve().addWindowListener(new Before(this));
				} else if (c.equals("영화")) {
					new MovieList().addWindowListener(new Before(this));
				} else if (c.equals("영화관")) {
					new Cinema().addWindowListener(new Before(this));
				} else {
					new Store().addWindowListener(new Before(this));
				}
			}), 100, 25);

			l.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					for (int j = 0; j < ns.getComponentCount(); j++) {
						var l = (JLabel) ns.getComponent(j);
						if (j > 0) {
							l.setBorder(new MatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));
						}
					}

					var me = (JLabel) e.getSource();
					me.setBorder(new CompoundBorder(me.getBorder(), new MatteBorder(0, 0, 2, 0, Color.red)));
				}

				@Override
				public void mouseExited(MouseEvent e) {
					var me = (JLabel) e.getSource();
					me.setBorder(((CompoundBorder) me.getBorder()).getOutsideBorder());
				}
			});

			ns.add(l);

			if (i > 0) {
				l.setBorder(new MatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));
			}
		}

		for (var f : new File("./datafile/배경").listFiles()) {
			var l = new JLabel(getIcon(f.getPath(), 940, 250));
			c.add(l).setBounds(back.size() * 940, 0, 940, 250);
			back.add(l);
		}

		s.add(prev = lbl("〈", 0, 1, 35, e -> {
			if (!prev.isEnabled())
				return;

			run(-1);
		}), "West");

		s.add(sc = sz(new JPanel(null), 850, 270));

		s.add(next = lbl("〉", 0, 0, 35, e -> {
			if (!next.isEnabled())
				return;

			run(1);
		}), "East");

		for (var rs : getRows(
				"select m.m_no, m.m_name from reservation r, schedule sc, movie m where r.sc_no = sc.sc_no and sc.m_no = m.m_no group by m.m_no order by count(*) desc limit 25")) {
			var tmp = new JPanel(new BorderLayout(10, 10));
			var img = new JLabel(getIcon("./datafile/영화/" + rs.get(1) + ".jpg", 150, 250));

			img.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (e.getClickCount() == 2) {
						var ans = JOptionPane.showConfirmDialog(null, "예매하시겠습니까?", "정보", JOptionPane.YES_NO_OPTION,
								JOptionPane.INFORMATION_MESSAGE);

						if (ans == JOptionPane.YES_OPTION) {
							m_no = toInt(rs.get(0));
							new Reserve().addWindowListener(new Before(Main.this));
						} else {
							new MovieDetail(toInt(rs.get(0))).addWindowListener(new Before(Main.this));
						}
					}
				}
			});

			img.setBorder(new EmptyBorder(0, 10, 0, 10));

			tmp.add(img);
			tmp.add(lbl(rs.get(1).toString(), 0), "South");

			sc.add(tmp).setBounds(items.size() * 170, 0, 150, 270);
			items.add(tmp);
		}

		prev.setEnabled(false);

		setVisible(true);

		nc.setBorder(new EmptyBorder(0, 200, 0, 0));

		CenterAnimation();
	}

	private void run(int bu) {
		if (worker != null && !worker.isDone()) {
			return;
		}

		this.bu = bu;
		
		worker = new Worker();
		worker.execute();
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
		void check() {
			left += bu;
			right += bu;
			prev.setEnabled(left != 0);
			next.setEnabled(right != 24);
		}
		
		@Override
		protected Object doInBackground() throws Exception {
			while (true) {
				for (int i = 0; i < items.size(); i++) {
					int x = items.get(i).getX();
					items.get(i).setLocation(x -= 10 * bu, 0);
				}

				Thread.sleep(20);

				// < 처리
				if (bu == -1 && items.get(left).getX() == 170) {
					check();
					return null;
				}
				
				// > 처리
				if(bu == 1 && items.get(right).getX() == 510) {
					check();
					return null;
				}
			}
		}
	}

}
