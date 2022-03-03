package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Account extends BaseDialog {

	JTextField txt[] = new JTextField[5];

	public Account() {
		super("계정", 300, 400);
		add(BaseFrame.lbl("계정", JLabel.LEFT, 20), "North");
		add(c = new JPanel(new GridLayout(0, 1, 5, 5)));
		add(s = new JPanel(new GridLayout(1, 0, 5, 5)), "South");

		try {
			var rs = BaseFrame.stmt
					.executeQuery("select id, pwd, name, email, point from user where no = " + BaseFrame.uno);
			while (rs.next()) {
				for (int i = 0; i < txt.length; i++) {
					var tmp = new JPanel(new BorderLayout());
					txt[i] = new JTextField();
					txt[i].setText(rs.getString(i + 1));
					tmp.add(BaseFrame.sz(BaseFrame.lbl("Id,pwd,name,email,point".split(",")[i], JLabel.LEFT, 12), 40,
							10), "West");
					tmp.add(txt[i]);

					c.add(tmp);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		txt[0].setEnabled(false);
		txt[4].setEnabled(false);
		txt[0].setDisabledTextColor(Color.BLACK);
		txt[4].setDisabledTextColor(Color.BLACK);

		for (var bcap : "수정,취소".split(",")) {
			s.add(BaseFrame.btn(bcap, a -> {
				if (a.getActionCommand().equals("취소")) {
					dispose();
				} else {
					for (int i = 0; i < txt.length; i++) {
						if (txt[i].getText().equals("")) {
							BaseFrame.eMsg("공란을 확인해주세요.");
							return;
						}
					}

					if (!txt[1].getText().matches(".*[\\W].*")) {
						BaseFrame.eMsg("특수문자를 포함해주세요.");
						return;
					}

					BaseFrame.execute("update user set pwd = '" + txt[1].getText() + "', name = '" + txt[2].getText()
							+ "', email = '" + txt[3].getText() + "'");

				}
			}));
		}

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
	}

}
