package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

public class MainFrame extends BaseFrame {
	static JLabel nav[] = new JLabel[4];
	

	public MainFrame() {
		super("Main", 800, 600);
		setDefaultCloseOperation(3);

		ui();

		setVisible(true);
	}

	private void ui() {
		var navP = new JPanel(new FlowLayout(1, 20, 10));
		add(n = new JPanel(new GridBagLayout()), "North");
		add(c = new JPanel(null));

		n.add(nc = sz(new JPanel(new BorderLayout(10, 10)), 400, 100));
		c.add(cc = new JPanel(new GridLayout(1, 0, 20, 50)));

		nc.add(lbl("<html><font color='white'>cinema", 0, 30), "North");
		nc.add(navP);

		var cap = "Movie,Reservation,Mypage,Login".split(",");
		for (int i = 0; i < cap.length; i++) {
			int idx = i;
			nav[i] = hyplbl(cap[i], 0, 15, e -> {
				if (e.getButton() == 1) {
					LoginFrame.isMain = true;
					var ca = nav[idx].getText();
					BasePage page = null;

					if (ca.equals(cap[0])) {
						page = new MoviePage();
					} else if (ca.equals(cap[1])) {
						page = new ReservationPage();
					} else if (ca.equals(cap[2])) {
						if (user == null) {
							var ans = JOptionPane.showConfirmDialog(null, "로그인이 필요한 작업입니다.\n로그인 하시겠습니까?", "질문",
									JOptionPane.YES_NO_OPTION);
							if (ans == JOptionPane.YES_OPTION) {
								new LoginFrame().addWindowListener(new Before(this));
								return;
							} else {
								return;
							}
						}

						page = new MypagePage();
					} else if (ca.equals(cap[3])) {
						new LoginFrame().addWindowListener(new Before(this));
					} else {
						logout();
					}

					if (idx < 3 && page != null) {
						BasePage.cf = new CinemaFrame();
						BasePage.cf.addWindowListener(new Before(this));
						BasePage.cf.lbl[idx].setFont(new Font("맑은 고딕", 1, 15));
						BasePage.cf.swapPage(page);
					}
				}
			});

			nav[i].setForeground(Color.white);
			navP.add(nav[i]);
		}
		
		var rs = getRows(
				"select r.m_no, m.m_title, count(*) from reservation r, movie m where r.m_no = m.m_no and r.r_date <= '2022-08-30' group by r.m_no order by count(*) desc limit 5");
		
		var ma = new MouseAdapter() {
			int startX = 0;

			@Override
			public void mousePressed(MouseEvent e) {
				startX = e.getX();
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				int diffX = e.getX() - startX;

				cc.setBounds(cc.getX() + diffX, 0, rs.size() * 300 + 10, 450);
				setScroll();
			}

			private void setScroll() {
				if (cc.getX() > 0) {
					cc.setBounds(0, 0, rs.size() * 300 + 10, 450);
				} else if (cc.getWidth() + cc.getX() <= c.getWidth()) {
					cc.setBounds(-725, 0, rs.size() * 300 + 10, 450);
				}
			}
		};

		
		for (var r : rs) {
			var img = new JLabel(getIcon("./지급자료/image/movie/" + r.get(0) + ".jpg", 300, 450)) {
				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					var g2 = (Graphics2D) g;
					var paint = new GradientPaint(20, 0, new Color(0, 0, 0, 0), 20, getHeight(),
							new Color(0, 0, 0, 150));

					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

					g2.setPaint(paint);
					g2.fillRect(0, 0, getWidth(), getHeight());

					g2.setColor(Color.white);
					g2.setFont(new Font("맑은 고딕", 1, 60));
					g2.drawString(rs.indexOf(r) + 1 + "", 20, getHeight() - 30);

					g2.setFont(new Font("맑은 고딕", 1, 15));
					g2.drawString(r.get(1).toString(), 60, getHeight() - 30);
				}
			};
			img.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					var sub1 = "(select sum(char_length(r_seat) - char_length(replace(r_seat, '.',''))) from reservation r where r.m_no = m.m_no) sub1";
					var sub2 = "(select ifnull(round(avg(c_rate), 1), 0) from comment c where c.m_no = m.m_no) `avg`";
					var sql = String.format("select m.*, %s, %s from movie m where m.m_no = ?", sub1, sub2);

					new MovieInfoDialog(getRows(sql, r.get(0)).get(0)).setVisible(true);
				}
			});
			img.addMouseListener(ma);
			img.addMouseMotionListener(ma);
			cc.add(img);
		}

	

		cc.addMouseListener(ma);
		cc.addMouseMotionListener(ma);

		getContentPane().setBackground(Color.black);
		n.setOpaque(false);
		nc.setOpaque(false);
		navP.setOpaque(false);
		navP.setBorder(new MatteBorder(1, 0, 0, 0, Color.white));
		c.setOpaque(false);
		cc.setOpaque(false);
		cc.setBounds(0, 0, 1510, 450);
	}

	static void login() {
		nav[3].setText("Logout");
		
		if(BasePage.cf != null) {
			CinemaFrame.lblState.setText("Logout");
		}
	}

	static void logout() {
		user = null;
		nav[3].setText("Login");
		
		if(BasePage.cf != null) {
			CinemaFrame.lblState.setText("Login");
		}
	}

	public static void main(String[] args) {
		new MainFrame();
	}
}
