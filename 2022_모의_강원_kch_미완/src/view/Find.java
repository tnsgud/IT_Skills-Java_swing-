package view;

import java.awt.GridLayout;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Find extends BaseDialog {

	JTextField txt1[] = { new JHintField("Name", 1), new JHintField("E-mail", 1) };
	JTextField txt2[] = { new JHintField("Name", 1), new JHintField("Id", 1), new JHintField("E-mail", 1) };

	
	JButton btn1 = BaseFrame.btn("���", a -> {
		if (txt1[0].toString().isEmpty() || txt1[1].toString().isEmpty()) {
			BaseFrame.eMsg("������ Ȯ�����ּ���.");
			return;
		}

		try {
			var rs = BaseFrame.stmt.executeQuery("select * from user where name like '%" + txt1[0].toString()
					+ "%' and email like '%" + txt1[1].toString() + "%'");

			System.out.println("select * from user where name like '%" + txt1[0].toString() + "%' and email like '%"
					+ txt1[1].toString() + "%'");
			if (rs.next()) {
				BaseFrame.iMsg("������ Id�� " + rs.getString("id") + "�Դϴ�.");
			} else {
				BaseFrame.eMsg("�������� �ʴ� �����Դϴ�.");
				return;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	});
	
	JButton btn2 = BaseFrame.btn("���", a -> {
		if (txt2[0].toString().isEmpty() || txt2[1].toString().isEmpty() || txt2[2].toString().isEmpty()) {
			BaseFrame.eMsg("������ Ȯ�����ּ���.");
			return;
		}

		try {
			var rs = BaseFrame.stmt.executeQuery("select * from user where name like '%" + txt2[0].toString()
					+ "%' and email like '%" + txt2[2].toString() + "%' and id like '%" + txt2[1].toString() + "%'");
			if (rs.next()) {
				BaseFrame.iMsg("������ Id�� PW�� " + rs.getString("pwd") + "�Դϴ�.");
			} else {
				BaseFrame.eMsg("�������� �ʴ� �����Դϴ�.");
				return;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	});

	JComponent jc[] = { BaseFrame.lbl("���̵� ã��", JLabel.LEFT, 20), txt1[0], txt1[1], btn1,
			BaseFrame.lbl("��й�ȣ ã��", JLabel.LEFT, 20), txt2[0], txt2[1], txt2[2], btn2 };

	public Find() {
		super("���̵�/��й�ȣ ã��", 300, 400);
		setLayout(new GridLayout(0, 1, 5, 5));
		for (int i = 0; i < jc.length; i++) {
			add(jc[i]);
		}
		((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));
	}
}
