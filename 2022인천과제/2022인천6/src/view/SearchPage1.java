package view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
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

public class SearchPage1 extends BasePage {
	West west;
	Map map;

	ArrayList<Integer> paths = new ArrayList<>();
	ArrayList<Object[]> objs = new ArrayList<>();
	ArrayList<ArrayList<Object[]>> adjList = new ArrayList<>();
	Entry<String, String> select;

	AffineTransform aff = new AffineTransform();

	String colorKey = "";
	int uno = 1;
//	int uno = toInt(user.get(0));

	public SearchPage1() {
		dataInit();

		add(west = sz(new West(), 250, 0), "West");
		add(map = new Map());
	}

	private void dataInit() {
		var cnt = toInt(getOne("select count(*) from building")) + 1;
		for (int i = 0; i < cnt; i++) {
			adjList.add(new ArrayList<>());
		}

		for (var rs : getRows(
				"select c.node1, c.node2, b1.x, b1.y, b2.x, b2.y, c.name from connection c, building b1, building b2 where c.node1 = b1.no and c.node2 = b2.no ")) {
			int no1 = toInt(rs.get(0)), no2 = toInt(rs.get(1));
			int x1 = toInt(rs.get(2)), y1 = toInt(rs.get(3));
			int x2 = toInt(rs.get(4)), y2 = toInt(rs.get(5));
			int cost = (int) Point.distance(x1, y1, x2, y2);
			var name = rs.get(6).toString();

			adjList.get(no1).add(new Object[] { no2, cost, name });
			adjList.get(no2).add(new Object[] { no1, cost, name });
		}

		for (var rs : getRows(
				"select b.*, ifnull(round(avg(rate), 0), 0) from building b left join rate r on b.no = r.building where type <> 3 group by b.no")) {
			int x = toInt(rs.get(6)), y = toInt(rs.get(7));
			objs.add(new Object[] { rs, x - 20, y - 20 });
		}

		System.out.println(objs.size());
	}

	private void findPath() {
		colorKey = "";
		west.cc.removeAll();

		paths.clear();

		if (west.deTxt.getText().isEmpty() || west.arrTxt.getText().isEmpty()) {
			return;
		}

		int start = toInt(west.deTxt.getName());
		int end = toInt(west.arrTxt.getName());
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
			if (dist[0][toInt(cur[0])] < toInt(cur[1])) {
				continue;
			}

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
			int n1 = paths.get(i), n2 = paths.get(i + 1);
			var node = adjList.get(n1).stream().filter(x -> toInt(x[0]) == n2).findFirst().get();

			if (!tmp.stream().filter(x -> x[0].equals(tmp.size() + ". " + node[2].toString())).findFirst()
					.isPresent()) {
				tmp.add(new String[] { tmp.size() + 1 + ". " + node[2].toString(), node[1].toString() });
			} else {
				var str = tmp.get(tmp.size() - 1);
				var cost = toInt(str[1]) + toInt(node[1]);
				tmp.set(tmp.size() - 1, new String[] { str[0], cost + "" });
			}
		}

		int tot = tmp.stream().mapToInt(x -> toInt(x[1])).sum();
		west.cc.add(lbl("총 거리:" + tot + "m", 4));

