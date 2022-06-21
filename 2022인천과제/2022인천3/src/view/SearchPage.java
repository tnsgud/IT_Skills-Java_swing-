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
	JTextField searchTxt, startTxt, endTxt;
	CardLayout pages;
	ArrayList<Integer> path = new ArrayList<>();
	ArrayList<MapItem> objList = new ArrayList<>();
	ArrayList<ArrayList<Node>> adjList = new ArrayList<>();
	String colorKey = "";
	Entry<String, String> selected;
	double startAffX, startAffY;
	double zoom = 0.5;
	Point2D curAffPoint;
	JPopupMenu menu = new JPopupMenu();
	JPanel srhP, pathP, pathRsP;

	public SearchPage() {
		dataInit();
		drawOnMap();

		add(c = new JPanel(new BorderLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				var g2 = (Graphics2D) g;
				aff.translate(affX, affY);
				g2.drawImage(map, aff, null);
			}
		});
		add(w = sz(new JPanel(new BorderLayout()), 280, 0), "West");

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

				var item = objList.stream().filter(
						obj -> clickeX >= obj.x && clickeX <= obj.x + 40 && clickeY >= obj.y && obj.y + 40 <= clickeY)
						.findFirst();

				if (item.isPresent()) {
					var list = item.get().list;
					if (e.getButton() == 1) {
						new InfoDialog(list).setVisible(true);
					} else if (e.getButton() == 3) {
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

		var toglbl = sz(new JLabel("") {
			String text = "◀";

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				var g2 = (Graphics2D) g;

				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(new JPanel().getBackground());
				g2.fillRoundRect(-10, 0, 30, 30, 5, 5);
				g2.setColor(Color.black);
				g2.setColor(BasePage.blue);
				g2.drawString(text, 5, 20);
			}
		}, 20, 30);

		toglbl.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (toglbl.text.equals("▶")) {
					toglbl.text = "◀";
					sz(w, 280, 0);
				} else {
					toglbl.text = "▶";
					sz(w, 0, 0);
				}

				toglbl.repaint();
				revalidate();
			};
		});

		w.add(wn = new JPanel(new BorderLayout(5, 5)), "North");
		w.add(wc = new JPanel(pages = new CardLayout()));
		w.add(hyplbl("메인으로", 2, 15, e -> mf.swapPage(new MainPage())), "South");
		wc.add(new JScrollPane(srhP = new JPanel(new GridLayout(0, 1, 5, 5))), "검색");
		wc.add(new JScrollPane(pathP = new JPanel(new BorderLayout())), "길찾기");

		c.add(cw = new JPanel(new GridBagLayout()), "West");
		cw.add(toglbl);

//		West -> North
		{
			var tmp1 = new JPanel(new BorderLayout(5, 5));
			var tmp2 = new JPanel(new GridLayout(1, 0, 5, 5));

			wn.add(tmp1);
			wn.add(tmp2, "South");

			tmp1.add(searchTxt = new JTextField());
			tmp1.add(btn("검색", a -> search()), "East");

			var bg = new ButtonGroup();
			var cap = "검색,길찾기".split(",");
			for (int i = 0; i < cap.length; i++) {
				final int j = i;
				tmp2.add(toggle[i] = sz(new JToggleButton(cap[i]), 0, 50));
				toggle[i].setFocusPainted(false);
				toggle[i].setBackground(blue);
				toggle[i].setForeground(Color.white);
				bg.add(toggle[i]);
				toggle[i].setUI(new MetalToggleButtonUI() {
					protected Color getSelectColor() {
						return blue.darker();
					};
				});

				toggle[i].addActionListener(a -> {
					pages.show(wc, cap[j]);

					repaint();
					revalidate();

				});
			}

			tmp1.setOpaque(false);
			tmp2.setOpaque(false);
		}

