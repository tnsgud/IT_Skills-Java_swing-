package view;

import java.awt.GridLayout;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Find extends BaseDialog {

	JHintField f_id[] = { new JHintField("Name", 1), new JHintField("E-mail", 1) };
	JHintField f_pw[] = { new JHintField("Name", 1), new JHintField("Id", 1), new JHintField("E-mail", 1) };

	public Find() {
		super("���̵�/��й�ȣ ã��", 300, 400);
		setLayout(new GridLayout(0, 1, 5, 5));

		add(BaseFrame.lbl("���̵� ã��", JLabel.LEFT, 20));

		for (var f : f_id)
			add(f);

		add(BaseFrame.btn("���", a -> {
			for (var f : f_id)
				if (f.toString().isEmpty()) {
					BaseFrame.eMsg("������ Ȯ�����ּ���.");
					return;
				}

			try {
				var rs = BaseFrame.stmt.executeQuery(
						"select * from user where name = '" + f_id[0] + "' and email = '" + f_id[1] + "'");
				if (rs.next()) {
					BaseFrame.iMsg("������ id�� " + rs.getString(2) + "�Դϴ�.");
				} else {
					BaseFrame.eMsg("�������� �ʴ� �����Դϴ�.");
					return;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}));

		add(BaseFrame.lbl("��к�ȣ ã��", JLabel.LEFT, 20));

		for (var f : f_pw)
			add(f);

		add(BaseFrame.btn("���", a -> {
			for (var f : f_pw)
				if (f.toString().isEmpty()) {
					BaseFrame.eMsg("������ Ȯ�����ּ���.");
					return;
				}
			
			try {
				var rs = BaseFrame.stmt.executeQuery(
						"select * from user where name = '" + f_pw[0] + "' and id = '" + f_pw[1] + "' and email = '"+f_pw[2]+"'");
				if (rs.next()) {
					BaseFrame.iMsg("������ id�� PW�� " + rs.getString("pwd") + "�Դϴ�.");
				} else {
					BaseFrame.eMsg("�������� �ʴ� �����Դϴ�.");
					return;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}));

		((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));
	}
}
