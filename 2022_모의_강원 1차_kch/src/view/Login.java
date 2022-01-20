package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Login extends BaseFrame {

	JHintField id;
	JHintPasswordField pw;

	public Login() {
		super("��������", 1200, 600);
		add(new JLabel(getIcon("./��������/images/login.jpg", 800, 600)), "West");
		add(c = new JPanel(new BorderLayout(5, 60)));

		c.add(cc = new JPanel(new BorderLayout(5, 5)));

		cc.add(lbl("�α���", JLabel.LEFT, 20), "North");

		var row1 = new JPanel(new BorderLayout(5, 5));
		var row1_c = new JPanel(new GridLayout(0, 1, 5, 5));

		row1.add(row1_c);
		row1.add(btn("����", a -> {
			if (id.toString().isEmpty()) {
				eMsg("���̵� �Է����ּ���.");
				return;
			}

			if (pw.toString().isEmpty()) {
				eMsg("��к�ȣ�� �Է����ּ���.");
				return;
			}

			if (id.toString().equals("admin") && pw.toString().equals("1234")) {
				iMsg("�����ڷ� �α����մϴ�.");
				new AdminMain().addWindowListener(new before(this));
			}

			try {
				var rs = stmt.executeQuery("select * from user where id = '" + id + "' and pwd = '" + pw + "'");
				if (rs.next()) {
					uno = rs.getString(1);
					uid = rs.getString("id");
					upwd = rs.getString("pwd");
					uname = rs.getString("name");
					uemail = rs.getString("email");
					upoint = rs.getString("point");
					new UserMain().addWindowListener(new before(this));
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}), "East");
		row1_c.add(id = new JHintField("id", 1));
		row1_c.add(pw = new JHintPasswordField("password", 1));

		cc.add(row1);

		var row2 = new JPanel(new BorderLayout());

		var f_lbl = lbl("���̵�/��й�ȣ ã��", JLabel.LEFT);
		row2.add(f_lbl);
		row2.add(themebtn(this), "East");

		cc.add(row2, "South");

		var n_lbl = lbl("���ο� ���� ����� ��", JLabel.LEFT);
		c.add(n_lbl, "South");

		c.setBorder(new EmptyBorder(150, 50, 200, 50));

		f_lbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				new Find().setVisible(true);
				super.mousePressed(e);
			}
		});

		n_lbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				new Sign().setVisible(true);
				super.mousePressed(e);
			}
		});

		setVisible(true);
	}

	public static void main(String[] args) {
		new Login();
	}
}
