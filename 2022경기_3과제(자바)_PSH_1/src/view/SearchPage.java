package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class SearchPage extends BasePage {
	ArrayList<String> list = new ArrayList<>();
	HashMap<Integer, String> map = new HashMap<>();
	JComboBox com[] = { new JComboBox<>(",인기,항일중,무료".split(",")), new JComboBox(g_genre) };
	JTextField txt = new JTextField(15);
	JLabel icon = new JLabel(getIcon("./datafiles/기본사진/9.png", 25, 25));
	JPanel result = new JPanel(new GridLayout(0, 1));
	String[] sqls = { "select * from v1 where true %s",
			"select v1.* from v1, library l where v1.g_no = l.g_no %s group by v1.g_no order by count(*) desc, v1.g_no limit 5",
			"select * from v1 where g_sale <> 0 %s", "select * from v1 where g_price = 0 %s" };

	@Override
	public JButton btn(String c, ActionListener a) {
		var b = super.btn(c, a);
		b.setBackground(null);
		return b;
	}

	public static void main(String[] args) {
		new LoginFrame();
	}

	public SearchPage() {
		super("검색");

		data();
		ui();
		event();
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
				initialSearch();

				setLayout(null);
				add(result).setBounds(txt.getX(), txt.getY() + 30, txt.getWidth(), result.getComponentCount() * 50);
				setComponentZOrder(result, 0);

				result.setVisible(!txt.getText().isEmpty());

				repaint();
				revalidate();
			}
		});
	}

	private void ui() {
		add(n = new JPanel(), "North");
		add(new JScrollPane(c = new JPanel()));
		c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));

		var cap = "구분,장르,검색".split(",");
		for (int i = 0; i < cap.length; i++) {
			n.add(lbl(cap[i], 0, 15));
			n.add(i < 2 ? com[i] : txt);
		}

		n.add(icon);

		if (user == null) {
			n.add(btn("등록하기", a->new GameInfoPage()));
		}

		search();
	}

	private void initialSearch() {
		result.removeAll();

		var word = Stream.of(txt.getText().split(""))
				.map(t -> list.indexOf(t) < 0 ? t : "[" + map.get(list.indexOf(t)) + "]").collect(Collectors.joining());
		var sql = String.format(sqls[com[0].getSelectedIndex()], "and g_name regexp ? and g_genre regexp ?");
		var rs = getRows(sql, "^" + word + ".*",
				"(" + (com[1].getSelectedIndex() == 0 ? "" : com[1].getSelectedIndex()) + ")");

		if (user != null) {
			rs = rs.stream().filter(r -> {
				for (var num : user.get(7).toString().split(",")) {
					if (("," + r.get(3) + ",").matches(".*," + num + ",.*")) {
						return false;
					}
				}

				if (uAgeFilter) {
					return toInt(g_age[toInt(r.get(4))]) <= u_age;
				}

				return true;
			}).collect(Collectors.toCollection(ArrayList::new));
		}

		for (var r : rs) {
			var tmp = new JPanel(new BorderLayout());

			tmp.add(new JLabel(getIcon(r.get(1), 40, 40)), "West");
			tmp.add(lbl("<html><font color='black'>" + r.get(2), 2));

			tmp.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					g_no = toInt(((JPanel) e.getSource()).getName());
					txt.setText("");
					result.setVisible(false);
					new GamePage();
				}
			});

			tmp.setBorder(new EmptyBorder(5, 5, 5, 5));
			tmp.setName(r.get(0).toString());
			tmp.setBackground(Color.white);
			tmp.setOpaque(true);

			result.add(tmp);
		}
	}

	private void search() {
		c.removeAll();

		var word = Stream.of(txt.getText().split(""))
				.map(t -> list.indexOf(t) < 0 ? t : "[" + map.get(list.indexOf(t)) + "]")
				.collect(Collectors.joining(","));
		var sql = String.format(sqls[com[0].getSelectedIndex()], "and g_name regexp ? and g_genre regexp ?");
		var rs = getRows(sql, "^" + word + ".*",
				"(" + (com[1].getSelectedIndex() == 0 ? "" : com[1].getSelectedIndex()) + ")");

		if (user != null) {
			rs = rs.stream().filter(r -> {
				for (var num : user.get(7).toString().split(",")) {
					if (("," + r.get(3) + ",").matches(".*," + num + ",.*")) {
						return false;
					}
				}

				if (uAgeFilter) {
					return toInt(g_age[toInt(r.get(4))]) <= u_age;
				}

				return true;
			}).collect(Collectors.toCollection(ArrayList::new));
		}

		for (var r : rs) {
			var tmp = new JPanel(new BorderLayout());
			var tmpC = new JPanel(new GridLayout(0, 1));

			tmp.add(new JLabel(getIcon(r.get(1), 150, 150)), "West");
			tmp.add(tmpC);

			tmp.setName(r.get(0).toString());
			tmp.setOpaque(true);

			var cap = "게임명,장르,연령,평점,가격".split(",");
			for (int i = 0; i < cap.length; i++) {
				var txt = r.get(i + 2).toString();

				if (i == 1) {
					txt = Stream.of(txt.split(",")).map(t -> g_genre[toInt(t)]).collect(Collectors.joining(","));
				} else if (i == 2) {
					txt = g_age[toInt(txt)];
				} else if (i == 3) {
					txt += "점";
				} else if (i == 4 && txt.equals("0")) {
					txt = "무료";
				} else if (i == 4) {
					txt += "원" + (r.get(7).toString().isEmpty() ? ""
							: String.format(" -> %s(%s 할인중) 대상:%s↑", r.get(8).toString(), r.get(7) + "%",
									g_gd[toInt(getOne("select g_gd from game where g_no = ?", r.get(0)))]));
				}

				tmpC.add(lbl(cap[i] + ":" + txt, 2, 20));
			}

			tmp.setOpaque(false);

			tmp.setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(5, 5, 5, 5)));
			tmp.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					g_no = toInt(((JPanel) e.getSource()).getName());

					if (user == null) {
						new GameInfoPage(g_no);
					} else {
						new GamePage();
					}
				}
			});

			c.add(tmp);
		}
		mf.repaint();
		mf.revalidate();
	}

	private void data() {
		list.add("ㄱ");
		list.add("ㄴ");
		list.add("ㄷ");
		list.add("ㄹ");
		list.add("ㅁ");
		list.add("ㅂ");
		list.add("ㅅ");
		list.add("ㅇ");
		list.add("ㅈ");
		list.add("ㅊ");
		list.add("ㅋ");
		list.add("ㅌ");
		list.add("ㅍ");
		list.add("ㅎ");

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
}
