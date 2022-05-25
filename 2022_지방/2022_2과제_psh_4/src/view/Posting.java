package view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class Posting extends BaseFrame {
	JTextField txt[] = new JTextField[3];
	JRadioButton rbtn[] = new JRadioButton[2];
	JTextArea are = new JTextArea();

	public Posting() {
		super("등록", 350, 450);

		add(c = new JPanel(new FlowLayout(1)));
		add(s = new JPanel(new FlowLayout(2)), "South");

		var cap = "아이디,등록일,제목,내용,공개여부".split(",");
		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel(new FlowLayout(0));
			p.add(sz(lbl(cap[i], 2), 80, 20));
			if (i == 4) {
				var b = new ButtonGroup();
				var t = "비공개,공개".split(",");
				for (int j = 0; j < t.length; j++) {
					p.add(rbtn[j] = new JRadioButton(t[j]));
					b.add(rbtn[j]);
				}
			} else if (i == 3) {
				p.add(sz(are, 200, 200));
				are.setBorder(new LineBorder(Color.black));
			} else {
				p.add(sz(txt[i] = new JTextField(), 200, 20));
				if (i == 2) {
					txt[i].setBorder(new LineBorder(Color.black));
				}
			}
			c.add(sz(p, 300, i == 3 ? 220 : 30));
		}

		txt[0].setText(user.get(1) + "");
		txt[1].setText(LocalDate.now() + "");
		

		for (var c : "등록,취소".split(",")) {
			s.add(btn(c, a -> {
				if (a.getActionCommand().equals("취소")) {
					dispose();
				} else {
					if (txt[2].getText().isEmpty() || are.getText().isEmpty()) {
						eMsg("빈칸이 존재합니다.");
						return;
					}

					iMsg("게시물 등록이 완료되었습니다.");
					execute("insert into notice values(0, ?, ?, ?, ?, ?, ?)", user.get(0), txt[1].getText(),
							txt[2].getText(), are.getText(), 0, rbtn[0].isSelected() ? 0 : 1);
				}
			}));
		}

		setVisible(true);
	}

	public static void main(String[] args) {
		user.add(1);
		user.add("room1");

		new Posting();
	}
}
