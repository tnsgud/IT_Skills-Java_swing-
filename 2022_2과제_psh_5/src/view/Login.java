package view;

import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Login extends BaseFrame {
	JTextField txt[] = new JTextField[2];

	public Login() {
		super("로그인", 250, 150);

		add(lblH("방탈출", 0, 0, 20), "North");
		add(c = new JPanel(new GridLayout(0, 1)));
		add(s = new JPanel(new GridLayout(1, 0, 5, 5)), "South");

		var cap = "아이디,비밀번호".split(",");
		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel(new FlowLayout(0));
			p.add(sz(lbl(cap[i], 2), 50, 20));
			p.add(txt[i] = i < 1 ? new JTextField(15) : new JPasswordField(15));
			c.add(p);
		}

		var c = "로그인,회원가입".split(",");
		for (int i = 0; i < c.length; i++) {
			s.add(btn(c[i], a -> {
				if (a.getActionCommand().equals("로그인")) {
					for (var t : txt) {
						if (t.getText().isEmpty()) {
							eMsg("빈칸이 있습니다.");
							return;
						}
					}
					
					var rs = rs("select * from user where u_id=? and u_pw=?", txt[0].getText(), txt[1].getText());
					if(rs.isEmpty()) {
						eMsg("회원 정보가 일치하지 않습니다.");
						return;
					}
					
					user = rs.get(0);
					iMsg(user.get(3)+"님 환영합니다.");
					dispose();
					Main.login();
				} else {
					new Sign().addWindowListener(new Before(this));
				}
			}));
		}

		setVisible(true);
	}

	public static void main(String[] args) {
		new Login();
	}
}
