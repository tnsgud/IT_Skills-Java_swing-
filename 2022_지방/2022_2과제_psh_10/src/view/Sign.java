package view;

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
	LocalDate date = LocalDate.now(), now = LocalDate.now();

	public Sign() {
		super("호원가입", 400, 300);

		add(c = new JPanel(new GridLayout(0, 1)));
		add(s = new JPanel(), "South");

		var cap = "이름,아이디,비밀번호,비밀번호 확인,생년월일".split(",");
		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel();
			p.add(sz(lbl(cap[i], 2, 12), 80, 20));
			if (i == 4) {
				var t = "년,월,일".split(",");
				for (int j = 0; j < t.length; j++) {
					p.add(com[j] = new JComboBox());
					p.add(lbl(t[j], 0, 12));
				}
			} else {
				p.add(txt[i] = new JTextField(18));
			}

			c.add(p);
		}

		for (int i = 1900; i <= LocalDate.now().getYear(); i++) {
			com[0].addItem(i);
		}

		setCom();

		for (int i = 0; i < com.length; i++) {
			com[i].setSelectedIndex(com[i].getItemCount() - 1);
		}

		com[0].addActionListener(a -> {
			com[1].removeAllItems();
			com[2].removeAllItems();

			date = LocalDate.of(toInt(com[0].getSelectedItem()), 1, 1);

			setCom();
		});

		com[1].addActionListener(a -> {
			if (com[1].getItemCount() == 0 || com[2].getItemCount() == 0) {
				return;
			}

			date = LocalDate.of(date.getYear(), toInt(com[1].getSelectedItem()), 1);

			com[2].removeAllItems();

			var t1 = date.getYear() == now.getYear();
			var t2 = date.getMonthValue() == now.getMonthValue();

			for (int i = 0; i < (t1 && t2 ? date.getDayOfMonth() : date.lengthOfMonth()); i++) {
				com[2].addItem(String.format("%02d", i + 1));
			}
		});

		s.add(btn("회원가입", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty()) {
					eMsg("빈칸이 있습니다.");
					return;
				}
			}

			String id = txt[1].getText(), pw = txt[2].getText();
			if (id.length() < 4 || id.length() > 8 || !rs("select * from user where u_id=?", id).isEmpty()) {
				eMsg("사용할 수 없는 아이디 입니다.");
				return;
			}

			for (int i = 0; i < id.length() - 4; i++) {
				if (pw.contains(id.substring(i, i + 4))) {
					eMsg("비밀번호는 아이디와 4글자 이상 연속을 겹쳐질 수 없습니다.");
					return;
				}
			}

			if (!pw.equals(txt[3].getText())) {
				eMsg("비밀번호가 일치하지 않습니다.");
				return;
			}

			iMsg(txt[0].getText() + "님 가입을 환영합니다.");

			execute("insert user values(0, ?, ?, ? ,?)", id, pw, txt[0].getText(), date);
			dispose();
		}));

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		setVisible(true);
	}

	private void setCom() {
		var t1 = date.getYear() == now.getYear();
		var t2 = date.getMonthValue() == now.getMonthValue();

		for (int i = 0; i < (t1 ? now.getMonthValue() : 12); i++) {
			com[1].addItem(String.format("%02d", i + 1));
		}

		for (int i = 0; i < (t1 && t2 ? date.getDayOfMonth() : date.lengthOfMonth()); i++) {
			com[2].addItem(String.format("%02d", i + 1));
		}
	}

	public static void main(String[] args) {
		new Sign();
	}
}
