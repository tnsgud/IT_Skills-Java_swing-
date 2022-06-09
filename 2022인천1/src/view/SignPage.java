package view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
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
	JComboBox<String> com;

	public SignPage() {
		setLayout(new GridBagLayout());

		add(c = sz(new JPanel(new GridLayout(0, 1, 5, 5)), 250, 500));

		c.add(lbl("회원가입", 0, 25));
		var cap = "이름,아이디,비밀번호,비밀번호 확인,전화번호,주민번호,거주지".split(",");
		for (int i = 0; i < cap.length; i++) {
			c.add(lbl(cap[i], 2, 15));

			if (i < 5) {
				c.add(txt[i] = (i == 2 || i == 3 ? new JPasswordField() : new JTextField()));
			} else if (i == 5) {
				var tmp = new JPanel(new FlowLayout(0, 0, 0));

				tmp.add(txt[i] = sz(new JTextField(15), 1, 23));
				tmp.add(lbl(" - ", 0));
				tmp.add(lbl("********", 2, 18));

				c.add(tmp);
			} else {
				c.add(com = new JComboBox<>(rs("select name from building where type=2").stream()
						.flatMap(a -> a.stream()).toArray(String[]::new)));
			}
		}

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

			if (!(pw.matches(".*[a-zA-Z].*") && pw.matches(".*[0-9].*") && pw.matches(".*[!@#$].*"))
					|| pw.length() < 4) {
				eMsg("비밀번호롤 확인해주세요.");
				return;
			}
			
			if(!pw.equals(txt[3].getText())) {
				eMsg("비밀번호가 일치하지 않습니다.");
				return;
			}
			
			if(!txt[5].getText().matches(".*[0-9].*")) {
				eMsg("주민번호를 확인해주세요.");
				return;
			}
			
			var arr = new ArrayList<>();
			Stream.of(txt).filter(t->Arrays.asList(txt).indexOf(t) != 3).forEach(t->arr.add(t.getText()));
			arr.add(com.getSelectedItem());
			
			execute("insert user values(0,?,?,?,?,?,?)", arr.toArray());
			
			iMsg("회원가입이 완료되었습니다.");
			mf.swap(new LoginPage());
		}));
		c.add(hyplbl("이미 계정이 있으십니까?", 2, 1, 15, Color.orange, () -> mf.swap(new LoginPage())));

		c.setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(5, 5, 5, 5)));
	}

	public static void main(String[] args) {
		mf.swap(new SignPage());
		mf.setVisible(true);
	}
}
