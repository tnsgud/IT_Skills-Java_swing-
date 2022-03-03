package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Login extends BaseFrame {
	String[] cap = "ID,PW".split(",");
	JTextField txt[] = new JTextField[2];
	JCheckBox chkBox;

	public Login() {
		super("로그인", 300, 200);

		ui();

		setVisible(true);
	}

	private void ui() {
		setLayout(new BorderLayout(10, 10));

		var c_c = new JPanel(new GridLayout(0, 1));

		add(lbl("Orange Ticket", 0, Font.ITALIC + Font.BOLD, 25), "North");
		add(c = new JPanel(new BorderLayout(5, 5)));
		add(s = new JPanel(new BorderLayout()), "South");

		c.add(c_c);

		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel(new FlowLayout());
			p.add(sz(lbl(cap[i], 2), 20, 20));
			p.add(txt[i] = (i == 0 ? new JTextField(15) : new JPasswordField(15)));
			c_c.add(p);
		}

		c.add(btn("로그인", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty()) {
					eMsg("빈칸이 존재합니다.");
					return;
				}
			}

			var rs = rs("select * from user where u_id=? and u_pw=?", txt[0].getText(), txt[1].getText() + "\r");
			try {
				if (rs.next()) {
					isLogin = true;
					uno = rs.getInt(1);
					txt[0].setText(chkBox.isSelected() ? txt[0].getText() : "");
					txt[1].setText("");

					iMsg(rs.getString(2) + "님 환영합니다.");

					if(pno == 0) {
						new Main();
					}else {
						dispose();
					}
				} else {
					eMsg("ID 또는 PW가 일치하지 않습니다.");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}), "East");

		s.add(chkBox = new JCheckBox("아이디 저장"), "West");
		s.add(btn("회원가입", a -> {
			new Sign().addWindowListener(new Before(Login.this));
		}), "East");

		((JPanel) getContentPane()).setBorder(new EmptyBorder(0, 5, 10, 10));
	}
}