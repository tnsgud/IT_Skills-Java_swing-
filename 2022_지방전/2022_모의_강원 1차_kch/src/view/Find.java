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
		super("아이디/비밀번호 찾기", 300, 400);
		setLayout(new GridLayout(0, 1, 5, 5));

		add(BaseFrame.lbl("아이디 찾기", JLabel.LEFT, 20));

		for (var f : f_id)
			add(f);

		add(BaseFrame.btn("계속", a -> {
			for (var f : f_id)
				if (f.toString().isEmpty()) {
					BaseFrame.eMsg("공란을 확인해주세요.");
					return;
				}

			try {
				var rs = BaseFrame.stmt.executeQuery(
						"select * from user where name = '" + f_id[0] + "' and email = '" + f_id[1] + "'");
				if (rs.next()) {
					BaseFrame.iMsg("귀하의 id는 " + rs.getString(2) + "입니다.");
				} else {
					BaseFrame.eMsg("존재하지 않는 정보입니다.");
					return;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}));

		add(BaseFrame.lbl("비밀빈호 찾기", JLabel.LEFT, 20));

		for (var f : f_pw)
			add(f);

		add(BaseFrame.btn("계속", a -> {
			for (var f : f_pw)
				if (f.toString().isEmpty()) {
					BaseFrame.eMsg("공란을 확인해주세요.");
					return;
				}
			
			try {
				var rs = BaseFrame.stmt.executeQuery(
						"select * from user where name = '" + f_pw[0] + "' and id = '" + f_pw[1] + "' and email = '"+f_pw[2]+"'");
				if (rs.next()) {
					BaseFrame.iMsg("귀하의 id에 PW는 " + rs.getString("pwd") + "입니다.");
				} else {
					BaseFrame.eMsg("존재하지 않는 정보입니다.");
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
