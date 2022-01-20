package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import db.DB;

import javax.swing.JTextField;

public class Sign extends BaseDialog {
	String[] h = "Name,Id,Password,Password확인,E-mail".split(",");
	JTextField txt[] = new JTextField[h.length];

	public Sign(JFrame jf) {
		super(jf, "계정 등록하기", 600, 800);

		ui();

		setVisible(true);
	}

	private void ui() {
		setLayout(new GridLayout(0, 1, 40, 40));
		add(BaseFrame.lbl("계정 정보", 2, 35));
		for (int i = 0; i < h.length; i++) {
			var cls = i == 2 || i == 3 ? JPasswordField.class : JTextField.class;
			add(txt[i] = BaseFrame.txt(cls, 25, h[i]));
			txt[i].setName(h[i]);
		}
		add(BaseFrame.btn("회원가입", a -> {
			for (var t : txt) {
				if (t.getText().equals(t.getName()) || t.getText().isEmpty()) {
					BaseFrame.eMsg("공란을 확인해주세요.");
					return;
				}
			}

			var pw = txt[2].getText();
			if (pw.equals(txt[3].getText())) {
				BaseFrame.eMsg("PW확인이 일치하지 않습니다.");
				return;
			}

			if (!pw.matches(".*[\\W].*")) {
				BaseFrame.eMsg("특수문자를 포함해주세요.");
				return;
			}

			if (DB.getOne("select * from user where id=?", txt[1].getText()) != null) {
				BaseFrame.eMsg("Id가 중복되었습니다.");
				return;
			}

			if (DB.getOne("select * from user where email=?", txt[4].getText()) != null) {
				BaseFrame.eMsg("E-mail이 중복되었습니다.");
				return;
			}

			BaseFrame.iMsg("회원가입이 완료되었습니다.");
			DB.execute("insert into user values(0, ?, ?, ?, ?, ?)", txt[1].getText(), txt[2].getText(),
					txt[0].getText(), txt[4].getText(), 0);
			dispose();
		}));

		((JPanel) getContentPane()).setBorder(new EmptyBorder(100, 50, 100, 50));
	}
}
