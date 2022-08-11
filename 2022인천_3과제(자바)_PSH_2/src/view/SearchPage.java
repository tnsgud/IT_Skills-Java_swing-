package view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
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
	ArrayList<ArrayList<Node>> adjList = new ArrayList<>();
	HashMap<Integer, String> select = new HashMap<>();

	String colorKey = "";
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

	private void dijkstra() {
		colorKey = "";
		if (west.txtAr.getText().isEmpty() || west.txtDe.getText().isEmpty()) {
			return;
		}

		int start = toInt(west.txtAr.getName());
		int end = toInt(west.txtDe.getName());

		int[][] dist = new int[2][adjList.size() + 1]; // [0][] = 코스트 [1][] = 이전 위치
		for (int i = 0; i < dist[0].length; i++) {
			dist[0][i] = Integer.MAX_VALUE;
			dist[1][i] = -1;
		}

		pathList.clear();

		var pq = new PriorityQueue<int[]>((i1, i2) -> Integer.compare(i1[1], i2[1])); // {이전 위치, 코스트}
		pq.offer(new int[] { start, 0 });
		dist[0][start] = 0;

		while (!pq.isEmpty()) {
			var cur = pq.poll();
			int from = cur[0];
			int cost = cur[1];

			if (dist[0][from] < cost)
				continue;

			for (int i = 0; i < adjList.get(from).size(); i++) {
				var next = adjList.get(from).get(i);

				if (dist[0][next.no] > cost + next.cost) {
					dist[0][next.no] = cost + next.cost;
					dist[1][next.no] = from;
					pq.offer(new int[] { next.no, dist[0][next.no] });
				}
			}
		}

		west.cc.removeAll();

		while (start != end) {
			pathList.add(end);
			end = dist[1][end];
		}

		pathList.add(start);

		Collections.reverse(pathList);

		var arr = new ArrayList<String[]>();
		for (int i = 1; i < pathList.size(); i++) {
			int n1 = pathList.get(i - 1);
			int n2 = pathList.get(i);

			var node = adjList.get(n1).stream().filter(a -> a.no == n2).findFirst().get();

			if (!arr.stream().filter(a -> a[0].equals(arr.size() + ":" + node.name)).findFirst().isPresent()) {
				arr.add(new String[] { arr.size() + 1 + ":" + node.name, node.cost + "" });
			} else {
				var str = arr.get(arr.size() - 1);
				int cost = toInt(str[1]) + node.cost;
				arr.set(arr.size() - 1, new String[] { str[0], cost + "" });
			}
		}

		int tot = arr.stream().mapToInt(a -> toInt(a[1])).sum();

		var l = lbl("총 거리:" + tot + "m", 4, 15);
		l.setMaximumSize(new Dimension(250, 50));
		west.cc.add(l);

		for (int i = 0; i < arr.size(); i++) {
			var text = "<html><font color='black'>";

			if (i == 0) {
				text = "<html><font color='red'>출발 </font>" + text;
			} else if (i == arr.size() - 1) {
				text = "<html><font color='blue'>도착 </font>" + text;
			}

			final int j = i;
			var lbl = lbl(text + "" + arr.get(i)[0] + " 총 " + arr.get(i)[1] + "m", 0, 1, 12, Color.black, e -> {
				colorKey = arr.get(j)[0];
				map.drawMap();
			});
			lbl.setBorder(new MatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
			west.cc.add(lbl);
		}

		map.drawMap();
		repaint();
		revalidate();
	}

	private void setPath(int i) {
		var arr = pop.getName().split(",");
		var me = i == 0 ? west.txtAr : west.txtDe;
		var comp = i == 0 ? west.txtDe : west.txtAr;

		if (comp.getName() != null && comp.getName().equals(select.get(i))) {
			eMsg("출발지와 도착지는 같을 수 없습니다.");
			return;
		}

		me.setName(arr[0]);
		me.setText(arr[1]);
	}

	private void data() {
		var cnt = toInt(getOne("select count(*) from building"));
		for (int i = 0; i < cnt + 1; i++) {
			adjList.add(new ArrayList<>());
		}

		for (var rs : getRows(
				"select node1, node2, b1.x, b1.y, b2.x, b2.y, c.name from connection c, building b1, building b2 where c.node1= b1.no and c.node2 = b2.no")) {
			int no1 = toInt(rs.get(0)), no2 = toInt(rs.get(1));
			int x1 = toInt(rs.get(2)), y1 = toInt(rs.get(3)), x2 = toInt(rs.get(4)), y2 = toInt(rs.get(5));
			int cost = (int) Point.distance(x1, y1, x2, y2);
			var name = rs.get(6).toString();

			adjList.get(no1).add(new Node(no2, cost, name));
			adjList.get(no2).add(new Node(no1, cost, name));
		}

		for (var rs : getRows(
				"select b.*, ifnull(round(avg(rate), 0), 0) from building b left join rate r on b.no = r.building where type <> 3 group by b.no")) {
			int x = toInt(rs.get(6)), y = toInt(rs.get(7));
			itemList.add(new MapItem(rs, x, y));
		}
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
			for (int i = 0; i < tog.length; i++) {
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

			path = new JPanel(new BorderLayout(5, 5));

			path.add(cn = new JPanel(new BorderLayout(5, 5)), "North");
			path.add(cc = new JPanel());
			cc.setLayout(new BoxLayout(cc, BoxLayout.PAGE_AXIS));

			{
				var tmp1 = new JPanel(new GridLayout(0, 1, 5, 5));
				var tmp2 = new JPanel(new FlowLayout(2, 0, 0));

				tmp1.add(txtAr = new JTextField());
				tmp1.add(txtDe = new JTextField());

				txtAr.setFocusable(false);
				txtDe.setFocusable(false);

				tmp2.add(btn("집을 출발지로", a -> {
					var rs = getRows("select b.no, b.name from user u, building b where u.building = b.no and u.no = ?",
							user.get(0)).get(0);
					pop.setName(rs.get(0) + "," + rs.get(1));
					setPath(0);
				}));

				cn.add(tmp1);
				cn.add(btn("↑↓", a -> {
					var tmp = txtAr.getName();
					txtAr.setName(txtDe.getName());
					txtDe.setName(tmp);

					tmp = txtAr.getText();
					txtAr.setText(txtDe.getText());
					txtDe.setText(tmp);

					dijkstra();
				}), "East");
				cn.add(tmp2, "South");
			}

			tog[0].doClick();

			n.setBackground(blue);
			nn.setOpaque(false);
			nc.setOpaque(false);

			n.setBorder(new EmptyBorder(5, 5, 5, 5));
			cn.setBorder(new EmptyBorder(5, 5, 5, 5));
		}

		void search() {
			if (txtSearch.getText().isEmpty()) {
				eMsg("검색 키워드를 입력하세요.");
				return;
			}

			var rs = itemList.stream().map(a -> a.info).filter(a -> a.get(1).toString().contains(txtSearch.getText())
					|| a.get(4).toString().contains(txtSearch.getText())).collect(Collectors.toList());

			if (rs.isEmpty()) {
				iMsg("검색결과가 업습니다.");
				return;
			}

			search.removeAll();

			search.add(lbl("<html>장소명 <font color=rgb(0, 123, 255)> " + txtSearch.getText() + "</font> 의 검색 결과", 2, 13),
					"North");

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
					public void mousePressed(java.awt.event.MouseEvent e) {
						if (e.getButton() != 1)
							return;

						if (e.getButton() == 1 && e.getClickCount() == 2) {
							map.center(toInt(r.get(6)), toInt(r.get(7)));
						} else if (e.getButton() == 3) {
							pop.setName(r.get(0) + "," + r.get(1));
							pop.show(tmp1, e.getX(), e.getY());
						}
					}
				});

				tmp1.setBorder(new MatteBorder(1, 0, 1, 0, Color.LIGHT_GRAY));
				tmp1.setAlignmentX(Component.LEFT_ALIGNMENT);

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
		Point2D sAffPoint = new Point2D.Float(), eAffPoint = new Point2D.Float();
		double affDx, affDy;
		double zoom = 1;
		Point fromPoint, toPoint;

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			var g2 = (Graphics2D) g;
			g2.drawImage(img, myAffine, null);
		}

		public Map() {
			add(w = new JPanel(new GridBagLayout()), "West");

			var lbl = sz(new JLabel() {
				String text = "◀";

				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					var g2 = (Graphics2D) g;

					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

					g2.setColor(Color.white);
					g2.fillRoundRect(0, 0, 20, 20, 5, 5);

					g2.setColor(blue);
					g2.drawString(text, 5, 20);
				}
			}, 40, 40);
			lbl.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (e.getButton() != 1)
						return;

					if (lbl.text.equals("▶")) {
						lbl.text = "◀";
						sz(west, 250, 0);
					} else {
						lbl.text = "▶";
						sz(west, 0, 0);
					}

					repaint();
					revalidate();
				}
			});
			w.add(lbl);

			var ma = new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					fromPoint = e.getPoint();
					toPoint = null;

					try {
						eAffPoint = myAffine.inverseTransform(e.getPoint(), new Point2D.Float());
					} catch (NoninvertibleTransformException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					var cX = eAffPoint.getX();
					var cY = eAffPoint.getY();

					int x = (int) cX;
					int y = (int) cY;

					var item = itemList.stream().filter(i -> (i.x <= x && x <= i.x + 40) && (i.y <= y && y <= i.y + 40))
							.findFirst();
					item.ifPresent(i -> {
						if (e.getButton() == 1 && toInt(i.info.get(5)) != 2) {
							new InfoDialog(i.info).setVisible(true);
						} else if (e.getButton() == 3) {
							pop.setName(i.info.get(0) + "," + i.info.get(1));
							pop.show(map, e.getX(), e.getY());
						}
					});

					repaint();
				}

				@Override
				public void mouseDragged(MouseEvent e) {
					try {
						toPoint = e.getPoint();

						sAffPoint = myAffine.inverseTransform(fromPoint, null);
						eAffPoint = myAffine.inverseTransform(toPoint, null);

						affDx = sAffPoint.getX() - eAffPoint.getX();
						affDy = sAffPoint.getY() - eAffPoint.getY();

						myAffine.translate(-affDx, -affDy);

						fromPoint = toPoint;
						toPoint = null;
					} catch (NoninvertibleTransformException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					repaint();
				}

				@Override
				public void mouseWheelMoved(MouseWheelEvent e) {
					try {
						eAffPoint = myAffine.inverseTransform(e.getPoint(), null);
					} catch (NoninvertibleTransformException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					var flag = false;

					if (e.getPreciseWheelRotation() < 0) {
						if (zoom == 2) {
							flag = true;
						} else {
							zoom = Math.min(2, zoom + 0.1);
						}
					} else {
						if (zoom == 0.1) {
							flag = true;
						} else {
							zoom = Math.max(0.1, zoom - 0.1);
						}
					}

					if (!flag) {
						myAffine.setToIdentity();
						myAffine.translate(e.getX(), e.getY());
						myAffine.scale(zoom, zoom);
						myAffine.translate(-eAffPoint.getX(), -eAffPoint.getY());

						repaint();
					}
				}
			};

			addMouseListener(ma);
			addMouseMotionListener(ma);
			addMouseWheelListener(ma);

			drawMap();

			w.setOpaque(false);
			setBackground(new Color(154, 217, 234));
		}

		private void drawMap() {
			try {
				img = ImageIO.read(new File("./datafiles/map.jpg"));

				var g2 = (Graphics2D) img.getGraphics();

				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setStroke(new BasicStroke(3));

				if (!pathList.isEmpty()) {
					var tmp = new ArrayList<String>();

					for (int i = 0; i < pathList.size() - 1; i++) {
						int n1 = pathList.get(i), n2 = pathList.get(i + 1);
						int x1 = 0, y1 = 0, x2 = 0, y2 = 0;
						var node = adjList.get(n1).stream().filter(x -> x.no == n2).findFirst().get();

						if (!tmp.contains(tmp.size() + ":" + node.name)) {
							tmp.add(tmp.size()+1 + ":" + node.name);
						}

						var rs = getRows("select x, y from building where no = ?", n1).get(0);
						x1 = toInt(rs.get(0));
						y1 = toInt(rs.get(1));

						rs = getRows("select x, y from building where no = ?", n2).get(0);
						x2 = toInt(rs.get(0));
						y2 = toInt(rs.get(1));

						g2.setColor(Color.yellow);
						if (colorKey.equals(tmp.get(tmp.size() - 1))) {
							g2.setColor(Color.magenta);
						}

						g2.drawLine(x1, y1, x2, y2);

						if (img != null) {
							repaint();
						}
					}
				}

				var cap = "진료소,병원,주거지".split(",");
				for (var item : itemList) {
					var img = ImageIO.read(new File("./datafiles/맵아이콘/" + cap[toInt(item.info.get(5))] + ".png"));

					g2.setColor(Color.red);
					g2.drawImage(img, item.x, item.y, 40, 40, null);
					g2.drawString(item.info.get(1).toString(),
							(item.x + 20) - g2.getFontMetrics().stringWidth(item.info.get(1).toString()) / 2,
							item.y - 5);
				}

				if (img != null) {
					repaint();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void center(int x, int y) {
			zoom = 1;
			myAffine.setToIdentity();
			myAffine.scale(zoom, zoom);

			fromPoint = new Point(0, 0);
			toPoint = new Point(getWidth() / 2 - x, getHeight() / 2 - y);

			try {
				sAffPoint = myAffine.inverseTransform(fromPoint, null);
				eAffPoint = myAffine.inverseTransform(toPoint, null);

				affDx = eAffPoint.getX() - sAffPoint.getX();
				affDy = eAffPoint.getY() - sAffPoint.getY();

				myAffine.translate(affDx, affDy);
			} catch (NoninvertibleTransformException e) {
				e.printStackTrace();
			}

			repaint();

			fromPoint = toPoint;
			toPoint = fromPoint;
		}
	}

	class MapItem {
		ArrayList<Object> info;
		int x;
		int y;

		public MapItem(ArrayList<Object> info, int x, int y) {
			super();
			this.info = info;
			this.x = x;
			this.y = y;
		}
	}

	class Node {
		int no;
		int cost;
		String name;

		public Node(int no, int cost, String name) {
			super();
			this.no = no;
			this.cost = cost;
			this.name = name;
		}
	}
}
