package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

public class RoomEscape extends BaseFrame {
	HashMap<Integer, JLabel> imgs = new HashMap<>();
	ArrayList<Integer> keys;
	JButton btn;
	Timer timer;
	int idx = 0;

	public RoomEscape() {
		super("방탈출", 400, 400);

		setLayout(new BorderLayout(5, 5));

		add(c = new JPanel(new GridLayout(0, 3, 10, 10)));
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
				new Quiz();
				setVisible(false);
			}
		}), "South");

		while (imgs.size() < 9) {
			var n = new Random().nextInt(30) + 1;
			if (!imgs.containsKey(n)) {
				var l = new JLabel(img("퀴즈/" + n + ".jpg", 100, 100));
				l.setEnabled(false);
				l.setBorder(new LineBorder(Color.black, 5));
				c.add(l);
				imgs.put(n, l);

			}
		}

		keys = new ArrayList<>(imgs.keySet());
		Collections.shuffle(keys);
		for (int i = 0; i < 5; i++) {
			imgs.get(keys.get(i)).setEnabled(true);
		}

		timer = new Timer(100, a -> {
			keys.forEach(k -> imgs.get(k).setBorder(new LineBorder(Color.black, 5)));
			imgs.get(keys.get(idx)).setBorder(new LineBorder(Color.red, 5));
			idx = idx == 4 ? 0 : idx + 1;
		});

		timer.start();

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (!btn.getText().equals("선택")) {
					var y = JOptionPane.showConfirmDialog(null, "게임 도중 나갈 시 게임은 다시 진행할 수 없습니다.\n나가시겠습니까?", "경고",
							JOptionPane.YES_NO_OPTION);
					if (y == JOptionPane.YES_OPTION) {
						execute("update reservation set r_attend=? where r_no=?", 1, rno);
						setDefaultCloseOperation(2);
					} else {
						setDefaultCloseOperation(0);
					}
				}
			}
		});

		setVisible(true);
	}
}
