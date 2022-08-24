package view;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.DefaultButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class LoginPage extends BasePage {
	JTextField txt[] = new JTextField[2];
	JCheckBox chk = new JCheckBox("로봇이 아닙니다.");
	boolean flag = false;

	public LoginPage() {
		add(c = new JPanel(new GridBagLayout()));
		c.add(cc = sz(new JPanel(new GridLayout(0, 1)), 200, 300));

		cc.add(lbl("COVID-19", 0, 30));

		var cap = "ID,PW".split(",");
		for (int i = 0; i < cap.length; i++) {
			cc.add(lbl(cap[i], 2, 20));
			cc.add(txt[i] = i == 0 ? new JTextField() : new JPasswordField());
		}

		cc.add(chk);
		cc.add(event(lblHyp("처음이십니까?", 2, 20), a -> mf.swap(new SignPage())));
		cc.add(btn("로그인 ", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty()) {
					eMsg("빈칸이 있습니다.");
					return;
				}
			}

			if (!flag) {
				eMsg("리캡차를 확인해주세요.");
				return;
			}

			if (txt[0].getText().equals("admin") && txt[1].getText().equals("1234")) {
				mf.swap(new AdminPage());
				return;
			}

			var rs = getRows("select * from user where id = ? and pw= ?", txt[0].getText(), txt[1].getText());
			if (rs.isEmpty()) {
				eMsg("아이디 또는 비밀번호가 잘못되었습니다.");
				return;
			}

			user = rs.get(0);
			mf.swap(new UserMainPage());
		}));

		chk.setModel(new DefaultButtonModel() {
			@Override
			public boolean isSelected() {
				return flag;
			}
		});
		chk.addActionListener(a -> {
			if (flag)
				return;

			new CapCha();
		});

		cc.setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(5, 5, 5, 5)));
	}

	public static void main(String[] args) {
		mf = new MainFrame();
		mf.swap(new LoginPage());
		mf.setVisible(true);
	}
}
