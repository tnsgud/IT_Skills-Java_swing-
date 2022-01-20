package view;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Sign extends BaseDialog {

	JTextField txt[] = { new JHintField("Name", 1), new JHintField("Id", 1), new JHintField("Password", 1),
			new JHintField("PasswordȮ��", 1), new JHintField("E-mail", 1) };

	public Sign() {
		super("���� ����ϱ�", 300, 500);
		setLayout(new GridLayout(0, 1, 10, 10));
		add(BaseFrame.lbl("���� ����", JLabel.LEFT, 20));
		for (int i = 0; i < txt.length; i++) {
			add(txt[i]);
		}
		add(BaseFrame.btn("ȸ������", a -> {
			for (int i = 0; i < txt.length; i++) {
				if (txt.toString().isEmpty()) {
					BaseFrame.eMsg("������ Ȯ�����ּ���.");
					return;
				}
			}

			if (!txt[3].toString().equals(txt[2].toString())) {
				BaseFrame.eMsg("PWȮ���� ��ġ���� �ʽ��ϴ�.");
				return;
			}

			if (!BaseFrame.getOne("select * from user where id like '%" + txt[1].toString() + "%'").equals("")) {
				BaseFrame.eMsg("Id�� �ߺ��Ǿ����ϴ�.");
				return;
			}

			if (!txt[2].toString().matches(".*[\\W].*")) {
				BaseFrame.eMsg("Ư�����ڸ� ������ �ּ���.");
				return;
			}

			if (!BaseFrame.getOne("select * from user where email like  '%" + txt[4].toString() + "%'").equals("")) {
				BaseFrame.eMsg("Id�� �ߺ��Ǿ����ϴ�.");
				return;
			}

			BaseFrame.execute("insert user values(0,'" + txt[1].toString() + "','" + txt[2].toString() + "','"
					+ txt[0].toString() + "','" + txt[4].toString() + "',1000)");
			BaseFrame.iMsg("ȸ�������� �Ϸ�Ǿ����ϴ�.");
		}));

		((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));

	}
}
