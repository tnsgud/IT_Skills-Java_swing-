package view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import javax.imageio.ImageIO;
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
import javax.swing.border.MatteBorder;
import javax.swing.plaf.metal.MetalToggleButtonUI;

public class SearchPage extends BasePage {
	
	West west;
	Map map;

	ArrayList<Integer> paths;
	ArrayList<Object[]> objList;
	ArrayList<ArrayList<Object[]>> adjList;
	Entry<String, String> selectEntry;

	AffineTransform aff = new AffineTransform();

	String colorKey;
	int uno = toInt(user.get(0));

	public SearchPage() {
		
		dataInit();

		add(west = new West(), "West");
		add(map = new Map());

		sz(west, 250, 0);
	}

	void dataInit() {
		objList = new ArrayList<Object[]>();
		adjList = new ArrayList<ArrayList<Object[]>>();

		for (int i = 0; i <= toInt(getOne("select count(*) from building")); i++) {
			adjList.add(new ArrayList<>());
		}

		for (var r : getRows(
				"select c.node1, c.node2, b1.x, b1.y, b2.x, b2.y, c.name from connection c, building b1, building b2 where c.node1 = b1.no and c.node2 = b2.no")) {
			int node1 = toInt(r.get(0)), node2 = toInt(r.get(1));
			int x1 = toInt(r.get(2)), y1 = toInt(r.get(3)), x2 = toInt(r.get(4)), y2 = toInt(r.get(5));
			String nodeName = r.get(6).toString();
			int cost = (int) Point.distance(x1, y1, x2, y2);
			adjList.get(node1).add(new Object[] { node2, cost, nodeName });
			adjList.get(node2).add(new Object[] { node1, cost, nodeName });
		}

		for (var r : getRows(
				"select b.*, ifnull((select round(avg(rate), 0) from rate where building = b.no), 0) from building b where type <> 3")) {
			int x = toInt(r.get(6)), y = toInt(r.get(7));
			objList.add(new Object[] { r, x - 20, y - 20 });
		}
	}

