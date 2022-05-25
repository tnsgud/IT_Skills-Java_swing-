package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.Iterator;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Sign extends BaseFrame {
	JTextField txt[] = new JTextField[4];
	JComboBox com[] = new JComboBox[3];
	LocalDate now = LocalDate.now(), date = LocalDate.now();

	public Sign() {
		super("회원가입", 350, 300);

		ui();
		data();
		event();

		setVisible(true);
	}

	private void event() {
		for (int i = 0; i < 2; i++) {
			com[i].addActionListener(a -> {
				if (a.getSource() == com[0]) {
					date = LocalDate.of(toInt(com[0].getSelectedItem()), 1, 1);
					for (int j = 1; j < com.length; j++) {
						com[j].removeAllItems();
					}

					if (date.getYear() == now.getYear()) {
						for (int j = 0; j < now.getMonthValue(); j++) {
							com[1].addItem(String.format("%02d", j + 1));
						}
						for (int j = 0; j < now.getDayOfMonth(); j++) {
							com[2].addItem(String.format("%02d", j + 1));
						}
					} else {
						for (int j = 0; j < 12; j++) {
							com[1].addItem(String.format("%02d", j + 1));
						}
						for (int j = 0; j < date.lengthOfMonth(); j++) {
							com[2].addItem(String.format("%02d", j + 1));
						}
					}
				} else {
					if (com[1].getItemCount() == 0)
						return;
					date = LocalDate.of(toInt(com[0].getSelectedItem()), toInt(com[1].getSelectedItem()), 1);
					com[2].removeAllItems();
					for (int j = 0; j < date.lengthOfMonth(); j++) {
						com[2].addItem(String.format("%02d", j + 1));
					}
				}
			});
		}
	}

	private void data() {
		for (int i = 1900; i <= now.getYear(); i++) {
			com[0].addItem(i);
			com[0].setSelectedIndex(i - 1900);
		}
		for (int i = 0; i < now.getMonthValue(); i++) {
			com[1].addItem(String.format("%02d", i + 1));
			com[1].setSelectedIndex(i);
		}
		for (int i = 0; i < now.getDayOfMonth(); i++) {
			com[2].addItem(String.format("%02d", i + 1));
			com[2].setSelectedIndex(i);
		}
	}

	private void ui() {
		add(c = new JPanel(new GridLayout(0, 1)));
		add(s = new JPanel(), "South");

		var cap = "이름,아이디,비밀번호,비밀번호 확인,생년월일".split(",");
		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel(new FlowLayout(0, 0, 0));
			p.add(sz(lbl(cap[i], 2, 12), 100, 20), "West");
			if (i == 4) {
				var cap2 = "년,월,일".split(",");
				for (int j = 0; j < cap2.length; j++) {
					p.add(com[j] = new JComboBox<>());
					p.add(lbl(cap2[j], 0));
				}
			} else {
				p.add(txt[i] = new JTextField(15));
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

			if (txt[1].getText().length() < 4 || txt[1].getText().length() > 8
					|| !getResult("select c_name from user where u_pw=?", txt[1].getText()).isEmpty()) {
				eMsg("사용할 수 없느 아이디입니다.");
				return;
			}

			for (int i = 0; i < txt[1].getText().length() - 4; i++) {
				if (txt[1].getText().contains(txt[2].getText().substring(i, i + 4))) {
					eMsg("비밀번호는 아이디와 4글자 이상 연속으로 겹칠 수 없습니다.");
					return;
				}
			}

			if (!txt[2].getText().equals(txt[3].getText())) {
				eMsg("비밀번호가 일치하지 않습니다.");
				return;
			}

			iMsg(txt[0].getText() + "님 가입을 환영합니다.");
			execute("insert into user values(0.?,?,?,?)", txt[1].getText(), txt[2].getText(), txt[0].getText(), date);
			dispose();
		}));

		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 20, 10, 20));
	}
}
