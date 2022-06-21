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
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class LoginPage extends BasePage {
	JTextField txt[] = new JTextField[2];
	ChkPanel chk = new ChkPanel();

	public LoginPage() {
		var cap = "ID,PW".split(",");

		setLayout(new GridBagLayout());

		add(c = sz(new JPanel(new GridLayout(0, 1, 5, 5)), 200, 280));
		c.add(lbl("COVID-19", 0, 20));

		for (int i = 0; i < txt.length; i++) {
			c.add(lbl(cap[i], 2, 15));
			c.add(txt[i] = i == 0 ? new JTextField() : new JPasswordField());
		}

		c.add(chk);
		c.add(hyplbl("처음이십니까?", 2, 0, 15, Color.orange, () -> mf.swapPage(new SignPage())));
		c.add(btn("로그인", a -> {
			for (var t : txt) {
				if(t.getText().isEmpty()) {
					eMsg("빈칸이 있습니다.");
					return;
				}
			}
			
			if (!chk.isCheck) {
				eMsg("캡챠를 확인해주세요.");
				return;
			}
			
			var rs = rs("select * from user where id=? and pw=?", txt[0].getText(), txt[1].getText());
			if(rs.isEmpty()) {
				eMsg("존재하는 회원이 없습니다.");
				return;
			}
			
			user = rs.get(0);
			mf.swapPage(new MainPage());
		}));
		c.setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(5, 5, 5, 5)));
	}

	public static void main(String[] args) {
		mf.swapPage(new LoginPage());
		mf.setVisible(true);
	}

	class ChkPanel extends JPanel {
		JPanel box;
		Timer timer;

		boolean isFocus, isCheck;
		int arc = 0;

		public ChkPanel() {
			super(new BorderLayout(5, 5));

			add(box = sz(new JPanel() {
				@Override
				public void paint(Graphics g) {
					super.paint(g);

					var g2 = (Graphics2D) g;
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2.setStroke(new BasicStroke(2f));

					if (isFocus) {
						g2.setColor(Color.LIGHT_GRAY);
						g2.drawOval(3, 3, 25, 25);
						g2.setColor(Color.black);
						g2.drawArc(3, 3, 25, 25, arc, arc);

						if (arc >= 365) {
							timer.stop();
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
			box.add(lbl("로봇이 아닙니다.", 2, 15));
			box.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					drawArc();
				}
			});
			box.setOpaque(false);
			setOpaque(false);
		}

		void drawArc() {
			if(!isFocus && !isCheck) {
				isFocus = true;
				timer = new Timer(1, a->{
					arc += 5;
					box.repaint();
				});
				timer.start();
			}else {
				box.repaint();
				box.revalidate();
			}
		}
	}
}