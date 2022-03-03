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

	
	JButton btn1 = BaseFrame.btn("계속", a -> {
		if (txt1[0].toString().isEmpty() || txt1[1].toString().isEmpty()) {
			BaseFrame.eMsg("공란을 확인해주세요.");
			return;
		}

		try {
			var rs = BaseFrame.stmt.executeQuery("select * from user where name like '%" + txt1[0].toString()
					+ "%' and email like '%" + txt1[1].toString() + "%'");

			System.out.println("select * from user where name like '%" + txt1[0].toString() + "%' and email like '%"
					+ txt1[1].toString() + "%'");
			if (rs.next()) {
				BaseFrame.iMsg("귀하의 Id는 " + rs.getString("id") + "입니다.");
			} else {
				BaseFrame.eMsg("존재하지 않는 정보입니다.");
				return;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	});
	
	JButton btn2 = BaseFrame.btn("계속", a -> {
		if (txt2[0].toString().isEmpty() || txt2[1].toString().isEmpty() || txt2[2].toString().isEmpty()) {
			BaseFrame.eMsg("공란을 확인해주세요.");
			return;
		}

		try {
			var rs = BaseFrame.stmt.executeQuery("select * from user where name like '%" + txt2[0].toString()
					+ "%' and email like '%" + txt2[2].toString() + "%' and id like '%" + txt2[1].toString() + "%'");
			if (rs.next()) {
				BaseFrame.iMsg("귀하의 Id에 PW는 " + rs.getString("pwd") + "입니다.");
			} else {
				BaseFrame.eMsg("존재하이 않는 정보입니다.");
				return;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	});

	JComponent jc[] = { BaseFrame.lbl("아이디 찾기", JLabel.LEFT, 20), txt1[0], txt1[1], btn1,
			BaseFrame.lbl("비밀번호 찾기", JLabel.LEFT, 20), txt2[0], txt2[1], txt2[2], btn2 };

	public Find() {
		super("아이디/비밀번호 찾기", 300, 400);
		setLayout(new GridLayout(0, 1, 5, 5));
		for (int i = 0; i < jc.length; i++) {
			add(jc[i]);
		}
		((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));
	}
}
