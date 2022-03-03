package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import db.DB;
import model.User;

public class Login extends BaseFrame {
	String cap[] = "ID,PW".split(",");
	JTextField txt[] = { new JTextField(15), new JPasswordField(15) };

	public Login() {
		super("로그인", 350, 200);

		setLayout(new BorderLayout(10, 10));

		add(new JLabel(img("./datafiles/캐릭터/로티1.jpg", 100, 150)), "West");
		add(c = new JPanel(new GridLayout(0, 1)));

		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new FlowLayout(FlowLayout.LEFT));
			tmp.add(sz(lbl(cap[i] + ":", JLabel.LEFT), 40, 12));
			tmp.add(txt[i]);
			c.add(tmp);
		}

		c.add(btn("로그인", a -> {
			for (int i = 0; i < txt.length; i++) {
				if (txt[i].getText().isEmpty()) {
					eMsg("ID 또는 PW를 입력해주세요.");
					return;
				}
			}

			if(txt[0].getText().equals("admin") && txt[1].getText().equals("1234")) {
				iMsg("관리자님 환영합니다.");
				isLoginned = 2;
				dispose();
				return;
			}
			
			user = DB.getModel(User.class, "select * from user where u_id=? and u_pw=?", txt[0].getText(),
					txt[1].getText());
			if (user == null) {
				eMsg("회워정보를 다시 확인해주세요.");
			} else {
				isLoginned = 1;
				user.age = user.age.contentEquals("1") ? "성인"
						: user.age.contentEquals("2") ? "청소년" : user.age.contentEquals("3") ? "어린이" : "노인";

				iMsg(user.name + "고객님 환영합니다.(" + user.age + (user.disable.contentEquals("1") ? ",장애인" : "") + ")");
				dispose();
			}
		}));

		c.setBorder(new EmptyBorder(25, 0, 25, 0));
		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

		setVisible(true);
	}
}
