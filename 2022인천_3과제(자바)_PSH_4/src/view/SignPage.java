package view;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class SignPage extends BasePage {
	JTextField txt[] = new JTextField[6];
	JComboBox<String> com = new JComboBox<String>(getRows("select name from building where type = 2").stream()
			.map(a -> a.get(0).toString()).toArray(String[]::new));

	public static void main(String[] args) {
		mf = new MainFrame();
		mf.swap(new SignPage());
		mf.setVisible(true);
	}
	
	public SignPage() {
		add(c = new JPanel(new GridBagLayout()));
		
		c.add(cc = sz(new JPanel(new GridLayout(0, 1)), 250, 450));

		cc.add(lbl("회원가입", 0, 30));

		var cap = "이름,아이디,비밀번호,비밀번호 확인,전화번호,생년월일,거주지".split(",");
		for (int i = 0; i < cap.length; i++) {
			cc.add(lbl(cap[i], 2, 20));

			if (i == 6) {
				cc.add(com);
			} else {
				cc.add(txt[i] = i == 2 || i == 3 ? new JPasswordField() : new JTextField());
			}
		}

		cc.add(event(lblHyp("이미 계정이 있으십니까?", 2, 15), e -> mf.swap(new LoginPage())));
		cc.add(btn("회원가입", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty()) {
					eMsg("빈칸이 있습니다.");
					return;
				}
			}

			if (!getOne("select * from user where id = ?", txt[1].getText()).isEmpty()) {
				eMsg("아이디가 중복되었습니다.");
				return;
			}

			var pw = txt[2].getText();
			var phone = txt[4].getText();
			var birth = txt[5].getText();

			if (!(pw.matches(".*[0-9].*") && pw.matches(".*[a-zA-Z].*") && pw.matches(".*[!@#$].*"))
					|| pw.length() < 4) {
				eMsg("비밀번호 형식이 잘못되었습니다.");
				return;
			}

			if (!pw.equals(txt[3].getText())) {
				eMsg("비밀번호가 일치하지 않습니다.");
				return;
			}

			if (!phone.matches("^\\d{3}-\\d{4}-\\d{4}$")) {
				eMsg("전화번호 형식이 잘못되었습니다.");
				return;
			}
			var data = Stream.of(txt).filter(t->Arrays.asList(txt).indexOf(t) != 3).map(t -> t.getText()).collect(Collectors.toList());
			data.add(getOne("select no from building where name = ?", com.getSelectedItem()));

			iMsg("회원가입이 완료되었습니다.");
			execute("insert user values(0, ?, ?, ?, ?, ?, ?)", data.toArray());
			mf.swap(new LoginPage());
		}));

		cc.setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(5, 5, 5, 5)));
	}
}
