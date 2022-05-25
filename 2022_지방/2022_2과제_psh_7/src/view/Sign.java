package view;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Sign extends BaseFrame {
	JTextField txt[] = new JTextField[4];
	JComboBox com[] = new JComboBox[3];
	LocalDate date = LocalDate.now();

	public Sign() {
		super("회원가입", 350, 300);

		setLayout(new GridLayout(0, 1));

		var cap = "이름,아이디,비밀번호,비밀번호 확인,생년월일".split(",");
		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel(new FlowLayout(1));
			p.add(sz(lbl(cap[i], 2, 12), 100, 20));
			if (i == 4) {
				var t = "년,월,일".split(",");
				for (int j = 0; j < t.length; j++) {
					p.add(com[j] = new JComboBox<>());
					p.add(lbl(t[j], 0));
				}
			} else {
				p.add(txt[i] = new JTextField(15));
			}
			add(p);
		}

		for (int i = 1900; i < 2023; i++) {
			com[0].addItem(i);
		}

		com[0].setSelectedIndex(com[0].getItemCount() - 1);
		date = LocalDate.of(toInt(com[0].getSelectedItem()), 1, 1);
		setCom();
		com[1].setSelectedIndex(com[1].getItemCount() - 1);
		com[2].setSelectedIndex(com[2].getItemCount() - 1);

		for (int i = 0; i < 2; i++) {
			com[i].addActionListener(a -> {
				if (a.getSource() == com[0]) {
					com[1].removeAllItems();
					com[2].removeAllItems();
					date = LocalDate.of(toInt(com[0].getSelectedItem()), 1, 1);
				} else {
					if (com[2].getItemCount() == 0 || com[1].getItemCount() == 0) {
						return;
					}
					com[2].removeAllItems();
					date = LocalDate.of(toInt(com[0].getSelectedItem()), toInt(com[1].getSelectedItem()), 1);
				}

				setCom();
			});
		}

		add(s = new JPanel());
		s.add(btn("회원가입", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty()) {
					eMsg("빈칸이 존재합니다.");
					return;
				}
			}

			String id = txt[1].getText(), pw = txt[2].getText();

			if (rs("select * from user where u_id=?", id).isEmpty() || id.length() < 4 || id.length() > 8) {
				eMsg("사용할 수 없는 아이디입니다.");
				return;
			}

			for (int i = 0; i < id.length() - 4; i++) {
				if (id.contains(pw.substring(i, i + 4))) {
					eMsg("비밀번호를 아이디와 4글자 이상 연속으로 겹쳐질 수 없습니다.");
					return;
				}
			}

			if (pw.equals(txt[3].getText())) {
				eMsg("비밀번호가 일치하지 않습니다.");
				return;
			}

			iMsg(txt[0].getText() + "님 가입을 환영합니다.");
			execute("insert user values(0, ?, ?, ?, ?)", id, pw, txt[0].getText(), date);
			dispose();
		}));

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
		setVisible(true);
	}

	private void setCom() {
		var t = date.getYear() == LocalDate.now().getYear();
		for (int i = 0; i < (t ? LocalDate.now().getMonthValue() : 12); i++) {
			com[1].addItem(String.format("%02d", i + 1));
		}
		for (int i = 0; i < (t ? LocalDate.now().getDayOfMonth() : date.lengthOfMonth()); i++) {
			com[2].addItem(String.format("%02d", i + 1));
		}
	}

	public static void main(String[] args) {
		new Sign();
	}
}
