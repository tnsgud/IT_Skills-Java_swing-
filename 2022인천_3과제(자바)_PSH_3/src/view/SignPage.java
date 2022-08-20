package view;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class SignPage extends BasePage {
	JTextField txt[] = new JTextField[6];
	JComboBox<String> com = new JComboBox<>(getRows("select name from building where type = 2").stream()
			.map(a -> a.get(0).toString()).toArray(String[]::new));

	public SignPage() {
		setLayout(new GridBagLayout());

		add(c = sz(new JPanel(new GridLayout(0, 1)), 250, 450));

		c.add(lbl("회원가입", 0, 30));

		var cap = "이름,아이디,비밀번호,비밀번호 확인,전화번호,생년월일,거주지".split(",");
		for (int i = 0; i < cap.length; i++) {
			c.add(lbl(cap[i], 2, 15));
			c.add(i < 6 ? txt[i] = new JTextField() : com);
		}
		c.add(lbl("이미 계정이 있으십니까?", 2, 0, 15, Color.orange, e -> mf.swap(new LoginPage())));
		c.add(btn("회원가입", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty()) {
					eMsg("빈칸이 있습니다.");
					return;
				}
			}

			if (!getOne("select * from user where id=?", txt[1].getText()).isEmpty()
					|| txt[1].getText().equals("admin")) {
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

			if (!txt[4].getText().matches("\\d{3}-\\d{4}-\\d{4}")) {
				eMsg("전화번혹 형식이 잘못되었습니다.");
				return;
			}

			iMsg("회원가입이 완료되었습니다.");
			
			var data = Stream.of(txt).filter(t -> Arrays.asList(txt).indexOf(t) != 3).map(JTextField::getText)
					.collect(Collectors.toList());
			data.add(getOne("select no from building where name = ?", com.getSelectedItem()));
			execute("insert data user(0, ?, ?, ?, ?, ?, ?)", data.toArray());

			mf.swap(new LoginPage());
		}));

		txt[5].addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (txt[5].getText().matches("[^0-9|^-]")) {
					eMsg("생년월일을 확인하세요.");
					return;
				}

				if (txt[5].getText().length() < 10) {
					return;
				}

				try {
					LocalDate.parse(txt[5].getText());
				} catch (Exception e2) {
					eMsg("생년월일을 확인하세요.");
					return;
				}
			}
		});

		c.setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(5, 5, 5, 5)));
	}
}
