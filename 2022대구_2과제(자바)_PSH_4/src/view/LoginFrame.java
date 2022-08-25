package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class LoginFrame extends BaseFrame {
	HintField id = new HintField("User ID", 0);
	HintPassword pw = new HintPassword("User PW", 0);
	static boolean isMain = false;

	public static void main(String[] args) {
		new MainFrame();
	}

	public LoginFrame() {
		super("Login", 450, 350);

		add(n = new JPanel(new BorderLayout(10, 10)), "North");
		add(c = new JPanel(new GridLayout(0, 1, 20, 20)));
		add(s = new JPanel(new BorderLayout()), "South");

		n.add(lblB("로그인", 2, 30), "North");
		n.add(lbl("더 많은 서비스를 이용하기 위해 로그인하세요!", 2, 15));

		var hint = "User ID,User Password".split(",");
		for (int i = 0; i < hint.length; i++) {
			c.add(i == 0 ? id:pw);
		}

		s.add(sz(btnRound("로그인", a -> {
			if(id.isEmpty()) {
				eMsg("아이디를 입력하세요.");
				id.requestFocus();
				return;
			}
			
			if(pw.isEmpty()) {
				eMsg("비밀번호를 입력하세요.");
				pw.requestFocus();
				return;
			}

			if (id.getText().equals("admin") && pw.getText().equals("1234")) {
				iMsg("관리자닙 환영합니다.");
				new AdminFrame().addWindowListener(new Before(((Before) getWindowListeners()[0]).b));
				setVisible(false);
				return;
			}

			var rs = getRows("select * from user where u_id=? and u_pw=?", id.getText(), pw.getText());
			if (rs.isEmpty()) {
				eMsg("아이디 또는 비밀번호가 일치하지 않습니다.");
				id.init();
				pw.init();
				id.requestFocus();
				return;
			}

			user = rs.get(0);
			iMsg(user.get(3) + "님 환영합니다.");

			if (isMain) {
				MainFrame.login();
			} else {
				CinemaFrame.lblState.setText("Logout");
			}

			dispose();
		}), 0, 35), "North");
		s.add(sc = new JPanel());

		sc.add(lbl("<html><font color='gray'>아직 계정이 없으신가요?", 0, 15));
		sc.add(event(lbl("계정 만들기 >", 0, 15), e -> new SignUpFrame().addWindowListener(new Before(this))));

		n.setOpaque(false);
		c.setOpaque(false);
		s.setOpaque(false);
		sc.setOpaque(false);

		c.setBorder(new EmptyBorder(20, 0, 20, 0));

		((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 40, 20, 40));

		((JComponent)s.getComponent(0)).requestFocus();

		setVisible(true);
	}
}
