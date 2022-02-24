package view;

import java.awt.GridLayout;
import java.util.stream.Stream;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class Sign extends BaseDialog {
	String[] h = "Name,Id,Password,Password확인,E-mail".split(",");
	JTextField txt[] = new JTextField[h.length];

	public Sign() {
		super("계정 등록하기", 300, 400);

		add(c = new JPanel(new GridLayout(0, 1, 10, 10)));
		c.add(lbl("계정 정보", 2, 25));
		for (int i = 0; i < h.length; i++) {
			c.add(txt[i] = (i == 2 || i == 3 ? new JHintPassword(h[i], 20) : new JHintField(h[i], 20)));
		}
		c.add(btn("회원가입", a -> {
			Stream.of(txt).forEach(t -> {
				if (t.getText().isEmpty()) {
					eMsg("공란을 확인해주세요.");
					return;
				}
			});

			if (!txt[2].getText().equals(txt[3].getText())) {
				eMsg("PW확인이 일치하지 않습니다.");
				return;
			}

			if (!txt[2].getText().matches(".*[\\W].*")) {
				eMsg("특수문자를 포함해주세요.");
				return;
			}

			if (!getOne("select * from user where id=?", txt[1].getText()).isEmpty()) {
				eMsg("Id가 중복되었습니다.");
				return;
			}

			if (!getOne("select * from user where email=?", txt[4].getText()).isEmpty()) {
				eMsg("E-mail이 중복되었습니다.");
				return;
			}

			iMsg("회원가입이 완료되었습니다.");
			execute("insert into user values(0, ?,?,?,?,?)", txt[1].getText(), txt[2].getText(), txt[0].getText(),
					txt[4].getText(), 0);
			dispose();
		}));

		setVisible(true);
	}

	public static void main(String[] args) {
		new Sign();
	}
}
