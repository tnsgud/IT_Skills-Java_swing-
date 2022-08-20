package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public class Main extends BaseFrame {
	JLabel lbl[] = new JLabel[4];
	Worker worker;
	int bu = 1, left = 0, right = 4;
	JLabel prev, next;

	public Main() {
		super("GGV", 900, 700);
		setDefaultCloseOperation(3);
		setLayout(new BorderLayout(10, 10));

		add(n = new JPanel(new BorderLayout(10, 10)), "North");
		add(c = new JPanel(null));
		add(s = new JPanel(new BorderLayout(5, 5)), "South");

		n.add(nc = new JPanel());
		n.add(ne = new JPanel(new FlowLayout(2, 10, 10)), "East");
		n.add(ns = new JPanel(), "South");

		nc.add(lblHY("GGV", 0, 1, 25));
		nc.add(lblSan("MOVIE", 0, 0, 20));

		var cap = "로그인,회원가입,마이페이지,통계".split(",");
		var icon = "Lock,Join,People,Analytics".split(",");
		for (int i = 0; i < cap.length; i++) {
			ne.add(lbl[i] = event(lblImg(cap[i], 0, "./datafile/아이콘/" + icon[i] + ".png", 15, 15), e -> {
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
			var l = sz(event(lbl(cap[i], 0, 12), e -> {
				var me = (JLabel) e.getSource();

				switch (me.getText()) {
				case "예매":
					new Reserve().addWindowListener(new Before(this));
					break;
				case "영화":
					new MovieList().addWindowListener(new Before(this));
					break;
				case "영화관":
					new Cinema().addWindowListener(new Before(this));
					break;
				case "스토어":
					new Store().addWindowListener(new Before(this));
					break;

				}
			}), 100, 25);
			l.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					for (int i = 1; i < ns.getComponentCount(); i++) {
						var lbl = (JLabel) ns.getComponent(i);
						lbl.setBorder(new MatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));
					}

					var me = (JLabel) e.getSource();
					me.setBorder(new CompoundBorder(me.getBorder(), new MatteBorder(0, 0, 2, 0, red)));
				}

				@Override
				public void mouseExited(MouseEvent e) {
					var me = (JLabel) e.getSource();
					me.setBorder(((CompoundBorder) me.getBorder()).getOutsideBorder());
				}
			});

			l.setBorder(i == 0 ? null : new MatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));
			ns.add(l);
		}

		for (var f : new File("./datafile/배경").listFiles()) {
			var l = new JLabel(getIcon(f.getPath(), 950, 250));
			c.add(l).setBounds((c.getComponentCount() - 1) * 950, 0, 950, 250);
		}

		s.add(prev = event(lbl("<", 0, 0, 35), e -> {
			if (!prev.isEnabled())
				return;

			run(-1);
		}), "West");
		s.add(sc = sz(new JPanel(null), 850, 270));
		s.add(next = event(lbl(">", 0, 0, 35), e -> {
			if (!next.isEnabled())
				return;

			run(1);
		}), "East");

		for (var rs : getRows(
				"select m.m_no, m_name from reservation r, schedule sc, movie m where r.sc_no = sc.sc_no and sc.m_no = m.m_no group by m.m_no order by count(*) desc limit 25")) {
			var tmp = new JPanel(new BorderLayout(10, 10));
			var img = event(new JLabel(getIcon("./datafile/영화/" + rs.get(1) + ".jpg", 150, 250)), e -> {
				if (e.getClickCount() != 2)
					return;

				var ans = JOptionPane.showConfirmDialog(null, "예매하시겠습니까?", "정보", JOptionPane.YES_NO_OPTION,
						JOptionPane.INFORMATION_MESSAGE);
				m_no = toInt(rs.get(0));
				if (ans == JOptionPane.YES_OPTION) {
					new Reserve().addWindowListener(new Before(this));
				} else {
					new MovieDetail().addWindowListener(new Before(this));
				}
			});
			img.setBorder(new EmptyBorder(0, 10, 0, 10));

			tmp.add(img);
			tmp.add(lbl(rs.get(1).toString(), 0), "South");

			sc.add(tmp).setBounds((sc.getComponentCount() - 1) * 170, 0, 150, 270);
		}

		prev.setEnabled(false);
		nc.setBorder(new EmptyBorder(0, 250, 0, 0));

		setVisible(true);

		new SwingWorker() {
			@Override
			protected Object doInBackground() throws Exception {
				Thread.sleep(3000);
				int idx = 0;
				while (true) {
					for (var com : c.getComponents()) {
						int x = com.getX();
						com.setLocation(x -= 10, 0);
					}

					Thread.sleep(5);

					if (c.getComponent(idx).getX() <= -950) {
						c.getComponent(idx).setLocation(1900, 0);
						idx = (idx + 1) % 3;
						Thread.sleep(3000);
					}
				}
			}
		}.execute();
	}

	private void run(int bu) {
		if(worker != null && !worker.isDone()) {
			
			return;
		}
		
		
		this.bu = bu;
		
		worker = new Worker();
		worker.execute();
	}

	void login() {
		lbl[0].setText("로그아웃");
		lbl[0].setIcon(getIcon("./datafile/아이콘/UnLock.png", 15, 15));
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
					com.setLocation(x -= 10 * bu, 0);
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
}
