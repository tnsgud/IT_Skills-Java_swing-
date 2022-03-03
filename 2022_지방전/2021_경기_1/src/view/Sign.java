package view;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import db.DB;

public class Sign extends BaseFrame {
	JCheckBox check = new JCheckBox("장애인");

	public Sign() {
		super("회워가입", 250, 300);

		var cap = "이름,ID,PW,키,생년월일".split(",");
		var lbls = new JLabel[cap.length];
		var txt = new JTextField[cap.length];

		add(c = new JPanel(new GridLayout(0, 1)));

		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new FlowLayout(FlowLayout.LEFT));
			lbls[i] = lblB(cap[i] + ":", JLabel.LEFT, 12);
			txt[i] = i == 2 ? new JPasswordField(15) : new JTextField(15);

			tmp.add(sz(lbls[i], 60, 12));
			tmp.add(txt[i]);
			c.add(tmp);
		}

		{
			var t = new JPanel();
			t.add(check);
			c.add(t);
		}

		{
			var t = new JPanel();
			t.add(btn("회원가입", a -> {
				for (int i = 0; i < txt.length; i++) {
					if (txt[i].getText().isEmpty()) {
						eMsg("빈칸없이 입력하세요.");
						return;
					}
				}

				if (!DB.getOne("select * from user where u_id=?", txt[1].getText()).isEmpty()) {
					eMsg("이미 사용중인 아이디입니다.");
					return;
				}

				var pw = txt[2].getText();
				if (!(pw.matches(".*[0-9].*") && pw.matches(".*[a-zA-Z].*") && pw.matches(".*[\\!\\@\\#\\$].*"))
						|| pw.length() < 4) {
					eMsg("비밀번호를 확인해주세요.");
					return;
				}

				if (!txt[3].getText().equals(toInt(txt[3].getText()) + "")) {
					eMsg("문자는 입력할 수 없습니다.");
					return;
				}

				try {
					LocalDate date = LocalDate.parse(txt[4].getText());
					int age = LocalDate.now().getYear() - date.getYear();
					if (age < 13) {
						age = 3;
					} else if (age < 19) {
						age = 2;
					} else if (age < 65) {
						age = 1;
					} else {
						age = 4;
					}

					iMsg("회원가입이 완료되었습니다.");
					DB.execute("insert into user values(0, ?, ?, ?, ?, ?, ?, ?)", txt[0].getText(), txt[1].getText(),
							txt[2].getText(), txt[3].getText(), date, age, check.isSelected());
					dispose();
				} catch (Exception e) {
					eMsg("생년월일을 확인해주세요.");
				}
			}));
			c.add(t);
		}

		setVisible(true);
	}
}
