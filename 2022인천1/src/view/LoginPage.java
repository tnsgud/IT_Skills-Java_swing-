package view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.ColorUIResource;

public class LoginPage extends BasePage {

	ChkPanel chk;
	JTextField txt[] = { new JTextField(), new JPasswordField() };

	public LoginPage() {
		setLayout(new GridBagLayout());
		ui();
	}

	private void ui() {
		add(sz(c = new JPanel(new BorderLayout(5, 5)), 200, 250));

		c.add(lbl("COVID-19", 0, 20), "North");
		c.add(cc = new JPanel(new GridLayout(0, 1, 5, 5)));
		c.add(cs = new JPanel(new BorderLayout()), "South");

		var cap = "ID,PW".split(",");
		for (int i = 0; i < cap.length; i++) {
			cc.add(lbl(cap[i], 2));
			cc.add(txt[i]);
		}

		cs.add(chk = new ChkPanel(), "North");
		cs.add(hyplbl("처음이십니까?", 2, Font.BOLD, 13, Color.orange, () -> {
			mf.swap(new SignPage());
		}));
		cs.add(btn("로그인", a -> login()), "South");

		c.setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(5, 5, 5, 5)));
		c.setBackground(Color.white);
		cc.setOpaque(false);
		cs.setOpaque(false);
	}

	private void login() {
		for (var t : txt) {
			if (t.getText().isEmpty()) {
				eMsg("빈칸이 존재합니다.");
				return;
			}
		}

		if (!chk.isCheck) {
			eMsg("캡챠를 확인해주세요.");
			return;
		}

		if (txt[0].getText().equals("admin") && txt[1].getText().equals("1234")) {
			mf.swap(new AdminPage());
			return;
		}

		var rs = map("select * from user where id=? and pw=?", txt[0].getText(), txt[1].getText());
		if (rs.isEmpty()) {
			eMsg("존재하는 회원이 없습니다.");
			return;
		} else {
			user = rs.get(0);
			for (var t : txt) {
				t.setText("");
			}
			mf.swap(new MainPage());
		}
	}

	class ChkPanel extends JPanel {
		JPanel box;
		Timer time;

		int arc = 0;
		boolean isFocus, isCheck;

		public ChkPanel() {
			super(new BorderLayout(5, 5));

			add(sz(box = new JPanel() {
				@Override
				public void paint(Graphics g) {
					super.paint(g);

					var g2 = (Graphics2D) g;
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2.setStroke(new BasicStroke(2));

					if (isFocus) {
						g2.setColor(Color.LIGHT_GRAY);
						g2.drawOval(3, 3, 25, 25);
						g2.setColor(Color.gray);
						g2.drawArc(3, 3, 25, 25, arc, arc);

						if (arc >= 365) {
							time.stop();
							new Capcha(chk).setVisible(true);
						}
					} else if (isCheck) {
						g2.setColor(Color.green);
						g2.setFont(new Font("", Font.BOLD, 20));
						g2.drawString("✔", 5, 25);
					} else {
						g2.setColor(Color.black);
						g2.drawRect(3, 3, 25, 25);
					}
				}
			}, 30, 30));
			box.add(lbl("로봇이 아닙니다.", 2, 13));

			box.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					drawArc();
				}
			});

			box.setOpaque(false);
			setOpaque(false);
		}

		private void drawArc() {
			if (!isFocus && !isCheck) {
				isFocus = true;
				time = new Timer(1, e -> {
					arc += 5;
					box.repaint();
				});
				time.start();
			} else {
				box.repaint();
				box.revalidate();
			}
		}
	}

	public static void main(String[] args) {
		mf.swap(new LoginPage());
		mf.setVisible(true);
	}
}
