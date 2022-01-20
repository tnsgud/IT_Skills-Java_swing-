package view;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Sign extends BaseDialog {

	JTextField txt[] = { new JHintField("Name", 1), new JHintField("Id", 1), new JHintField("Password", 1),
			new JHintField("Password확인", 1), new JHintField("E-mail", 1) };

	public Sign() {
		super("계정 등록하기", 300, 500);
		setLayout(new GridLayout(0, 1, 10, 10));
		add(BaseFrame.lbl("계정 정보", JLabel.LEFT, 20));
		for (int i = 0; i < txt.length; i++) {
			add(txt[i]);
		}
		add(BaseFrame.btn("회원가입", a -> {
			for (int i = 0; i < txt.length; i++) {
				if (txt.toString().isEmpty()) {
					BaseFrame.eMsg("공란을 확인해주세요.");
					return;
				}
			}

			if (!txt[3].toString().equals(txt[2].toString())) {
				BaseFrame.eMsg("PW확인이 일치하지 않습니다.");
				return;
			}

			if (!BaseFrame.getOne("select * from user where id like '%" + txt[1].toString() + "%'").equals("")) {
				BaseFrame.eMsg("Id가 중복되었습니다.");
				return;
			}

			if (!txt[2].toString().matches(".*[\\W].*")) {
				BaseFrame.eMsg("특수문자를 포함해 주세요.");
				return;
			}

			if (!BaseFrame.getOne("select * from user where email like  '%" + txt[4].toString() + "%'").equals("")) {
				BaseFrame.eMsg("Id가 중복되었습니다.");
				return;
			}

			BaseFrame.execute("insert user values(0,'" + txt[1].toString() + "','" + txt[2].toString() + "','"
					+ txt[0].toString() + "','" + txt[4].toString() + "',1000)");
			BaseFrame.iMsg("회원가입이 완료되었습니다.");
		}));

		((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));

	}
}
