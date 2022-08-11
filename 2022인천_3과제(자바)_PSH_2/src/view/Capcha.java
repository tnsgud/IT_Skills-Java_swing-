package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.io.ByteArrayInputStream;
import java.io.File;
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

import org.w3c.dom.Element;

public class Capcha extends BaseDialog {
	HashMap<String, HashSet<File>> map = new HashMap<>();
	HashSet<File> answer = new HashSet<>();
	HashSet<File> select = new HashSet<>();
	ArrayList<File> img = new ArrayList<>();
	JComboBox<String> com;
	String key = "";
	LoginPage loginPage = (LoginPage) BasePage.mf.getContentPane().getComponent(0);

	public Capcha() {
		super(400, 400);
		
		try {
			data();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setLayout(new BorderLayout());

		add(n = new JPanel(new FlowLayout(0)), "North");
		add(c = new JPanel(new GridLayout(0, 3, 5, 5)));
		add(s = new JPanel(new GridLayout(1, 0, 5, 5)), "South");

		n.add(com);
		n.add(lbl("<html><font color='white'>가 포함된 이미지를 고르시오", 2));

		shuffle();

		for (var cap : "확인,새로고침".split(",")) {
			s.add(btn(cap, a -> {
				if (cap.equals("확인")) {
					if (select.isEmpty()) {
						eMsg("이미즐ㄹ 선택하세요.");
						return;
					}

					if (!(select.containsAll(answer) && answer.containsAll(select))) {
						eMsg("틀렸습니다.");
						shuffle();
						return;
					}
					
					loginPage.flag = true;
					dispose();
				} else {
					shuffle();
				}
			}));
		}

		com.addActionListener(a -> shuffle());

		n.setBackground(blue);

		((JPanel) getContentPane())
				.setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(5, 5, 5, 5)));
	}

	private void shuffle() {
		c.removeAll();

		answer.clear();
		img.clear();
		select.clear();

		key = com.getSelectedItem().toString();

		var list = map.entrySet().stream().filter(a -> !a.getKey().equals(key)).map(a -> a.getValue())
				.flatMap(a -> a.stream()).distinct().sorted().collect(Collectors.toList());
		var answerList = new ArrayList<>(map.get(key));

		answer.addAll(answerList.subList(0, Math.min(new Random().nextInt(5) + 1, answerList.size())));
		img.addAll(answer);

		list.stream().filter(a -> !answerList.contains(a)).limit(9 - answer.size()).forEach(img::add);

		Collections.shuffle(img);

		for (var img : img) {
			var lbl = new JLabel(getIcon(img.getPath(), 150, 150));
			lbl.addMouseListener(new MouseAdapter() {
				public void mousePressed(java.awt.event.MouseEvent e) {
					if (e.getButton() != 1)
						return;

					if (lbl.getBorder() == null) {
						lbl.setBorder(new LineBorder(Color.green));
						select.add(img);
					} else {
						lbl.setBorder(null);
						select.remove(img);
					}
				}
			});

			c.add(lbl);
		}
		c.repaint();
		c.revalidate();
	}

	private void data() throws Exception {
		for (var file : new File("./datafiles/리캡차").listFiles()) {
			var data = Files.readAllBytes(file.toPath());
			var txt = new String(data, "utf-8");
			var s = txt.indexOf("<x:xmpmeta");
			var e = txt.indexOf("</x:xmpmeta>");
			var doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(new ByteArrayInputStream(txt.substring(s, e + 12).getBytes()));
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
