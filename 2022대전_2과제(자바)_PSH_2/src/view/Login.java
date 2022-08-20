package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Login extends BaseFrame {
	JTextField txt[] = new JTextField[2];

	public Login() {
		super("", 300, 150);

		add(c = new JPanel(new GridLayout(0, 1, 5, 5)));
		add(s = new JPanel(), "South");

		var cap = "ID,PW".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new BorderLayout());

			tmp.add(sz(lbl(cap[i] + ":", 2), 60, 20), "West");
			tmp.add(txt[i] = i == 0 ? new JTextField() : new JPasswordField());

			c.add(tmp);
		}

		s.add(btn("로그인", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty()) {
					eMsg("공백이 존재합니다.");
					return;
				}
			}

			if (txt[0].getText().equals("admin") && txt[1].getText().equals("1234")) {
				iMsg("관리자로 로그인하였습니다.");
				return;
			}

			var rs = getRows("select * from user where u_id = ? and u_pw = ?", txt[0].getText(), txt[1].getText());
			if (rs.isEmpty()) {
				eMsg("로그인에 실패하였습니다.");
				return;
			}

			user = rs.get(0);

			iMsg(user.get(1) + "님 로그인에 성공하였습니다.");
			((Main) ((Before) getWindowListeners()[0]).b).login();
			dispose();
		}));

		evt(txt[1], e -> {
			txt[1].setText("");
			new KnockCode(txt[1]).addWindowListener(new Before(this));
		});

		setVisible(true);
	}

	public static void main(String[] args) {
		new Login();
	}
}
