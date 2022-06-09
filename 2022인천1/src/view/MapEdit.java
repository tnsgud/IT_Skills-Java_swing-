package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

public class MapEdit extends BaseDialog {
	String no;

	JLabel map;
	JScrollPane scr;

	HashMap<String, JRadioButton> points = new HashMap<String, JRadioButton>();
	HashMap<String, HashSet<String>> connections = new HashMap<>();

	JRadioButton radio, first, second;

	public MapEdit() {
		super(800, 800);
		setTitle("맵 수정");
		setResizable(true);

		add(scr = new JScrollPane(map = new JLabel(img("./datafiles/map.jpg")) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				var g2 = (Graphics2D) g;

				g2.setStroke(new BasicStroke(3f));
				g2.setColor(Color.orange);

				for (var key : connections.keySet()) {
					var p1 = points.get(key).getLocation();
					for (var k : connections.get(key)) {
						var p2 = points.get(k).getLocation();

						g2.drawLine(p1.x + 7, p1.y + 7, p2.x + 7, p2.y + 7);
					}
				}

				if (first != null && second != null) {
					var p1 = first.getLocation();
					var p2 = second.getLocation();

					g2.setColor(Color.green);
					g2.drawLine(p1.x + 7, p1.y + 7, p2.x + 7, p2.y + 7);
				}
			}
		}));

		for (var rs : rs("select no, x, y, type from building")) {
			var r = new JRadioButton();
			r.setName(rs.get(0) + "");
			r.setOpaque(false);
			points.put(r.getName(), r);
			map.add(r).setBounds(toInt(rs.get(1)) - 7, toInt(rs.get(2)) - 7, 20, 20);

			if (toInt(rs.get(3)) == 3) {
				connections.put(r.getName(), new HashSet<>());
			} else {
				r.setEnabled(false);
			}

			r.addActionListener(a -> {
				if (first != null && second != null) {
					first.setSelected(false);
					second.setSelected(false);
					first = second = null;

					repaint();
				}

				if (first == null) {
					first = (JRadioButton) a.getSource();
				} else {
					second = (JRadioButton) a.getSource();

					repaint();

					var no1 = first.getName();
					var no2 = second.getName();
					var type = false;

					if (connections.get(no1) != null) {
						type = connections.get(no1).contains(no2);
					}

					var an = JOptionPane.showConfirmDialog(null, type ? "연결울 취소하시겠습니까?" : "연결하시겠습니까?", "질문",
							JOptionPane.YES_NO_OPTION);
					if (an == JOptionPane.YES_OPTION && type) {
						execute("delete from connection where (node1=? and node2=?) or (node1= ? and node2=?)", no1,
								no2, no2, no1);
						connections.get(no1).remove(no2);
						connections.get(no2).remove(no1);
					} else if (an == JOptionPane.YES_OPTION && !type) {
						var name = JOptionPane.showInputDialog(null, "도로이름을 입력하세요.");
						execute("insert connection values(?,?,?)", no1, no2, name);
						connections.get(no1).add(no2);
						if (!connections.containsKey(no2)) {
							connections.put(no2, new HashSet<>());
						}
						connections.get(no2).add(no1);
					}

					first = second = null;

					repaint();

					if (!no.isEmpty()) {
						first = (JRadioButton) points.get(no);
						first.setSelected(true);
					}
				}
			});
		}

		for (var rs : rs(
				"select node1, node2 from connection c, building b1, building b2 where c.node1=b1.no and c.node2=b2.no and b1.type = 3 and b2.type = 3")) {
			connections.get(rs.get(0).toString()).add(rs.get(1).toString());
			connections.get(rs.get(1).toString()).add(rs.get(0).toString());
		}

		repaint();

		var ma = new MouseAdapter() {
			private Point first;

			@Override
			public void mousePressed(MouseEvent e) {
				first = e.getPoint();
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (first != null) {
					var port = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, map);

					if (port != null) {
						var dx = first.getX() - e.getX();
						var dy = first.getY() - e.getY();
						var view = port.getViewRect();

						view.x += dx;
						view.y += dy;

						map.scrollRectToVisible(view);
					}
				}
			}
		};

		map.addMouseListener(ma);
		map.addMouseMotionListener(ma);

		scr.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		scr.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	}

	public MapEdit(String no) {
		this();
		this.no = no;

		var r = (JRadioButton) points.get(no);

		r.setEnabled(true);
		r.setSelected(true);

		connections.clear();

		connections.put(no, new HashSet<>());

		for (var rs : rs("select * from connection where node1=? or node2=?", no, no)) {
			var no2 = (rs.get(0).toString().equals(no) ? rs.get(1) : rs.get(0)) + "";

			if (!connections.containsKey(no2)) {
				connections.put(no2, new HashSet<>());
			}
			
			points.get(no2).setSelected(true);

			connections.get(no).add(no2);
			connections.get(no2).add(no);
		}

		first = r;
	}
}
