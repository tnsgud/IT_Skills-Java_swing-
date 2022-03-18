package view;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Sign extends BaseFrame {
	LocalDate now = LocalDate.now(), date = LocalDate.now();
	JTextField txt[] = new JTextField[4];
	JComboBox com[] = new JComboBox[3];

	public Sign() {
		super("회원가입", 400, 300);

		add(c = new JPanel(new GridLayout(0, 1)));
		add(s = new JPanel(), "South");

		var cap = "이름,아이디,비밀번호,비밀번호확인,생년월일".split(",");
		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel(new FlowLayout(0));
			p.add(sz(lbl(cap[i], 2), 80, 20));
			if (i == 4) {
				var c = "년,월,일".split(",");
				for (int j = 0; j < c.length; j++) {
					p.add(com[j] = new JComboBox<>());
					p.add(lbl(c[j], 2));
				}
			} else {
				p.add(txt[i] = new JTextField(18));
			}
			c.add(p);
		}
		s.add(btn("회원가입", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty()) {
					eMsg("빈칸이 있습니다.");
					return;
				}
			}

			String id = txt[1].getText(), pw = txt[2].getText();
			if (id.length() < 4 || id.length() > 8 || !rs("select * from user where u_id=?", id).isEmpty()) {
				eMsg("사용할 수 없는 아이디입니다.");
				return;
			}

			for (int i = 0; i < id.length() - 4; i++) {
				if (id.contains(pw.substring(i, i + 4))) {
					eMsg("비밀번호는 아이디와 4글자 이상 연속으로 겹칠 수 없습니다.");
					return;
				}
			}

			if (!pw.equals(txt[3].getText())) {
				eMsg("비밀번호가 일치하지 않습니다.");
				return;
			}

			iMsg(txt[0].getText() + "님 가입을 환영합니다.");
			execute("insert into user values(0, ?, ?, ?, ?)", id, pw, txt[0].getText(), date);
			dispose();
		}));

		for (int i = 1900; i <= 2022; i++) {
			com[0].addItem(i);
		}
		for (int i = 0; i < now.getMonthValue(); i++) {
			com[1].addItem(String.format("%02d", i + 1));
		}
		for (int i = 0; i < now.getDayOfMonth(); i++) {
			com[2].addItem(String.format("%02d", i + 1));
		}
		for (int i = 0; i < com.length; i++) {
			com[i].setSelectedIndex(com[i].getItemCount() - 1);
		}
		for (int i = 0; i < 2; i++) {
			com[i].addActionListener(a -> {
				if (a.getSource() == com[0]) {
					for (int j = 1; j < 3; j++) {
						com[j].removeAllItems();
					}

					date = LocalDate.of(toInt(com[0].getSelectedItem()), 1, 1);
					var t = date.getYear() == now.getYear();

					for (int k = 0; k < (t ? now.getMonthValue() : 12); k++) {
						com[1].addItem(String.format("%02d", k + 1));
					}
					for (int l = 0; l < (t ? now.getDayOfMonth() : date.lengthOfMonth()); l++) {
						com[2].addItem(String.format("%02d", l + 1));
					}
				} else {
					if (com[1].getItemCount() == 0)
						return;
					date = LocalDate.of(date.getYear(), toInt(com[1].getSelectedItem()), 1);
					var t = date.getYear() == now.getYear();

					for (int j = 0; j < (t ? now.getDayOfMonth() : date.lengthOfMonth()); j++) {
						com[2].addItem(String.format("%02d", j + 1));
					}
				}
			});
		}

		((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 40, 20, 0));

		setVisible(true);
	}

	public static void main(String[] args) {
		new Sign();
	}
}
