package view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.time.LocalDate;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class Posting extends BaseFrame {
	JTextField txt[] = new JTextField[3];
	JTextArea area = new JTextArea();
	JRadioButton rbtn[] = new JRadioButton[2];

	public Posting() {
		super("게시물 작성", 280, 350);

		add(c = new JPanel(new FlowLayout(0)));
		add(s = new JPanel(new FlowLayout(2)), "South");

		var cap = "아이디,등록일,제목,내용,공개여부".split(",");
		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel(new FlowLayout(0));
			p.add(sz(lbl(cap[i], 2, 12), 60, 20));
			if (i == 4) {
				var t = "비공개,공개".split(",");
				var g = new ButtonGroup();
				for (int j = 0; j < t.length; j++) {
					p.add(rbtn[j] = new JRadioButton(t[j]));
					g.add(rbtn[j]);
				}
				rbtn[1].setSelected(true);
			} else if (i == 3) {
				p.add(sz(area, 170, 100));
			} else {
				p.add(txt[i] = new JTextField(15));
				txt[i].setEnabled(i == 2);
			}
			c.add(p);
		}

		txt[2].setBorder(new LineBorder(Color.black));
		area.setBorder(new LineBorder(Color.black));

		area.setLineWrap(true);

		for (var c : "등록,취소".split(",")) {
			s.add(btn(c, a -> {
				if (a.getActionCommand().equals("등록")) {
					if (txt[2].getText().isEmpty() || area.getText().isEmpty()) {
						eMsg("빈칸이 존재합니다.");
						return;
					}

					iMsg("게시물 등록이 완료되었습니다.");
					execute("insert notice values(0, ?, ?,?,?,?,?)", user.get(0), txt[1].getText(), txt[2].getText(),
							area.getText(), 0, rbtn[0].isSelected() ? 0 : 1);

					dispose();
				} else {
					dispose();
				}
			}));
		}

		txt[0].setText(user.get(1) + "");
		txt[1].setText(LocalDate.now() + "");

		setVisible(true);
	}

	public static void main(String[] args) {
		new Posting();
	}
}
