package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.stream.Stream;

import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Login extends BaseFrame {
	JTextField txt[] = new JTextField[2];

	public Login() {
		super("로그인", 300, 150);

		add(c = new JPanel(new GridLayout(0, 1, 0, 10)));
		add(s = new JPanel(), "South");

		var cap = "ID,Pw".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new BorderLayout());

			tmp.add(sz(lbl(cap[i] + ":", 2), 80, 20), "West");
			tmp.add(txt[i] = i == 0 ? new JTextField() : new JPasswordField());

			c.add(tmp);
		}

		txt[1].setEditable(false);
		event(txt[1], e -> new KnockCode(txt[1]));

		s.add(btn("로그인", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty()) {
					eMsg("공백이 존재합니다.");
					return;
				}
			}

			if (txt[0].getText().equals("admin") && txt[1].getText().equals("1234")) {
				iMsg("관리자로 로그인하였습니다.");
				new AdminMain().addWindowListener(getWindowListeners()[0]);
				setVisible(false);
				return;
			}

			var rs = getRows("select * from user where u_id = ? and u_pw = ?", txt[0].getText(), txt[1].getText());
			if (rs.isEmpty()) {
				eMsg("로그인에 실패하였습니다.");
				Stream.of(txt).forEach(t -> t.setText(""));
				return;
			}

			user = rs.get(0);
			iMsg(user.get(1) + "님 로그인에 성공하였습니다.");
			((Main) ((Before) getWindowListeners()[0]).b).login();
			dispose();
		}));

		setVisible(true);
	}
}
