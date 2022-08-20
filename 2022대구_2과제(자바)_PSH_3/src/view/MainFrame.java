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
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

public class MainFrame extends BaseFrame {
	@Override
	public JLabel lbl(String c, int a, int st, int sz) {
		var l = super.lbl(c, a, st, sz);
		l.setForeground(Color.white);
		return l;
	}

	public static void main(String[] args) {
		new MainFrame();
	}

	public MainFrame() {
		super("Main", 800, 600);
		setDefaultCloseOperation(3);
		getContentPane().setBackground(Color.black);
		ui();
		setVisible(true);
	}

	private void ui() {

		var bag = new JPanel(new GridBagLayout());

		add(bag, "North");
		add(c = new JPanel(null));

		bag.add(n = sz(new JPanel(new BorderLayout(10, 10)), 400, 100));

		n.add(lbl("cinema", 0, 0, 30));
		n.add(ns = sz(new JPanel(new FlowLayout(1, 20, 10)), 400, 50), "South");

		var cap = "Movie,Reservation,Mypage,Login".split(",");
		for (int i = 0; i < cap.length; i++) {
			int idx = i;
			ns.add(hyplbl(cap[i], 0, 15, e -> {
				var text = ((JLabel) e.getSource()).getText();
				BasePage page = null;

				if (text.equals(cap[0])) {
					page = new MoviePage();
				} else if (text.equals(cap[1])) {
					page = new ReservationPage();
				} else if (text.equals(cap[2])) {
					if (BasePage.user == null) {
						if (loginCheck()) {
							new LoginFrame().addWindowListener(new Before(this));
						}

						return;
					}

					page = new MypagePage();
				} else if (text.equals(cap[3])) {
					new LoginFrame().addWindowListener(new Before(this));
					return;
				} else {
					logout();
					return;
				}

				BasePage.cf = new CinemaFrame();
				BasePage.cf.addWindowListener(new Before(this));
				BasePage.cf.lbl[idx].setFont(new Font("맑은 고딕", 1, 15));
				BasePage.cf.swap(page);
			}));
		}

		c.add(cc = new JPanel(new GridLayout(1, 0, 20, 50)));
		cc.setBounds(0, 0, 1510, 450);

		var rs = getRows(
				"select m.m_no, m_title from reservation r, movie m where r.m_no = m.m_no group by m.m_no order by count(*) desc limit 5");

		var ma = new MouseAdapter() {
			int x = 0;

			@Override
			public void mousePressed(MouseEvent e) {
				x = e.getX();
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				int diff = e.getX() - x;

				cc.setBounds(cc.getX() + diff, 0, rs.size() * 300 + 10, 450);

				if (cc.getX() > 0) {
					cc.setBounds(0, 0, rs.size() * 300 + 10, 450);
				} else if (cc.getWidth() + cc.getX() <= c.getWidth()) {
					cc.setBounds(-725, 0, rs.size() * 300 + 10, 450);
				}
			}
		};

		for (var r : rs) {
			int i = rs.indexOf(r);

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
					g2.drawString(i + 1 + "", 20, getHeight() - 30);

					g2.setFont(new Font("맑은 고딕", 1, 15));
					g2.drawString(r.get(1).toString(), 60, getHeight() - 30);
				}
			};
			img.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getButton() != 1)
						return;

					var sub1 = "(select sum(char_length(r_seat) - char_length(replace(r_seat, '.', ','))) from reservation r where r.m_no = m.m_no) sub1";
					var sub2 = "(select ifnull(round(avg(c_rate), 1), 0) from comment  c where c.m_no = m.m_no ) sub2";
					var sql = String.format("select m.*, %s, %s from movie m where m_no = ?", sub1, sub2);

					new MovieInfoDialog(getRows(sql, r.get(0)).get(0)).setVisible(true);
				}
			});
			img.addMouseListener(ma);
			img.addMouseMotionListener(ma);
			cc.add(img);
		}

		ns.setBorder(new MatteBorder(1, 0, 0, 0, Color.white));
	}

	void logout() {
		var l = (JLabel) ns.getComponent(3);

		l.setText("Login");
		BasePage.user = null;
	}

	void login() {
		var l = (JLabel) ns.getComponent(3);

		l.setText("Logout");
	}
}
