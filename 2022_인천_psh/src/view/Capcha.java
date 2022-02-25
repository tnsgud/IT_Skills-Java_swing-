package view;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.swing.JDialog;

import view.LoginPage.ChkBoxPanel;

public class Capcha extends JDialog {
	ChkBoxPanel chk;
	HashMap<String, List<File>> theme = new HashMap<String, List<File>>();
	ArrayList<String> imgList = new ArrayList<String>();
	String keyTheme;
	Random rnd = new Random();

	public Capcha(ChkBoxPanel chk) {
		this.chk = chk;
		setModal(true);
		
		data();
		ui();
		
		setVisible(true);
	}

	private void ui() {
		// TODO Auto-generated method stub
		
	}

	private void data() {
		for (var file : new File("./datafiles/캡챠").listFiles()) {
			theme.put(file.getName(), Arrays.asList(file.listFiles()));
		}
		var keys = new ArrayList<String>(theme.keySet());

		Collections.shuffle(keys);
		keyTheme = keys.get(0);
		keys.remove(keyTheme);

		do {
			var t = theme.get(keyTheme).get(rnd.nextInt(5));
			if (!imgList.contains(t.getAbsolutePath())) {
				imgList.add(t.getAbsolutePath());
			}
		} while (imgList.size() < rnd.nextInt(theme.get(keyTheme).size()));

		while (imgList.size() < 9) {
			var t = keys.get(rnd.nextInt(keys.size()));
			var p = theme.get(t).get(rnd.nextInt(theme.get(t).size()));
			if (!imgList.contains(p.getAbsolutePath())) {
				imgList.add(p.getAbsolutePath());
			}
		}
		
		Collections.shuffle(imgList);
	}
}
