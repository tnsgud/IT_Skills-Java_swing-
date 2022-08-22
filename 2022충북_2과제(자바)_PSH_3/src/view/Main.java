package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;

public class Main extends BaseFrame {
	int idx = 0, bu, left = 0, right = 4;
	JLabel prev, next;
	static JLabel lbl[] = new JLabel[4];
	Worker worker;

	public Main() {
		super("GGV", 900, 750);

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = sz(new JPanel(null), 900, 350));
		add(s = new JPanel(new BorderLayout()), "South");

		n.add(nc = new JPanel());
		n.add(ne = new JPanel(new FlowLayout(2)), "East");
		n.add(ns = new JPanel(), "South");

		nc.add(lblHY("GGV", 0, 1, 20));
		nc.add(lblSerif("MOVIE", 0, 0, 20));

		var cap = "로그인,회원가입,마이페이지,통계".split(",");
		var icon = "Lock,Join,People,Analytics".split(",");
		for (int i = 0; i < cap.length; i++) {
			ne.add(lbl[i] = event(lblIcon(cap[i], 0, "./datafile/아이콘/" + icon[i] + ".png", 15, 15), e -> {
				var me = (JLabel) e.getSource();

				switch (me.getText()) {
				case "로그인":
					new Login().addWindowListener(new Before(this));
					break;
				case "로그아웃":
					logout();
					break;
				case "회원가입":
					new Sign().addWindowListener(new Before(this));
					break;
				case "마이페이지":
					if (user == null) {
						eMsg("로그인을 먼저 해주세요.");
						new Login().addWindowListener(new Before(this));
						return;
					}

					new MyPage().addWindowListener(new Before(this));
					break;
				case "통계":
					new Chart().addWindowListener(new Before(this));
					break;
				}
			}));
		}

		cap = "예매,영화,영화관,스토어".split(",");
		for (int i = 0; i < cap.length; i++) {
			var l = event(lblHY(cap[i], 0, 0, 15), e -> {
				var me = (JLabel) e.getSource();

				switch (me.getText()) {
				case "예매":
					new Reserve().addWindowListener(new Before(this));
					break;
				case "영화":
					new MovieList().addWindowListener(new Before(this));
					break;
				case "영화관":
					new Theater().addWindowListener(new Before(this));
					break;
				case "스토어":
					new Store().addWindowListener(new Before(this));
					break;
				}
			});
			l.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					var me = (JLabel) e.getSource();

					me.setBorder(new CompoundBorder(me.getBorder(), new MatteBorder(0, 0, 1, 0, red)));
				}

				@Override
				public void mouseExited(MouseEvent e) {
					var me = (JLabel) e.getSource();
					me.setBorder(null);

					for (int j = 1; j < ns.getComponentCount(); j++) {
						((JComponent) ns.getComponent(j)).setBorder(new MatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));
					}
				}
			});

			if (i > 0) {
				l.setBorder(new MatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));
			}

			ns.add(sz(l, 80, 20));
		}

		for (var f : new File("./datafile/배경").listFiles()) {
			c.add(new JLabel(getIcon(f.getAbsolutePath(), 900, 350))).setBounds((c.getComponentCount() - 1) * 900, 0,
					900, 300);
		}

		s.add(prev = event(new JLabel(getIcon("./datafile/아이콘/Left.png", 20, 30)), e -> {
			if (!prev.isEnabled())
				return;

			run(-1);
		}), "West");
		s.add(sc = sz(new JPanel(null), 900, 300));
		s.add(next = event(new JLabel(getIcon("./datafile/아이콘/Right.png", 20, 30)), e -> {
			if (!next.isEnabled())
				return;

			run(1);
		}), "East");

		for (var rs : getRows(
				"select m.m_no, m_name from reservation r, schedule sc, movie m where r.sc_no = sc.sc_no and sc.m_no = m.m_no group by m.m_no order by count(*) desc limit 25")) {
			var tmp = new JPanel(new BorderLayout());
			var img = event(new JLabel(getIcon("./datafile/영화/" + rs.get(1) + ".jpg", 150, 250)), e -> {
				if(e.getClickCount() != 2) return;
				
				var ans = JOptionPane.showConfirmDialog(null, "예매하시겠습니까?", "정보", JOptionPane.YES_NO_OPTION,
						JOptionPane.INFORMATION_MESSAGE);

				if (ans == JOptionPane.YES_OPTION) {
					new Reserve().addWindowListener(new Before(this));
				} else {
					m_no = toInt(rs.get(0));
					new MovieDetail().addWindowListener(new Before(this));
				}
			});

			tmp.add(img);
			tmp.add(lbl(rs.get(1).toString(), 0), "South");

			sc.add(tmp).setBounds((sc.getComponentCount() - 1) * 170, 0, 170, 270);
		}

		prev.setEnabled(false);

		setVisible(true);

		animation();
	}

	private void run(int bu) {
		if (worker != null && !worker.isDone()) {
			return;
		}

		this.bu = bu;

		worker = new Worker();
		worker.execute();
	}

	private void animation() {
		new SwingWorker() {
			@Override
			protected Object doInBackground() throws Exception {
				Thread.sleep(3000);
				while (true) {
					for (var com : c.getComponents()) {
						int x = com.getX();
						com.setLocation(x - 10, 0);
					}

					Thread.sleep(20);

					if (c.getComponent(idx).getX() == -900) {
						c.getComponent(idx).setLocation(1800, 0);
						idx = idx == 2 ? 0 : idx + 1;
						Thread.sleep(3000);
					}
				}
			}
		}.execute();
	}

	static void login() {
		lbl[0].setText("로그인");
		lbl[0].setIcon(new ImageIcon(
				Toolkit.getDefaultToolkit().getImage("./datafile/아이콘/Lock.png").getScaledInstance(15, 15, 4)));
		lbl[1].setVisible(false);
	}

	void logout() {
		user = null;
		lbl[0].setText("로그인");
		lbl[0].setIcon(getIcon("./datafile/아이콘/Lock.png", 15, 15));
		lbl[1].setVisible(true);
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
				for (var com : sc.getComponents()) {
					int x = com.getX();
					com.setLocation(x - 10 * bu, 0);
				}

				Thread.sleep(20);

				if (bu == -1 && sc.getComponent(left).getX() == 170) {
					check();
					return null;
				}

				if (bu == 1 && sc.getComponent(right).getX() == 510) {
					check();
					return null;
				}
			}
		}
	}

	public static void main(String[] args) {
		new Main();
	}
}