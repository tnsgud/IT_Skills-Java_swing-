package view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.PriorityQueue;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.metal.MetalToggleButtonUI;

import tool.Tool;

public class SearchPage extends BasePage {
	BufferedImage image;
	double affTargetX = 0, affTargetY = 0;
	AffineTransform aff = new AffineTransform();
	MapView map;
	String colorKey = "";
	SearchPanel searchPanel;
	ArrayList<Integer> path;
	ArrayList<ArrayList<Node>> adjList;
	ArrayList<MapObject> objList = new ArrayList<SearchPage.MapObject>();

	class MapObject {
		int type;
		int x;
		int y;
		int width;
		int height;

		ArrayList<Object> building;

		public MapObject(int type, int x, int y, int width, int height, ArrayList<Object> building) {
			this.type = type;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.building = building;
		}

		int getRight() {
			return x + width;
		}

		int getBottom() {
			return y + height;
		}

	}

	public SearchPage() {
		super();
		user = getRows("select * from user where no=?", 1).get(0);
		datainit();
		ui();
	}

	void datainit() {
		adjList = new ArrayList<ArrayList<Node>>();
		for (int i = 0; i < toInt(getRows("SELECT count(*) FROM building").get(0).get(0)) + 1; i++) {
			adjList.add(new ArrayList<Node>());
		}
		var rs = getRows(
				"select c.node1, c.node2, b1.x, b1.y, b2.x, b2.y, c.name from connection c, building b1, building b2 where c.node1 = b1.no and c.node2 = b2.no");
		for (var r : rs) {
			int x1 = toInt(r.get(2));
			int y1 = toInt(r.get(3));
			int x2 = toInt(r.get(4));
			int y2 = toInt(r.get(5));
			int cost = (int) Point.distance(x1, y1, x2, y2);

			adjList.get(toInt(r.get(0))).add(new Node(toInt(r.get(1)), cost, r.get(6) + ""));
			adjList.get(toInt(r.get(1))).add(new Node(toInt(r.get(0)), cost, r.get(6) + ""));
		}

		for (var r : getRows(
				"select b.*, ifnull((select round(avg(rate),0) from rate where building = b.no),0) from building b where type <> 3")) {

			var x = toInt(r.get(6));
			var y = toInt(r.get(7));
			objList.add(new MapObject(toInt(r.get(5)), x - 20, y - 20, 40, 40, r));
		}
	}

	void ui() {
		setLayout(new BorderLayout());
		drawOnMapImage();
		add(map = new MapView());
		add(sz(searchPanel = new SearchPanel(), 280, 0), "West");
	}

