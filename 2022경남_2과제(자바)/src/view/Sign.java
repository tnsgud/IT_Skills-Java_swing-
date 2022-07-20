package view;

import java.awt.FlowLayout;

import javax.swing.JPanel;

public class Sign extends BaseFrame {
	public Sign() {
		super("회원가입", 500, 500);

		add(n = new JPanel(), "North");
		add(c = new JPanel());
		add(s = new JPanel(new FlowLayout(1, 50, 0)), "South");

		setVisible(true);
	}
}
