package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Login extends BaseFrame {

	JTextField txt[] = { new JTextField(15), new JTextField(15) };
	Main m;

	public Login(Main m) {
		super("로그인", 300, 150);
		setLayout(new BorderLayout(10, 10));

		this.m = m;

		add(crt_lbl("아르바이트", JLabel.CENTER, "HY헤드라인M", 0, 20), "North");
		add(c = new JPanel(new BorderLayout(5, 5)));
		c.add(cc = new JPanel(new GridLayout(0, 1, 10, 10)));

		for (int i = 0; i < 2; i++) {
			var temp = new JPanel(new BorderLayout());
			temp.add(sz(crt_lbl("아이디,비밀번호".split(",")[i], JLabel.LEFT), 50, 0), "West");
			temp.add(txt[i]);
			cc.add(temp);
		}

		c.add(crt_evt_btn("로그인", a -> {
			for (int i = 0; i < txt.length; i++) {
				if (txt[i].getText().isEmpty()) {
					eMsg("빈칸이 존재합니다.");
					return;
				}
			}

			if (txt[0].getText().equals("admin") && txt[1].getText().equals("1234")) {
				iMsg("관리자로 로그인하였습니다.");
				new Admin();
				setVisible(false);
				return;
			}

			var rs = getResults("select * from user where u_id = ? and u_pw = ?", txt[0].getText(), txt[1].getText());

			if (rs.size() == 0) {
				eMsg("회원 정보가 일치하지 않습니다.");
				txt[0].setText("");
				txt[1].setText("");
				txt[0].requestFocus();
				return;
			}

			uno = rs.get(0).get(0).toString();
			uname = rs.get(0).get(1).toString();
			ugender = gender[toInt(rs.get(0).get(6)) - 1];
			ugraduate = graduate[toInt(rs.get(0).get(7)) - 1];
			iMsg(uname + "님 환영합니다.");
			m.login(rs.get(0).get(9));
			dispose();

		}), "East");

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
		setVisible(true);
	}
}
