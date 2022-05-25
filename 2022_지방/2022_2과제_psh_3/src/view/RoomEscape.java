package view;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import javax.sound.sampled.Control;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Line.Info;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

public class RoomEscape extends BaseFrame {
	JButton btn;
	HashMap<Integer, JLabel> imgs = new HashMap<>();
	ArrayList<Integer> keys = new ArrayList<Integer>();
	Timer timer;
	int idx = 0;

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
			int n = new Random().nextInt(new File("./Datafiles/퀴즈/").listFiles().length) + 1;
			if (!imgs.containsKey(n)) {
				imgs.put(n, new JLabel(img("퀴즈/" + n + ".jpg", 200, 200)));
				imgs.get(n).setName(n + "");
				imgs.get(n).setEnabled(false);
				imgs.get(n).setBorder(new LineBorder(Color.black));
				c.add(imgs.get(n));
			}
		}

		keys = new ArrayList<>(imgs.keySet());
		Collections.shuffle(keys);

		for (int i = 0, idx = 0; i < keys.size(); i++, idx++) {
			var l = imgs.get(keys.get(i));
			l.setEnabled(idx < 5);
		}

		timer = new Timer(100, a -> {
			imgs.forEach((k, v) -> v.setBorder(new LineBorder(Color.black)));
			imgs.get(keys.get(idx++)).setBorder(new LineBorder(Color.red));
			idx = idx == 5 ? 0 : idx;

			repaint();
			revalidate();
		});

		setVisible(true);

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				if (btn.getText().equals("선택")) {
					dispose();
				} else {
					var y = JOptionPane.showConfirmDialog(null, "게임 도중 나갈 시 게임은 다시 진행할 수 없습니다.\n나가시겠습니까?", "경고",
							JOptionPane.YES_NO_OPTION);
					if (y == JOptionPane.YES_OPTION) {
						execute("update reservation set r_attend = 1 where r_no=?", rno);
						dispose();
						setDefaultCloseOperation(2);
					} else {
						setDefaultCloseOperation(0);
					}
				}

			};
		});

		timer.start();
	}

	public static void main(String[] args) {
		new RoomEscape();
	}
}