	void drawOnMapImage() {
		try {
			image = ImageIO.read(new FileInputStream("./datafiles/map.jpg"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		var g2 = (Graphics2D) image.getGraphics();

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

		var pos = getRows("select no, name, type, x, y from building where type <> 3");
		var d = "진료소,병원,주거지".split(",");
		var itr = pos.iterator();

		itr.next();

		g2.setStroke(new BasicStroke(8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		if (path != null) {
			ArrayList<String> temp = new ArrayList<String>();

			for (int i = 0; i < path.size() - 1; i++) {
				int n1 = path.get(i);
				int n2 = path.get(i + 1);

				g2.setColor(Color.YELLOW);

				var node = adjList.get(n1).get(adjList.get(n1).indexOf(new Node(n2)));
				if (!temp.contains((temp.size()) + ". " + node.name)) {
					temp.add((temp.size() + 1) + ". " + node.name);
				}

				if (colorKey.equals(temp.get(temp.size() - 1))) {
					g2.setColor(Color.MAGENTA);
				}

				var n1pos = getRows("select x, y from building where no = ?", n1);
				var n2pos = getRows("select x, y from building where no = ?", n2);
				int x1 = toInt(n1pos.get(0).get(0));
				int y1 = toInt(n1pos.get(0).get(1));
				int x2 = toInt(n2pos.get(0).get(0));
				int y2 = toInt(n2pos.get(0).get(1));
				if (!temp.contains((temp.size()) + ". " + node.name)) {
					temp.add((temp.size() + 1) + ". " + node.name);
				}

				if (colorKey.equals(temp.get(temp.size() - 1))) {
					g2.setColor(Color.MAGENTA);
				}
				g2.drawLine(x1, y1, x2, y2);
			}
		}

		while (itr.hasNext()) {
			var p = itr.next();
			g2.setColor(Color.RED);
			var x = toInt(p.get(3));
			var y = toInt(p.get(4));
			g2.drawString(p.get(0) + "", x, y);
			BufferedImage img = null;
			try {
				img = ImageIO.read(new File("./datafiles/맵아이콘/" + d[toInt(p.get(2))] + ".png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			g2.drawImage(img, x - 20, y - 20, 40, 40, null);
		}

		if (this.map != null)
			map.repaint();
	}

	void pathFind() {
		colorKey = "";

		if (searchPanel.dep.getName() == null || searchPanel.arrv.getName() == null)
			return;

		int depart = toInt(searchPanel.dep.getName());
		int arrive = toInt(searchPanel.arrv.getName());

		int[][] dist = new int[2][adjList.size() + 1];

		for (int i = 1; i < dist[0].length; i++) {
			dist[0][i] = Integer.MAX_VALUE;
			dist[1][i] = -1;
		}

		var pq = new PriorityQueue<Node>((o1, o2) -> Integer.compare(o1.cost, o2.cost));

		pq.offer(new Node(depart, 0));
		dist[1][depart] = 0;

		while (!pq.isEmpty()) {
			var cur = pq.poll();

			if (dist[0][cur.idx] < cur.cost)
				continue;

			for (int i = 0; i < adjList.get(cur.idx).size(); i++) {
				var next = adjList.get(cur.idx).get(i);
				if (dist[0][next.idx] > cur.cost + next.cost) {
					dist[0][next.idx] = cur.cost + next.cost;
					dist[1][next.idx] = cur.idx; // from...
					pq.offer(new Node(next.idx, dist[0][next.idx]));
				}
			}
		}

		trace(dist, arrive, depart);

	}

	void trace(int dist[][], int arrive, int depart) {

		searchPanel.pathResultPanel.removeAll();
		path = new ArrayList<Integer>();
		// backtracking
		while (arrive != depart) {
			path.add(arrive);
			arrive = dist[1][arrive];
		}

		path.add(arrive);
		Collections.reverse(path);

		searchPanel.togglePath.setEnabled(true);

		ArrayList<String> temp = new ArrayList<String>();
		ArrayList<Integer> cost = new ArrayList<Integer>();

		for (int i = 0; i < path.size() - 1; i++) {
			int n1 = path.get(i);
			int n2 = path.get(i + 1);
			var node = adjList.get(n1).get(adjList.get(n1).indexOf(new Node(n2)));
			if (!temp.contains((temp.size()) + ". " + node.name)) {
				temp.add((temp.size() + 1) + ". " + node.name);
				cost.add(node.cost);
			} else {
				cost.set(temp.size() - 1, cost.get(temp.size() - 1) + node.cost);
			}
		}
		int totalcost = cost.stream().mapToInt(a -> toInt(a)).sum();

		searchPanel.pathResultPanel.add(lbl("총 거리:" + totalcost + "m", JLabel.RIGHT));

		for (int i = 0; i < temp.size(); i++) {
			String text = "";
			if (i == 0) {
				text = "<html><font color = 'red'>출발 </font>";
			} else if (i == temp.size() - 1) {
				text = "<html><font color = 'blue'>도착 </font>";
			}

			final int j = i;
			var hylbl = hyplbl(text + "" + temp.get(i) + " 총 " + cost.get(i) + "m", JLabel.CENTER, 0, 13,
					Color.BLACK, () -> {
						colorKey = temp.get(j);
						drawOnMapImage();
					});
			hylbl.setBorder(new MatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
			searchPanel.pathResultPanel.add(hylbl);

		}

		drawOnMapImage();
		searchPanel.pathResultPanel.revalidate();
		searchPanel.pathResultPanel.repaint();

	}

	class Node {
		int idx; // 자기자신번호
		int cost = 0;
		int direction;
		String name;

		public Node(int idx) {
			this.idx = idx;
		}

		public Node(int idx, int cost) {
			this(idx);
			this.cost = cost;
		}

		public Node(int idx, int cost, String name) {
			this(idx, cost);
			this.name = name;
		}

		public Node(int idx, int cost, String name, int direction) {
			this(idx, cost, name);
			this.direction = direction;
		}

		@Override
		public String toString() {
			return idx + "";
		}

		@Override
		public boolean equals(Object obj) {
			return ((Node) obj).idx == idx;
		}
	}

	public class MapView extends JPanel {
		public MapView() {
			setLayout(new BorderLayout());
			var w = new JPanel(new GridBagLayout());

			add(w, "West");
			final var showToggle = new JLabel() {
				String text = "◀";

				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					var g2 = (Graphics2D) g;
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
					g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

					g2.setColor(new JScrollPane().getBackground());
					g2.fillRoundRect(-2, 0, 18, 28, 5, 5);
					g2.drawRoundRect(-2, 0, 18, 28, 5, 5);
					g2.setColor(new Color(0, 123, 255));
					g2.drawString(text, 4, 18);

				}
			};

			w.add(sz(showToggle, 20, 30));
			w.setBackground(new Color(0, 0, 0, 0));
			showToggle.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (e.getButton() == 1) {
						if (showToggle.text.equals("◀")) {
							sz(searchPanel, 0, 0);
							showToggle.text = "▶";
						} else {
							sz(searchPanel, 280, 0);
							showToggle.text = "◀";
						}
						revalidate();
						repaint();
					}
				}
			});

			var ma = new MouseAdapter() {
				double startAffX, startAffY;
				double zoom = 0.5;
				Point2D curPoint;

				public void mousePressed(MouseEvent e) {
					try {
						curPoint = aff.inverseTransform(e.getPoint(), null);
					} catch (NoninvertibleTransformException te) {
						te.printStackTrace();
					}

					int clickedX = (int) curPoint.getX();
					int clickedY = (int) curPoint.getY();

					var item = objList.stream().filter(obj -> clickedX >= obj.x && clickedX <= obj.getRight()
							&& clickedY >= obj.y && clickedY <= obj.getBottom()).findFirst();

					var menu = new JPopupMenu();
					if (item.isPresent()) {
						// 발견된 값이 있을 시
						for (var i : "출발,도착".split(",")) {
							var it = new JMenuItem(i);
							menu.add(it);
							it.addActionListener(a -> {
								if (a.getActionCommand().equals("출발")) {
									searchPanel.dep.setText(item.get().building.get(1) + "");
									searchPanel.dep.setName(item.get().building.get(0) + "");
								} else {
									searchPanel.arrv.setText(item.get().building.get(1) + "");
									searchPanel.arrv.setName(item.get().building.get(0) + "");
								}
								pathFind();
							});
						}
						if (e.getButton() == 1) {
							new InfoDialog(item.get().building).setVisible(true);
						} else if (e.getButton() == 3) {
							menu.show(MapView.this, e.getX(), e.getY());
						}

					} else {

						System.out.println("Empty");
					}
					startAffX = curPoint.getX();
					startAffY = curPoint.getY();

				}

				public void mouseDragged(MouseEvent e) {
					try {
						curPoint = aff.inverseTransform(e.getPoint(), null);
					} catch (NoninvertibleTransformException te) {
					}

					double affDiffX = curPoint.getX() - startAffX;
					double affDiffY = curPoint.getY() - startAffY;

					affTargetX += affDiffX;
					affTargetY += affDiffY;

					map.repaint();

					startAffX = curPoint.getX();
					startAffY = curPoint.getY();
				}

				@Override
				public void mouseWheelMoved(MouseWheelEvent e) {
					try {
						curPoint = aff.inverseTransform(e.getPoint(), null);
					} catch (NoninvertibleTransformException e1) {
					}

					var flag = false;
					if (e.getPreciseWheelRotation() < 0) {
						if (zoom == 1)
							flag = true;
						else
							zoom = Math.min(1, zoom + 0.1);
					} else if (zoom == 0.1) {
						flag = true;
					} else {
						zoom = Math.max(0.1, zoom - 0.1);
					}

					if (!flag) {
						aff.setToIdentity();
						aff.translate(e.getX(), e.getY());
						aff.scale(zoom, zoom);
						aff.translate(-curPoint.getX(), -curPoint.getY());

						repaint();
					}
				}
			};

			setBackground(new Color(153, 217, 234));
			addMouseListener(ma);
			addMouseMotionListener(ma);
			addMouseWheelListener(ma);
		}

		@Override
		protected void paintComponent(Graphics g) {
			var g2 = (Graphics2D) g;
			super.paintComponent(g2);
			aff.translate(affTargetX, affTargetY);
			g2.drawImage(image, aff, null);
		}

	}

	class SearchPanel extends JPanel {

		JPanel nPanel, nnPanel, ncPanel, sPanel;
		JTextField searchTextField, arrv, dep;
		JButton searchButton;
		JToggleButton toggleSearch, togglePath;

		ButtonGroup toggleGroup;
		JScrollPane resultPane, pathPane;
		JPanel searchPanel, pathPanel, pathResultPanel;

		public SearchPanel() {
			ui();
			events();
			toggleSearch.setSelected(true);
		}

		void ui() {
			setLayout(new BorderLayout());
			add(sz(nPanel = new JPanel(new BorderLayout(5, 5)), 0, 80), "North");
			nPanel.add(nnPanel = new JPanel(new BorderLayout(5, 5)), "North");
			nPanel.add(ncPanel = new JPanel(new GridLayout(1, 0, 5, 5)));

			nnPanel.add(searchTextField = new JTextField(15));
			nnPanel.add(searchButton = btn("검색", a -> {
			}), "East");

			toggleGroup = new ButtonGroup();

			for (var tog : Arrays.asList(toggleSearch = new JToggleButton("검색"),
					togglePath = new JToggleButton("길찾기"))) {
				toggleGroup.add(tog);
				ncPanel.add(tog);
				tog.setBackground(new Color(0, 123, 255));
				tog.setFocusPainted(false);
				tog.setUI(new MetalToggleButtonUI() {
					@Override
					protected Color getSelectColor() {
						return new Color(0, 123, 255).darker();
					}
				});
				tog.setForeground(Color.WHITE);
			}

			add(resultPane = new JScrollPane());
			add(sPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)), "South");

			sPanel.add(hyplbl("메인으로", JLabel.LEFT, 1, 15, Color.orange, () -> mf.swap(new MainPage())));

			searchPanel = new JPanel(new GridBagLayout()); // temp
			pathPanel = new JPanel(new BorderLayout(5, 5)); // temp

			var pathPanelN = new JPanel(new BorderLayout(5, 5));
			var pathPanelNN = new JPanel(new GridLayout(0, 1, 5, 5));
			var pathPanelNS = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			pathPanel.add(pathPanelN, "North");
			pathPanelN.add(pathPanelNN);

			pathPanelNN.add(dep = new JTextField());
			pathPanelNN.add(arrv = new JTextField());
			pathPanelN.add(pathPanelNS, "South");

			pathPanelN.add(btn("↑↓", a -> {
				String temp = dep.getText();
				dep.setText(arrv.getText());
				arrv.setText(temp);
			}), "East");

			pathPanelNS.add(btn("집을 출발지로", a -> {
				var rs = getRows("select no, name from building where no = (select building from user where no = ?);",
						user.get(0));
				dep.setText(rs.get(0).get(1) + "");
				dep.setName(rs.get(0).get(0) + "");
				pathFind();
			}));

			pathPanel.add(pathPane = new JScrollPane(pathResultPanel = new JPanel(new GridLayout(0, 1))));

			dep.setRequestFocusEnabled(false);
			arrv.setRequestFocusEnabled(false);

			resultPane.setViewportView(searchPanel);
			pathPane.setBorder(BorderFactory.createEmptyBorder());

			searchPanel.add(lbl("<html><center>텍스트 필드에 텍스트를 입력하고<br>검색버튼을 누르세요.", 0));
			pathPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
			searchPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

			nPanel.setBackground(new Color(0, 123, 255));
			nnPanel.setOpaque(false);
			ncPanel.setOpaque(false);
			nPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		}

		void events() {
			for (var tog : ncPanel.getComponents()) {
				((JToggleButton) tog).addItemListener(i -> {
					var me = (JToggleButton) i.getSource();
					if (i.getStateChange() == ItemEvent.SELECTED) {
						if (me.getText().equals("검색")) {
							resultPane.setViewportView(searchPanel);
						} else if (me.getText().equals("길찾기")) {
							resultPane.setViewportView(pathPanel);
						}
					}
				});
			}

			searchButton.addActionListener(a -> search());

		}

		void search() {
			searchPanel.removeAll();

			if (searchTextField.getText().trim().isEmpty()) {
				searchPanel.add(new JLabel("<html><center><font color = '#007BFF'>" + "공백이 존제합니다.", 0));
				revalidate();
				repaint();
				return;
			}

			((JToggleButton) ncPanel.getComponents()[0]).setSelected(true);

			var result = getRows(
					"select b.*, ifnull(round(avg(r.rate),1),0) from building b left join rate r on b.no = r.building where name like '%"
							+ searchTextField.getText().trim() + "%' or info like '%" + searchTextField.getText().trim()
							+ "%' group by b.no");

			if (result.isEmpty()) {
				searchPanel.setLayout(new GridBagLayout());
				searchPanel.add(new JLabel(
						"<html><center><font color = '#007BFF'>" + searchTextField.getText() + "</font><br>의 결과가 없어요",
						0));
			} else {
				searchPanel.setLayout(new BorderLayout());
				searchPanel
						.add(lbl("<html>장소명 <font color = '#007BFF'>" + searchTextField.getText() + "</font> 의 검색 결과",
								JLabel.LEFT, 13), "North");

				var resultPanel = new JPanel(new GridLayout(0, 1));
				searchPanel.add(resultPanel = new JPanel(new GridLayout(0, 1)));
				int index = 1;

				// 대충 첫 인덱스로 이동하는 코드가 들어갈 자리//....

				for (var r : result) {

					var pop = new JPopupMenu();
					var temp = new JPanel(new BorderLayout());
					var tempc = new JPanel(new GridLayout(0, 1));

					temp.setComponentPopupMenu(pop);

					for (var item : "출발지,도착지".split(",")) {
						var i = new JMenuItem(item);
						pop.add(i);
						i.addActionListener(evt -> {
							if (evt.getActionCommand().equals("도착지")) {
								arrv.setText(r.get(1) + "");
								arrv.setName(r.get(0) + "");
								if (dep.getText().equals(r.get(1) + "")) {
									eMsg("출발지와 도착지는 같을 수 없습니다.");
									return;
								}
							} else {
								dep.setName(r.get(0) + "");
								dep.setText(r.get(1) + "");
								if (arrv.getText().equals(r.get(1) + "")) {
									eMsg("출발지와 도착지는 같을 수 없습니다.");
									return;
								}
							}

							if (!arrv.getText().isEmpty() && !dep.getText().isEmpty()) {
								pathFind();
							}

						});
					}

					temp.add(tempc);

					temp.add(hyplbl(String.valueOf(index) + ". " + r.get(1).toString(), JLabel.LEFT, 1, 15,
							Color.DARK_GRAY, () -> new InfoDialog(r).setVisible(true)), "North");

					if (r.get(8) != null)
						temp.add(new JLabel(img(r.get(8), 80, 80)), "East");

					tempc.add(lbl("<html>" + r.get(4) + "", JLabel.LEFT));
					temp.add(lbl("평점: " + r.get(9) + "", JLabel.LEFT, 13), "South");
					temp.addMouseListener(new MouseAdapter() {

						@Override
						public void mousePressed(MouseEvent e) {
							if (e.getButton() == 1 && e.getClickCount() == 2) {
								new InfoDialog(r).setVisible(true);
							}
						}

					});

					temp.setBorder(new CompoundBorder(new LineBorder(Color.lightGray), new EmptyBorder(5, 5, 5, 5)));
					resultPanel.add(sz(temp, 180, 120));
					index++;
				}

				if (resultPanel.getComponents().length < 3) {
					while (resultPanel.getComponents().length < 3) {
						resultPanel.add(sz(lbl("", 0), 180, 120));
					}
				}
			}
			revalidate();
			repaint();
		};
	}

	public static void main(String[] args) {
		BasePage.mf.swap(new SearchPage());
		BasePage.mf.setVisible(true);
	}
}