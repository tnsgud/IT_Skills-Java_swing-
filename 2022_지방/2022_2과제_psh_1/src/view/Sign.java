package view;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Sign extends BaseFrame {
	String[] cap = "이름,아이디,비밀번호,비밀번호 확인,생년월일".split(",");
	JTextField[] txt = new JTextField[cap.length - 1];
	JComboBox com[] = new JComboBox[3];
	LocalDate date = LocalDate.of(2022, 4, 5);

	public Sign() {
		super("회원가입", 350, 300);

		ui();
		data();
		event();

		setVisible(true);
	}

	private void event() {
		com[0].addActionListener(a -> {
			date = LocalDate.of((int) com[0].getSelectedItem(), 1, 1);

			Stream.of(com[1], com[2]).filter(c -> c.getItemCount() != 0).forEach(JComboBox::removeAllItems);

			if (date.getYear() == now.getYear()) {
				for (int i = 0; i < now.getMonthValue(); i++) {
					com[1].addItem(String.format("%02d", i + 1));
				}
				for (int i = 0; i < now.getDayOfMonth(); i++) {
					com[2].addItem(String.format("%02d", i + 1));
				}
			} else {
				for (int i = 0; i < 12; i++) {
					com[1].addItem(String.format("%02d", i + 1));
				}
				for (int i = 0; i < date.lengthOfMonth(); i++) {
					com[2].addItem(String.format("%02d", i + 1));
				}
			}
		});
		com[1].addActionListener(a -> {
			if (com[1].getItemCount() == 0)
				return;
			com[2].removeAllItems();
			date = LocalDate.of((int) com[0].getSelectedItem(), toInt(com[1].getSelectedItem()), 1);
			for (int i = 0; i < date.lengthOfMonth(); i++) {
				com[2].addItem(String.format("%02d", i + 1));
			}
		});
	}

	private void data() {
		for (int i = 0; i < com.length; i++) {
			com[i].removeAllItems();
		}

		for (int i = 1900; i < now.getYear() + 1; i++) {
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
	}

	private void ui() {
		add(c = new JPanel(new GridLayout(0, 1)));
		add(s = new JPanel(new FlowLayout()), "South");

		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel();
			p.add(sz(lblB(cap[i], 2, 12), 100, 20));
			if (i == cap.length - 1) {
				var tmp = "년,월,일".split(",");
				for (int j = 0; j < tmp.length; j++) {
					p.add(com[j] = new JComboBox<>());
					p.add(lbl(tmp[j], 2));
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

			if (txt[1].getText().length() < 4 || txt[1].getText().length() > 8
					|| !getOne("select * from user where u_id=?", txt[1].getText()).isEmpty()) {
				eMsg("사용할 수 없는 아이디입니다.");
				return;
			}
			
			if(isSame(txt[1].getText(), txt[2].getText())) {
				eMsg("비밀번호는 아이디와 4글자 이상 연속으로 겹칠 수 없습니다.");
				return;
			}
			
			if(!txt[2].getText().equals(txt[3].getText())) {
				eMsg("비밀번호가 일치하지 않습니다.");
				return;
			}
			
			iMsg(txt[0].getText()+"님 가입을 환영합니다.");
			execute("insert into user values(0, ?, ?, ?, ?)", txt[1].getText(), txt[2].getText(), txt[0].getText(), date);
			dispose();
		}));
	}
	
	boolean isSame(String id, String pw) {
		for (int i = 0; i < id.length()-4; i++) {
			if(id.contains(pw.substring(i, i+4))) {
				return true;
			}
		}
		
		return false;
	}
	
	public static void main(String[] args) {
		new Sign();
	}
}
