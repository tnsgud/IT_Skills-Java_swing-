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
	ChkBoxPanel chk = new ChkBoxPanel();
	JLabel sign;

	public LoginPage() {
		ui();
	}

	private void ui() {
		var cc = new JPanel(new GridLayout(0, 1, 5, 5));
		var cs = new JPanel(new BorderLayout(5, 5));
		setLayout(new GridBagLayout());
		add(sz(border(c = new JPanel(new BorderLayout()),
				new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(5, 5, 5, 5))), 200, 250));
		c.add(lbl("COVID-19", 0, 20), "North");
		c.add(cc);
		c.add(cs, "South");
		for (var cap : "ID,PW".split(",")) {
			cc.add(lbl(cap, 2, 12));
			cc.add(txt[Arrays.asList("ID,PW".split(",")).indexOf(cap)]);
		}
		cs.add(chk, "North");
		cs.add(sign = lbl("<html><u>처음이십니까?", 2, Font.BOLD, 13));
		cs.add(btn("로그인", a -> {
		}), "South");
		sign.setForeground(Color.orange);

	}

	class ChkBoxPanel extends JPanel {
		JPanel box;
		boolean tri, chk;
		int sarc = 0;
		Timer timer;

		public ChkBoxPanel() {
			super(new BorderLayout(5, 5));
			add(sz(box = new JPanel() {
				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					var g2 = (Graphics2D) g;

					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2.setStroke(new BasicStroke(2));

					if (tri) {
						g2.setStroke(new BasicStroke(3));
						g2.setColor(Color.LIGHT_GRAY);
						g2.drawOval(3, 3, 25, 25);
						g2.setColor(Color.GRAY);
						g2.drawArc(3, 3, 25, 25, sarc, sarc);
						if(sarc >= 365) {
							timer.stop();
							new Capcha();
						}
					} else if (chk) {

					} else {
						g2.setColor(Color.BLACK);
						g2.drawRect(3, 3, 25, 25);
					}
				}
			}, 30, 30), "West");
			add(lbl("로봇이 아닙니다.", 2, 13));
			box.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					drawChk();
				}
			});
		}

		void drawChk() {
			if (!tri && !chk) {
				tri = true;
				box.repaint();
				timer = new Timer(1, a -> {
					sarc += 5;
					box.repaint();
				});
				timer.start();
			} else {

			}
		}
	}

	public static void main(String[] args) {
		BasePage.mf.swapPage(new LoginPage());
		BasePage.mf.setVisible(true);
	}
}
