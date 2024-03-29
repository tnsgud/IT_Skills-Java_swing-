package view;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class SignPage extends BasePage {
	JTextField txt[] = new JTextField[6];
	JComboBox<String> com = new JComboBox<>(
			getRows("select name from building where type=2").stream().flatMap(a -> a.stream()).toArray(String[]::new));

	public SignPage() {
		setLayout(new GridBagLayout());

		add(c = sz(new JPanel(new GridLayout(0, 1, 5, 5)), 200, 500));

		c.setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(5, 5, 5, 5)));

		c.add(lbl("회원가입", 0, 20));
		var cap = "이름,아이디,비밀번호,비밀번호 확인,전화번호,생년월일,거주지".split(",");
		for (int i = 0; i < cap.length; i++) {
			c.add(lbl(cap[i], 2, 15));

			if (i == 6) {
				c.add(com);
			} else {
				c.add(txt[i] = i == 2 || i == 3 ? new JPasswordField() : new JTextField());
			}
		}
		c.add(hyplbl("이미 계정이 있으십니까?", 2, 15, Color.orange, e -> mf.swapPage(new LoginPage())));
		c.add(btn("회원가입", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty()) {
					eMsg("빈칸이 있습니다.");
					return;
				}
			}

			if (!getOne("select * from user where id=?", txt[1].getText()).isEmpty()) {
				eMsg("아이디가 중복되었습니다.");
				return;
			}

			var pw = txt[2].getText();
			if (!(pw.matches(".*[0-9].*") && pw.matches(".*[a-zA-Z].*") && pw.matches(".*[!@#$].*"))
					|| pw.length() < 4) {
				eMsg("비밀번호 형식이 잘못되었습니다.");
				return;
			}

			if (!pw.equals(txt[3].getText())) {
				eMsg("비밀번호가 일치하지 않습니다.");
				return;
			}

			if (txt[4].getText().matches("^\\d{3}-\\d{4}-\\d{4}$")) {
				eMsg("전화번호 형식이 잘못되었습니다.");
				return;
			}

			iMsg("회원가입이 완료되었습니다.");
			var data = new ArrayList<>();
			for (int i = 0; i < cap.length; i++) {
				if (i == 6) {
					data.add(com.getSelectedIndex() + 1);
				} else {
					data.add(txt[i].getText());
				}
			}
			execute("insert into user values(0,?,?,?,?,?,?)", data.toArray());
			mf.swapPage(new LoginPage());
		}));
	}

	public static void main(String[] args) {
		mf.swapPage(new SignPage());
		mf.setVisible(true);
	}
}
