package view;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Sign extends BaseDialog {

	JHintField fields[] = { new JHintField("Name", 1), new JHintField("Id", 1), new JHintField("Password", 1),
			new JHintField("Password 확인", 1), new JHintField("E-mail", 1) };

	public Sign() {
		super("계정 등록하기", 300, 400);
		setLayout(new GridLayout(0, 1, 5, 5));
		add(BaseFrame.lbl("계정 정보", JLabel.LEFT, 20));
		for (var f : fields)
			add(f);
		add(BaseFrame.btn("회원가입", a -> {
			for (var f : fields)
				if (f.toString().isEmpty()) {
					BaseFrame.eMsg("공란을 확인해주세요.");
					return;
				}
			String name = fields[0].toString(), id = fields[1].toString(), pw = fields[2].toString(),
					pwchk = fields[3].toString(), email = fields[4].toString();

			if (!pwchk.equals(pw)) {
				BaseFrame.eMsg("PW확인이 일치하지 않습니다.");
				return;
			}

			if (!pw.matches(".*[~!@#$%^&*()_+<>?\\|].*")) {
				BaseFrame.eMsg("특무순자를 포함해주세요.");
				return;
			}

			if (!BaseFrame.getOne("select * from user where id = '" + id + "'").equals("")) {
				BaseFrame.eMsg("id가 중복되었습니다.");
				return;
			}

			if (!BaseFrame.getOne("select * from user where email = '" + email + "'").equals("")) {
				BaseFrame.eMsg("E-mail이 중복되었습니다.");
				return;
			}

			BaseFrame.execute(
					"insert into user values(0,'" + id + "', '" + pw + "', '" + name + "', '" + email + "', 1000)");
			BaseFrame.iMsg("회원가입이 완료되었습니다.");
		}));
		((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));
	}
}
