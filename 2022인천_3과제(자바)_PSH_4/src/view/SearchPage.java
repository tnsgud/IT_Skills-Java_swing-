package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.metal.MetalToggleButtonUI;

public class SearchPage extends BasePage {
	West west;
	Map map;

	ArrayList<ArrayList<Node>> adjList = new ArrayList<>();
	ArrayList<Integer> pathList = new ArrayList<>();
	ArrayList<MapItem> itemList = new ArrayList<>();
	HashMap<Integer, String> select = new HashMap<>();
	JPopupMenu pop = new JPopupMenu();
	{
		for (var cap : "출발지,도착지".split(",")) {
			var i = new JMenuItem(cap);
			i.addActionListener(a -> {
				int idx = cap.equals("출발지") ? 0 : 1;
				select.put(idx, pop.getName());
				setPath(idx);
			});
			pop.add(i);
		}
	}

	public static void main(String[] args) {
		mf = new MainFrame();
		mf.swap(new SearchPage());
		mf.setVisible(true);
	}

	private void setPath(int idx) {
		var arr = pop.getName().split(",");
		var me= idx == 0 ? west.txtDe : west.txtAr;
		var comp = idx == 0 ? west.txtAr : west.txtDe;

		if (comp.getName() != null && comp.getName().equals(arr[0])) {
			eMsg("출발지와 도착지는 같을 수 없습니다.");
			return;
		}

		me.setName(arr[0]);
		me.setText(arr[1]);
	}

	public SearchPage() {
		data();

		add(sz(west = new West(), 250, 0), "West");
		add(map = new Map());
	}

	private void data() {
		var cnt = toInt(getRows("select count(*) from building"));
		for (int i = 0; i < cnt + 1; i++) {
			adjList.add(new ArrayList<>());
		}

		for (var rs : getRows(
				"select node1, node2, b1.x, b1.y, b2.x, b2.y, c.name from connection c, building b1, building b2 where b1.no = c.node1 and b2.no = c.node2")) {
			int n1 = toInt(rs.get(0)), n2 = toInt(rs.get(1));
			int x1 = toInt(rs.get(2)), y1 = toInt(rs.get(3)), x2 = toInt(rs.get(4)), y2 = toInt(rs.get(5));
			int cost = (int) Point.distance(x1, y1, x2, y2);
			var name = rs.get(6).toString();

			adjList.get(n1).add(new Node(n2, cost, name));
			adjList.get(n2).add(new Node(n1, cost, name));
		}

		for (var rs : getRows(
				"select b.*, ifnull(round(avg(rate), 0), 0) from building b left join rate r on b.no = r.building where type <> 3 group by b.no")) {
			int x = toInt(rs.get(6)), y = toInt(rs.get(7));
			itemList.add(new MapItem(rs, x, y));
		}
	}

	class West extends BasePage {

		ButtonGroup buttonGroup = new ButtonGroup();
		JTextField txt = new JTextField(), txtDe = new JTextField(), txtAr = new JTextField();
		JPanel path, search;
		JScrollPane scr;
		JToggleButton tog[] = Stream.of("검색,길찾기".split(",")).map(c -> {
			var tg = new JToggleButton(c);
			tg.setBackground(blue);
			tg.setForeground(Color.white);
			tg.setUI(new MetalToggleButtonUI() {
				@Override
				protected Color getSelectColor() {
					return blue.darker();
				}
			});
			tg.addActionListener(a -> scr.setViewportView(c.equals("검색") ? search : path));
			buttonGroup.add(tg);
			return tg;
		}).toArray(JToggleButton[]::new);

