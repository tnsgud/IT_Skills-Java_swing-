package view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class LoginPage extends BasePage {

	JTextField txt[] = { new JTextField(15), new JPasswordField(15) };

	ChkBoxPanel chkp;

	public LoginPage() {
		init();
	}

	void init() {
		setLayout(new GridBagLayout());
		add(sz(c = new JPanel(new BorderLayout(5, 5)), 200, 250));
		c.setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(5, 5, 5, 5)));
		c.add(lbl("COVID-19", JLabel.CENTER, 20), "North");
		c.add(cc = new JPanel(new GridLayout(0, 1, 5, 5)));
		for (var cap : "ID,PW".split(",")) {
			cc.add(lbl(cap, JLabel.LEFT));
			cc.add(txt[Arrays.asList("ID,PW".split(",")).indexOf(cap)]);
		}
		c.add(cs = new JPanel(new BorderLayout()), "South");
		cs.add(chkp = new ChkBoxPanel(), "North");

		cs.add(hyplbl("처음이십니까?", JLabel.LEFT, 13, Font.PLAIN, Color.ORANGE, new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				mf.swapPage(new SignPage());
				super.mousePressed(e);
			}
		}));
		cs.add(btn("로그인", a -> {
			if (txt[0].getText().isEmpty() || txt[1].getText().isEmpty()) {
				eMsg("빈칸");
				return;
			}

			if (!chkp.chk) {
				eMsg("캡챠");
				return;
			}

			try {
				var rs = stmt.executeQuery("select * from user u, region r, point p where id = '" + txt[0].getText()
						+ "' and pw = '" + txt[1].getText() + "' and r.no = u.region and u.point=p.no");
				if (rs.next()) {
					System.out.println(rs.getString(13) + rs.getString(14));
					uno = rs.getString(1);
					uname = rs.getString(2);
					uid = rs.getString(3);
					upw = rs.getString(4);
					ubirth = rs.getString(6);
					upoint = rs.getString(13) +", "+ rs.getString(14);
					uphone = "010-2776-5930";
					ulocal = rs.getString(11);
					uage = (LocalDate.now().getYear() - LocalDate.parse(rs.getString("birth")).getYear()) + "세";
					mf.addNavigater();
					mf.swapPage(new SearchPage());
				} else {
					eMsg("없어");
					return;
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}), "South");
	}

	class ChkBoxPanel extends JPanel {
		Timer timer;
		JPanel clickBox;
		JLabel label;
		boolean trigger, chk;
		int sarc = 0;

		public ChkBoxPanel() {
			super(new BorderLayout(5, 5));
			add(sz(clickBox = new JPanel() {
				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					var g2 = (Graphics2D) g;
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

					g2.setStroke(new BasicStroke(2f));

					if (trigger) {
						g2.setStroke(new BasicStroke(3f));
						g2.setColor(Color.LIGHT_GRAY);
						g2.drawOval(3, 3, 25, 25);
						g2.setColor(Color.GRAY);
						g2.drawArc(3, 3, 25, 25, sarc, sarc);
						if (sarc >= 365) {
							timer.stop();
							new reCAPCHA(LoginPage.this).setVisible(true);
						}
					} else if (chk) {
						g2.setColor(Color.GREEN);
						g2.setFont(new Font("", Font.BOLD + Font.PLAIN, 20));
						var fn = g2.getFontMetrics();
						var w = fn.stringWidth("✔");
						g2.drawString("✔", w / 2, 25);
					} else {
						g2.setColor(Color.BLACK);
						g2.drawRect(3, 3, 25, 25);
					}
				}
			}, 30, 30), "West");
			add(label = lbl("로봇이 아닙니다.", JLabel.LEFT, Font.PLAIN, 13));
			clickBox.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					drawChk();
				}
			});
		}

		void drawChk() {
			if (!trigger && !chk) {
				System.out.println("It's Called");
				trigger = true;
				clickBox.repaint();
				timer = new Timer(1, a -> {
					sarc += 5;
					clickBox.repaint();
				});
				timer.start();
			} else {
				clickBox.revalidate();
				clickBox.repaint();
			}
		}
	}

}
