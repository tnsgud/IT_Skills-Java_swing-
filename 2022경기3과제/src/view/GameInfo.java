package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;

public class GameInfo extends BasePage {
	JLabel img, genreLbl;
	JTextField nameTxt, priceTxt, dcTxt;
	JComboBox ageCom, gdCom;
	ArrayList<ImageIcon> icons = new ArrayList<>();
	ArrayList<Item> items = new ArrayList<>();
	JTextArea area = new JTextArea();
	JFileChooser jfc = new JFileChooser();
	File f;

	public GameInfo() {
		super("게임정보");

		jfc.removeChoosableFileFilter(jfc.getAcceptAllFileFilter());
		jfc.addChoosableFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return "JPG Images";
			}

			@Override
			public boolean accept(File f) {
				return f.getName().toLowerCase().endsWith("jpg") || f.isDirectory();
			}
		});

		add(n = sz(new JPanel(new BorderLayout()), 0, 200), "North");
		add(c = new JPanel(new FlowLayout(0)));
		add(s = sz(new JPanel(new BorderLayout()), 0, 150), "South");

		n.add(img = sz(new JLabel(), 100, 0), "West");
		n.add(nc = new JPanel(new GridLayout(0, 1)));

		img.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() == 2) {
					if (jfc.showOpenDialog(img) == JFileChooser.APPROVE_OPTION) {
						f = jfc.getSelectedFile();
						img.setIcon(getIcon(f.getPath(), 100, 100));
					}
				}
			}
		});

		var cap = "게임명,장르,가격,연령".split(",");
		for (int i = 0; i < cap.length; i++) {
			var tmp = new JPanel(new FlowLayout(0));

			tmp.add(lbl(cap[i] + " : ", 2));

			if (i == 0) {
				tmp.add(nameTxt = new JTextField(15));
			} else if (i == 1) {
				var add = new JLabel(getIcon("./datafiles/기본사진/10.png", 50, 50));
				add.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						new GenreSelect(GameInfo.this).setVisible(true);
					}
				});
				tmp.add(genreLbl = lbl("", 0));
				tmp.add(add);
			} else if (i == 2) {
				tmp.add(priceTxt = new JTextField(15));
				tmp.add(lbl("할인율 : ", 2));
				tmp.add(dcTxt = new JTextField(15));
				tmp.add(lbl("%", 0));

				dcTxt.setText("0");
				dcTxt.setEnabled(false);

				priceTxt.getDocument().addDocumentListener(new DocumentListener() {
					void setDcTxt() {
						dcTxt.setEnabled(toInt(priceTxt.getText()) > -1);
					}

					@Override
					public void removeUpdate(DocumentEvent e) {
						setDcTxt();
					}

					@Override
					public void insertUpdate(DocumentEvent e) {
						setDcTxt();
					}

					@Override
					public void changedUpdate(DocumentEvent e) {
						setDcTxt();
					}
				});
			} else {
				var gd = lbl("일반↑", 0);
				for (int j = 0; j < 6; j++) {
					icons.add(getIcon("./datafiles/등급사진/" + j + ".jpg", 50, 50));
				}

				tmp.add(ageCom = new JComboBox(g_age));
				tmp.add(lbl("할인 대상 : ", 2));
				tmp.add(gdCom = new JComboBox(icons.toArray()));
				tmp.add(gd);

				gdCom.setRenderer(new Renderer());
				gdCom.addActionListener(a -> {
					gd.setText(g_gd[gdCom.getSelectedIndex()] + "↑");

					repaint();
				});
			}

			nc.add(tmp);
		}

		c.add(cc = sz(new JPanel(new FlowLayout(1, 5, 5)), 300, 120));
		c.add(btn("아이템 추가", a -> {
			if (items.stream().filter(i -> i.img.getIcon() == null).count() == 0) {
				eMsg("아이템을 모두 선택했습니다.");
				return;
			}

			if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				var l = items.stream().filter(i -> i.img.getIcon() == null).findFirst().get();
				l.f = jfc.getSelectedFile();
				l.img.setIcon(getIcon(l.f.getPath(), 80, 80));
				l.txt.setEnabled(true);
			}
		}));

		for (int i = 0; i < 3; i++) {
			var item = new Item();
			items.add(item);
			cc.add(item);
		}

		s.add(area);
		s.add(ss = new JPanel(new FlowLayout(2)), "South");

		ss.add(btn("등록하기", a -> {
			for (var t : new JTextField[] { nameTxt, priceTxt, dcTxt }) {
				if (t.getText().isEmpty()) {
					eMsg("빈칸이 있습니다.");
					return;
				}
			}

			if (!getOne("select * from game where g_name=?", nameTxt.getText()).isEmpty()) {
				eMsg("중복된 게임명이 있습니다.");
				return;
			}

			if (toInt(priceTxt.getText()) == -1 || toInt(dcTxt.getText()) == -1) {
				eMsg("숫자로 입력해주세요.");
				return;
			}

			var dc = toInt(dcTxt.getText());
			if (dc < 0 || dc > 100) {
				eMsg("할인율은 0~100%만 가능합니다.");
				return;
			}

			var cnt = items.stream().filter(i -> i.img.getIcon() == null).count();

			if (!(cnt == 0 || cnt == 3)) {
				eMsg("아이템을 확인해주세요.");
				return;
			}

			iMsg("등록이 완료되었습니다.");
			var genre = String.join(",", Stream.of(genreLbl.getText().split(","))
					.map(s -> Arrays.asList(g_genre).indexOf(s) + "").toArray(String[]::new));
			try {
				execute("insert into game values(0, ?,?,?,?,?,?,?,?,?)", genre, nameTxt.getText(),
						ageCom.getSelectedIndex(), area.getText(), priceTxt.getText(), dcTxt.getText(),
						gdCom.getSelectedIndex(), cnt > 0 ? 1 : 0, new FileInputStream(f));

				if (cnt > 0) {
					var g_no = getOne("select g_no from game order by g_no desc");
					for (var i : items) {
						execute("insert into item values(0, ?, ?, ?)", g_no, i.txt.getText(), new FileInputStream(f));
					}
				}
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}));

		area.setLineWrap(true);
		area.setBorder(new TitledBorder(new LineBorder(Color.black), "설명"));
		cc.setBorder(new TitledBorder(new LineBorder(Color.black), "아이템"));
		img.setBorder(new LineBorder(Color.black));
		mf.setJPanelOpaque(this);
		setBackground(Color.white);
		setOpaque(true);

		repaint();
	}

	class Item extends JPanel {
		JLabel img;
		JTextField txt;
		File f;

		public Item() {
			super(new BorderLayout());

			sz(this, 80, 80);

			add(img = new JLabel());
			add(txt = new JTextField(), "South");

			img.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (img.getIcon() == null || e.getButton() != 3) {
						return;
					}

					var idx = items.indexOf(Item.this);

					items.remove(idx);
					items.add(new Item());

					cc.removeAll();

					for (var i : items) {
						cc.add(i);
					}

					cc.repaint();
					cc.revalidate();
				}
			});

			txt.setEnabled(false);
			img.setBorder(new LineBorder(Color.black));
		}
	}

	class Renderer extends JLabel implements ListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {

			setIcon(icons.get(icons.indexOf(value)));

			return this;
		}
	}

	public static void main(String[] args) {
		mf = new MainFrame();
		new GameInfo();
	}
}
