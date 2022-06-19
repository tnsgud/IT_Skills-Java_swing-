package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
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

import javax.swing.JCheckBox;
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

public class Capcha extends BaseDialog {
	JComboBox<String> com;
	JCheckBox chk;

	HashMap<String, HashSet<File>> keys = new HashMap<>();
	HashSet<File> selects = new HashSet<>();
	HashSet<File> answers = new HashSet<>();
	ArrayList<File> imgs = new ArrayList<>();
	String key;

	public Capcha(JCheckBox chk) {
		super(400, 400);
		this.chk = chk;

		try {
			data();
		} catch (IOException | SAXException | ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		setLayout(new BorderLayout(5, 5));
		((JPanel) getContentPane())
				.setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(5, 5, 5, 5)));

		add(n = new JPanel(new FlowLayout(0, 5, 5)), "North");
		add(c = new JPanel(new GridLayout(0, 3, 5, 5)));
		add(s = new JPanel(new GridLayout(1, 0, 5, 5)), "South");

		n.setBackground(BasePage.blue);
		n.setBorder(new EmptyBorder(5, 5, 5, 5));

		n.add(com);
		n.add(lbl("<html><font color='white'>가 포함된 이미지를 고르시오", 0));

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				chk.setSelected(true);
			}
		});

		shuffle();

		for (var cap : "확인,새로고침".split(",")) {
			s.add(btn(cap, a -> {
				if (cap.equals("확인")) {
					if (selects.isEmpty()) {
						eMsg("이미지를 선택하세요.");
						return;
					}

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
	}

	private void shuffle() {
		c.removeAll();

		key = com.getSelectedItem().toString();
		
		answers.clear();
		selects.clear();
		imgs.clear();

		var list = keys.entrySet().stream().filter(a -> !a.getKey().equals(key)).map(a -> a.getValue())
				.flatMap(a -> a.stream()).distinct().sorted().collect(Collectors.toList());
		var answerList = new ArrayList<>(keys.get(key));

		Collections.shuffle(answerList);

		answers.addAll(answerList.subList(0, Math.min(new Random().nextInt(5) + 1, answerList.size())));
		answers.forEach(imgs::add);

		list.stream().filter(a -> !answerList.contains(a)).limit(9 - answers.size()).forEach(imgs::add);

		Collections.shuffle(imgs);

		for (var img : imgs) {
			var lbl = new JLabel(getIcon(img.getPath(), 150, 150));
			lbl.addMouseListener(new MouseAdapter() {
				public void mousePressed(java.awt.event.MouseEvent e) {
					if (lbl.getBorder() == null) {
						lbl.setBorder(new LineBorder(Color.green));
						selects.add(img);
					} else {
						lbl.setBorder(null);
						selects.remove(img);
					}
				};
			});

			c.add(lbl);
		}

		repaint();
		revalidate();
	}

	private void data() throws IOException, SAXException, ParserConfigurationException {
		for (var file : new File("./datafiles/리캡차").listFiles()) {
			var data = Files.readAllBytes(file.toPath());
			var txt = new String(data, "utf-8");
			var s = txt.indexOf("<x:xmpmeta");
			var e = txt.indexOf("</x:xmpmeta>");
			var doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(new ByteArrayInputStream(txt.substring(s, e + 12).getBytes()));
			var nodeList = ((Element) doc.getDocumentElement().getElementsByTagName("dc:subject").item(0))
					.getElementsByTagName("rdf:li");

			for (int i = 0; i < nodeList.getLength(); i++) {
				var tag = nodeList.item(i).getTextContent();

				if (!keys.containsKey(tag)) {
					keys.put(tag, new HashSet<>());
				}

				keys.get(tag).add(file);
			}
		}

		com = new JComboBox<>(new ArrayList<>(keys.keySet()).toArray(String[]::new));
	}
}
