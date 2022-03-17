package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Login extends BaseFrame {
	JTextField txt[] = new JTextField[2];

	public Login() {
		super("로그인", 300, 150);

		setLayout(new BorderLayout(5, 5));

		add(lblH("아르바이트", 0, 0, 25), "North");
		add(c = new JPanel(new BorderLayout(5, 5)));

		c.add(cc = new JPanel(new GridLayout(0, 1, 10, 10)));
		c.add(btn("로그인", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty()) {
					eMsg("빈만이 존재합니다.");
					return;
				}
			}

			if (txt[0].getText().equals("admin") && txt[1].getText().equals("1234")) {
				iMsg("관리자로 로그인하였습니다.");
				new Admin();
				setVisible(false);
			}

			user = rs("select * from user where u_id=? and u_pw=?", txt[0].getText(), txt[1].getText()).get(0);
			if (user.isEmpty()) {
				eMsg("회원 정보가 일치하지 않습니다.");
				txt[0].setText("");
				txt[1].setText("");
				txt[0].requestFocus();
				return;
			}

			Main.name.setText(user.get(1) + "님 환영합니다.");
			iMsg(Main.name.getText());
			Main.login();
			dispose();
		}), "East");

		var cap = "아이디,비밀번호".split(",");
		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel(new BorderLayout());
			p.add(sz(lbl(cap[i], 2), 50, 20), "West");
			p.add(txt[i] = i < 1 ? new JTextField() : new JPasswordField());
			cc.add(p);
		}

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		setVisible(true);
	}

	public static void main(String[] args) {
		new Login();
	}
}
