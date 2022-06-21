package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import view.LoginPage.ChkPanel;

public class Capcha extends BaseDialog {
	JComboBox<String> com;

	ArrayList<File> imgs = new ArrayList<>();
	HashMap<String, HashSet<File>> keyMap;
	HashSet<File> selects = new HashSet<>();
	HashSet<File> answers = new HashSet<>();

	String key;

	public Capcha(ChkPanel chk) {
		super(400, 400);

		try {
			data();
		} catch (IOException | SAXException | ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setLayout(new BorderLayout(5, 5));

		add(n = new JPanel(new FlowLayout(0)), "North");
		add(c = new JPanel(new GridLayout(0, 3, 10, 10)));
		add(s = new JPanel(new GridLayout(1, 0, 5, 5)), "South");

		n.add(com, "West");
		n.add(lbl("<html><font color='white'>가 포합된 이미지를 고르시오.", 2));

		for (var cap : "확인,새로고침".split(",")) {
			s.add(btn(cap, a -> {
				if (cap.equals("확인")) {
					if (!(selects.containsAll(answers) && answers.containsAll(selects))) {
						eMsg("틀렸습니다.");
						shuffle();
						return;
					}

					dispose();
				} else {
					shuffle();
				}
			}));
		}

		com.addItemListener(a -> {
			if (a.getStateChange() == 1) {
				shuffle();
			}
		});

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				chk.isFocus = false;
				chk.isCheck = true;

				chk.repaint();
			}
		});

		n.setBackground(Color.blue);
		((JPanel) getContentPane())
				.setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(5, 5, 5, 5)));

		shuffle();
	}

	private void shuffle() {
		c.removeAll();

		imgs.clear();
		answers.clear();
		selects.clear();

		key = com.getSelectedItem().toString();

		var list = keyMap.entrySet().stream().filter(a -> !a.getKey().equals(key)).map(a -> a.getValue())
				.flatMap(a -> a.stream()).distinct().sorted().collect(Collectors.toList());
		var answerList = new ArrayList<File>(keyMap.get(key));

		Collections.shuffle(answerList);

		answers.addAll(answerList.subList(0, Math.min(new Random().nextInt(5) + 1, answerList.size())));

		answers.forEach(a -> imgs.add(a));

		Collections.shuffle(list);

		list.stream().filter(a -> !answerList.contains(a)).limit(9 - answers.size()).forEach(imgs::add);

		for (var img : imgs) {
			var lbl = new JLabel(img(img.toString(), 150, 150));

			lbl.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(java.awt.event.MouseEvent e) {
					if (lbl.getBorder() == null) {
						lbl.setBorder(new LineBorder(Color.green));
						System.out.println(img.toPath());
						selects.add(img);
					} else {
						selects.remove(img);
						lbl.setBorder(null);
					}
				};
			});

			c.add(lbl);
		}

		repaint();
		revalidate();
	}

	private void data() throws IOException, SAXException, ParserConfigurationException {
		keyMap = new HashMap<>();

		for (var f : new File("./datafiles/캡챠").listFiles()) {
			var data = Files.readAllBytes(f.toPath());
			var txt = new String(data, "utf-8");
			var s = txt.indexOf("<x:xmpmeta");
			var e = txt.indexOf("</x:xmpmeta>");
			var is = new ByteArrayInputStream(txt.substring(s, e + 12).getBytes());
			var doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
			var nodeList = ((Element) doc.getDocumentElement().getElementsByTagName("dc:subject").item(0))
					.getElementsByTagName("rdf:li");

			for (int i = 0; i < nodeList.getLength(); i++) {
				var node = nodeList.item(i).getTextContent();

				if (!keyMap.containsKey(node)) {
					keyMap.put(node, new HashSet<>());
				}

				keyMap.get(node).add(f);
			}
		}
		
		System.out.println(keyMap.keySet());

		com = new JComboBox<String>(keyMap.keySet().toArray(String[]::new));
	}

	public static void main(String[] args) {
		BasePage.mf.add(new LoginPage());
		BasePage.mf.setVisible(true);
	}
}
