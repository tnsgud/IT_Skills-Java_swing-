package view;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Sign extends BaseDialog {
	String[] h = "Name,Id,Password,Password확인,E-mail".split(",");
	JTextField[] txt = new JTextField[h.length];

	public Sign() {
		super("계정 등록하기", 400, 600);

		ui();

		setVisible(true);
	}

	private void ui() {
		setLayout(new GridLayout(0, 1, 40, 40));

		add(lbl("계정 정보", 2, 25));

		for (int i = 0; i < h.length; i++) {
			add(txt[i] = (i == 2 || i == 3 ? new JHintPassword(15, h[i]) : new JHintField(15, h[i])));
		}

		add(btn("회원가입", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty()) {
					eMsg("공란을 확인해주세요.");
					return;
				}
			}

			var pw = txt[2].getText();
			if (!pw.equals(txt[3].getText())) {
				eMsg("PW확인이 일치하지 않습니다.");
				return;
			}

			if (!pw.matches(".*[\\W].*")) {
				eMsg("특수문자를 포함해주세요.");
				return;
			}

			if (!getOne("seelct * from user where id=?", txt[1].getText()).isEmpty()) {
				eMsg("Id가 중복되었습니다.");
				return;
			}

			if (!getOne("select * from user where email=?", txt[4].getText()).isEmpty()) {
				eMsg("E-mail이 중복되었습니다.");
				return;
			}

			iMsg("회원가입이 완료되었습니다.");
			execute("insert into user values(0,?,?,?,?,?)", txt[1].getText(), txt[2].getText(), txt[0].getText(),
					txt[4].getText(), 0);
			dispose();
		}));

		((JPanel) getContentPane()).setBorder(new EmptyBorder(50, 50, 50, 50));
	}
}
