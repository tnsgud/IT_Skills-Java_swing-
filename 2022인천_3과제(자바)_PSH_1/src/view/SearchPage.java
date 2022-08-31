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
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.metal.MetalToggleButtonUI;

public class SearchPage extends BasePage {
	West west;
	Map map;

	ArrayList<Integer> pathList = new ArrayList<>();
	ArrayList<MapItem> itemList = new ArrayList<>();
	ArrayList<ArrayList<Node>> adjList = new ArrayList<>();
	Entry<String, String> select;

	AffineTransform myAffine = new AffineTransform();
	Point2D sAffPoint = new Point2D.Float(), eAffPoint = new Point2D.Float();
	double affDx, affDy;
	double zoom = 1;
	Point fromPoint, toPoint;
	String colorKey = "";
	int uno = 1;

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
				"select node1, node2, b1.x, b1.y, b2.x, b2.y, c.name from connection c, building b1, building b2 where c.node1=b1.no and c.node2 = b2.no")) {
			int no1 = toInt(rs.get(0)), no2 = toInt(rs.get(1));
			int x1 = toInt(rs.get(2)), y1 = toInt(rs.get(3)), x2 = toInt(rs.get(4)), y2 = toInt(rs.get(5));
			int cost = (int) Point.distance(x1, y1, x2, y2);
			var name = rs.get(6).toString();

			adjList.get(no1).add(new Node(no2, cost, name));
			adjList.get(no2).add(new Node(no1, cost, name));
		}

		for (var rs : getRows(
				"select b.*, ifnull(round(avg(rate), 0), 0) from building b left join rate r on b.no = r.building where type <> 3")) {
			int x = toInt(rs.get(6)), y = toInt(rs.get(7));
			itemList.add(new MapItem(rs, x - 20, y - 20));
		}
	}

	class West extends BasePage {
		JTextField tagetXtSearch, tagetXtAr, tagetXtDe;
		JToggleButton tog[] = new JToggleButton[2];
		JPanel search, path;
		JScrollPane scr;

		public West() {
			add(n = new JPanel(new BorderLayout(5, 5)), "North");
			add(scr = new JScrollPane(search = new JPanel()));
			add(lbl("메인으로", 2, 20, Color.orange, e -> mf.swap(new MainPage())), "South");
			search.setLayout(new BoxLayout(search, BoxLayout.Y_AXIS));

			n.add(nn = new JPanel(new BorderLayout(5, 5)), "North");
			n.add(nc = new JPanel(new GridLayout(1, 0, 5, 5)));

			nn.add(tagetXtSearch = new JTextField());
			nn.add(btn("검색", a -> {
			}), "East");

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
			cc.setLayout(new BoxLayout(cc, BoxLayout.Y_AXIS));

			{
				var tmp1 = new JPanel(new GridLayout(0, 1, 5, 5));
				var tmp2 = new JPanel(new FlowLayout(2, 0, 0));

				tmp1.add(tagetXtAr = new JTextField());
				tmp1.add(tagetXtDe = new JTextField());

				tmp2.add(btn("집을 출발지로", a -> {
				}));

				cn.add(tmp1);
				cn.add(btn("↑↓", a -> {
				}), "East");
				cn.add(tmp2, "South");
			}

			tog[0].doClick();

			n.setBackground(blue);
			nn.setOpaque(false);
			nc.setOpaque(false);

			n.setBorder(new EmptyBorder(5, 5, 5, 5));
			path.setBorder(new EmptyBorder(5, 5, 5, 5));
		}
	}

	class Map extends BasePage {
		BufferedImage img;
		JPopupMenu pop;

		double zoom = 1;
		Point2D p;

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			var g2 = (Graphics2D) g;
			g2.drawImage(img, myAffine, null);
		}

		public Map() {
			add(w = new JPanel(new GridBagLayout()), "West");

			var lbl = sz(new JLabel() {
				String tagetXt = "◀";

				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					var g2 = (Graphics2D) g;

					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

					g2.setColor(Color.white);
					g2.fillRoundRect(0, 0, 20, 30, 5, 5);

					g2.setColor(blue);
					g2.drawString(tagetXt, 5, 20);
				}
			}, 40, 40);
			lbl.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (lbl.tagetXt.equals("◀")) {
						lbl.tagetXt = "▶";
						sz(west, 0, 0);
					} else {
						lbl.tagetXt = "◀";
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
							select = java.util.Map.entry(i.info.get(0).toString(), i.info.get(1).toString());
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
						if (zoom == 0.1)
							flag = true;
						else
							zoom = Math.max(0.1, zoom - 0.1);
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

			setBackground(new Color(154, 217, 234));
			w.setOpaque(false);
		}

		void drawMap() {
			try {
				img = ImageIO.read(new File("./datafiles/map.jpg"));

				var g2 = (Graphics2D) img.getGraphics();

				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setStroke(new BasicStroke(8, 1, 1));

				if (!pathList.isEmpty()) {
					var tmp = new ArrayList<String>();

					for (int i = 0; i < pathList.size() - 1; i++) {
						int n1 = pathList.get(i), n2 = pathList.get(i + 1);
						int x1 = 0, y1 = 0, x2 = 0, y2 = 0;
						var node = adjList.get(n1).stream().filter(x -> x.no == n2).findFirst().get();

						if (!tmp.contains(tmp.size() + ". " + node.name)) {
							tmp.add(tmp.size() + 1 + ". " + node.name);
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
					}
				}

				var cap = "진료소,병원,거주지".split(",");
				for (var item : itemList) {
					var img = ImageIO.read(new File("./datafiles/맵아이콘/" + cap[toInt(item.info.get(5))] + ".png"));
					
					g2.setColor(Color.red);
					g2.drawImage(img, item.x, item.y, 40, 40, null);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
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

	public static void main(String[] args) {
		mf = new MainFrame();
		mf.swap(new SearchPage());
		mf.setVisible(true);
	}
}
