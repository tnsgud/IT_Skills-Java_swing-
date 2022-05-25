package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Login extends BaseFrame {
	JTextField txt[] = new JTextField[2];

	public Login() {
		super("로그인", 300, 150);

		add(lblH("아르바이트", 0, 0, 20), "North");
		add(c = new JPanel(new BorderLayout(5, 5)));

		c.add(cc = new JPanel(new GridLayout(0, 1)));
		c.add(btn("로그인", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty()) {
					eMsg("빈칸이 존재합니다.");
					return;
				}
			}

			if (txt[0].getText().equals("admin") && txt[1].getText().equals("1234")) {
				iMsg("관리자로 로그인하였습니다.");
				new Admin();
				setVisible(false);
				return;
			}

			var rs = rs("select * from user where u_id=? and u_pw=?", txt[0].getText(), txt[1].getText());
			if (rs.isEmpty()) {
				eMsg("회원 정보가 일치하지 않습니다.");
				txt[0].setText("");
				txt[1].setText("");
				txt[0].requestFocus();
				return;
			}

			user = rs.get(0);

			iMsg(user.get(1) + "님 환영합니다.");
			Main.img.setIcon(img("회원사진/" + user.get(0) + ".jpg", 30, 30));
			Main.name.setText(user.get(1) + "님 환영합니다.");
			Main.login();

			dispose();
		}), "East");

		var cap = "아이디,비밀번호".split(",");
		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel(new FlowLayout(0));
			p.add(sz(lbl(cap[i], 2), 60, 20));
			p.add(txt[i] = new JTextField(10));
			cc.add(p);
		}

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		setVisible(true);
	}

	public static void main(String[] args) {
		new Main();
	}
}