		for (int i = 0; i < tmp.size(); i++) {
			var txt = "<html><font color='black'>";

			if (i == 0) {
				txt = "<html><font color='red'>출발 </font><font color='black'>";
			} else if (i == tmp.size() - 1) {
				txt = "<html><font color='blue'>도착 </font><font color='black'>";
			}

			int idx = i;
			var lbl = hyplbl(txt + tmp.get(i)[0] + " 총 " + tmp.get(i)[1] + "m", 0, 12, Color.orange, e -> {
				colorKey = tmp.get(idx)[0];
				try {
					map.drawMap();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			});
			lbl.setBorder(new MatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

			west.cc.add(lbl);
		}

		try {
			map.drawMap();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		west.tog[1].doClick();
	}

	class West extends BasePage {
		JScrollPane scr;
		JPanel search = new JPanel(new BorderLayout()), path = new JPanel(new BorderLayout(5, 5));
		JToggleButton tog[] = new JToggleButton[2];
		JTextField srhTxt, deTxt, arrTxt;

		public West() {
			add(n = new JPanel(new BorderLayout(5, 5)), "North");
			add(scr = new JScrollPane(search));
			add(hyplbl("메인으로", 2, 20, Color.orange, e -> mf.swapPage(new MainPage())), "South");

			n.add(nn = new JPanel(new BorderLayout(5, 5)), "North");
			n.add(nc = new JPanel(new GridLayout(1, 0, 5, 5)));

			nn.add(srhTxt = new JTextField());
			nn.add(btn("검색", a -> search()), "East");

			var bg = new ButtonGroup();
			for (int i = 0; i < tog.length; i++) {
				nc.add(tog[i] = new JToggleButton("검색,길찾기".split(",")[i]));
				bg.add(tog[i]);
				tog[i].setBackground(blue);
				tog[i].setForeground(Color.white);
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

			path.add(cn = new JPanel(new BorderLayout()), "North");
			path.add(cc = new JPanel(new GridLayout(0, 1)));

			{
				var tmp = new JPanel(new GridLayout(0, 1));

				tmp.add(deTxt = new JTextField());
				tmp.add(arrTxt = new JTextField());

				cn.add(tmp);
			}
			cn.add(btn("↑↓", a -> {
			}), "East");

			tog[0].doClick();

			n.setBackground(blue);
			n.setBorder(new EmptyBorder(5, 5, 5, 5));
			nn.setOpaque(false);
			nc.setOpaque(false);
			path.setBorder(new EmptyBorder(5, 5, 5, 5));
		}

		void search() {
			search.removeAll();
			tog[0].doClick();

			if (srhTxt.getText().isEmpty()) {
				eMsg("검색 키워드를 입력하세요.");
				return;
			}

			var rs = getRows(
					"select type, b.no, name, info, img, x, y, ifnull(round(avg(rate),0),0) from rate r, building b where r.building = b.no and b.type <> 3 and b.name like ?",
					"%" + srhTxt.getText() + "%");
			if (rs.isEmpty()) {
				eMsg("검색 결과가 없습니다.");
				return;
			}

			search.add(lbl("<html>장소명 <font color=rgb(0,123,255)>" + srhTxt.getText() + "</font> 의 검색 결과", 2, 15),
					"North");
			var tmp = new JPanel(new GridLayout(0, 1));

			map.goCenter(toInt(rs.get(0).get(5)), toInt(rs.get(0).get(6)));

			for (var r : rs) {
				var temp = new JPanel(new BorderLayout(5, 5));

				temp.add(hyplbl("<html>" + (rs.indexOf(r) + 1) + ":" + r.get(2), 2, 15, Color.black, e -> {
					if (toInt(r.get(0)) == 2) {
						return;
					}

					new InfoDialog(r).setVisible(true);
				}), "North");
				temp.add(lbl("<html>" + r.get(3), 2, 13));
				temp.add(new JLabel(getIcon(r.get(4), 75, 80)), "East");
				temp.add(lbl("평점: " + r.get(5), 2, 13), "South");
				temp.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						if (e.getButton() == 1 && e.getClickCount() == 2) {
							map.goCenter(toInt(r.get(5)), toInt(r.get(6)));
						}
					}
				});

				var pop = new JPopupMenu();

				for (var cap : "출발지,도착지".split(",")) {
					var item = new JMenuItem(cap);
					item.addActionListener(a -> {
						System.out.println(r);

						if (a.getActionCommand().equals("출발지")) {
							if (arrTxt.getText().equals(r.get(2).toString())) {
								eMsg("출발지와 도착지가 같을 수 없습니다.");
								return;
							}

							deTxt.setName(r.get(1).toString());
							deTxt.setText(r.get(2).toString());
						} else {
							if (deTxt.getText().equals(r.get(2).toString())) {
								eMsg("출발지와 도착지가 같을 수 없습니다.");
								return;
							}

							arrTxt.setName(r.get(1).toString());
							arrTxt.setText(r.get(2).toString());
						}

						findPath();
					});
					pop.add(item);
				}

				temp.setBorder(new LineBorder(Color.lightGray));
				temp.setComponentPopupMenu(pop);

				tmp.add(temp);
			}

			while (tmp.getComponentCount() < 3) {
				tmp.add(new JLabel());
			}

			search.add(tmp);
		}
	}

	class Map extends BasePage {
		BufferedImage img;
		JPopupMenu pop = new JPopupMenu();

		double targetX = 0, targetY = 0, startX, startY;
		double zoom = 1;
		Point2D point;

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
					g2.fillRoundRect(0, 0, 20, 30, 5, 5);
					g2.setColor(blue);
					g2.drawString(text, 5, 20);
				}
			}, 30, 35);
			lbl.addMouseListener(new MouseAdapter() {
				@Override
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
				}
			});
			w.add(lbl);

			var ma = new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					try {
						point = aff.inverseTransform(e.getPoint(), null);
					} catch (NoninvertibleTransformException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					startX = point.getX();
					startY = point.getY();

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
				};

				@Override
				public void mouseDragged(MouseEvent e) {
					try {
						point = aff.inverseTransform(e.getPoint(), null);
					} catch (NoninvertibleTransformException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					var diffX = point.getX() - startX;
					var diffY = point.getY() - startY;

					targetX += diffX;
					targetY += diffY;

					startX = point.getX();
					startY = point.getY();

					repaint();
				}

				@Override
				public void mouseWheelMoved(MouseWheelEvent e) {
					try {
						point = aff.inverseTransform(e.getPoint(), null);
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
						aff.translate(-point.getX(), -point.getY());
						repaint();
					}
				}
			};

			addMouseListener(ma);
			addMouseMotionListener(ma);
			addMouseWheelListener(ma);

			try {
				drawMap();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			w.setOpaque(false);
			setBackground(new Color(153, 217, 234));
		}

		void goCenter(int x, int y) {
			try {
				point = aff.inverseTransform(new Point((getWidth() - map.getWidth()) / 2 - (x - map.getWidth() / 2),
						(getHeight() - map.getHeight()) / 2 - (y - map.getWidth() / 2)), null);
			} catch (NoninvertibleTransformException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			targetX = point.getX();
			targetY = point.getY();

			repaint();
			revalidate();
		}

		public void drawMap() throws IOException {
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
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			var g2 = (Graphics2D) g;
			aff.translate(targetX, targetY);
			g2.drawImage(img, aff, null);
		}
	}

	public static void main(String[] args) {
		mf.swapPage(new SearchPage1());
		mf.setVisible(true);
	}
}
