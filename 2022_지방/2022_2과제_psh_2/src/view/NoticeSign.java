package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class NoticeSign extends BaseFrame {
	String[] cap = "아이디,등록일,제목,내용,공개여부".split(",");
	JRadioButton rbtn[] = new JRadioButton[2];
	JTextField txt[] = new JTextField[cap.length - 1];

	public NoticeSign() {
		super("등록", 300, 350);

		setLayout(new BorderLayout(5, 5));

		add(c = new JPanel(new FlowLayout(0)));
		add(s = new JPanel(new FlowLayout(2)), "South");
		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel(new FlowLayout(0));
			p.add(sz(lbl(cap[i], 2), 50, 20));
			if (i == 4) {
				var t = new JPanel();
				var g = new ButtonGroup();
				for (int j = 0; j < rbtn.length; j++) {
					t.add(rbtn[i] = new JRadioButton("비공개,공개".split(",")[j]));
					g.add(rbtn[i]);
				}
				rbtn[1].setSelected(true);
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

					iMsg("게시물 등록이 완료되엇습니다.");
					execute("insert into notice values (0. ?.?.?.?,?,?)", uno, LocalDate.now(), txt[2].getText(),
							txt[3].getText(), 0, rbtn[0].isSelected() ? 0 : 1);
				}
			}));
		}

		setVisible(true);
	}
}