//		West -> Center
		{
			var tmp1 = new JPanel(new BorderLayout(5, 5));
			var tmp2 = new JPanel(new GridLayout(0, 1, 5, 5));
			var tmp3 = new JPanel(new FlowLayout(2, 0, 0));

			tmp2.add(startTxt = new JTextField());
			tmp2.add(endTxt = new JTextField());

			startTxt.setEditable(false);
			endTxt.setEditable(false);

			tmp1.add(tmp2);
			tmp1.add(btn("↑↓", a -> {
			}), "East");
			tmp1.add(tmp3, "South");
			tmp3.add(btn("집을 출발지로", a -> {
			}));

			pathP.add(tmp1, "North");
		}

		pathP.add(pathRsP = new JPanel(new GridLayout(0, 1)));
		srhP.add(lbl("<html><center>텍스트 필드에 택스트를 입력하고<br>검색버튼을 누르세요.", 0));

		pathP.setBorder(new EmptyBorder(5, 5, 5, 5));
		srhP.setBorder(new EmptyBorder(5, 5, 5, 5));

		toggle[0].doClick();

		c.setBackground(new Color(153, 217, 234));

		c.addMouseListener(ma);
		c.addMouseMotionListener(ma);
		c.addMouseWheelListener(ma);

		wn.setBorder(new EmptyBorder(5, 5, 5, 5));
		wn.setBackground(blue);
		cw.setOpaque(false);

	}

	private void search() {
		srhP.removeAll();
		toggle[0].doClick();
		srhP.setLayout(new GridBagLayout());

		if (searchTxt.getText().isEmpty()) {
			srhP.add(lbl("<html><center><font color=rgb(0,123,255)>공백이 존재합니다.", 0));
			return;
		}

		var rs = getRows(
				"select b.*, ifnull(round(avg(rate), 0), 0) from building b left join rate r on b.no = r.building where name like ? or info like ? group by b.no",
				"%" + searchTxt.getText() + "%", "%" + searchTxt.getText() + "%");
		if (rs.isEmpty()) {
			srhP.add(lbl("<html><center><font color=rgb(0,123,255)>" + searchTxt.getText() + "</font><br>의 결과가 없어요.",
					0));
			return;
		}

		srhP.setLayout(new BorderLayout());

		srhP.add(lbl("<html>장소명 <font color=rgb(0,123,255)>" + searchTxt.getText() + "</font> 의 검색 결과", 2, 13),
				"North");

		var p = new JPanel(new GridLayout(0, 1));

		srhP.add(p);

		gotoCenter(toInt(rs.get(0).get(6)), toInt(rs.get(0).get(7)));

		for (var r : rs) {
			var tmp1 = sz(new JPanel(new BorderLayout()), 180, 120);
			var tmp2 = new JPanel(new BorderLayout());

			tmp1.add(hyplbl(rs.indexOf(r) + 1 + ":" + r.get(1), 2, 15, e -> {
			}), "North");
			tmp1.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (e.getButton() == 1 && e.getButton() == 2) {
						gotoCenter(toInt(r.get(6)), toInt(r.get(7)));
						return;
					}

					if (e.getButton() == 2) {
						selected = Map.entry(r.get(0) + "", r.get(1) + "");
						menu.show(tmp1, e.getX(), e.getY());
					}
				}
			});
			tmp1.add(tmp2);
			if (r.get(8) != null) {
				tmp2.add(new JLabel(getIcon(r.get(8), 80, 80)), "East");
			}
			tmp2.add(lbl(r.get(4) + "", 2));
			tmp1.add(lbl("평점:" + r.get(9), 2), "South");

			tmp1.setBorder(new EmptyBorder(5, 5, 5, 5));

			p.add(tmp1);
		}

		while (p.getComponentCount() < 3) {
			p.add(new JLabel());
		}

		p.setBorder(new EmptyBorder(5, 5, 5, 5));

		repaint();
		revalidate();
	}

	private void gotoCenter(int x, int y) {
		zoom = 1;
		aff.setToIdentity();
		aff.scale(zoom, zoom);

		try {
			curAffPoint = aff.inverseTransform(new Point((c.getWidth() - map.getWidth()) / 2 - (x - map.getWidth() / 2),
					(c.getHeight() - map.getHeight()) / 2 - (y - map.getWidth() / 2)), null);
		} catch (NoninvertibleTransformException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		affX = curAffPoint.getX();
		affY = curAffPoint.getY();

		c.repaint();
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

		var tmp = new ArrayList<String>();
		for (int i = 0; i < path.size() - 1; i++) {
			int n1 = path.get(i), n2 = path.get(i + 1);
			g2.setColor(Color.yellow);

			var node = adjList.get(n1).stream().filter(a -> a.idx == n2).findFirst().get();

			if (!tmp.contains(tmp.size() + "." + node.name)) {
				tmp.add(tmp.size() + "." + node.name);
			}

			var pos1 = getRows("select x, y from building where no=?", n1).get(0);
			var pos2 = getRows("select x, y from building where no=?", n2).get(0);

			if (colorKey.equals(tmp.get(tmp.size() - 1))) {
				g2.setColor(Color.magenta);
			}

			g2.drawLine(toInt(pos1.get(0)), toInt(pos1.get(1)), toInt(pos2.get(0)), toInt(pos2.get(0)));
		}

		var d = "진료소,병원,주거지".split(",");
		for (var obj : objList) {
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

	void dataInit() {
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
			adjList.get(no2).add(new Node(no2, cost, rs.get(6) + ""));
		}

		for (var rs : getRows(
				"select b.*, ifnull(round(avg(rate), 0), 0) from building b left join rate r on b.no = r.building where type <> 3 group by b.no")) {
			int x = toInt(rs.get(6)), y = toInt(rs.get(7));

			objList.add(new MapItem(rs, x - 20, y - 20));
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