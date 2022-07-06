package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

public class SearchPage extends BasePage {
	JComboBox com[] = { new JComboBox<>(",인기,할인중,무료".split(",")), new JComboBox(g_genre) };
	JTextField txt = new JTextField(15);
	JLabel icon = new JLabel(getIcon("./datafiles/기본사진/9.png", 50, 50));
	JPanel result = new JPanel(new GridLayout(0, 1, 5, 5));
	String sqls[] = { "select v1.* from v1 where true %s",
			"select v1.* from v1, library l where v1.g_no = l.g_no %s group by v1.g_no order by count(*) desc, v1.g_no limit 5",
			"select v1.* from v1 where g_sale <> 0 %s", "select * from v1 where g_price = 0 %s" };
	ArrayList<String> list1 = new ArrayList<>();
	HashMap<Integer, String> map = new HashMap<>();

	public SearchPage() {
		super("검색");
		user = getRows("select * from user where u_no=1").get(0);

		var birth = LocalDate.parse(user.get(4).toString());
		u_age = LocalDate.now().getYear() - birth.getYear();
		u_age -= birth.getMonthValue() > LocalDate.now().getMonthValue() ? 1 : 0;

		u_ageFilter = Arrays.asList(user.get(7).toString().split(",")).contains("12");

		data();
		ui();
		event();
	}

	private void data() {
		list1.add("ㄱ");
		list1.add("ㄴ");
		list1.add("ㄷ");
		list1.add("ㄹ");
		list1.add("ㅁ");
		list1.add("ㅂ");
		list1.add("ㅅ");
		list1.add("ㅇ");
		list1.add("ㅈ");
		list1.add("ㅊ");
		list1.add("ㅋ");
		list1.add("ㅌ");
		list1.add("ㅍ");
		list1.add("ㅎ");

		map.put(0, "가-깋");
		map.put(1, "나-닣");
		map.put(2, "다-딯");
		map.put(3, "라-맇");
		map.put(4, "마-밓");
		map.put(5, "바-빟");
		map.put(6, "사-싷");
		map.put(7, "아-잏");
		map.put(8, "자-짛");
		map.put(9, "차-칳");
		map.put(10, "카-킿");
		map.put(11, "타-팋");
		map.put(12, "파-핗");
		map.put(13, "하-힣");
	}

	private void event() {
		icon.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				search();
			}
		});
		txt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				pre_search();

				setLayout(null);
				add(result).setBounds(txt.getX(), txt.getY() + 30, txt.getWidth(), 30 * result.getComponentCount());
				setComponentZOrder(result, 0);

				if (txt.getText().isEmpty()) {
					result.setVisible(false);
				} else {
					result.setVisible(true);
				}

				repaint();
				revalidate();
			}
		});
	}

	private void ui() {
		add(n = new JPanel(new FlowLayout(1)), "North");
		add(new JScrollPane(c = new JPanel(new GridLayout(0, 1))));

		var cap = "구분,장르,검색".split(",");
		for (int i = 0; i < cap.length; i++) {
			n.add(lbl(cap[i], 0, 15));
			n.add(i < 2 ? com[i] : txt);
		}

		n.add(icon);

		search();
	}

	void pre_search() {
		result.removeAll();

		var keyword = Stream.of(txt.getText().split("")).map(t -> {
			t = list1.indexOf(t) < 0 ? t : "[" + map.get(list1.indexOf(t)) + "]";

			return t;
		}).collect(Collectors.joining());
		var sql = String.format(sqls[com[0].getSelectedIndex()], "and g_name regexp ? and g_genre regexp ?");
		sql += sql.contains("limit 5") ? "" : "limit 5";
		var rs = getRows(sql, "^" + keyword + ".*",
				"(" + (com[1].getSelectedIndex() == 0 ? "" : com[1].getSelectedIndex()) + ")");
		
		for (var r : rs) {
			var tmp = new JPanel(new BorderLayout());

			tmp.add(new JLabel(getIcon(r.get(1), 30, 30)), "West");
			tmp.add(lbl("<html><font color='black'>" + r.get(2).toString(), 2));

			tmp.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					g_no = toInt(((JPanel)e.getSource()).getName());
					new GamePage();
				}
			});

			tmp.setName(r.get(0).toString());
			tmp.setBackground(Color.white);
			tmp.setOpaque(true);

			result.add(tmp);
		}
	}

	void search() {
		c.removeAll();

		var keyword = Stream.of(txt.getText().split("")).map(t -> {
			t = list1.indexOf(t) < 0 ? t : "[" + map.get(list1.indexOf(t)) + "]";

			return t;
		}).collect(Collectors.joining());
		var sql = String.format(sqls[com[0].getSelectedIndex()], "and g_name regexp ? and g_genre regexp ?");
		sql += sql.contains("limit 5") ? "" : "limit 5";
		var rs = getRows(sql, "^" + keyword + ".*",
				"(" + (com[1].getSelectedIndex() == 0 ? "" : com[1].getSelectedIndex()) + ")");

		rs = rs.stream().filter(r -> {
			if (user.get(7).equals("0")) {
				return true;
			}

			for (var num : user.get(7).toString().split(",")) {
				if (("," + r.get(3)).matches(".*," + num)) {
					return false;
				}
			}

			if (u_ageFilter) {
				return toInt(g_age[toInt(r.get(4))]) <= u_age;
			}

			return true;
		}).collect(Collectors.toCollection(ArrayList::new));

		for (var r : rs) {
			var tmp = new JPanel(new BorderLayout(5, 5));
			var tmp_c = new JPanel(new GridLayout(0, 1));

			tmp.add(new JLabel(getIcon(r.get(1), 150, 150)), "West");
			tmp.add(tmp_c);

			tmp.setName(r.get(0).toString());

			var cap = "게임명,장르,연령,평점,가격".split(",");
			for (int i = 0; i < cap.length; i++) {
				var txt = r.get(i + 2).toString();

				if (i == 1) {
					txt = String.join(",",
							Stream.of(txt.split(",")).map(t -> g_genre[toInt(t)]).toArray(String[]::new));
				} else if (i == 2) {
					txt = g_age[toInt(txt)];
				} else if (i == 3) {
					txt += "점";
				} else if (i == 4 && !txt.equals("0")) {
					txt += String.format(" -> %s(%s 할인중) 대상:%s↑", r.get(8), r.get(7),
							g_gd[toInt(getOne("select g_gd from game where g_no=?", r.get(0)))]);
				} else if (i == 4) {
					txt = "무료";
				}

				tmp_c.add(lbl(cap[i] + ":" + txt, 2, 20));
			}

			tmp.setBorder(new LineBorder(Color.black));
			tmp.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					g_no = toInt(((JPanel) e.getSource()).getName());
					new GamePage();
				}
			});

			c.add(tmp);
		}

		c.repaint();
		c.revalidate();

		mf.repaint();
	}

	public static void main(String[] args) {
		mf = new MainFrame();
		new SearchPage();
		mf.setVisible(true);
	}
}
