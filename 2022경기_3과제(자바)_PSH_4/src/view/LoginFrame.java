package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.stream.Stream;

import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import view.BaseFrame.Before;

public class LoginFrame extends BaseFrame {
	JTextField txt[] = new JTextField[2];

	public LoginFrame() {
		super("로그인", 300, 200);

		add(lbl("게임유통관리", 0, 20), "North");
		add(c = new JPanel(new GridLayout(0, 1, 5, 5)));
		add(btn("로그인", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty()) {
					eMsg("빈칸이 존재합니다.");
					return;
				}
			}

			if (txt[0].getText().equals("admin") && txt[1].getText().equals("1234")) {
				iMsg("관리자로 로그인하였습니다.");
				BasePage.mf = new MainFrame();
				BasePage.mf.addWindowListener(new Before(this));
				new AdminMainPage();
				Stream.of(txt).forEach(t -> t.setText(""));
				return;
			}

			var rs = getRows("select * from user where u_id=? and u_pw =?", txt[0].getText(), txt[1].getText());
			if (rs.isEmpty()) {
				eMsg("회원정보가 일치하지 않습니다.");
				return;
			}

			BasePage.user = rs.get(0);
			iMsg(BasePage.user.get(3) + "님 로그인하였습니다.");

			var birth = LocalDate.parse(BasePage.user.get(4).toString());
			BasePage.uAge = LocalDate.now().getYear() - birth.getYear();
			BasePage.uAge -= birth.isAfter(LocalDate.now()) ? 1 : 0;

			BasePage.uAgeFilter = Arrays.asList(rs.get(0).get(7).toString().split(",")).contains("12");
			BasePage.uExp = toInt(getOne("select count(*) from library where u_no = ?", BasePage.user.get(0))) * 3
					+ getRows("select * from v2 where u_no = ? group by g_no having count(*) > 2", BasePage.user.get(0))
							.size() * 10;
			BasePage.uGd = BasePage.uExp / 20;
			
			BasePage.mf = new MainFrame();
			BasePage.mf.addWindowListener(new Before(this));
			new UserMainPage();
			Stream.of(txt).forEach(t -> t.setText(""));
		}), "South");

		var cap = "ID,PW".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new BorderLayout());

			tmp.add(sz(lbl(cap[i], 2, 15), 60, 20), "West");
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
