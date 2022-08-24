package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.stream.Collectors;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Element;

public class CapCha extends BaseDialog {
	JComboBox<String> com;
	HashMap<String, HashSet<File>> map = new HashMap<>();
	HashSet<File> answers = new HashSet<>();
	HashSet<File> selects = new HashSet<>();
	ArrayList<File> list = new ArrayList<>();
	LoginPage loginPage;

	@Override
	public JLabel lbl(String c, int a, int st, int sz) {
		var l = super.lbl(c, a, st, sz);
		l.setForeground(Color.white);
		return l;
	}

	public CapCha() {
		super(400, 400);
		loginPage = (LoginPage) BasePage.mf.getContentPane().getComponent(0);
		setLayout(new BorderLayout(5, 5));

		try {
			data();
		} catch (Exception e) {
			e.printStackTrace();
		}

		add(n = new JPanel(new FlowLayout(0)), "North");
		add(c = new JPanel(new GridLayout(0, 3, 5, 5)));
		add(s = new JPanel(new GridLayout(1, 0, 5, 5)), "South");

		n.add(com);
		n.add(lbl("가 포함된 이미지를 고르시오", 2));

		shuffle();

		for (var cap : "확인,새로고침".split(",")) {
			s.add(btn(cap, a -> {
				if (cap.equals("새로고침")) {
					shuffle();
					return;
				}

				if (selects.size() == 0) {
					eMsg("이미지를 선택하세요.");
					return;
				}

				if (!(selects.containsAll(answers) && answers.containsAll(selects))) {
					eMsg("틀렸습니다.");
					shuffle();
					return;
				}

				loginPage.flag = true;
				dispose();
			}));
		}

		com.addActionListener(a -> shuffle());

		((JPanel) getContentPane())
				.setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(5, 5, 5, 5)));
		n.setBackground(new Color(0, 123, 255));

		setVisible(true);
	}

	private void shuffle() {
		var key = com.getSelectedItem().toString();
		
		answers.clear();
		selects.clear();
		list.clear();

		var allAnswers = new ArrayList<>(map.get(key));
		var other = map.entrySet().stream().map(a -> a.getValue()).flatMap(a -> a.stream()).distinct()
				.filter(a -> !allAnswers.contains(a)).collect(Collectors.toList());

		Collections.shuffle(other);
		Collections.shuffle(allAnswers);

		answers.addAll(allAnswers.subList(0, Math.min(new Random().nextInt(5) + 1, allAnswers.size())));
		list.addAll(answers);

		other.stream().limit(9 - list.size()).forEach(list::add);
		
		Collections.shuffle(list);

		c.removeAll();
		
		for (var f : list) {
			c.add(event(new JLabel(getIcon(f.getAbsolutePath(), 150, 150)), e -> {
				var me = (JLabel) e.getSource();

				me.setBorder(me.getBorder() == null ? new LineBorder(Color.green) : null);

				if (selects.contains(f)) {
					selects.remove(f);
				} else {
					selects.add(f);
				}
			}));
		}
		c.repaint();
		c.revalidate();
	}

	private void data() throws Exception {
		for (var file : new File("./datafiles/리캡차").listFiles()) {
			var txt = new String(Files.readAllBytes(file.toPath()), "utf-8");
			var s = txt.indexOf("<x:xmpmeta");
			var e = txt.indexOf("</x:xmpmeta>");
			var doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(new ByteArrayInputStream(txt.substring(s, e + 12).getBytes("utf-8")));
			var nodeList = ((Element) doc.getElementsByTagName("dc:subject").item(0)).getElementsByTagName("rdf:li");

			for (int i = 0; i < nodeList.getLength(); i++) {
				var tag = nodeList.item(i).getTextContent();

				if (!map.containsKey(tag)) {
					map.put(tag, new HashSet<>());
				}

				map.get(tag).add(file);
			}
		}

		com = new JComboBox<>(map.keySet().toArray(String[]::new));
	}
}
