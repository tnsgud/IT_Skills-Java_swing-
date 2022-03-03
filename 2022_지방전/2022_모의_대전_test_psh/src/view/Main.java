package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import tool.Tool.Before;

public class Main extends BaseFrame {
	String[] cap = "TICKETING,MONTH SCHEDULE,CHART,LOGIN,MYPAGE".split(","), type = "뮤지컬,오페라,콘서트".split(","),
			pf = "M,O,C".split(",");
	static JLabel img, login;
	JPanel c_c;
	int idx = 0;
	Timer timer;

	public Main() {
		super("메인", 600, 300);

		timer = new Timer(1000, a -> {
			idx++;
			idx = idx > 2 ? 0 : idx;
			popular();
		});

		ui();

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		setVisible(true);
		
		timer.start();
	}

	private void popular() {
		c.removeAll();

		var c_c = new JPanel(new FlowLayout(1, 10, 10));
		var c_s = new JPanel(new FlowLayout(1, 10, 10));

		c.add(c_c);
		c.add(c_s, "South");

		var rs = rs(
				"select pf_no, p_name, count(*) as cnt from perform p, ticket t where t.p_no=p.p_no and left(pf_no, 1)=? group by p_name order by cnt desc limit 5",
				pf[idx]);
		try {
			while (rs.next()) {
				var p = new JPanel(new BorderLayout());
				var img = img("공연사진/" + rs.getString(1) + ".jpg", 100, 100);

				p.add(lbl(rs.getRow() + "위", 2, 15), "North");
				p.add(img);
				p.add(lbl(rs.getString(2), 0), "South");

				img.setBorder(new LineBorder(Color.black));

				c_c.add(p);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (var cap : "1,2,3".split(",")) {
			var lbl = lbl(cap, 0, 12);
			c_s.add(lbl);
			lbl.setForeground(idx == toInt(cap) - 1 ? Color.red : Color.black);
		}

		c.setBorder(new TitledBorder(new LineBorder(Color.black), "인기공연(" + type[idx] + ")"));
		
		c.repaint();
		c.revalidate();
	}

	private void ui() {
		var n_w = new JPanel(new FlowLayout(1, 10, 10));
		var n_e = new JPanel(new FlowLayout(1, 10, 10));

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new BorderLayout(10, 10)));

		n.add(n_w, "West");
		n.add(n_e, "East");

		n_e.add(img = new JLabel());
		n_e.add(login = new JLabel());
		for (int i = 0; i < cap.length; i++) {
			var l = lbl(cap[i], 0);
			l.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					var s = (JLabel) e.getSource();
					var i = toInt(s.getName());

					switch (i) {
					case 0:
						new Search().addWindowListener(new Before(Main.this));
						break;
					case 1:
						new Schedule().addWindowListener(new Before(Main.this));
						break;
					case 2:
						new Chart().addWindowListener(new Before(Main.this));
						break;
					case 3:
						if (isLogin) {
							eMsg("로그아웃되었습니다.");
							uno = 0;
							isLogin = false;
							l.setText("LOGIN");
							img.setVisible(false);
						} else {
							new Login().addWindowListener(new Before(Main.this));
						}
						break;
					case 4:
						if (isLogin) {
							eMsg("로그인을 하세요.");
							break;
						}

						new MyPage().addWindowListener(new Before(Main.this));
						break;
					}
				}
			});
			l.setName(i + "");
			if (i == 3) {
				login = l;
			}
			(i < 3 ? n_w : n_e).add(l);
		}

		img.setBorder(new LineBorder(Color.black));
		img.setVisible(false);

		popular();
	}
	
	public static void main(String[] args) {
		new Main();
	}
}
