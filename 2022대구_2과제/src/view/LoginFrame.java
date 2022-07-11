package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class LoginFrame extends BaseFrame {
	JTextField txt[] = new JTextField[2];
	static boolean isMain = false;

	public LoginFrame() {
		super("Login", 450, 350);

		add(n = new JPanel(new BorderLayout(10, 10)), "North");
		add(c = new JPanel(new GridLayout(0, 1, 20, 20)));
		add(s = new JPanel(new BorderLayout()), "South");

		n.add(lblB("로그인", 2, 30), "North");
		n.add(lbl("더 많은 서비스를 이용하기 위해 로그인하세요!", 2, 15));

		var hint = "User ID,User Password".split(",");
		for (int i = 0; i < hint.length; i++) {
			c.add(txt[i] = i == 0 ? hintField(hint[i], 0) : hintPassField(hint[i], 0));
		}

		s.add(sz(btnRound("로그인", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty()) {
					eMsg((t instanceof JTextField ? "아이디" : "비밀번호") + "를 입력하세요.");
					t.requestFocus();
					return;
				}
			}

			if (txt[0].getText().equals("admin") && txt[1].getText().equals("1234")) {
				iMsg("관리자닙 환영합니다.");
				new AdminFrame().addWindowListener(new Before(this));
				return;
			}

			var rs = getRows("select * from user where u_id=? and u_pw=?", txt[0].getText(), txt[1].getText());
			if (rs.isEmpty()) {
				eMsg("아이디 또는 비밀번호가 일치하지 않습니다.");
				txt[0].setText("");
				txt[1].setText("");
				txt[0].requestFocus();
				return;
			}

			user = rs.get(0);
			iMsg(user.get(3) + "님 환영합니다.");

			if (isMain) {
				MainFrame.nav[3].setText("Logout");
			} else {
				CinemaFrame.lblState.setText("Logout");
			}
			
			dispose();
		}), 0, 35), "North");
		s.add(sc = new JPanel());

		sc.add(lbl("<html><font color='gray'>아직 계정이 없으신가요?", 0, 15));
		sc.add(lbl("계정 만들기", 0, 15, e -> new SignFrame().addWindowListener(new Before(this))));

		n.setOpaque(false);
		c.setOpaque(false);
		s.setOpaque(false);
		sc.setOpaque(false);

		c.setBorder(new EmptyBorder(20, 0, 20, 0));

		((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 40, 20, 40));

		setVisible(true);
	}
}
