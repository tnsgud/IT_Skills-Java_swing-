package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class CommentDialog extends BaseDialog {
	JLabel stars[] = new JLabel[5];
	JTextField txt = hintField("감상평을 등록해주세요.", 0);
	JButton btn;
	int c_rate = 0;

	public CommentDialog(int m_no, MypagePage mypage) {
		super("Comment", 500, 200);

		add(lbl("별점을 선택해주세요.", 0, 25), "North");
		add(c = new JPanel());
		add(s = new JPanel(new BorderLayout()), "South");

		for (int i = 0; i < stars.length; i++) {
			c.add(stars[i] = lbl("★", 0, 50, a -> {
				var idx = toInt(((JLabel) a.getSource()).getName()) + 1;

				Stream.of(stars).forEach(x -> x.setText("☆"));

				for (int j = 0; j < idx; j++) {
					stars[j].setText("★");
				}

				c_rate = idx;
			}));
			stars[i].setForeground(Color.yellow);
			stars[i].setName(i + "");
		}

		s.add(txt);
		s.add(btn = btn("등록", a -> {
			if (btn.getBackground() == Color.LIGHT_GRAY)
				return;

			execute("insert comment values(0,?,?,?,?)", BaseFrame.user.get(0), m_no, txt.getText(), c_rate);
			mypage.data();
			dispose();
		}), "East");

		btn.setForeground(Color.white);
		btn.setBackground(Color.LIGHT_GRAY);

		txt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				btn.setBackground(txt.getText().isEmpty() ? Color.LIGHT_GRAY : red);
			}
		});
	}
}
