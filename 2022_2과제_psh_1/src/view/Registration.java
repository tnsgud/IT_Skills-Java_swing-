package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.Iterator;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class Registration extends BaseFrame {
	String[] cap = "아이디,등록일,제목,내용,공개여부".split(",");
	JRadioButton radio[] = new JRadioButton[2];
	JTextField txt[] = new JTextField[cap.length - 1];
	ButtonGroup group = new ButtonGroup();

	public Registration() {
		super("등록", 300, 350);

		ui();
		data();
		radio[1].setSelected(true);

		setVisible(true);
	}

	private void data() {
		txt[0].setText(getOne("select u_id from user where u_no=?", uno));
		txt[1].setText(LocalDate.now()+"");
	}

	private void ui() {
		setLayout(new BorderLayout(5, 5));

		add(c = new JPanel(new FlowLayout(0)));
		add(s = new JPanel(new FlowLayout(2)), "South");
		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel(new FlowLayout(0));
			p.add(sz(lbl(cap[i], 2), 50, 20));
			if (i == 4) {
				var t = new JPanel();
				for (int j = 0; j < radio.length; j++) {
					t.add(radio[j] = new JRadioButton(j == 0 ? "비공개" : "공개"));
				}
				p.add(t);
			} else {
				p.add(txt[i] = new JTextField(20));
				txt[i].setEnabled(i > 1 && i < 4);
			}

			if (i == 3) {
				sz(txt[i], 300, 100);
			}

			c.add(p);
		}

		for (var c : "등록,취소".split(",")) {
			s.add(btn(c, a -> {
				if (a.getActionCommand().equals("취소")) {
					dispose();
				} else {
					for (var t : txt) {
						if (t.getText().isEmpty()) {
							eMsg("빈칸이 존재합니다.");
							return;
						}
					}

					iMsg("게시물 등록이 완료되었습니다.");
					execute("insert into notice values (0, ?, ?, ?, ?, ?, ?)", uno, now, txt[2].getText(),
							txt[3].getText(), 0, radio[0].isSelected() ? 0 : 1);
				}
			}));
		}
	}

	public static void main(String[] args) {
		uno = 1;
		new Registration();
	}
}
