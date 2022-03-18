package view;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.io.File;
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

import view.BaseFrame.Before;

public class RoomEscape extends BaseFrame {
	HashMap<Integer, JLabel> imgs = new HashMap<>();
	ArrayList<Integer> keys = new ArrayList<Integer>();
	Timer timer;
	int idx = 0;
	JButton btn;

	public RoomEscape() {
		super("방탈출", 500, 500);

		add(c = new JPanel(new GridLayout(0, 3, 5, 5)));
		add(btn = btn("선택", a -> {
			if (a.getActionCommand().equals("선택")) {
				timer.stop();
				btn.setText("게임시작");
			} else {
				qno = toInt(imgs.get(keys.get(idx)).getName());
				new Quiz().addWindowListener(new Before(this));
			}
		}), "South");

		while (imgs.size() < 9) {
			var n = new Random().nextInt(new File("./Datafiles/퀴즈/").listFiles().length) + 1;
			if (!imgs.containsKey(n)) {
				var l = new JLabel(img("퀴즈/" + n + ".jpg", 200, 200));
				l.setName(n + "");
				l.setEnabled(false);
				c.add(l);
				imgs.put(n, l);
			}
		}

		keys = new ArrayList<>(imgs.keySet());
		Collections.shuffle(keys);

		for (int i = 0; i < keys.size(); i++) {
			var l = imgs.get(keys.get(i));
			l.setEnabled(i < 5);
		}

		timer = new Timer(1, a -> {
			imgs.forEach((k, v) -> v.setBorder(new LineBorder(Color.black)));
			imgs.get(keys.get(idx++)).setBorder(new LineBorder(Color.red));
			idx = idx == 5 ? 0 : idx;

			repaint();
			revalidate();
		});

		this.addWindowListener(new WindowAdapter() {
			public void windowClosed(java.awt.event.WindowEvent e) {
				if (btn.getText().equals("선택")) {
					dispose();
				} else {
					var y = JOptionPane.showConfirmDialog(null, "게임 도중 나갈 시 게임은 다시 진행할 수 업습니다.\n나가시겠습니까?", "경고",
							JOptionPane.YES_NO_OPTION);
					if (y == JOptionPane.YES_OPTION) {
						execute("update reservation set r_attend=1 where r_no=?", rno);
						dispose();
						setDefaultCloseOperation(2);
					} else {
						setDefaultCloseOperation(0);
					}
				}
			};
		});
		
		timer.start();

		setVisible(true);
	}

	public static void main(String[] args) {
		new RoomEscape();
	}
}
