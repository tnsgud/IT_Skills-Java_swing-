package view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
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
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
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
	BufferedImage map;
	double affX = 0, affY = 0;
	AffineTransform aff = new AffineTransform();
	JToggleButton toggle[] = new JToggleButton[2];
	JTextField search, start, end;
	CardLayout card;
	ArrayList<Integer> path = new ArrayList<>();
	ArrayList<MapItem> objs = new ArrayList<>();
	ArrayList<ArrayList<Node>> adjList = new ArrayList<>();
	String colorKey = "";
	Entry<String, String> selected;
	double startAffX, startAffY;
	double zoom = 0.5;
	Point2D curAffPoint;
	JPanel srchP, pathP, pathRsP;
	JPopupMenu menu;

	public SearchPage() {
		dataInit();
		drawOnMap();

		add(w = sz(new JPanel(new BorderLayout()), 280, 0), "West");
		add(c = new JPanel(new BorderLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				var g2 = (Graphics2D) g;
				aff.translate(affX, affY);
				g2.drawImage(map, aff, null);
			}
		});

		c.setBackground(new Color(153, 217, 234));

		var ma = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					curAffPoint = aff.inverseTransform(e.getPoint(), null);
				} catch (NoninvertibleTransformException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				startAffX = curAffPoint.getX();
				startAffY = curAffPoint.getY();

				int clickeX = (int) startAffX, clickeY = (int) startAffY;

				var item = objs.stream().filter(
						obj -> clickeX >= obj.x && clickeX <= obj.x + 40 && clickeY >= obj.y && obj.y + 40 <= clickeY)
						.findFirst();

				if (item.isPresent()) {
					var list = item.get().list;

					if (e.getButton() == 1) {
						new InfoDialog(list).setVisible(true);
					} else {
						selected = Map.entry(list.get(0) + "", list.get(1) + "");
						menu.show(c, e.getX(), e.getY());
					}
				}
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				try {
					curAffPoint = aff.inverseTransform(e.getPoint(), null);
				} catch (NoninvertibleTransformException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				var affDiffX = curAffPoint.getX() - startAffX;
				var affDiffY = curAffPoint.getY() - startAffY;

				affX += affDiffX;
				affY += affDiffY;

				c.repaint();

				startAffX = curAffPoint.getX();
				startAffY = curAffPoint.getY();
			}

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				try {
					curAffPoint = aff.inverseTransform(e.getPoint(), null);
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
					aff.translate(-curAffPoint.getX(), -curAffPoint.getY());
					c.repaint();
				}
			}
		};

		c.addMouseListener(ma);
		c.addMouseMotionListener(ma);
		c.addMouseWheelListener(ma);

		w.add(wn = new JPanel(new BorderLayout(5, 5)), "North");
		w.add(wc = new JPanel(card = new CardLayout()));
		w.add(hyplbl("메인으로", 2, 20, Color.orange, e -> mf.swapPage(new MainPage())), "South");

		wn.setBackground(blue);
		wn.setBorder(new EmptyBorder(5, 5, 5, 5));

		{
			var tmp1 = new JPanel(new BorderLayout(5, 5));
			var tmp2 = new JPanel(new GridLayout(1, 0, 5, 5));

			wn.add(tmp1);
			wn.add(tmp2, "South");

			tmp1.add(search = new JTextField());
			tmp1.add(btn("검색", a -> search()), "East");

			var bg = new ButtonGroup();
			var cap = "검색,길찾기".split(",");
			for (int i = 0; i < cap.length; i++) {
				tmp2.add(toggle[i] = new JToggleButton(cap[i]));
				bg.add(toggle[i]);
				toggle[i].setForeground(Color.white);
				toggle[i].setBackground(blue);
				toggle[i].setUI(new MetalToggleButtonUI() {
					protected Color getSelectColor() {
						return blue.darker();
					};
				});

				toggle[i].addActionListener(a -> {
					card.show(wc, ((JToggleButton) a.getSource()).getText());

					repaint();
					revalidate();
				});
			}

			tmp1.setOpaque(false);
			tmp2.setOpaque(false);
		}

		toggle[0].doClick();

		wc.add(new JScrollPane(srchP = new JPanel(new GridLayout(0, 1, 5, 5))), "검색");
		wc.add(new JScrollPane(pathP = new JPanel(new BorderLayout(5, 5))), "길찾기");

		pathP.setBorder(new EmptyBorder(5, 5, 5, 5));

		{
			var tmp1 = new JPanel(new BorderLayout(5, 5));
			var tmp2 = new JPanel(new GridLayout(0, 1, 5, 5));
			var tmp3 = new JPanel(new FlowLayout(2));

			tmp1.add(tmp2);
			tmp1.add(btn("↑↓", a -> {
			}), "East");
			tmp1.add(tmp3, "South");

			tmp2.add(start = new JTextField());
			tmp2.add(end = new JTextField());

			tmp3.add(btn("집을 출발지로", a -> {
			}));

			pathP.add(tmp1, "North");
			pathP.add(pathRsP = new JPanel(new GridLayout(0, 1, 5, 5)));
		}

		var to = new JLabel() {
			String text = "◀";

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				var g2 = (Graphics2D) g;

				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(new JPanel().getBackground());
				g2.fillRoundRect(-10, 0, 30, 30, 5, 5);
				g2.setColor(blue);
				g2.drawString(text, 5, 20);
			}
		};

		to.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (to.text.equals("▶")) {
					to.text = "◀";
					sz(w, 280, 0);
				} else {
					to.text = "▶";
					sz(w, 0, 0);
				}

				to.repaint();
				revalidate();
			};
		});

		c.add(cw = new JPanel(new GridBagLayout()), "West");
		cw.add(to);
	}

	private void search() {

	}

	private void drawOnMap() {
		try {
			map = ImageIO.read(new File("./datafiles/map.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		var g2 = (Graphics2D) map.getGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		var tmp = new ArrayList<>();
		for (int i = 0; i < path.size() - 1; i++) {
			int n1 = path.get(i), n2 = path.get(i + 1);

			g2.setColor(Color.yellow);

			var node = adjList.get(n1).stream().filter(a -> a.idx == n2).findFirst().get();

			if (!tmp.contains(tmp.size() + "." + node.name)) {
				tmp.add(tmp.size() + "." + node.name);
			}

			var pos1 = getRows("select x, y from building where no = ?", n1).get(0);
			var pos2 = getRows("select x, y from building where no = ?", n2).get(0);

			if (colorKey.equals(tmp.get(tmp.size() - 1))) {
				g2.setColor(Color.magenta);
			}

			g2.drawLine(toInt(pos1.get(0)), toInt(pos1.get(1)), toInt(pos2.get(0)), toInt(pos2.get(1)));
		}

		var d = "진료소,병원,주거지".split(",");
		for (var obj : objs) {
			g2.setColor(Color.red);

			try {
				var img = ImageIO.read(new File("./datafiles/맵아이콘/" + d[toInt(obj.list.get(5))] + ".png"));
				g2.drawString(obj.list.get(1) + "",
						(obj.x + 20) - g2.getFontMetrics().stringWidth(obj.list.get(1) + "") / 2, obj.y - 5);
				g2.drawImage(img, obj.x, obj.y, 40, 40, null);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (c != null) {
			c.repaint();
		}
	}

	private void dataInit() {
		for (int i = 0; i < toInt(getOne("select count(*) from building")) + 1; i++) {
			adjList.add(new ArrayList<>());
		}

		for (var rs : getRows(
				"select c.node1, c.node2, b1.x, b1.y, b2.x, b2.y, c.name from connection c, building b1, building b2 where c.node1 = b1.no and c.node2 = b2.no")) {
			int no1 = toInt(rs.get(0)), no2 = toInt(rs.get(1));
			int x1 = toInt(rs.get(2)), y1 = toInt(rs.get(3));
			int x2 = toInt(rs.get(4)), y2 = toInt(rs.get(5));
			int cost = (int) Point.distance(x1, y1, x2, y2);
			adjList.get(no1).add(new Node(no2, cost, rs.get(6) + ""));
			adjList.get(no2).add(new Node(no1, cost, rs.get(6) + ""));
		}

		for (var rs : getRows(
				"select b.*, ifnull(round(avg(rate), 0), 0) from building b left join rate r on b.no = r.building where type <> 3 group by b.no")) {
			int x = toInt(rs.get(6)), y = toInt(rs.get(7));

			objs.add(new MapItem(rs, x - 20, y - 20));
		}
	}

	class MapItem {
		ArrayList<Object> list;
		int x, y;

		public MapItem(ArrayList<Object> list, int x, int y) {
			this.list = list;
			this.x = x;
			this.y = y;
		}
	}

	class Node {
		int idx;
		int cost;
		String name;

		public Node(int idx, int cost, String name) {
			this.idx = idx;
			this.cost = cost;
			this.name = name;
		}
	}

	public static void main(String[] args) {
		mf.swapPage(new SearchPage());
		mf.setVisible(true);
	}
}