		public West() {
			add(n = new JPanel(new BorderLayout(5, 5)) {
				@Override
				public void setOpaque(boolean isOpaque) {
					super.setOpaque(true);
				}
			}, "North");
			add(scr = new JScrollPane(search = new JPanel()));
			add(event(lblHyp("메인으로", 2, 15), e -> mf.swap(new UserMainPage())), "South");
			search.setLayout(new BoxLayout(search, BoxLayout.Y_AXIS));

			n.add(nn = new JPanel(new BorderLayout(5, 5)), "North");
			n.add(nc = new JPanel(new GridLayout(1, 0, 5, 5)));

			nn.add(txt);
			nn.add(btn("검색", a -> search()), "East");

			Stream.of(tog).forEach(nc::add);

			tog[0].doClick();

			path = new JPanel(new BorderLayout(5, 5));

			path.add(cn = new JPanel(new BorderLayout(5, 5)), "North");
			path.add(cc = new JPanel());
			cc.setLayout(new BoxLayout(cc, BoxLayout.Y_AXIS));
			{
				var tmp1 = new JPanel(new GridLayout(0, 1, 5, 5));
				var tmp2 = new JPanel(new FlowLayout(2, 0, 0));

				tmp1.add(txtDe = new JTextField());
				tmp1.add(txtAr = new JTextField());

				txtDe.setFocusable(false);
				txtAr.setFocusable(false);

				tmp2.add(btn("집을 출발지로", a -> {
					var rs = getRows(
							"select b.no, b.name from user u, building b where u.building = b.no and u.u_no = ?",
							user.get(0)).get(0);
					txtDe.setName(rs.get(0).toString());
					txtDe.setText(rs.get(1).toString());
				}));

				cn.add(tmp1);
				cn.add(btn("↑↓", a -> {
					var tmpName = txtDe.getName();
					var tmpText = txtDe.getText();

					txtDe.setName(txtAr.getName());
					txtDe.setText(txtAr.getText());

					txtAr.setName(tmpName);
					txtAr.setText(tmpText);
				}), "East");
				cn.add(tmp2, "South");
			}

			n.setBackground(blue);
			n.setBorder(new EmptyBorder(5, 5, 5, 5));
		}

		private void search() {
			if (txt.getText().isEmpty()) {
				eMsg("검색 키워드를 입력하세요.");
				return;
			}

			var rs = itemList.stream().map(a -> a.info).filter(
					a -> a.get(1).toString().contains(txt.getText()) || a.get(4).toString().contains(txt.getText()))
					.collect(Collectors.toList());
			if (rs.isEmpty()) {
				eMsg("검색결가가 없습니다.");
				return;
			}

			search.removeAll();

			search.add(lbl("<html>장소명 <font color=rgb(0,123,255)>" + txt.getText() + "</font> 의 검색 결과", 2, 15));
			for (var r : rs) {
				var tmp = event(new JPanel(new BorderLayout()), e -> {
				});
				var tmpC = new JPanel(new GridLayout(0, 1));

				tmp.add(tmpC);
				tmp.add(new JLabel(getIcon(r.get(8), 80, 80)), "East");

				tmpC.add(lblHyp("<html><b><font color='black'>" + (rs.indexOf(r) + 1) + ":" + r.get(1), 2, 15));
				tmpC.add(lbl("<html>" + r.get(4), 2));
				tmpC.add(lbl("평점 : " + r.get(9), 2));

				tmp.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						if (e.getButton() == 3) {
							pop.setName(r.get(0) + "," + r.get(1));
						}
					}
				});

				tmp.setComponentPopupMenu(pop);
				tmp.setBorder(new MatteBorder(1, 0, 1, 0, Color.LIGHT_GRAY));
				tmp.setAlignmentX(LEFT_ALIGNMENT);
				tmp.setMaximumSize(new Dimension(240, 120));
				
				search.add(tmp);
			}

			search.repaint();
			search.revalidate();
		}
	}

	class Map extends BasePage {

	}

	class Node {
		int no, cost;
		String name;

		public Node(int no, int cost, String name) {
			super();
			this.no = no;
			this.cost = cost;
			this.name = name;
		}
	}

	class MapItem {
		ArrayList<Object> info;
		int x, y;

		public MapItem(ArrayList<Object> info, int x, int y) {
			super();
			this.info = info;
			this.x = x;
			this.y = y;
		}
	}
}
