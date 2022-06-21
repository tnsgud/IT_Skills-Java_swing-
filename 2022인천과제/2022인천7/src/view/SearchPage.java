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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Map.Entry;

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

	ArrayList<Integer> paths = new ArrayList<>();// list, x, y
	ArrayList<Object[]> objs = new ArrayList<>(); // no, cost, name
	ArrayList<ArrayList<Object[]>> adjList = new ArrayList<>();
	Entry<String, String> select;

	AffineTransform aff = new AffineTransform();
	String colorKey = "";
	int uno = 1;
//	int uno = toInt(user.get(0));

	public SearchPage() {
		data();

		add(west = sz(new West(), 250, 0), "West");
		add(map = new Map());
	}

	private void data() {
		var cnt = toInt(getOne("select count(*) from building"));
		for (int i = 0; i < cnt + 1; i++) {
			adjList.add(new ArrayList<>());
		}

		for (var rs : getRows(
				"select c.node1, c.node2, b1.x, b1.y, b2.x, b2.y, c.name from connection c, building b1, building b2 where c.node1 = b1.no and c.node2 = b2.no")) {
			int no1 = toInt(rs.get(0)), no2 = toInt(rs.get(0));
			int x1 = toInt(rs.get(2)), y1 = toInt(rs.get(3)), x2 = toInt(rs.get(4)), y2 = toInt(rs.get(5));
			int cost = (int) Point.distance(x1, y1, x2, y2);
			var name = rs.get(6).toString();

			adjList.get(no1).add(new Object[] { no2, cost, name });
			adjList.get(no2).add(new Object[] { no1, cost, name });
		}

		for (var rs : getRows(
				"select b.*, ifnull(round(avg(rate), 0), 0) from building b left join rate r on b.no = r.building where type <> 3")) {
			int x = toInt(rs.get(6)), y = toInt(rs.get(7));
			objs.add(new Object[] { rs, x - 20, y - 20 });
		}
	}

	private void findPath() {
		colorKey = "";
		west.cc.removeAll();

		paths.clear();

		if (west.deTxt.getText().isEmpty() || west.arTxt.getText().isEmpty()) {
			return;
		}

		int start = toInt(west.deTxt.getName());
		int end = toInt(west.arTxt.getName());
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
					dist[1][toInt(next[0])] = toInt(cur[1]);
					que.offer(new Object[] { toInt(next[0]), dist[0][toInt(next[0])] });
				}
			}
		}

		int arv = start, dep = end;
		while (arv != dep) {
			paths.add(dep);
			dep = dist[1][dep];
		}

		paths.add(arv);
		Collections.reverse(paths);

		var tmp = new ArrayList<String[]>();
		for (int i = 0; i < paths.size() - 1; i++) {
			int n1 = paths.get(i), n2 = paths.get(i + 1);
			var node = adjList.get(n1).stream().filter(x -> toInt(x[0]) == n2).findFirst().get();

			if (!tmp.stream().filter(x -> x[0].equals(tmp.size() + ". " + node[2])).findFirst().isPresent()) {
				tmp.add(new String[] { tmp.size() + ". " + node[2], node[1].toString() });
			} else {
				var str = tmp.get(tmp.size() - 1);
				var cost = toInt(str[1]) + toInt(node[1]);
				tmp.set(tmp.size() - 1, new String[] { str[0], cost + "" });
			}
		}

		int tot = tmp.stream().mapToInt(x -> toInt(x[1])).sum();
		west.cc.add(lbl("총 거리:" + tot, 4));

		for (int i = 0; i < tmp.size(); i++) {
			var txt = "<html><font color='black'>";

			if (i == 0) {
				txt = "<html><font color='red'>출발</font>";
			} else if (i == tmp.size() - 1) {
				txt = "<html><font color='blue'>도착</font>";
			}

			int idx = i;
			var lbl = hyplbl(txt + tmp.get(i)[0] + " 총 " + tmp.get(i)[1] + "m", 0, 12, Color.black, e -> {
				colorKey = tmp.get(idx)[0];

				map.drawMap();
			});
			lbl.setBorder(new MatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

			west.cc.add(lbl);
		}

		map.drawMap();
		west.tog[1].doClick();
	}

	class West extends BasePage {
		JTextField srhTxt, deTxt, arTxt;
		JToggleButton tog[] = new JToggleButton[2];
		JScrollPane scr;
		JPanel search, path;

		public West() {
			add(n = new JPanel(new BorderLayout(5, 5)), "North");
			add(scr = new JScrollPane(search = new JPanel(new BorderLayout())));
			add(hyplbl("메인으로", 2, 20, Color.orange, e -> mf.swapPage(new MainPage())), "South");

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
					protected Color getSelectColor() {
						return blue.darker();
					};
				});
				tog[i].addActionListener(a -> {
					if (a.getActionCommand().equals("검색")) {
						scr.setViewportView(search);
					} else {
						scr.setViewportView(path);
					}
				});
			}

			path = new JPanel(new BorderLayout(5, 5));

			path.add(cn = new JPanel(new BorderLayout(5, 5)), "North");
			path.add(cc = new JPanel(new GridLayout(0, 1)));

			{
				var tmp1 = new JPanel(new GridLayout(0, 1, 5, 5));
				var tmp2 = new JPanel(new FlowLayout(2));

				tmp1.add(deTxt = new JTextField());
				tmp1.add(arTxt = new JTextField());

				tmp2.add(btn("집을 출발지로", a -> {
					var rs = getRows("select b.no, b.name from building b, user u where b.no = u.building and u.no=?",
							uno).get(0);
					deTxt.setName(rs.get(0).toString());
					deTxt.setText(rs.get(1).toString());
				}));

				cn.add(tmp1);
				cn.add(btn("↑↓", a -> {
				}), "East");
				cn.add(tmp2, "South");
			}

			tog[0].doClick();

			n.setBorder(new EmptyBorder(5, 5, 5, 5));
			n.setBackground(blue);
			ns.setOpaque(false);
			cn.setBorder(new EmptyBorder(5, 5, 5, 5));
		}

		private void search() {
			search.removeAll();
			tog[0].doClick();

			if (srhTxt.getText().isEmpty()) {
				eMsg("검색 키워드를 입력하세요.");
				return;
			}

			var rs = getRows(
					"select b.*, ifnull(round(avg(rate), 0), 0) from building b left join rate r on b.no = r.building where b.name like ? and type <> 3",
					"%" + srhTxt.getText() + "%");

			if (rs.isEmpty() || rs.get(0).get(0) == null) {
				eMsg("검색 결과가 없습니다.");
				return;
			}

			search.add(lbl("<html>장소명 <font color=rgb(0,123,255)>" + srhTxt.getText() + "</font> 의 검색 결과", 2, 15),
					"North");
			map.goCenter(toInt(rs.get(0).get(6)), toInt(rs.get(0).get(7)));
			var tmp = new JPanel(new GridLayout(0, 1));
			for (var r : rs) {
				var temp = new JPanel(new BorderLayout(5, 5));

				temp.add(hyplbl(rs.indexOf(r) + 1 + ":" + r.get(1), 2, 15, Color.black, e -> {
					if (toInt(r.get(5)) == 2) {
						return;
					}

					new InfoDialog(r).setVisible(true);
				}), "North");
				temp.add(lbl("<html>" + r.get(4), 2));
				temp.add(new JLabel(getIcon(r.get(8), 80, 80)), "East");
				temp.add(lbl("평점:" + r.get(9), 2), "South");
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
							if (arTxt.getText().equals(r.get(1).toString())) {
								eMsg("출발지와 도착지가 같을 수 없습니다.");
								return;
							}

							deTxt.setName(r.get(0).toString());
							deTxt.setText(r.get(1).toString());
						} else {
							if (deTxt.getText().equals(r.get(1).toString())) {
								eMsg("출발지와 도착지가 같을 수 없습니다.");
								return;
							}

							arTxt.setName(r.get(0).toString());
							arTxt.setText(r.get(1).toString());
						}

						findPath();
					});
					pop.add(item);
				}

				temp.setBorder(new LineBorder(Color.LIGHT_GRAY));
				temp.setComponentPopupMenu(pop);

				tmp.add(temp);
			}

			while (tmp.getComponentCount() < 3) {
				tmp.add(lbl("", 0));
			}

			search.add(tmp);
		}
	}
	
	public static void main(String[] args) {
		mf.swapPage(new SearchPage());
		mf.setVisible(true);
	}

	class Map extends BasePage {
		BufferedImage img;
		JPopupMenu pop = new JPopupMenu();

		double targetX = 0, targetY = 0, startX, startY;
		double zoom = 1;
		Point2D p;

		public Map() {
			add(w = new JPanel(new GridBagLayout()), "West");

			var lbl = sz(new JLabel() {
				String txt = "◀";

				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					var g2 = (Graphics2D) g;

					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2.setColor(Color.white);
					g2.fillRoundRect(0, 0, 20, 30, 5, 5);
					g2.setColor(blue);
					g2.drawString(txt, 5, 20);
				}
			}, 35, 35);
			lbl.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (lbl.txt.equals("◀")) {
						lbl.txt = "▶";
						sz(w, 0, 0);
					} else {
						lbl.txt = "◀";
						sz(w, 250, 0);
					}

					repaint();
					revalidate();
				}
			});
			w.add(lbl);

			var ma = new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					try {
						p = aff.inverseTransform(e.getPoint(), null);
					} catch (NoninvertibleTransformException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					startX = p.getX();
					startY = p.getY();

					int x = (int) startX;
					int y = (int) startY;

					var item = objs.stream().filter(obj -> (x >= toInt(obj[1]) && x <= toInt(obj[1]) + 40)
							&& (y >= toInt(obj[2]) && y <= toInt(obj[1]) + 40)).findFirst();

					if (item.isPresent()) {
						var building = (ArrayList<Object>) item.get()[0];

						if (e.getButton() == 1) {
							if (toInt(building.get(5)) == 2) {
								return;
							}

							new InfoDialog(building).setVisible(true);
						} else if (e.getButton() == 3) {
							select = java.util.Map.entry(building.get(0).toString(), building.get(1).toString());
							pop.show(map, e.getX(), e.getY());
						}
					}
				}

				@Override
				public void mouseDragged(MouseEvent e) {
					try {
						p = aff.inverseTransform(e.getPoint(), null);
					} catch (NoninvertibleTransformException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					var diffX = p.getX() - startX;
					var diffY = p.getY() - startY;

					targetX += diffX;
					targetY += diffY;

					startX = p.getX();
					startY = p.getY();

					repaint();
				}

				@Override
				public void mouseWheelMoved(MouseWheelEvent e) {
					try {
						p = aff.inverseTransform(e.getPoint(), null);
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
					} else if (zoom == 0.1) {
						flag = true;
					} else {
						zoom = Math.max(0.1, zoom - 0.1);
					}

					if (!flag) {
						aff.setToIdentity();
						aff.translate(e.getX(), e.getY());
						aff.scale(zoom, zoom);
						aff.translate(-p.getX(), -p.getY());
						repaint();
					}
				}
			};
			addMouseListener(ma);
			addMouseMotionListener(ma);
			addMouseWheelListener(ma);

			drawMap();

			w.setOpaque(false);
			setBackground(new Color(153, 217, 234));
		}

		public void goCenter(int x, int y) {
			try {
				p = aff.inverseTransform(new Point((getWidth() - map.getWidth()) / 2 - (x - map.getWidth() / 2),
						(getHeight() - map.getHeight()) / 2 - (y - map.getWidth() / 2)), null);
			} catch (NoninvertibleTransformException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			targetX = p.getX();
			targetY = p.getY();

			repaint();
			revalidate();
		}

		public void drawMap() {
			try {
				img = ImageIO.read(new File("./datafiles/map.jpg"));

				var g2 = (Graphics2D) img.getGraphics();

				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setStroke(new BasicStroke(8, 1, 1));

				if (!paths.isEmpty()) {
					var tmp = new ArrayList<String>();

					for (int i = 0; i < paths.size() - 1; i++) {
						int n1 = paths.get(i), n2 = paths.get(i + 1);
						int x1 = 0, y1 = 0, x2 = 0, y2 = 0;
						var node = adjList.get(n1).stream().filter(x -> toInt(x[0]) == n2).findFirst().get();

						if (!tmp.contains(tmp.size() + ". " + node[2])) {
							tmp.add(tmp.size() + 1 + ". " + node[2]);
						}

						var rs = getRows("select x, y from building where no=?", n1).get(0);
						x1 = toInt(rs.get(0));
						y1 = toInt(rs.get(1));
						rs = getRows("select x, y from building where no=?", n2).get(0);
						x2 = toInt(rs.get(0));
						y2 = toInt(rs.get(1));

						g2.setColor(Color.yellow);
						if (colorKey.equals(tmp.get(tmp.size() - 1))) {
							g2.setColor(Color.magenta);
						}
						g2.drawLine(x1, y1, x2, y2);

					}
				}

				var cap = "진료소,병원,거주지".split(",");
				for (var obj : objs) {
					var building = (ArrayList<Object>) obj[0];
					int x = toInt(obj[1]), y = toInt(obj[2]);
					System.out.println("./datafiles/맵아이콘/" + cap[toInt(building.get(5))] + ".png");
					var img = ImageIO.read(new File("./datafiles/맵아이콘/" + cap[toInt(building.get(5))] + ".png"));

					g2.setColor(Color.red);
					g2.drawImage(img, x, y, 40, 40, null);
					g2.drawString(building.get(1).toString(),
							(x + 20) - g2.getFontMetrics().stringWidth(building.get(1).toString()) / 2, y - 5);
				}

				if (map != null) {
					repaint();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			var g2 = (Graphics2D) g;
			aff.translate(targetX, targetY);
			g2.drawImage(img, aff, null);
		}
	}
}
