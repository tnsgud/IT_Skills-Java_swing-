package view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class Posting extends BaseFrame {
	JRadioButton rbtn[] = new JRadioButton[2];
	JTextField txt[] = new JTextField[3];
	JTextArea area = new JTextArea();

	public Posting(Notice notice) {
		super("등록", 300, 450);

		add(c = new JPanel(new FlowLayout(0)));
		add(s = new JPanel(new FlowLayout(2)), "South");

		var cap = "아이디,등록일,제목,내용,공개여부".split(",");
		for (int i = 0; i < cap.length; i++) {
			var p = new JPanel(new FlowLayout(0));
			p.add(sz(lbl(cap[i], 2, 12), 80, 20));
			if (i == 4) {
				var t = "비공개,공개".split(",");
				var b = new ButtonGroup();
				for (int j = 0; j < t.length; j++) {
					p.add(rbtn[j] = new JRadioButton(t[j]));
					b.add(rbtn[j]);
				}
				rbtn[1].setSelected(true);
			} else if (i == 3) {
				p.add(sz(area, 170, 200));
			} else {
				p.add(txt[i] = new JTextField(15));
				txt[i].setEnabled(i == 2);
			}
			c.add(p);
		}

		for (var c : "등록,취소".split(",")) {
			s.add(btn(c, a -> {
				if (a.getActionCommand().equals("등록")) {
					execute("insert notice valus(0, ?,?,?,?,?)", user.get(0), txt[1].getText(), txt[2].getText(),
							area.getText(), 0, rbtn[0].isSelected() ? 0 : 1);
					dispose();
				} else {
					dispose();
				}
			}));
		}

		txt[0].setText(user.get(1) + "");
		txt[1].setText(LocalDate.now() + "");

		txt[2].setBorder(new LineBorder(Color.black));
		area.setBorder(new LineBorder(Color.black));

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				notice.com.setSelectedIndex(0);
				notice.txt.setText("");
				notice.data();
			}
		});

		setVisible(true);
	}
}
