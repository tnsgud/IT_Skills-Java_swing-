package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class LoginFrame extends BaseFrame {
	JTextField txt[] = new JTextField[2];

	public LoginFrame() {
		super("Login", 450, 350);

		add(n = new JPanel(new BorderLayout(10, 10)), "North");
		add(c = new JPanel(new GridLayout(0, 1, 20, 20)));
		add(s = new JPanel(new BorderLayout()), "South");

		n.add(lblB("로그인", 2, 30), "North");
		n.add(lbl("더 많은 서비스를 이용하기 위해 로그인하세요!", 2, 15));

		var hint = "User ID,User Password".split(",");
		for (int i = 0; i < hint.length; i++) {
			c.add(txt[i] = i == 0 ? hintField(hint[i], 0) : hintPassField(hint[i], 0));
		}

		s.add(btnRound("로그인", a -> {
		}), "North");
		s.add(sc = new JPanel());

		setVisible(true);
	}
}
