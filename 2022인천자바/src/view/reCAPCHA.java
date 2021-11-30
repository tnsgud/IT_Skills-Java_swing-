package view;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class reCAPCHA extends JDialog {

	LoginPage lp;
	HashMap<String, List<File>> themes = new HashMap<String, List<File>>();
	ArrayList<String> imgList = new ArrayList<>();

	String keyTheme;
	JLabel imgLabels[] = new JLabel[9];

	public reCAPCHA(LoginPage lp) {
		this.lp = lp;
		setData();
		ui();
		events();
	}

	void setData() {
		File f = new File("./지급자료/캡챠");
		for (var ipath : f.listFiles()) {
			themes.put(ipath.getName(), Arrays.asList(ipath.listFiles()));
		}

		var keyList = new ArrayList<>(themes.keySet());

		Collections.shuffle(keyList);

		keyTheme = keyList.get(0);

		while (imgList.size() < ThreadLocalRandom.current().nextInt(1, themes.get(keyTheme).size())) {
			var t = themes.get(keyTheme).get(new Random().nextInt(5));
			if (imgList.contains(t.getAbsolutePath()))
				continue;
			imgList.add(t.getAbsolutePath());
		}

		System.out.println(imgList);
		while (imgList.size() < 9) {
			var t = keyList.get(new Random().nextInt(keyList.size()));
			if (t.equals(keyTheme))
				continue;
			var path = themes.get(t).get(new Random().nextInt(themes.get(t).size()));
			if (imgList.contains(path.getAbsolutePath()))
				continue;
			imgList.add(path.getAbsolutePath());
		}

		Collections.shuffle(imgList);
	}

	void events() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				lp.chkp.trigger = false;
				lp.chkp.chk = true;
				lp.chkp.drawChk();
			}
		});
	}

	void ui() {
		setSize(250, 300);
		setModal(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setUndecorated(true);
		getRootPane().setBorder(new LineBorder(new Color(0, 123, 255)));

		var n = new JPanel(new BorderLayout());
		var c = new JPanel(new GridLayout(0, 3));

		for (int i = 0; i < imgLabels.length; i++) {
			c.add(imgLabels[i] = new JLabel(BasePage.getIcon(imgList.get(i), 50, 50)));
		}

		add(n, "North");
		add(c);
		add(BasePage.btn("확인", a -> dispose()), "South");
		n.add(BasePage.lbl("<HTML><Font color = 'WHITE'> " + keyTheme + " <br>가 있는 이미지를 모두 선택하세요.", JLabel.CENTER,
				15));
		n.setBackground(new Color(0, 123, 255));
	}
}
