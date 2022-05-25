package view;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class NoticeSign extends BaseFrame {
	JTextField txt[] = new JTextField[4];
	JRadioButton rbtn[] = new JRadioButton[2];

	public NoticeSign() {
		super("등록", 300, 350);

		add(c = new JPanel(new FlowLayout()));
		add(s = new JPanel(new FlowLayout(2)), "South");

		var cap = "아이디,등록일,제목,내용,공개여부".split(",");
		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel();
			p.add(sz(lbl(cap[i], 2), 80, 20));
			if (i == 4) {
				var tmp = new JPanel();
				var bg = new ButtonGroup();
				var rcap = "비공개,공개".split(",");
				for (int j = 0; j < rcap.length; j++) {
					tmp.add(rbtn[j] = new JRadioButton(rcap[j]));
					bg.add(rbtn[j]);
				}
				rbtn[1].setSelected(true);
				p.add(sz(tmp, 150, 30));
			} else {
				p.add(sz(txt[i] = new JTextField(14), 120, i == 3 ? 100 : 20));
				txt[i].setEnabled(i == 2 || i == 3);
			}

			c.add(p);
		}

		txt[0].setText(rs("select u_id from user where u_no=?", uno).get(0).get(0) + "");
		txt[1].setText(LocalDate.now() + "");

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
					execute("insert into notice values(0, ?, ?, ?, ? ,? ,?)", uno, txt[1].getText(), txt[2].getText(),
							txt[3].getText(), 0, rbtn[0].isSelected() ? 0 : 1);
					dispose();
				}
			}));
		}

		setVisible(true);
	}

	public static void main(String[] args) {
		new NoticeSign();
	}
}
