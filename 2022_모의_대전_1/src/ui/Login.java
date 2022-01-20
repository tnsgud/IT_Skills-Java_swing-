package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.Arrays;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import db.DB;
import model.User;

public class Login extends BaseFrame {
	JLabel title = lbl("Orange Ticket", 0);
	String cap[] = "ID,PW".split(",");
	JTextField txt[] = { new JTextField(15), new JPasswordField(15) };
	JCheckBox chk = new JCheckBox("아이디 저장");

	public Login() {
		super("로그인", 350, 200);

		this.setLayout(new BorderLayout(10, 10));

		add(title, "North");
		add(c = new JPanel(new GridLayout(0, 1)));
		add(btn("로그인", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty()) {
					eMsg("빈칸이 존재합니다.");
					return;
				}
			}

			var user = DB.getModel(User.class, "select * from user where u_id like ? and u_pw like ?",
					"%" + txt[0].getText() + "%", "%" + txt[1].getText() + "%");
			if(user == null) {
				eMsg("ID 또는 PW가 일치하지 않습니다.");
				return;
			}
			
			iMsg(user.u_name+"님 환영합니다.");
			BaseFrame.user = user;
			dispose();
		}), "East");
		add(s = new JPanel(new BorderLayout()), "South");

		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel();
			var l = lbl(cap[i], 2);
			p.add(sz(l, 40, 20));
			p.add(txt[i]);
			c.add(p);
		}

		s.add(chk, "West");
		s.add(btn("회원가입", a -> {
			new Sign().addWindowListener(new Before(Login.this));
		}), "East");

		title.setFont(new Font("HY헤드라인M", Font.BOLD + Font.ITALIC, 25));

		((JPanel) getContentPane()).setBorder(new EmptyBorder(0, 10, 10, 10));

		setVisible(true);
	}

	public static void main(String[] args) {
		new Main();
	}
}
