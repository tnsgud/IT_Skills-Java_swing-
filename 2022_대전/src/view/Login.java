package view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Login extends BaseFrame {
	JCheckBox chk = new JCheckBox("아이디 저장");
	JTextField[] txt = { new JTextField(15), new JPasswordField(15) };
	String[] cap = "ID,PW".split(",");

	public Login() {
		super("로그인", 350, 200);

		ui();

		setVisible(true);
	}

	private void ui() {
		var c_c = new JPanel(new GridLayout(0, 1, 5, 5));

		setLayout(new BorderLayout(5, 5));
		add(lbl("Orange Ticket", 0, Font.BOLD + Font.ITALIC, 35), "North");
		add(c = new JPanel(new BorderLayout(5, 5)));
		add(s = new JPanel(new BorderLayout()), "South");

		c.add(c_c);
		c.add(btn("로그인", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty()) {
					eMsg("빈칸이 존재합니다.");
					return;
				}
			}

			var rs = rs("select * from user where u_id=? and u_pw=?", txt[0].getText(), txt[1].getText());
			try {
				if (rs.next()) {
					uno = rs.getInt(1);
					isLogin = true;
					Main.img.setIcon(img("회원사진/" + uno + ".jpg", 25, 25).getIcon());
					Main.img.setVisible(true);
					Main.lbl[3].setText("LOGOUT");
					txt[0].setText(chk.isSelected() ? txt[0].getText() : "");
					txt[1].setText("");
					iMsg(rs.getString(2) + "님 환영합니다.");
					dispose();
				} else {
					eMsg("ID 또는 PW가 일치하지 않습니다.");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}), "East");

		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel();
			p.add(sz(lbl(cap[i], 2), 50, 20));
			p.add(txt[i]);
			c_c.add(p);
		}

		s.add(chk, "West");
		s.add(btn("회원가입", a -> {
			new Sign().addWindowListener(new Before(Login.this));
		}), "East");

	}
	public static void main(String[] args) {
		new Login();
	}

}
