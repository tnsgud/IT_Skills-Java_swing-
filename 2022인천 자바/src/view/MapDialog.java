package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class MapDialog extends BaseDialog {

	JLabel map;
	JScrollPane pane;
	int pathDim[][];
	int dep, arrv;
	ArrayList<Integer> beeLine;

	public MapDialog(int dep, int arrv) {
		super("맵", 1000, 700);
		try {
			BasePage.datainit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.arrv = arrv;
		this.dep = dep;
		dijkstra();
		beeLine = getBeePath(arrv);
		ui();
		events();
	}

	void ui() {
		add(pane = new JScrollPane(map = new JLabel(BasePage.getIcon("./datafiles/map.jpg", 2500, 2500)) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				var g2 = (Graphics2D) g;

				g2.setStroke(new BasicStroke(3f));
				g2.setColor(Color.ORANGE);
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				if (beeLine != null) {

					for (int i = 1; i < beeLine.size(); i++) {
						if (i - 1 == 0 || i == beeLine.size() - 1) {
							g2.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
									new float[] { 2 }, 0));
						} else {
							g2.setStroke(new BasicStroke(3f));
						}
						int x1 = BasePage.posDim[beeLine.get(i - 1)][0];
						int x2 = BasePage.posDim[beeLine.get(i)][0];
						int y1 = BasePage.posDim[beeLine.get(i - 1)][1];
						int y2 = BasePage.posDim[beeLine.get(i)][1];
						g2.drawLine(x1, y1, x2, y2);
					}

					g2.setStroke(new BasicStroke(3f));
				}
			}
		}));

		var s = new JRadioButton("출발");
		var e = new JRadioButton("도착");

		map.setLayout(null);
		map.setAutoscrolls(true);
		map.setOpaque(true);
		map.setBackground(Color.blue);
		pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		s.setVerticalTextPosition(SwingConstants.BOTTOM);
		e.setVerticalTextPosition(SwingConstants.BOTTOM);
		s.setHorizontalTextPosition(SwingConstants.CENTER);
		e.setHorizontalTextPosition(SwingConstants.CENTER);

		map.add(s);
		map.add(e);

		s.setForeground(Color.ORANGE);
		e.setForeground(Color.ORANGE);

		s.setSelected(true);
		e.setSelected(true);
		s.setFocusable(false);
		e.setFocusable(false);
		s.setBounds((BasePage.posDim[dep][0] - 40 / 2) + 5, (BasePage.posDim[dep][1] - 50 / 2) + 10, 40, 50);
		e.setBounds((BasePage.posDim[arrv][0] - 40 / 2) + 5, (BasePage.posDim[arrv][1] - 50 / 2) + 10, 40, 50);
		s.setOpaque(false);
		e.setOpaque(false);
	}

	void events() {
		var ma = new MouseAdapter() {
			private Point first;

			@Override
			public void mousePressed(MouseEvent e) {
				first = e.getPoint();
				super.mousePressed(e);
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (first != null) {
					var viewPort = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, map);
					if (viewPort != null) {
						var dx = first.getX() - e.getX();
						var dy = first.getY() - e.getY();

						var view = viewPort.getViewRect();
						view.x += dx;
						view.y += dy;

						map.scrollRectToVisible(view);
					}

				}
				super.mouseDragged(e);
			}
		};

		map.addMouseListener(ma);
		map.addMouseMotionListener(ma);
	}

	void dijkstra() {
		int l = BasePage.posDim.length;
		pathDim = new int[4][l];
		for (int i = 1; i < l; i++) {
			pathDim[1][i] = BasePage.adjDim[dep][i];
			pathDim[2][i] = 0;
			if (BasePage.adjDim[i][dep] < BasePage.INF)
				pathDim[3][i] = dep;
		}

		pathDim[2][dep] = 1;
		pathDim[3][dep] = -1;

		for (int i = 1; i < l - 1; i++) {
			int min = 2147483647, idx = 0;
			for (int j = 1; j < l; j++) {
				if (pathDim[2][j] == 0 && pathDim[1][j] < min) {
					min = pathDim[1][j];
					idx = j;
				}
			}

			pathDim[2][idx] = 1;
			int from = idx;
			for (int to = 1; to < l; to++) {
				if (pathDim[2][to] == 0 && pathDim[1][to] > BasePage.adjDim[from][to] + pathDim[1][from]) {
					pathDim[1][to] = BasePage.adjDim[from][to] + pathDim[1][from];
					pathDim[3][to] = from;
				}
			}
		}
	}

	ArrayList<Integer> getBeePath(int arrv) {
		var path = new ArrayList<Integer>();

		path.add(arrv);

		while (pathDim[3][arrv] != -1) {
			path.add(pathDim[3][arrv]);
			arrv = pathDim[3][arrv];
		}
		Collections.reverse(path);
		return path;
	}
}