package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import db.DB;

public class Login extends BaseFrame {

	JPanel c_c, c_e, r1, r2;
	JLabel find, sign;
	String[] h = "Id,Password".split(",");
	JTextField txt[] = new JTextField[2];

	public Login() {
		super(1000, 500);

		ui();
		event();

		setVisible(true);
	}

	private void event() {
		sign.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				new Sign(Login.this);
			}
		});
		find.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				new Find(Login.this);
			}
		});
	}

	private void ui() {
		add(new JLabel(new ImageIcon(img("login.jpg", 600, 500))), "West");
		add(r1 = new JPanel(new GridBagLayout()));
		r1.add(r2 = new JPanel(new BorderLayout(100, 100)));
		r2.add(c = new JPanel(new BorderLayout(5, 5)));
		r2.add(s = new JPanel(new FlowLayout(0, 0, 0)), "South");
		c.add(lbl("로그인", 2, 25), "North");
		c.add(c_c = new JPanel(new GridLayout(0, 1)));
		c.add(c_e = new JPanel(new BorderLayout(5, 5)), "East");
		s.add(sign = lbl("새로운 계정 만들기→", 2));

		for (int i = 0; i < h.length; i++) {
			var cls = (i == 0) ? JTextField.class : JPasswordField.class;
			c_c.add(sz(txt[i] = txt(cls, 15, h[i]), txt[i].getWidth(), 30));
		}
		c_c.add(sz(find = lbl("아이디/비밀번호 찾기", 2, 12), 160, 30));

		c_e.add(btn("다음", a -> {
			for (int i = 0; i < txt.length; i++) {
				if (txt[i].getText().isEmpty() || txt[i].getText().equals(txt[i].getName())) {
					eMsg((i == 0 ? "아이디" : "비밀번호") + "를 입력해주세요.");
					return;
				}
			}

			if (txt[0].getText().equals("admin") && txt[1].getText().equals("1234")) {
				iMsg("관리자로 로그인합니다.");
				txt[0].setText("");
				txt[1].setText("");
				new AdminMain().addWindowListener(new Before(Login.this));
				return;
			}

			try {
				var rs = DB.rs("select * from user where id=? and pwd=?", txt[0].getText(), txt[1].getText());
				if (rs.next()) {
					new UserMain().addWindowListener(new Before(Login.this));
					no = rs.getInt("no");
					return;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}));
		c_e.add(themeBtn(Login.this, txt), "South");
	}

	public static void main(String[] args) {
		new Login();
	}
}
