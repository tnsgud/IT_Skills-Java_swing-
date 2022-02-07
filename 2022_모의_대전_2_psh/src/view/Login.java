package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.sql.SQLException;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import db.DB;
import tool.Tool;

public class Login extends BaseFrame implements Tool {
	String[] cap = "ID,PW".split(",");
	JCheckBox chk = new JCheckBox("아이디 저장");
	JTextField txt[] = new JTextField[2];

	public Login() {
		super("로그인", 350, 200);

		ui();

		setVisible(true);
	}

	private void ui() {
		setLayout(new BorderLayout(10, 10));

		add(lbl("Orange Ticket", 0, "HY헤드라인M", Font.BOLD + Font.ITALIC, 25), "North");
		add(c = new JPanel(new BorderLayout()));
		add(s = new JPanel(new BorderLayout()), "South");

		var p = new JPanel();
		for (int i = 0; i < cap.length; i++) {
			var t = new JPanel();
			t.add(sz(lbl(cap[i], 2), 50, 20));
			t.add(txt[i] = i == 0 ? new JTextField(15) : new JPasswordField(15));
			p.add(t);
		}
		c.add(p);
		c.add(sz(btn("로그인", a -> {
			for (var t : txt) {
				if (t.getText().isBlank()) {
					eMsg("비낰이 존재합니다.");
					return;
				}
			}

			var rs = DB.rs("select * from user where u_id=? and u_pw=?", txt[0].getText(), txt[1].getText() + "\r");
			try {
				if (rs.next()) {
					iMsg(rs.getString("u_name") + "님 환영합니다.");
					isLogin = true;
					uno = rs.getInt(1);

					txt[0].setText(chk.isSelected() ? txt[0].getText() : "");
					txt[1].setText("");

					new Main();
					setVisible(false);
				} else {
					eMsg("ID 또는  PW가 일치하지 않습니다.");
					return;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				eMsg("ID 또는  PW가 일치하지 않습니다.");
				return;
			}
		}), 80, 80), "East");

		s.add(chk, "West");
		s.add(btn("회원가입 ", a -> {
			new Sign().addWindowListener(new Before(this));
		}), "East");

		((JPanel) getContentPane()).setBorder(new EmptyBorder(0, 10, 10, 10));
	}

	public static void main(String[] args) {
		new Main();
	}
}