	void findPath() {
		colorKey = "";
		west.routePanel.removeAll();
		paths = new ArrayList<Integer>();

		if (west.departTxt.getText().isEmpty() || west.arriveTxt.getText().isEmpty()) {
			return;
		}

		int start = toInt(west.departTxt.getName());
		int end = toInt(west.arriveTxt.getName());
		int dist[][] = new int[2][adjList.size() + 1];

		for (int i = 0; i < dist[0].length; i++) {
			dist[0][i] = Integer.MAX_VALUE;
			dist[1][i] = -1;
		}

		var que = new PriorityQueue<Object[]>((o1, o2) -> Integer.compare(toInt(o1[1]), toInt(o2[1])));
		que.offer(new Object[] { start, 0 });
		dist[1][start] = 0;
		while (!que.isEmpty()) {
			var cur = que.poll();
			if (dist[0][toInt(cur[0])] < toInt(cur[1]))
				continue;
			
			for (int i = 0; i < adjList.get(toInt(cur[0])).size(); i++) {
				var next = adjList.get(toInt(cur[0])).get(i);
				if (dist[0][toInt(next[0])] > toInt(cur[1]) + toInt(next[1])) {
					dist[0][toInt(next[0])] = toInt(cur[1]) + toInt(next[1]);
					dist[1][toInt(next[0])] = toInt(cur[0]);
					que.offer(new Object[] { toInt(next[0]), dist[0][toInt(next[0])] });
				}
			}
		}

		int arv = start, dep = end;
		while (dep != arv) {
			paths.add(dep);
			dep = dist[1][dep];
		}
		paths.add(arv);
		Collections.reverse(paths);

		var tmp = new ArrayList<String[]>();
		for (int i = 0; i < paths.size() - 1; i++) {
			int n1 = paths.get(i);
			int n2 = paths.get(i + 1);
			var node = adjList.get(n1).stream().filter(x -> toInt(x[0]) == n2).findFirst().get();

			if (!tmp.stream().filter(x -> x[0].equals(tmp.size() + ". " + node[2].toString())).findFirst()
					.isPresent()) {
				tmp.add(new String[] { (tmp.size() + 1) + ". " + node[2].toString(), node[1].toString() });
			} else {
				var str = tmp.get(tmp.size() - 1);
				var cost = toInt(str[1]) + toInt(node[1]);
				tmp.set(tmp.size() - 1, new String[] { str[0], cost + "" });
			}
		}

		int tot = tmp.stream().mapToInt(x -> toInt(x[1])).sum();
		west.routePanel.add(new JLabel("총 거리:" + tot + "m", 4));

		for (int i = 0; i < tmp.size(); i++) {
			var txt = "<html><font color='black'>";
			if (i == 0)
				txt = "<html><font color='red'>출발 </font><font color='black'>";
			else if (i == tmp.size() - 1)
				txt = "<html><font color='blue'>도착 </font><font color='black'>";

			int idx = i;
			var lbl = hyplbl(txt + tmp.get(i)[0] + " 총 " + tmp.get(i)[1] + "m", 0, 12, Color.orange, e -> {
				colorKey = tmp.get(idx)[0].toString();
				map.drawMap();
			});

			lbl.setBorder(new MatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
			west.routePanel.add(lbl);
		}

		map.drawMap();
		west.tog[1].doClick();
	}

	class West extends JPanel {
		JPanel n, s, nc, ns;
		JPanel searchPanel, pathPanel, routePanel;
		JPanel pathPanelN, pathPanelNc, pathPanelNs;
		JScrollPane jsc;
		JTextField searchTxt, departTxt, arriveTxt;
		JToggleButton tog[] = { new JToggleButton("검색"), new JToggleButton("길찾기") };
		ButtonGroup bg = new ButtonGroup();

		public West() {
			super(new BorderLayout());

			add(n = new JPanel(new BorderLayout()), "North");
			add(jsc = new JScrollPane(searchPanel = new JPanel(new BorderLayout())));
			add(s = new JPanel(new FlowLayout(0)), "South");

			n.add(nc = new JPanel(new BorderLayout(5, 5)));
			n.add(ns = new JPanel(new GridLayout(1, 0, 5, 5)), "South");
			s.add(hyplbl("메인으로", 2, 15, Color.orange, e -> mf.swapPage(new MainPage())));

			nc.add(searchTxt = new JTextField());
			nc.add(btn("검색", e -> search()), "East");

			for (int i = 0; i < tog.length; i++) {
				ns.add(tog[i]);
				bg.add(tog[i]);

				tog[i].setBackground(blue);
				tog[i].setForeground(Color.WHITE);
				tog[i].setUI(new MetalToggleButtonUI() {
					protected Color getSelectColor() {
						return blue.darker();
					};
				});
				tog[i].addActionListener(e -> {
					if (e.getActionCommand().equals("검색")) {
						jsc.setViewportView(searchPanel);
					} else {
						jsc.setViewportView(pathPanel);
					}
				});
				sz(tog[i], 0, 50);
			}

			pathPanel = new JPanel(new BorderLayout());
			pathPanel.add(pathPanelN = new JPanel(new BorderLayout()), "North");
			pathPanel.add(routePanel = new JPanel(new GridLayout(0, 1)));

			pathPanelN.add(pathPanelNc = new JPanel(new GridLayout(0, 1, 5, 5)));
			pathPanelN.add(pathPanelNs = new JPanel(new FlowLayout(2)), "South");
			pathPanelN.add(btn("↑↓", e -> {
				var name = departTxt.getName();
				var txt = departTxt.getText();

				departTxt.setName(arriveTxt.getName());
				departTxt.setText(arriveTxt.getText());
				arriveTxt.setName(name);
				arriveTxt.setText(txt);
			}), "East");

			pathPanelNc.add(departTxt = new JTextField());
			pathPanelNc.add(arriveTxt = new JTextField());
			pathPanelNs.add(btn("집을 출발지로", e -> {
				departTxt
						.setName(getOne("select b.no from building b, user u where u.building = b.no and u.no=" + uno));
				departTxt.setText(
						getOne("select b.name from building b, user u where u.building = b.no and u.no=" + uno));
				findPath();
			}));

			departTxt.setEditable(false);
			arriveTxt.setEditable(false);

			n.setBackground(blue);
			nc.setOpaque(false);
			ns.setOpaque(false);
			n.setBorder(new EmptyBorder(5, 5, 5, 5));
			pathPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
			tog[0].doClick();
		}

		void search() {
			searchPanel.removeAll();
			tog[0].doClick();

			if (searchTxt.getText().isEmpty()) {
				eMsg("검색 키워드를 입력하세요.");
				return;
			}

			var rs = getRows(
					"select *, (select ifnull(round(avg(rate), 1), 0) from rate where building = b.no) from building b where b.name like ?",
					"%" + searchTxt.getText() + "%");
			if (rs.isEmpty() || rs.get(1).toString().contains("도로")) {
				eMsg("검색 결과가 없습니다.");
				return;
			}

			searchPanel.add(
					lbl("<html>장소명 <font color=rgb(0,123,255)>" + searchTxt.getText() + "</font> 의 검색 결과", 2, 15),
					"North");
			var tmp = new JPanel(new GridLayout(0, 1));
			for (var r : rs) {
				map.goCenter(toInt(rs.get(0).get(6)), toInt(rs.get(0).get(7)));

				var tmp1 = new JPanel(new BorderLayout(5, 5));
				var tmp2 = new JPanel(new BorderLayout(5, 5));

				tmp.add(tmp1);
				tmp1.add(tmp2);

				tmp1.add(hyplbl("<html><font color='black'>" + (rs.indexOf(r) + 1) + ":" + r.get(1), 2, 15,
						Color.orange, e -> {
							if (r.get(5).toString().equals("2")) {
								return;
							}

							new InfoDialog(r, uno, SearchPage.this).setVisible(true);
						}), "North");
				tmp2.add(lbl("<html>" + r.get(4).toString(), 2, 13));
				tmp2.add(new JLabel(getIcon(r.get(8), 75, 80)), "East");
				tmp1.add(lbl("평점: " + r.get(9), 2, 13), "South");
				tmp1.setBorder(new LineBorder(Color.LIGHT_GRAY));
				tmp1.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						if (e.getButton() == 1 && e.getClickCount() == 2) {
							map.goCenter(toInt(r.get(6)), toInt(r.get(7)));
						}
					}
				});

				var pop = new JPopupMenu();

				for (var i : "출발지,도착지".split(",")) {
					var item = new JMenuItem(i);
					item.addActionListener(e -> {
						if (e.getActionCommand().equals("출발지")) {
							if (west.arriveTxt.getText().equals(r.get(1).toString())) {
								eMsg("출발지와 도착지는 같을 수 없습니다.");
								return;
							}

							west.departTxt.setName(r.get(0).toString());
							west.departTxt.setText(r.get(1).toString());
						} else {
							if (west.departTxt.getText().equals(r.get(1).toString())) {
								eMsg("출발지와 도착지는 같을 수 없습니다.");
								return;
							}

							west.arriveTxt.setName(r.get(0).toString());
							west.arriveTxt.setText(r.get(1).toString());
						}

						findPath();
					});
					pop.add(item);
				}

				tmp1.setComponentPopupMenu(pop);
			}

