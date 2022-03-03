package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

//import base.BasePage;

public class LoginPage extends BasePage {

	String[] cap = "ID,PW".split(",");
	JTextField txt[] = { new JTextField(10), new JPasswordField(10) };

	JPanel m;

	public LoginPage() {
		setLayout(new GridBagLayout());
		setBackground(Color.BLACK);

		add(size(m = new JPanel(new BorderLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				RoundRectangle2D rec = new RoundRectangle2D.Float(1.5f, 1.5f, getWidth() - 3, getHeight() - 3, 25, 25);
				g2.setColor(Color.LIGHT_GRAY);
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.fill(rec);
			}
		}, 250, 280));

		var c = new JPanel(new GridLayout(0, 1, 5, 5));
		c.add(lbl("Music Player", JLabel.CENTER, Font.BOLD, 30), "North");
		m.add(c);

		for (int i = 0; i < txt.length; i++) {
			txt[i].setBackground(Color.GRAY);
			c.add(lbl(cap[i], JLabel.LEFT, Font.BOLD, 10));
			c.add(txt[i]);
		}

		c.add(btn("�α���", a -> {
			if (txt[0].getText().equals("") || txt[1].getText().equals("")) {
				eMsg("���̵�� ��й�ȣ�� ��� �Է��ؾ� �մϴ�.");
				return;
			}
			if (txt[0].getText().equals("admin") && txt[1].getText().equals("1234")) {
				iMsg("�����ڴ� ȯ���մϴ�.");
				mf.swapPage(new AdminPage());
			} else {
				try {
					var rs = stmt.executeQuery("select serial, name, region from user where id = '" + txt[0].getText()
							+ "' and pw ='" + txt[1].getText() + "'");
					if (rs.next()) {
						u_serial = rs.getInt(1);
						u_region = rs.getInt(3);
						iMsg(rs.getString(2) + "�� ȯ���մϴ�.");
						mf.home();
						mf.swapView(new HomePage());
					} else {
						eMsg("���̵� �Ǵ� ��й�ȣ�� ��ġ���� �ʽ��ϴ�.");
						return;
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}), "South");

		c.setOpaque(false);
		m.setOpaque(false);

		c.setBorder(new EmptyBorder(10, 20, 20, 20));
	}

}
