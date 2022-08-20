package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

public class Capcha extends BaseDialog {
	JComboBox<String> com;

	HashMap<String, HashSet<File>> map = new HashMap<>();
	HashSet<File> answers = new HashSet<>();
	HashSet<File> selects = new HashSet<>();
	ArrayList<File> img = new ArrayList<>();
	LoginPage login = (LoginPage) BasePage.mf.getContentPane().getComponent(0);

	public Capcha() {
		super(400, 400);

		try {
			data();
		} catch (IOException | SAXException | ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setLayout(new BorderLayout(5, 5));

		add(n = new JPanel(new FlowLayout(0)), "North");
		add(c = new JPanel(new GridLayout(0, 3, 5, 5)));
		add(s = new JPanel(new GridLayout(1, 0, 5, 5)), "South");

		n.add(com);
		n.add(lbl("<html><font color='white'>가 포함된 이미지를 고르시오", 2));

		shuffle();

		for (var cap : "확인,새로고침".split(",")) {
			s.add(btn(cap, a -> {
				if (cap.equals("새로고침")) {
					shuffle();
					return;
				}

				if (!(selects.containsAll(answers) && answers.containsAll(selects))) {
					eMsg("틀렸습니다.");
					shuffle();
					return;
				}

				login.flag = true;
				dispose();
			}));
		}

		com.addActionListener(a -> shuffle());

		n.setOpaque(true);
		n.setBackground(new Color(0, 123, 255));

		((JPanel) getContentPane())
				.setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(5, 5, 5, 5)));
	}

	private void shuffle() {
		var key = com.getSelectedItem().toString();

		answers.clear();
		selects.clear();
		img.clear();

		var other = map.entrySet().stream().filter(e -> !e.getKey().equals(key)).map(e -> e.getValue())
				.flatMap(e -> e.stream()).distinct().sorted().collect(Collectors.toList());
		var answerList = new ArrayList<>(map.get(key));

		answers.addAll(answerList.subList(0, Math.min(new Random().nextInt(5) + 1, answerList.size())));
		img.addAll(answers);

		other.stream().limit(9 - answers.size()).forEach(img::add);

		Collections.shuffle(img);

		c.removeAll();
		for (var i : img) {
			var lbl = new JLabel(getIcon(i.getPath(), 150, 150));
			lbl.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (e.getButton() != 1)
						return;

					if (lbl.getBorder() == null) {
						lbl.setBorder(new LineBorder(Color.green));
						selects.add(i);
					} else {
						lbl.setBorder(null);
						selects.remove(i);
					}
				}
			});
			c.add(lbl);
		}
		c.repaint();
		c.revalidate();
	}

	private void data() throws IOException, SAXException, ParserConfigurationException {
		for (var file : new File("./datafiles/리캡차").listFiles()) {
			var data = Files.readAllBytes(file.toPath());
			var txt = new String(data, "utf-8");
			var s = txt.indexOf("<x:xmpmeta");
			var e = txt.indexOf("</x:xmpmeta>");
			var doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(new ByteArrayInputStream(txt.substring(s, e + 12).getBytes("utf-8")));
			var nodeList = ((Element) doc.getElementsByTagName("dc:subject").item(0)).getElementsByTagName("rdf:li");

			for (int i = 0; i < nodeList.getLength(); i++) {
				var content = nodeList.item(i).getTextContent();

				if (!map.containsKey(content)) {
					map.put(content, new HashSet<>());
				}

				map.get(content).add(file);
			}
		}

		com = new JComboBox<>(map.keySet().toArray(String[]::new));
	}

	public static void main(String[] args) {
		BasePage.mf = new MainFrame();
		BasePage.mf.swap(new LoginPage());
		BasePage.mf.setVisible(true);
	}
}
