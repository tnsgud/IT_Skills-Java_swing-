package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Element;

import tool.Tool;
import view.LoginPage.ChkPanel;

public class Capcha extends BaseDialog {

	JPanel n, c, s;

	ArrayList<File> imgList;
	HashMap<String, HashSet<File>> keyMap;
	HashSet<File> selectSet = new HashSet<File>();
	HashSet<File> answerSet = new HashSet<File>();
	String mainKey;
	JComboBox<String> combo;

	public Capcha(ChkPanel chk) {
		super(350, 400);

		try {
			dataInit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setLayout(new BorderLayout(5, 5));

		add(n = new JPanel(new FlowLayout(JLabel.CENTER)), "North");
		add(c = new JPanel(new GridLayout(0, 3, 5, 5)));
		add(s = new JPanel(new GridLayout(1, 0, 5, 5)), "South");

		n.add(combo);
		n.add(lbl("<html><font color = 'white'>가 포함된 이미지를 고르시오", JLabel.CENTER, 13));

		s.add(btn("확인", a -> {
			if (selectSet.isEmpty()) {
				eMsg("선택을 하세요.");
				return;
			}

			if (!(selectSet.containsAll(answerSet) && answerSet.containsAll(selectSet))) {
				eMsg("틀렸습니다.");
				crtImgList();
				return;
			}

			dispose();
		}));

		s.add(btn("새로고침", a -> crtImgList()));
		combo.addItemListener(i -> {
			if (i.getStateChange() == ItemEvent.SELECTED) {
				crtImgList();
			}
		});

		((JPanel) getContentPane())
				.setBorder(new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(5, 5, 5, 5)));

		n.setBackground(new Color(0, 123, 255));

		addWindowListener(new WindowAdapter() {
			public void windowClosed(java.awt.event.WindowEvent e) {
				chk.isFocus = false;
				chk.isCheck = true;
				chk.repaint();
			};
		});

		crtImgList();
 	}

	void dataInit() throws Exception {
		keyMap = new HashMap<String, HashSet<File>>();

		for (var f : new File("./datafiles/캡챠").listFiles()) {
			var data = Files.readAllBytes(f.toPath());
			String txt = new String(data, "utf-8");
			int s = txt.indexOf("<x:xmpmeta");
			int e = txt.indexOf("</x:xmpmeta>");
			String xml = txt.substring(s, e + 12).toString();
			var is = new ByteArrayInputStream(xml.getBytes("utf-8"));
			var doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
			var nodeList = ((Element) doc.getDocumentElement().getElementsByTagName("dc:subject").item(0))
					.getElementsByTagName("rdf:li");

			for (int j = 0; j < nodeList.getLength(); j++) {
				var key = nodeList.item(j).getTextContent();
				if (keyMap.containsKey(key)) {
					keyMap.get(key).add(f);
				} else {
					keyMap.put(key, new HashSet<File>());
					keyMap.get(key).add(f);
				}
			}
		}

		combo = new JComboBox<String>(new ArrayList<String>(keyMap.keySet()).toArray(String[]::new));
	}

	void crtImgList() {
		c.removeAll();
		answerSet.clear();
		selectSet.clear();

		mainKey = combo.getSelectedItem().toString();
		imgList = new ArrayList<File>();

		var lst = keyMap.entrySet().stream().filter(a -> !a.getKey().equals(mainKey)).map(a -> a.getValue())
				.flatMap(a -> a.stream()).distinct().sorted().collect(Collectors.toList()); // distinct 없으면 중복 key들의 충돌이
																							// 생김

		var anslst = new ArrayList<File>(keyMap.get(mainKey));

		Collections.shuffle(anslst);

		answerSet.addAll(anslst.subList(0, Math.min(new Random().nextInt(5) + 1, anslst.size()))); // 이거 시발 왜이럼???

		for (var ans : answerSet)
			imgList.add(ans);

		Collections.shuffle(lst);

		lst.stream().filter(a -> !anslst.contains(a)).limit(9 - answerSet.size()).forEach(imgList::add);
		// 들어가는 애들중에 mainkey가 포함된 이미지가 있다!!!!
		for (var img : imgList) {
			var lbl = new JLabel(
					new ImageIcon(Toolkit.getDefaultToolkit().getImage(img.getPath()).getScaledInstance(150, 150, 4))) {
				File f = img;
			};

			lbl.addMouseListener(new MouseAdapter() {
				public void mousePressed(java.awt.event.MouseEvent e) {
					if (lbl.getBorder() == null) {
						lbl.setBorder(new LineBorder(Color.GREEN, 2));
						selectSet.add(lbl.f);
					} else {
						selectSet.remove(lbl.f);
						lbl.setBorder(null);
					}

				};
			});

			c.add(lbl);
		}

		revalidate();
		repaint();
	}
}