package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Login extends BaseFrame {
	String cap[] = "아이디,비밀번호".split(",");
	JTextField txt[] = new JTextField[2];

	public Login() {
		super("로그인", 250, 200);

		ui();

		setVisible(true);
	}

	private void ui() {
		setLayout(new BorderLayout(5, 5));

		add(lbl("방탈출", 0, 30), "North");
		add(c = new JPanel(new GridLayout(0, 1, 5, 5)));
		add(s = new JPanel(new GridLayout(1, 0, 5, 5)), "South");

		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel();
			p.add(sz(lbl(cap[i], 2), 50, 20));
			p.add(txt[i] = (i == 0 ? new JTextField(15) : new JPasswordField(15)));
			c.add(p);
		}

		for (var c : "로그인,회원가입".split(",")) {
			s.add(btn(c, a -> {
				if (c.equals("로그인")) {
					for (var t : txt) {
						if (t.getText().isEmpty()) {
							eMsg("빈칸이 있습니다.");
							return;
						}
					}

					var rs = rs("select * from user where u_id=? and u_pw=?", txt[0].getText(), txt[1].getText());
					try {
						if (rs.next()) {
							iMsg(rs.getString(4) + "님 환영합니다.");
							uno = rs.getInt(1);
							Main.btns.forEach(b->b.setEnabled(true));
							dispose();
						} else {
							eMsg("회원 정보가 일치하지 않습니다.");
							return;
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					new Sign().addWindowListener(new Before(this));
				}
			}));
		}

		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
	}
}
