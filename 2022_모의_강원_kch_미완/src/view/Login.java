package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Login extends BaseFrame {

	JTextField txt[] = { new JHintField("Id", 15), new JHintPasswordField("Password", 15) };

	public Login() {
		super("�α���", 1000, 600);
		theme = true;
		setVisible(true);

		add(new JLabel(getIcon("./��������/images/login.jpg", (int) (getContentPane().getWidth() * 0.65),
				getContentPane().getHeight())), "West");
		add(getPos(c = new JPanel(null)));
		
		var ccc = new JPanel(new GridLayout(0, 1, 5, 5));

		c.add(cc = new JPanel(new BorderLayout(5, 5)));

		cc.add(lbl("�α���", JLabel.LEFT, 20), "North");

		cc.add(ccc);
		ccc.add(txt[0]);
		ccc.add(txt[1]);
		cc.add(cs = new JPanel(new BorderLayout()), "South");
		var f = lbl("�ƾƵ�/��й�ȣ ã��", JLabel.LEFT, 12);
		cs.add(f);
		f.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				new Find().setVisible(true);
				super.mousePressed(e);
			}
		});
		cs.add(themeButton(), "East");
		cc.add(btn("�α���", a -> {
			if (txt[0].toString().isEmpty()) {
				eMsg("���̵� �Է����ּ���.");
				return;
			}

			if (txt[1].toString().isEmpty()) {
				eMsg("��й�ȣ�� �Է����ּ���.");
				return;
			}

			if (txt[0].toString().equals("Admin") && txt[1].toString().equals("1234")) {
				iMsg("�����ڷ� �α��� �մϴ�.");
				return;
			}

			try {
				var rs = stmt.executeQuery("select * from user where id = '" + txt[0].getText() + "' and pwd = '"
						+ txt[1].getText() + "'");
				if (rs.next()) {
					uno = rs.getString(1);
					uname = rs.getString(4);
					new UserMain().addWindowListener(new Before(this));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}), "East");

		var rg = lbl("���ο� ���� ������", JLabel.LEFT);
		rg.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				new Sign().setVisible(true);;
				super.mousePressed(e);
			}
		});
		c.add(rg);
		rg.setBounds(40, 300, 150, 20);

		cc.setBounds(40, 140, 250, 120);
		revalidate();
		repaint();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			Login l = new Login();
			BaseFrame.setTheme(l, true);
		});
	}
}
