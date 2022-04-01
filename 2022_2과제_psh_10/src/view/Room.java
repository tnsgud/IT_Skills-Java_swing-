package view;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class Room extends BaseFrame {
	HashMap<Integer, JLabel> imgs = new HashMap<>();
	ArrayList<Integer> keys;

	public Room() {
		super("방탈출", 800, 400);

		add(c = new JPanel(new GridLayout(0, 3, 5, 5)));
		add(btn("선택", a -> {
		}), "South");

		while (imgs.size() < 9) {
			var n = new Random(30).nextInt() + 1;
			if (!imgs.containsKey(n)) {
				var l = new JLabel(img("퀴즈/" + n + ".jpg", 100, 100));
				l.setBorder(new LineBorder(Color.black));
				l.setEnabled(false);
				imgs.put(n, l);
			}
		}

		setVisible(true);
	}
}
