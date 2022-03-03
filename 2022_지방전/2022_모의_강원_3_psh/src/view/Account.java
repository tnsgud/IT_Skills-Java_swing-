package view;

import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class Account extends BaseDialog {
	String[] cap = "id,pwd,name,email,point".split(",");
	JTextField[] txt = new JTextField[cap.length];

	public Account() {
		super("계정", 300, 400);

		ui();

		setVisible(true);
	}

	private void ui() {
		setLayout(new GridLayout(0, 1, 10, 10));

		add(lbl("계정", 2, 25), "North");

		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel(new FlowLayout(0));

			add(p);
			p.add(sz(lbl(cap[i], 2), 50, 20));
			p.add(txt[i] = new JTextField(15));

			txt[i].setEnabled(i > 0 && i < 4);
			txt[i].setText(getOne("select " + cap[i] + " from user where no=?", BaseFrame.uno));
		}

		add(s = new JPanel(new GridLayout(1, 0, 10, 10)));

		for (var c : "수정,취소".split(",")) {
			s.add(btn(c, a -> {
				if (a.getActionCommand().equals("취소")) {
					dispose();
				} else {
					for (var t : txt) {
						if (t.getText().isEmpty()) {
							eMsg("공란을 확인해주세요.");
							return;
						}

						if (t == txt[1] && !t.getText().matches(".*[\\W].*")) {
							eMsg("특수문자를 포함해주세요.");
							return;
						}
					}

					execute("update user set pwd=?, email=?, pwe=? where uno=?", txt[1].getText(), txt[2].getText(),
							txt[3].getText(), BaseFrame.uno);
					dispose();
				}
			}));
		}
	}
}
