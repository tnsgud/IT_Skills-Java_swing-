package view;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Sign extends BaseDialog {

	JHintField fields[] = { new JHintField("Name", 1), new JHintField("Id", 1), new JHintField("Password", 1),
			new JHintField("Password Ȯ��", 1), new JHintField("E-mail", 1) };

	public Sign() {
		super("���� ����ϱ�", 300, 400);
		setLayout(new GridLayout(0, 1, 5, 5));
		add(BaseFrame.lbl("���� ����", JLabel.LEFT, 20));
		for (var f : fields)
			add(f);
		add(BaseFrame.btn("ȸ������", a -> {
			for (var f : fields)
				if (f.toString().isEmpty()) {
					BaseFrame.eMsg("������ Ȯ�����ּ���.");
					return;
				}
			String name = fields[0].toString(), id = fields[1].toString(), pw = fields[2].toString(),
					pwchk = fields[3].toString(), email = fields[4].toString();

			if (!pwchk.equals(pw)) {
				BaseFrame.eMsg("PWȮ���� ��ġ���� �ʽ��ϴ�.");
				return;
			}

			if (!pw.matches(".*[~!@#$%^&*()_+<>?\\|].*")) {
				BaseFrame.eMsg("Ư�����ڸ� �������ּ���.");
				return;
			}

			if (!BaseFrame.getOne("select * from user where id = '" + id + "'").equals("")) {
				BaseFrame.eMsg("id�� �ߺ��Ǿ����ϴ�.");
				return;
			}

			if (!BaseFrame.getOne("select * from user where email = '" + email + "'").equals("")) {
				BaseFrame.eMsg("E-mail�� �ߺ��Ǿ����ϴ�.");
				return;
			}

			BaseFrame.execute(
					"insert into user values(0,'" + id + "', '" + pw + "', '" + name + "', '" + email + "', 1000)");
			BaseFrame.iMsg("ȸ�������� �Ϸ�Ǿ����ϴ�.");
		}));
		((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));
	}
}
