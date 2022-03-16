package view;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Login extends BaseFrame {
	JTextField txt[] = new JTextField[2];

	public Login() {
		super("로그인", 250, 200);

		add(lblH("방탈출", 0, 0, 25), "North");
		add(c = new JPanel(new GridLayout(0, 1, 5, 5)));
		add(s = new JPanel(new GridLayout(1, 0, 5, 5)), "South");

		var cap = "아이디,비밀번호".split(",");
		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel();
			p.add(sz(lbl(cap[i], 2), 50, 20));
			p.add(txt[i] = i < 1 ? new JTextField(13) : new JPasswordField(13));
			c.add(p);
		}

		for (var c : "로그인,회원가입".split(",")) {
			s.add(btn(c, a -> {
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
					
					var r = rs.get(0);
					uno = toInt(r.get(0));
					iMsg(r.get(3)+"님 환영합니다.");
					Main.login();
					dispose();
				} else {
					new Sign().addWindowListener(new Before(this));
				}
			}));
		}

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		setVisible(true);
	}

	public static void main(String[] args) {
		new Login();
	}
}
