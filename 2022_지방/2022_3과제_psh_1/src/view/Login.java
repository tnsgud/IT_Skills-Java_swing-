package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Login extends BaseFrame {
	JTextField txt[] = new JTextField[2];
	Main main;

	public Login(Main main) {
		super("로그인", 300, 150);
		this.main = main;

		ui();

		setVisible(true);
	}

	private void ui() {
		add(lblH("아르바이트", 0, 0, 20), "North");
		add(c = new JPanel(new BorderLayout(5, 5)));
		c.add(cc = new JPanel(new GridLayout(0, 1, 10, 10)));
		c.add(btn("로그인", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty()) {
					eMsg("빈칸이 존재합니다.");
					return;
				}
			}

			if (txt[0].getText().equals("admin") && txt[1].getText().equals("1234")) {
				eMsg("관리자로 로그인했습니다.");
				new Admin().addWindowListener(new Before(this));
				return;
			}

			var rs = getResults("select * from user where u_id=? and u_pw=?", txt[0].getText(), txt[1].getText());
			if (rs.isEmpty()) {
				eMsg("회원 정보가 일치하지 않습니다.");
				txt[0].setText("");
				txt[1].setText("");
				txt[0].requestFocus();
				return;
			}

			uno = rs.get(0).get(0) + "";
			uname = rs.get(0).get(1) + "";
			ugender = gender[toInt(rs.get(0).get(6)) - 1];
			ugraduate = graduate[toInt(rs.get(0).get(7))-1];
			main.login(rs.get(0).get(9));
			dispose();
		}), "East");

		for (int i = 0; i < txt.length; i++) {
			var tmp = new JPanel(new BorderLayout());
			tmp.add(sz(lbl("아이디,비밀번호".split(",")[i], 2), 50, 0), "West");
			tmp.add(txt[i] = i < 1 ? new JTextField() : new JPasswordField());
			cc.add(tmp);
		}

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
	}
}
