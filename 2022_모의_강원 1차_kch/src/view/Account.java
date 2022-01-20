package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Account extends BaseDialog {

	JTextField txt[] = new JTextField[5];
	String cap[] = "id,pwd,name,email,point".split(",");

	public Account() {
		super("계정", 300, 400);
		setLayout(new BorderLayout(5, 5));
		add(BaseFrame.lbl("계정", JLabel.LEFT, 20), "North");
		add(c = new JPanel(new GridLayout(0, 1, 10, 10)));
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new BorderLayout(5, 5));
			tmp.add(BaseFrame.sz(new JLabel(cap[i], JLabel.LEFT), 50, 30), "West");
			tmp.add(txt[i] = new JTextField(1));
			txt[i].setForeground(Color.BLACK);
			c.add(tmp);
		}

		add(s = new JPanel(new GridLayout(1, 0, 5, 5)), "South");

		s.add(BaseFrame.btn("수정", a -> {
			String email = txt[3].getText(), pwd = txt[2].getText();

			if (!pwd.matches(".*[~!@#$%^&*()_+<>?\\|].*")) {
				BaseFrame.eMsg("특무순자를 포함해주세요.");
				return;
			}

			if (!BaseFrame.getOne("select * from user where email = '" + email + "'").equals("")) {
				BaseFrame.eMsg("E-mail이 중복되었습니다.");
				return;
			}

			BaseFrame.execute("update user set pwd = '" + pwd + "', email = '" + email + "', name = '"
					+ txt[2].getText() + "' where no = " + BaseFrame.uno);
			
		}));
		s.add(BaseFrame.btn("취소", a -> {
			dispose();
		}));

		txt[0].setText(BaseFrame.uid);
		txt[1].setText(BaseFrame.upwd);
		txt[2].setText(BaseFrame.uname);
		txt[3].setText(BaseFrame.uemail);
		txt[4].setText(BaseFrame.getOne("select point from user where no = " + BaseFrame.uno + ""));
		txt[0].setDisabledTextColor(Color.BLACK);
		txt[4].setDisabledTextColor(Color.BLACK);
		txt[0].setEnabled(false);
		txt[4].setEnabled(false);

		((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));
	}

}
