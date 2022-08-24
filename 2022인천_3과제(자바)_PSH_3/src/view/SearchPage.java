package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

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

	ArrayList<Integer> pathList = new ArrayList<>();
	ArrayList<MapItem> itemList = new ArrayList<>();
	ArrayList<ArrayList<Node>> adjLists = new ArrayList<>();
	HashMap<Integer, String> select = new HashMap<>();

	JPopupMenu pop = new JPopupMenu();
	{
		for (var cap : "출발지,도착지".split(",")) {
			var i = new JMenuItem(cap);
			i.addActionListener(a -> {
				int idx = cap.equals("출발지") ? 0 : 1;
				select.put(idx, pop.getName());
				setPath(idx);
				west.tog[1].doClick();
				dijkstra();
			});
			pop.add(i);
		}
	}

	public SearchPage() {
		data();

		add(west = sz(new West(), 250, 0), "West");
		add(map = new Map());
	}

	private void data() {
		var cnt = toInt(getOne("select count(*) from building"));
		for (int i = 0; i < cnt + 1; i++) {
			adjLists.add(new ArrayList<>());
		}

		for (var rs : getRows(
				"select node1, node2, b1.x, b1.y, b2.x, b2.y, c.name from connection c, building b1, building b2 where c.node1 = b1.no and c.node2 = b2.no")) {
			int n1 = toInt(rs.get(0)), n2 = toInt(rs.get(1));
			int x1 = toInt(rs.get(2)), y1 = toInt(rs.get(3)), x2 = toInt(rs.get(4)), y2 = toInt(rs.get(5));
			int cost = (int) Point.distance(x1, y1, x2, y2);
			var name = rs.get(6).toString();

			adjLists.get(n1).add(new Node(n2, cost, name));
			adjLists.get(n2).add(new Node(n1, cost, name));
		}

		for (var rs : getRows(
				"select b.*, ifnull(round(avg(rate), 0), 0) from building b left join rate r on b.no = r.building where type <> 3 group by b._no")) {
			int x = toInt(rs.get(6)), y = toInt(rs.get(7));
			itemList.add(new MapItem(x, y, rs));
		}
	}

	private void setPath(int idx) {

	}

	private void dijkstra() {

	}

	class West extends BasePage {
		JTextField txtSearch, txtAr, txtDe;
		JToggleButton tog[] = new JToggleButton[2];
		JPanel search, path;
		JScrollPane scr;

		public West() {
			add(n = new JPanel(new BorderLayout(5, 5)), "North");
			add(scr = new JScrollPane(search = new JPanel()));
			add(lbl("메인으로", 2, 0, 15, Color.orange, e -> mf.swap(new MainPage())), "South");
			search.setLayout(new BoxLayout(search, BoxLayout.Y_AXIS));

			n.add(nn = new JPanel(new BorderLayout(5, 5)), "North");
			n.add(nc = new JPanel(new GridLayout(1, 0, 5, 5)));

			nn.add(txtSearch = new JTextField());
			nn.add(btn("검색", a -> search()), "East");

			var bg = new ButtonGroup();
			for (int i = 0; i < 2; i++) {
				tog[i] = new JToggleButton("검색,길찾기".split(",")[i]);
				tog[i].setForeground(Color.white);
				tog[i].setBackground(blue);
				tog[i].setUI(new MetalToggleButtonUI() {
					@Override
					protected Color getSelectColor() {
						return blue.darker();
					}
				});
				tog[i].addActionListener(a -> scr.setViewportView(a.getActionCommand().equals("검색") ? search : path));

				bg.add(tog[i]);
				nc.add(tog[i]);
			}

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
				}));

				cn.add(tmp1);
				cn.add(btn("↑↓", a -> {
				}), "East");
				cn.add(tmp2, "South");
			}

			n.setBorder(new EmptyBorder(5, 5, 5, 5));
			cn.setBorder(new EmptyBorder(5, 5, 5, 5));
			n.setOpaque(true);
			nn.setOpaque(false);
			nc.setOpaque(false);
			n.setBackground(blue);
		}

		void search() {
			if (txtSearch.getText().isEmpty()) {
				eMsg("검색 키워드를 입력하세요");
				return;
			}

			var rs = itemList.stream().map(a -> a.info).filter(a -> a.get(1).toString().contains(txtSearch.getText())
					|| a.get(4).toString().contains(txtSearch.getText())).collect(Collectors.toList());
			if (rs.isEmpty()) {
				iMsg("검색결과가 없습니다.");
				return;
			}

			search.removeAll();

			search.add(lbl("<html>장소명 <font color=rgb(0, 123, 255)>" + txtSearch.getText() + "</font> 의 검색 결과", 2, 13));
			for (var r : rs) {
				var tmp1 = new JPanel(new BorderLayout());
				var tmp2 = new JPanel(new GridLayout(0, 1));

				tmp1.add(tmp2);
				tmp1.add(new JLabel(getIcon(r.get(8), 80, 80)), "East");

				tmp2.add(lbl(rs.indexOf(r) + 1 + ":" + r.get(1), 2, 1, 13, Color.black, e -> {
				}));
				tmp2.add(lbl("<html>" + r.get(4), 2));
				tmp2.add(lbl("평점:" + r.get(9), 2));

				tmp1.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						if (e.getButton() != 1)
							return;

						if (e.getButton() == 1 && e.getClickCount() == 2) {
							map.center(toInt(rs.get(6)), toInt(rs.get(8)));
						} else if (e.getButton() == 3) {
							pop.setName(r.get(0) + "," + r.get(1));
							pop.show(tmp1, e.getX(), e.getY());
						}
					}
				});

				tmp1.setBorder(new MatteBorder(1, 0, 1, 0, Color.LIGHT_GRAY));
				tmp1.setAlignmentX(LEFT_ALIGNMENT);

				tmp1.setMaximumSize(new Dimension(240, 120));

				search.add(sz(tmp1, 0, 120));
			}

			search.repaint();
			search.revalidate();

			map.center(toInt(rs.get(0).get(6)), toInt(rs.get(0).get(7)));
		}
	}

	class Map extends BasePage {
		BufferedImage img;
		AffineTransform myAffine = new AffineTransform();
		Point2D sAffPoint = new Point2D.Float(), eAffinePoint = new Point2D.Float();
		double affX, affY;
		double zoom = 1;
		Point fromPoint, toPoint;

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			var g2 = (Graphics2D) g;

			g2.drawImage(img, myAffine, null);
		}

		public void center(int x, int y) {

		}
		
		public Map() {
			
		}
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
		int x, y;
		ArrayList<Object> info;

		public MapItem(int x, int y, ArrayList<Object> info) {
			super();
			this.x = x;
			this.y = y;
			this.info = info;
		}
	}

	public static void main(String[] args) {
		mf = new MainFrame();
		mf.swap(new SearchPage());
		mf.setVisible(true);
	}
}
