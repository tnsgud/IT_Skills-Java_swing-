package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import jdk.management.jfr.SettingDescriptorInfo;

public class RoomEscape extends BaseFrame {
	JButton btn;
	ArrayList<String> files = new ArrayList<String>();
	ArrayList<JLabel> img = new ArrayList<>();
	int idx = 0;
	Timer timer;

	public RoomEscape() {
		super("방탈출", 400, 400);

		ui();
		event();
		timer = new Timer(100, a -> {
			img.forEach(l -> l.setBorder(new LineBorder(Color.black, 2)));
			img.get(idx).setBorder(new LineBorder(Color.red, 2));

			idx = (idx == 4 ? 0 : idx + 1);

			repaint();
			revalidate();
		});
		timer.start();

		setVisible(true);
	}

	private void event() {
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (btn.getText().equals("선택")) {
					dispose();
				} else {
					var yn = JOptionPane.showConfirmDialog(null, "게임 도중 나갈 시 게임은 다시 진행할 수 없습니다.\n나가시겠습니까?", "경고",
							JOptionPane.YES_NO_OPTION);
					if (yn == JOptionPane.YES_OPTION) {
						execute("update reservation set r_attend=1 where r_no=?", rno);
						setDefaultCloseOperation(2);
						dispose();
					} else {
						setDefaultCloseOperation(0);
					}
				}
			}
		});
	}

	private void ui() {

		setLayout(new BorderLayout(10, 10));

		add(c = new JPanel(new GridLayout(3, 3, 10, 10)));
		add(btn = btn("선택", a -> {
			if (a.getActionCommand() == "선택") {
				timer.stop();
				btn.setText("게임시작");
			} else {
				setVisible(false);
				qno = toInt(img.get(idx).getName());
				new Quiz().addWindowListener(getWindowListeners()[1]);
			}
		}), "South");

		var max = new File("./Datafiles/퀴즈/").listFiles().length;

		while (files.size() != 9) {
			var idx = new Random().nextInt(max) + 1 + "";
			if (!files.contains(idx)) {
				files.add(idx);
				var l = img("퀴즈/" + files.get(files.size() - 1) + ".jpg", 150, 150);
				img.add(l);
				l.setName(max + "");
				c.add(img.get(img.size() - 1));
				img.get(img.size() - 1).setEnabled(false);
			}
		}

		Collections.shuffle(img);
		for (int j = 0; j < 5; j++) {
			img.get(j).setEnabled(true);
		}

		c.setBorder(new EmptyBorder(10, 10, 10, 10));
	}
}
