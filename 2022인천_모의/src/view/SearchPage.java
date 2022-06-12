package view;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

public class SearchPage extends BasePage {
	static BufferedImage map;
	static double affTargetX = 0, affTargetY = 0;
	static AffineTransform aff = new AffineTransform();
	static double startAffX, startAffY, diffAffX, diffAffY;
	static double zoom = 0.5;
	static Point2D curAffPoint;

	JToggleButton toggle[] = new JToggleButton[2];
	JPanel searchP, pathP, pathRsp;
	JTextField searchTxt, start, end;
	CardLayout pages;
	ArrayList<Integer> path;
	ArrayList<Object[]> objList;
	ArrayList<ArrayList<Object[]>> adjList;
	Entry<String, String> selected;

	JPopupMenu menu = new JPopupMenu();

	{
		for (var cap : "출발지,도착지".split(",")) {
			var i = new JMenuItem(cap);
			menu.add(i);
			i.addActionListener(a -> {
				if (cap.equals("출발지")) {
					if (end.getName() != null && end.getName().equals(selected.getValue())) {
						eMsg("출발지와 도착지는 같을 수 없습니다.");
						return;
					}

					start.setName(selected.getKey());
					start.setText(selected.getValue());
				} else {
					if (start.getName() != null && start.getName().equals(selected.getValue())) {
						eMsg("출발지와 도착지는 같을 수 없습니다.");
						return;
					}

					end.setName(selected.getKey());
					end.setText(selected.getValue());
				}

				dijkstra();
			});
		}
	}

	public SearchPage() {

	}

	private void dijkstra() {
		if (start.getText().isEmpty() || end.getText().isEmpty()) {
			return;
		}

		int start = toInt(this.start.getName());
		int end = toInt(this.end.getName());

		int[][] dist = new int[2][adjList.size() + 1];

		for (int i = 1; i < dist[0].length; i++) {
			dist[0][i] = Integer.MAX_VALUE;
			dist[1][i] = -1;
		}

		var pq = new PriorityQueue<Object[]>((o1, o2) -> Integer.compare(toInt(o1[1]), toInt(o2[1])));
		pq.offer(new Object[] { start, 0 });
		dist[1][start] = 0;

		while (!pq.isEmpty()) {
			var cur = pq.poll();
			if (dist[0][toInt(cur[0])] < toInt(cur[1])) {
				continue;
			}

			for (int i = 0; i < adjList.get(toInt(cur[0])).size(); i++) {
				var next = adjList.get(toInt(cur[0])).get(i);
				if (dist[0][toInt(next[0])] > toInt(cur[1]) + toInt(next[1])) {
					dist[0][toInt(next[0])] = toInt(cur[1]) + toInt(next[1]);
					dist[1][toInt(next[0])] = toInt(cur[0]);
					pq.offer(new Object[] { toInt(next[0]), dist[0][toInt(next[0])] });
				}
			}
		}

		pathRsp.removeAll();
		path = new ArrayList<>();
		int arv = start, dest = end;

		while (dest != arv) {
			path.add(dest);
			dest = dist[1][dest];
		}

		path.add(arv);
		Collections.reverse(path);

		var tmp = new ArrayList<String[]>();

		for (int i = 0; i < path.size(); i++) {
			int n1 = path.get(i - 1);
			int n2 = path.get(i);

			var node = adjList.get(n1).stream().filter(a -> toInt(a[0]) == n2).findFirst().get();

			if (!tmp.stream().filter(a -> a[0].equals(tmp.size() + ". " + node[2].toString())).findFirst()
					.isPresent()) {
				tmp.add(new String[] { tmp.size() + 1 + ". " + node[2].toString(), node[1].toString() });
			} else {
				var str = tmp.get(tmp.size() - 1);
				int cost = toInt(str[1]) + toInt(node[1]);
				tmp.set(tmp.size() - 1, new String[] { str[0], cost + "" });
			}
		}

		int total = tmp.stream().mapToInt(a -> toInt(a[1])).sum();

		pathRsp.add(lbl("총 거리:" + total + "m", 4));

		for (int i = 0; i < tmp.size(); i++) {
			var text = "<html><font color='black'>";

			if (i == 0) {
				text = "<html><font color='red'>출발</font><font color='black'>";
			} else if (i == tmp.size() - 1) {
				text = "<html><font color='blue'>도착</font><font color='black'>";
			}

			final int j = i;

			int x = -1, y = -1;

			var lbl = hyplbl(text + "" + tmp.get(i)[0] + " 총 " + tmp.get(i)[1] + "m", 0, 13, Color.orange, () -> {
				Point2D labelCenteringAffPoint = null, mapCenterAffPoint = null;

				SearchPage.zoom = 1;
				SearchPage.aff.setToIdentity();
				SearchPage.aff.scale(SearchPage.zoom, SearchPage.zoom);

				try {
					labelCenteringAffPoint = SearchPage.aff.inverseTransform(new Point(
							(c.getWidth() - SearchPage.map.getWidth()) / 2 - (x - SearchPage.map.getWidth() / 2),
							(c.getHeight() - SearchPage.map.getHeight()) / 2 - (y - SearchPage.map.getHeight() / 2)),
							null);
					mapCenterAffPoint = SearchPage.aff.inverseTransform(new Point(c.getWidth() / 2, c.getHeight() / 2),
							null);
				} catch (NoninvertibleTransformException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				SearchPage.affTargetX = labelCenteringAffPoint.getX();
				SearchPage.affTargetY = labelCenteringAffPoint.getY();
				SearchPage.aff.translate(-SearchPage.affTargetX, -SearchPage.affTargetY);

				SearchPage.startAffX = mapCenterAffPoint.getX() - labelCenteringAffPoint.getX();
				SearchPage.startAffY = mapCenterAffPoint.getY() - labelCenteringAffPoint.getY();

				c.repaint();
			});
		}
	}
}
