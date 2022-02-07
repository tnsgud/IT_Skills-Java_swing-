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

import db.DB;
import tool.Tool;

public class Main extends BaseFrame implements Tool {
	String[] cap = "TICKET,MONTH SCHEDULE,CHART,LOGIN,MYPAGE".split(",");
	static JLabel profile;
	String pf_no = "M", t = "뮤지컬";
	int idx = 1;
	Timer timer;

	public Main() {
		super("메인", 600, 280);
		
		n = new JPanel(new BorderLayout());
		c = new JPanel(new BorderLayout());

		ui();
		event();

		timer = new Timer(1000, a -> {
			idx = idx == 3 ? 1 : ++idx;
			popular();
			
			repaint();
			revalidate();
		});
		timer.start();
		setVisible(true);
	}

	private void event() {
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}
	
	private void ui() {
		n.removeAll();
		c.removeAll();
		
		add(n, "North");
		add(c);
		
//		North
		{
			var w = new JPanel(new FlowLayout(1));
			var e = new JPanel(new FlowLayout(1));
			n.add(w, "West");
			n.add(e, "East");

			for (int i = 0; i < cap.length; i++) {
				var lbl = lbl(cap[i], 0);
				(i < 3 ? w : e).add(lbl);
				if(i == 3 && isLogin) {
					lbl.setText("LOGOUT");
				}
				
				if (i == 2 && isLogin) {
					e.add(sz(profile = img("회원사진/" + uno + ".jpg", 25, 25), 25, 25));
					profile.setBorder(new LineBorder(Color.black));
				}

				lbl.setName(i + "");

				lbl.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						var i = toInt(((JLabel) e.getSource()).getName());

						if (i == 0) {
							new Search().addWindowListener(new Before(Main.this));
						} else if (i == 1) {
							new Schedule().addWindowListener(new Before(Main.this));
						} else if (i == 2) {
							new Chart().addWindowListener(new Before(Main.this));
						} else if (i == 3) {
							if (isLogin) {
								isLogin = false;
								uno = 0;
								eMsg("로그아웃되었습니다.");
								ui();
							} else {
								new Login().addWindowListener(new Before(Main.this));
							}
						} else {
							if(!isLogin) {
								eMsg("로그인을 하세요.");
								return;
							}
								
							new MyPage().addWindowListener(new Before(Main.this));
						}
					}
				});
			}
		}

		popular();
	}

	private void popular() {
		c.removeAll();

		pf_no = idx == 1 ? "M" : idx == 2 ? "O" : "C";
		t = idx == 1 ? "뮤지컬" : idx == 2 ? "오페라" : "콘서트";

		var c_c = new JPanel(new FlowLayout(1));
		var s = new JPanel(new FlowLayout(1));

		c.add(c_c);
		c.add(s, "South");

		var rs = DB.rs(
				"select pf_no, p_name, count(*) from perform p, ticket t where t.p_no = p.p_no and left(pf_no, 1) = ? group by p_name order by count(*) desc limit 5",
				pf_no);
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

		for (var c : "1,2,3".split(",")) {
			var lbl = lbl(c, 0, 12);
			s.add(lbl);
			lbl.setName(c);
			lbl.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					idx = toInt(((JLabel) e.getSource()).getName());
					timer.stop();
					popular();
					timer.restart();
				}
			});
			lbl.setForeground(idx == toInt(lbl.getName()) ? Color.red : Color.black);
		}

		c.setBorder(new TitledBorder(new LineBorder(Color.black), "인기공연(" + t + ")"));

		c.revalidate();
		c.repaint();
	}

	public static void main(String[] args) {
		new Main();
	}
}
