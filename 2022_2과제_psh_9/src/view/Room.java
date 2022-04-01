package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Room extends BaseFrame {
	HashMap<Integer, JLabel> imgs = new HashMap<>();
	ArrayList<Integer> keys;
	JButton btn;
	Timer timer;
	int idx = -1;

	public Room() {
		super("방탈출", 300, 300);

		setLayout(new BorderLayout(5, 5));

		add(c = new JPanel(new GridLayout(0, 3, 5, 5)));
		add(btn = btn("선택", a -> {
			if (a.getActionCommand().equals("선택")) {
				timer.stop();
				btn.setText("게임시작");
			} else {
				imgs.forEach((k, v) -> {
					if (((LineBorder) v.getBorder()).getLineColor().equals(Color.red)) {
						qno = k;
					}
				});
				setVisible(false);
				new Quiz().addWindowListener(this.getWindowListeners()[1]);
			}
		}), "South");

		while (imgs.size() < 9) {
			var n = new Random().nextInt(30) + 1;
			if (!imgs.containsKey(n)) {
				var l = new JLabel(img("퀴즈/" + n + ".jpg", 100, 100));
				l.setEnabled(false);
				l.setBorder(new LineBorder(Color.black));
				imgs.put(n, l);
				c.add(l);
			}
		}

		keys = new ArrayList<>(imgs.keySet());

		Collections.shuffle(keys);

		for (int i = 0; i < 5; i++) {
			imgs.get(keys.get(i)).setEnabled(true);
		}

		c.setBorder(new EmptyBorder(5, 5, 5, 5));

		timer = new Timer(100, a -> {
			imgs.forEach((k, v) -> v.setBorder(new LineBorder(Color.black)));
			idx = idx == 4 ? 0 : idx + 1;
			imgs.get(keys.get(idx)).setBorder(new LineBorder(Color.red));
		});

		timer.start();

		addWindowListener(new WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				if (btn.getText().equals("게임시작")) {
					var n = JOptionPane.showConfirmDialog(null, "게임 도중 나갈 시 게임은 다시 진행할 수 없습니다.\n나가시겠습니까?", "경고",
							JOptionPane.YES_NO_OPTION);
					if (n == JOptionPane.YES_OPTION) {
						execute("update reservation set r_attend=1 where r_no=?", rno);
						setDefaultCloseOperation(2);
					} else {
						setDefaultCloseOperation(0);
					}
				}
			};
		});

		setVisible(true);
	}

	public static void main(String[] args) {
		new Room();
	}
}
