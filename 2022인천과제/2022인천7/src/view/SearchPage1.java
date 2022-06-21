package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Map.Entry;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.metal.MetalButtonUI;
import javax.swing.plaf.metal.MetalToggleButtonUI;

public class SearchPage1 extends BasePage {

	West west;
	Map map;

	ArrayList<Integer> paths = new ArrayList<>();
	ArrayList<Object[]> objs = new ArrayList<>();
	ArrayList<ArrayList<Object>> adjList = new ArrayList<>();
	Entry<String, String> select;

	AffineTransform aff = new AffineTransform();
	String colorKey = "";
	int uno = 1;
//	int uno = toInt(user.get(0));

	public SearchPage1() {
		data();

		add(west = sz(new West(), 250, 0), "West");
		add(map = new Map());
	}

	private void data() {
		var cnt = toInt(getOne("select count(*) from building")) + 1;
		for (int i = 0; i < cnt; i++) {
			adjList.add(new ArrayList<>());
		}

		for (var rs : getRows(
				"select c.node1, c.node2, b1.x, b1.y, b2.x, b2.y, c.name from connection c, building b1, building b2 where c.node1 = b1.no and c.node2 = b2.no")) {
			int n1 = toInt(rs.get(0)), n2 = toInt(rs.get(1));
			int x1 = toInt(rs.get(2)), y1 = toInt(rs.get(3)), x2 = toInt(rs.get(4)), y2 = toInt(rs.get(5));
			int cost = (int) Point.distance(x1, y1, x2, y2);
			var name = rs.get(6).toString();

			adjList.get(n1).add(new Object[] { n2, cost, name });
			adjList.get(n2).add(new Object[] { n1, cost, name });
		}

		for (var rs : getRows(
				"select b.*, ifnull(round(avg(rate), 0), 0) from building b left join rate r on b.no = r.building where type <> 3")) {
			int x = toInt(rs.get(6)), y = toInt(rs.get(7));
			objs.add(new Object[] { rs, x - 20, y - 20 });
		}
	}

	public void findPath() {
		
	}

	class West extends BasePage {
		JPanel search, path;
		JScrollPane scr;
		JTextField srhTxt, depTxt, arvTxt;
		JToggleButton tog[] = new JToggleButton[2];

		public West() {
			add(n = new JPanel(new BorderLayout(5, 5)), "North");
			add(scr = new JScrollPane(search = new JPanel(new BorderLayout())));
			add(hyplbl("메인으로", 2, 20, Color.orange, e -> {
			}), "South");

			n.add(srhTxt = new JTextField());
			n.add(btn("검색", a -> search()), "East");
			n.add(ns = new JPanel(new GridLayout(1, 0, 5, 5)), "South");

			var bg = new ButtonGroup();
			for (int i = 0; i < tog.length; i++) {
				ns.add(tog[i] = new JToggleButton(i == 0 ? "검색" : "길찾기"));
				bg.add(tog[i]);
				tog[i].setForeground(Color.white);
				tog[i].setBackground(blue);
				tog[i].setUI(new MetalToggleButtonUI() {
					@Override
					protected Color getSelectColor() {
						return blue.darker();
					}
				});
				tog[i].addActionListener(a -> {
				});
			}

			n.setBackground(blue);
			n.setBorder(new EmptyBorder(5, 5, 5, 5));
			ns.setOpaque(false);
		}

		void search() {
			if (srhTxt.getText().isEmpty()) {
				eMsg("검색 키워드를 입력하세요.");
				return;
			}

			var rs = getRows(
					"select b.*, ifnull(round(avg(rate), 0), 0) from building b left join rate r on b.no = r.building where type <> 3 and b.name like ? group by b.no",
					"%" + srhTxt.getText() + "%");
			if (rs.isEmpty() || rs.get(0).get(0) == null) {
				eMsg("검색 결과가 없습니다.");
				return;
			}

			search.add(lbl("<html>장소명 <font color=rgb(0,123,255)>" + srhTxt.getText() + "</font> 의 검색 결과", 2, 20),
					"North");
			var tmp = new JPanel(new GridLayout(0, 1));
			map.goCenter(toInt(rs.get(0).get(6)), toInt(rs.get(0).get(7)));
			for (var r : rs) {
				var temp = new JPanel(new BorderLayout(5, 5));

				temp.add(hyplbl(rs.indexOf(r) + 1 + ":" + r.get(1), 2, 15, Color.black, e -> {
					if (toInt(r.get(5)) == 2)
						return;
 
				}), "North");
				temp.add(lbl(r.get(4) + "", 2));
				temp.add(new JLabel(getIcon(r.get(8), 80, 70)), "East");
				temp.add(lbl("평점:" + String.format("%.1f", (double) toInt(r.get(9))), 2), "South");
				temp.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						if (e.getButton() == 1 && e.getClickCount() == 2) {
							map.goCenter(toInt(r.get(6)), toInt(r.get(7)));
						}
					}
				});

				var pop = new JPopupMenu();
				for (var cap : "출발지,도착지".split(",")) {
					var item = new JMenuItem(cap);
					item.addActionListener(a -> {
						if (cap.equals("출발지")) {
							depTxt.setName(r.get(0).toString());
							depTxt.setText(r.get(1).toString());
						} else {
							arvTxt.setName(r.get(0).toString());
							arvTxt.setText(r.get(1).toString());
						}

						findPath();
					});
					pop.add(item);
				}

				temp.setComponentPopupMenu(pop);
				temp.setBorder(new LineBorder(Color.lightGray));

				tmp.add(temp);
			}
			while (tmp.getComponentCount() < 3) {
				tmp.add(lbl("", 0));
			}

			search.add(tmp);

			repaint();
			revalidate();
		}
	}

	class Map extends BasePage {
		public void goCenter(int x, int y) {

		}
	}

	public static void main(String[] args) {
		mf.swapPage(new SearchPage1());
		mf.setVisible(true);
	}
}
