package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.Arrays;

import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginFrame extends BaseFrame {
	JTextField txt[] = new JTextField[2];

	public LoginFrame() {
		super("로그인", 250, 200);

		add(lbl("게임유통관리", 0, 25), "North");
		add(c = new JPanel(new GridLayout(0, 1, 10, 10)));
		add(btn("로그인", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty()) {
					eMsg("빈칸이 있습니다.");
					return;
				}
			}

			if (txt[0].getText().equals("admin") && txt[1].getText().equals("1234")) {
				iMsg("관리자로 로그인하였습니다.");
				BasePage.mf = new MainFrame();
				BasePage.mf.addWindowListener(new Before(this));
				new AdminMainPage();
				return;
			}

			var rs = getRows("seelct * from user where u_id = ? and u_pw = ?", txt[0].getText(), txt[1].getText());

			if (rs.isEmpty()) {
				eMsg("회원 정보가 일치하지 않습니다.");
				return;
			}

			BasePage.user = rs.get(0);

			var birth = LocalDate.parse(BasePage.user.get(4).toString());
			BasePage.u_age = LocalDate.now().getYear() - birth.getYear();
			BasePage.u_age -= birth.isAfter(LocalDate.now()) ? 1 : 0;

			BasePage.uAgeFilter = Arrays.asList(rs.get(0).get(7).toString().split(",")).contains("12");
			
			BasePage.mf = new MainFrame();
			BasePage.mf.addWindowListener(new Before(this));
			
			new UserMainPage();

			iMsg(BasePage.user.get(3) + "님이 로그인하였습니다.");
		}), "South");

		var cap = "ID,PW".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new BorderLayout());

			tmp.add(sz(lbl(cap[i], 2), 80, 20), "West");
			tmp.add(txt[i] = i == 0 ? new JTextField() : new JPasswordField());

			c.add(tmp);
		}

		((JPasswordField) txt[1]).setEchoChar('●');

		setVisible(true);
	}

	public static void main(String[] args) {
		new LoginFrame();
	}
}
