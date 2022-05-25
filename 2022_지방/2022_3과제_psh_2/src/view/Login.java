package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.stream.Stream;

import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import view.BaseFrame.Before;

public class Login extends BaseFrame {
	JTextField txt[] = new JTextField[2];

	public Login(Main main) {
		super("로그인", 350, 150);
		setLayout(new BorderLayout(10, 10));

		add(lblH("아르바이트", 0, 0, 25), "North");
		add(c = new JPanel(new BorderLayout(10, 10)));

		c.add(cc = new JPanel(new GridLayout(0, 1, 10, 10)));
		c.add(btn("로그인", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty()) {
					eMsg("빈칸이 존재합니다.");
					return;
				}
			}

			if (txt[0].getText().equals("admin") && txt[1].getText().equals("1234")) {
				new Admin().addWindowListener(new Before(this));
			}

			var rs = rs("select * from user where u_id=? and u_pw=?", Stream.of(txt).map(t -> t.getText()).toArray());
			if (rs.isEmpty()) {
				eMsg("회원정보가 일치하지 않습니다.");
				Stream.of(txt).forEach(t -> t.setText(""));
				txt[0].requestFocus();
				return;
			}

			uno = rs.get(0).get(0) + "";
			uname = rs.get(0).get(1) + "";
			ugender = gender[toInt(rs.get(0).get(6)) - 1];
			ugraduate = graduate[toInt(rs.get(0).get(7)) - 1];
			main.login(rs.get(0).get(9));
			dispose();
		}), "East");

		var cap = "아이디,회원가입".split(",");
		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel(new BorderLayout());
			p.add(sz(lbl(cap[i], 2), 80, 0), "West");
			p.add(txt[i] = i == 0 ? new JTextField() : new JPasswordField());
			cc.add(p);
		}

		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

		setVisible(true);
	}

	private void ui() {

	}

	public static void main(String[] args) {
		new Main();
	}
}