			while (tmp.getComponentCount() < 3)
				tmp.add(new JLabel());

			searchPanel.add(tmp);
		}
	}

	class Map extends JPanel {
		BufferedImage img;
		JPanel w;
		JPopupMenu pop = new JPopupMenu();

		double targetX = 0, targetY = 0, startX, startY;
		double zoom = 1;
		Point2D p2d;

		public Map() {
			super(new BorderLayout());

			add(w = new JPanel(new GridBagLayout()), "West");
			var lbl = sz(new JLabel() {
				String text = "◀";

				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					Graphics2D g2d = (Graphics2D) g;
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2d.setColor(Color.white);
					g2d.fillRoundRect(0, 0, 20, 30, 5, 5);
					g2d.setColor(blue);
					g2d.drawString(text, 5, 20);
				}
			}, 30, 35);
			var ma = new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					try {
						p2d = aff.inverseTransform(e.getPoint(), null);
					} catch (NoninvertibleTransformException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					startX = p2d.getX();
					startY = p2d.getY();

					int x = (int) startX;
					int y = (int) startY;
					var item = objList.stream().filter(obj -> (x >= toInt(obj[1]) && x <= toInt(obj[1]) + 40)
							&& (y >= toInt(obj[2]) && y <= toInt(obj[2]) + 40)).findFirst();

					if (item.isPresent()) {
						var building = (ArrayList<Object>) item.get()[0];
						if (e.getButton() == 1) {
							if (toInt(building.get(5)) != 2) {
								new InfoDialog(building, uno, SearchPage.this).setVisible(true);
							}
						} else if (e.getButton() == 3) {
							selectEntry = java.util.Map.entry(building.get(0).toString(), building.get(1).toString());
							pop.show(map, e.getX(), e.getY());
						}
					}
				}

				@Override
				public void mouseDragged(MouseEvent e) {
					try {
						p2d = aff.inverseTransform(e.getPoint(), null);
					} catch (NoninvertibleTransformException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					double diffX = p2d.getX() - startX;
					double diffY = p2d.getY() - startY;

					targetX += diffX;
					targetY += diffY;

					startX = p2d.getX();
					startY = p2d.getY();

					repaint();
				}

				@Override
				public void mouseWheelMoved(MouseWheelEvent e) {
					try {
						p2d = aff.inverseTransform(e.getPoint(), null);
					} catch (NoninvertibleTransformException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					boolean flag = false;
					if (e.getPreciseWheelRotation() < 0) {
						if (zoom == 2) {
							flag = true;
						} else
							zoom = Math.min(2, zoom + 0.1);
					} else if (zoom == 0.1) {
						flag = true;
					} else {
						zoom = Math.max(0.1, zoom - 0.1);
					}

					if (!flag) {
						aff.setToIdentity();
						aff.translate(e.getX(), e.getY());
						aff.scale(zoom, zoom);
						aff.translate(-p2d.getX(), -p2d.getY());
						repaint();
					}
				}
			};

			addMouseListener(ma);
			addMouseMotionListener(ma);
			addMouseWheelListener(ma);
			lbl.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					if (lbl.text.equals("◀")) {
						lbl.text = "▶";
						sz(west, 0, 0);
					} else {
						lbl.text = "◀";
						sz(west, 250, 0);
					}

					repaint();
					revalidate();
				};
			});

			for (var i : "출발지,도착지".split(",")) {
				var item = new JMenuItem(i);
				item.addActionListener(e -> {
					if (e.getActionCommand().equals("출발지")) {
						if (west.arriveTxt.getText().equals(selectEntry.getValue())) {
							eMsg("출발지와 도착지는 같을 수 없습니다.");
							return;
						}

						west.departTxt.setName(selectEntry.getKey());
						west.departTxt.setText(selectEntry.getValue());
					} else {
						if (west.departTxt.getText().equals(selectEntry.getValue())) {
							eMsg("출발지와 도착지는 같을 수 없습니다.");
							return;
						}

						west.arriveTxt.setName(selectEntry.getKey());
						west.arriveTxt.setText(selectEntry.getValue());
					}

					findPath();
				});
				pop.add(item);
			}

			w.add(lbl);

			drawMap();

			w.setBackground(new Color(0, 0, 0, 0));
			setBackground(new Color(153, 217, 234));
		}

		void drawMap() {
			try {
				img = ImageIO.read(new File("datafiles/map.jpg"));

				Graphics2D g2d = (Graphics2D) img.getGraphics();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setStroke(new BasicStroke(8, 1, 1));

				if (paths != null) {
					ArrayList<String> tmp = new ArrayList<String>();
					for (int i = 0; i < paths.size() - 1; i++) {
						int n1 = paths.get(i);
						int n2 = paths.get(i + 1);
						int x1 = 0, y1 = 0, x2 = 0, y2 = 0;
						var node = adjList.get(n1).stream().filter(x -> toInt(x[0]) == n2).findFirst().get();

						if (!tmp.contains(tmp.size() + ". " + node[2])) {
							tmp.add((tmp.size() + 1) + ". " + node[2]);
						}

						var rs = getRows("select x,y from building where no=?", n1);
						x1 = toInt(rs.get(0).get(0));
						y1 = toInt(rs.get(0).get(1));
						rs = getRows("select x,y from building where no=?", n2);
						x2 = toInt(rs.get(0).get(0));
						y2 = toInt(rs.get(0).get(1));

						g2d.setColor(Color.yellow);
						if (colorKey.equals(tmp.get(tmp.size() - 1))) {
							g2d.setColor(Color.magenta);
						}
						g2d.drawLine(x1, y1, x2, y2);
					}
				}

				var str = "진료소,병원,주거지".split(",");
				for (var r : objList) {
					var building = (ArrayList<Object>) r[0];
					int x = (int) r[1], y = (int) r[2];
					var img = ImageIO.read(new File("datafiles/맵아이콘/" + str[toInt(building.get(5))] + ".png"));
					g2d.setColor(Color.red);
					g2d.drawImage(img, x, y, 40, 40, null);
					g2d.drawString(building.get(1).toString(),
							(x + 20) - g2d.getFontMetrics().stringWidth(building.get(1).toString()) / 2, y - 5);
				}

				if (map != null)
					map.repaint();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		void goCenter(int x, int y) {
			try {
				p2d = aff.inverseTransform(new Point((getWidth() - map.getWidth()) / 2 - (x - map.getWidth() / 2),
						(getHeight() - map.getHeight()) / 2 - (y - map.getHeight() / 2)), null);
			} catch (NoninvertibleTransformException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			targetX = p2d.getX();
			targetY = p2d.getY();
			repaint();
			revalidate();
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			aff.translate(targetX, targetY);
			g2d.drawImage(img, aff, null);
		}
	}
}
