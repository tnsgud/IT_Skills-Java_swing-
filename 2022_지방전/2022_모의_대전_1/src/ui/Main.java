package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import db.DB;
import ui.BaseFrame.Before;

public class Main extends BaseFrame {
	int cnt = 0;
	Timer timer;
	String cap[] = "TICKETING,MONTH SCHEDULE,CHART,LOGIN,MYPAGE".split(","), tit[] = "뮤지컬,오페라,콘서트".split(","),
			code[] = "M,O,C".split(",");
	JPanel n_e, n_w;
	static JLabel img = new JLabel();
	JLabel lbl[] = new JLabel[cap.length];
	Popular pop;

	public Main() {
		super("메인", 600, 300);

		ui();
		event();

		setVisible(true);

		timer = new Timer(2000, a -> {
			pop.data(tit[cnt % 3], code[cnt % 3]);
			cnt++;
		});
		timer.start();
		
	}

	private void event() {
		for (int i = 0; i < this.lbl.length; i++) {
			lbl[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					var idx = Integer.parseInt(((JLabel)e.getSource()).getName());
					switch (idx) {
					case 0:
						new Search().addWindowListener(new Before(Main.this));
						break;
					case 1:
						new MonthSchedule().addWindowListener(new Before(Main.this));
						break;
					case 2:
						new Chart().addWindowListener(new Before(Main.this));
						break;
					case 3:
						if(isLogin) {
							eMsg("로그아웃되었습니다.");
							isLogin = false;
							img.setVisible(isLogin);
						}else {
							new Login().addWindowListener(new Before(Main.this));
						}
						break;
					case 4:
						if(!isLogin) {
							eMsg("로그인을 하세요.");
							break;
						}
						new MyPage().addWindowListener(new Before(Main.this));
						break;
					}
				}
			});
		}
	}

	private void ui() {
		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new BorderLayout()));

		n.add(n_w = new JPanel(new FlowLayout(1)), "West");
		n.add(n_e = new JPanel(new FlowLayout(1)), "East");

		c.add(pop = new Popular());

		n_e.add(img);

		for (int i = 0; i < lbl.length; i++) {
			lbl[i] = lbl(cap[i], 0);
			lbl[i].setName(i + "");
			(i < 3 ? n_w : n_e).add(lbl[i]);
		}
	}

	class Popular extends JPanel {
		JPanel c, s;
		JLabel lbl[] = new JLabel[3];

		void data(String t, String c) {
			this.c.removeAll();

			setBorder(new TitledBorder(new LineBorder(Color.black), "인기공연(" + t + ")"));

			try {
				var rs = DB.rs(
						"select p.p_name, p.pf_no, count(*) from perform p, ticket t where p.p_no = t.p_no and p.pf_no like ? group by p.p_name order by count(*) desc limit 5",
						c + "%");
				while (rs.next()) {
					var p = new JPanel(new BorderLayout());
					var img = new JLabel(img("공연사진/" + rs.getString(2), 100, 100));
					p.add(lbl(rs.getRow() + "위", 2), "North");
					p.add(img);
					p.add(lbl(rs.getString(1), 0), "South");
					this.c.add(p);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			Arrays.stream(lbl).forEach(a -> a.setForeground(Color.black));
			lbl[cnt % 3].setForeground(Color.red);

			repaint();
			revalidate();
		}

		public Popular() {
			this.setLayout(new BorderLayout());
			add(this.c = new JPanel(new FlowLayout(1, 10, 10)));
			add(this.s = new JPanel(new FlowLayout(1)), "South");

			for (int i = 0; i < this.lbl.length; i++) {
				this.lbl[i] = lbl(i + 1 + "", 0);
				this.lbl[i].setForeground(i == 0 ? Color.red : Color.black);
				this.lbl[i].addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						timer.stop();
						var l = (JLabel) e.getSource();

						int idx = Integer.parseInt(l.getText()) - 1;
						data(tit[idx], code[idx]);

						for (int j = 0; j < lbl.length; j++) {
							lbl[j].setForeground(Color.black);
						}
						cnt = idx;

						l.setForeground(Color.red);
						timer.restart();
					}
				});
				this.s.add(this.lbl[i]);
			}

			data(tit[0], code[0]);
		}
	}

	public static void main(String[] args) {
		new Main();
	}
}
