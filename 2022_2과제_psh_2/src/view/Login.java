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
		super("로그인", 250, 200);

		ui();

		setVisible(true);
	}

	private void ui() {
		setLayout(new BorderLayout(5, 5));

		add(lblH("방탈출", 0, 30), "North");
		add(c = new JPanel(new GridLayout(0, 1, 5, 5)));
		add(s = new JPanel(new GridLayout(1, 0, 5, 5)), "South");

		var cap = "아이디,비밀번호".split(",");
		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel();
			p.add(sz(lbl(cap[i], 2), 50, 20));
			p.add(txt[i] = (i == 0 ? new JTextField(14) : new JPasswordField(14)));
			c.add(p);
		}

		for (var c : "로그인,회원가임".split(",")) {
			s.add(btn(c, a -> {
				if (a.getActionCommand().equals("로그인")) {
					for (var t : txt) {
						if (t.getText().isEmpty()) {
							eMsg("빈칸이 있습니다.");
							return;
						}
					}

					var rs = getResult("select * from user where u_id=? and u_pw=?", txt[0].getText(),
							txt[1].getText());
					if (rs.isEmpty()) {
						eMsg("회우너 정보가 일치하지 않습니다.");
						return;
					}

					iMsg(rs.get(0).get(3) + "님 환영합니다.");
					uno = toInt(rs.get(0).get(0));
					Main.login();
					dispose();
				} else {
					new Sign();
				}
			}));
		}

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
	}

	public static void main(String[] args) {
		new Login();
	}
}
