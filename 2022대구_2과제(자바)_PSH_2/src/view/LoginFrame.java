package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.stream.Stream;

import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginFrame extends BaseFrame {
	JTextField txt[] = new JTextField[2];

	public static void main(String[] args) {
		new LoginFrame();
	}
	
	public LoginFrame() {
		super("Login", 400, 350);

		setLayout(new BorderLayout(10, 10));

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new GridLayout(0, 1, 0, 5)));
		add(s = new JPanel(new BorderLayout()), "South");

		n.add(lbl("로그인", 2, 30));
		n.add(lbl("더 많은 서비스를 이용하기 위해 로그인하세요!", 2), "South");

		var hint = "User ID,User Password".split(",");
		for (int i = 0; i < hint.length; i++) {
			c.add(txt[i] = i == 0 ? txt(hint[i], 0) : txtPw(hint[i], 0));
		}

		s.add(btnRound("로그인", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty()) {
					eMsg((t instanceof JPasswordField ? "비밀번호" : "아이디") + "를 입력하세요.");
					t.requestFocus();
					return;
				}
			}

			if (txt[0].getText().equals("admin") && txt[1].getText().equals("1234")) {
				iMsg("관리자님 환영합니다.");
				new AdminFrame().addWindowListener(getWindowListeners()[0]);
				setVisible(false);
				return;
			}

			var rs = getRows("select * from user where u_id =? and u_pw = ?", txt[0].getText(), txt[1].getText());
			if (rs.isEmpty()) {
				eMsg("아이디 또는 비밀번호가 일치하지 않습니다.");
				Stream.of(txt).forEach(t -> t.setText(""));
				txt[0].requestFocus();
				return;
			}

			BasePage.user = rs.get(0);
			iMsg(BasePage.user.get(3) + "님 환영합니다.");

			var before = ((Before) getWindowListeners()[0]).b;

			if (before instanceof MainFrame) {
				((MainFrame) before).login();
			} else {
				((CinemaFrame) before).login();
				((MainFrame) ((Before) before.getWindowListeners()[0]).b).login();
			}
			dispose();
		}));
		s.add(ss = new JPanel(new FlowLayout(1)), "South");

		((JPasswordField) txt[1]).setEchoChar('●');

		ss.add(lbl("<html><font color='gray'>아직 계정 없으신가요?", 0));
		ss.add(lbl("계정 만들기 >", 0, 12, e -> new SignFrame().addWindowListener(new Before(this))));

		setVisible(true);
	}
}
