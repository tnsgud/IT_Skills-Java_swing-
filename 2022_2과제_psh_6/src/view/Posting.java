package view;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class Posting extends BaseFrame {
	JTextField txt[] = new JTextField[4];
	JRadioButton rbtn[] = new JRadioButton[2];

	public Posting() {
		super("게시물 등록", 320, 400);

		add(c = new JPanel(new FlowLayout(0)));
		add(s = new JPanel(new FlowLayout(4)), "South");

		var cap = "아이디,등록일,제목,내용,공개여부".split(",");
		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel(new FlowLayout(0));
			p.add(sz(lbl(cap[i], 2), 60, 20));
			if (i < 4) {
				p.add(sz(txt[i] = new JTextField(20), 200, i == 3 ? 150 : 20));
			} else {
				var t = "비공개,공개".split(",");
				var g = new ButtonGroup();
				for (int j = 0; j < t.length; j++) {
					p.add(rbtn[j] = new JRadioButton(t[j]));
					g.add(rbtn[j]);
				}
				rbtn[1].setSelected(true);
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
					execute("insert notice valus(0, ?.?,?,?,?,?)", user.get(0), LocalDate.now(), txt[2].getText(),
							txt[3].getText(), 0, rbtn[0].isSelected() ? 0 : 1);
					dispose();
				}
			}));
		}

		setVisible(true);
	}

	public static void main(String[] args) {
		new Posting();
	}
}
